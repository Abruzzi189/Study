package com.application.uploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import com.application.chat.ChatManager;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.UploadImageRequest;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.StorageUtil;
import com.application.util.Utility;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

public class Uploader extends AsyncTask<UploadRequest, Integer, UploadResponse> {

  protected static final String TAG = "Uploader";

  private UploadProgress mUploadProgress;
  private UploadRequest mUploadRequest;
  private Context mContext;

  public Uploader(Context context, UploadProgress uploadProgress) {
    mContext = context;
    mUploadProgress = uploadProgress;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    LogUtils.i(TAG, "upload started()");
    if (mUploadProgress != null) {
      mUploadProgress.uploadStart();
    }
  }

  @Override
  protected UploadResponse doInBackground(UploadRequest... params) {
    LogUtils.i(TAG, "upload doInBackground()");
    mUploadRequest = params[0];
    if (mUploadRequest == null || mUploadRequest.getFile() == null) {
      return null;
    }
    if ((mUploadRequest.getType().equals(ChatManager.PHOTO))) {
      int angle = Utility.getAngle(mUploadRequest.getFile().getPath());
      Bitmap bitmap = BitmapFactory.decodeFile(mUploadRequest.getFile()
          .getPath());
      Bitmap bitmap2 = Utility.rotateImage(bitmap, angle);
      try {
        FileOutputStream fileOutputStream = new FileOutputStream(
            mUploadRequest.getFile());
        bitmap2.compress(CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.close();
        bitmap.recycle();
        bitmap = null;
        bitmap2.recycle();
        bitmap2 = null;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
      }

    }
    UploadResponse uploadResponse = up(mUploadRequest.toURL(),
        mUploadRequest.getFile(), true);
    if (mUploadRequest.getType().equals(ChatManager.VIDEO)
        && uploadResponse.getCode() == Response.SERVER_SUCCESS) {
      Bitmap bitmap = ThumbnailUtils
          .createVideoThumbnail(mUploadRequest.getFile().getPath(), Thumbnails.MINI_KIND);

      File file = null;
      FileOutputStream outputStream = null;
      try {
        String fileName = String.format("temp_%s", System.currentTimeMillis());
        file = StorageUtil.createFileTemp(mContext, fileName);
        outputStream = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, outputStream);
        bitmap.recycle();
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        if (outputStream != null) {
          try {
            outputStream.flush();
            outputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      if (file != null) {
        String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
        UploadImageRequest uploadImageRequest = new UploadImageRequest(mUploadRequest.getToken(),
            UploadImageRequest.VIDEO_THUMBNAIL, md5Encrypted);
        up(uploadImageRequest.toURLWithFileId(uploadResponse.getFileId()), file, false);
        file.delete();
      }
    }
    return uploadResponse;
  }

  @Override
  protected void onPostExecute(UploadResponse result) {
    super.onPostExecute(result);
    LogUtils.i(TAG, "upload onPostExecute()");
    if (mUploadProgress != null) {
      if (result.getCode() == Response.SERVER_SUCCESS) {
        mUploadProgress.uploadSuccess(mUploadRequest, result);
      } else {
        mUploadProgress.uploadFail(result.getCode());
      }
    }
  }

  @Override
  protected void onProgressUpdate(Integer... progress) {
    if (mUploadProgress != null) {
      mUploadProgress.uploadProgress(progress[0]);
    }
  }

  private UploadResponse upload(String url, File file) {
    LogUtils.d(TAG, "destination=" + url);
    ResponseData responseData = new ResponseData();
    String responseBody = "";
    HttpClient httpclient = new DefaultHttpClient();

    HttpPost httppost = new HttpPost(url);
    InputStreamEntity reqEntity;
    try {
      reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
      reqEntity.setContentType("binary/octet-stream");
      reqEntity.setChunked(true); // Send in multiple parts if needed
      httppost.setEntity(reqEntity);
      HttpResponse response = httpclient.execute(httppost);
      // Do something with response...
      responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
      responseData.setStatus(Response.CLIENT_SUCCESS);
      responseData.setText(responseBody);
      responseData.makeJSONObject();
      LogUtils.d(TAG, "data receive=" + responseBody);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      responseData.setStatus(Response.CLIENT_FILE_NOT_FOUND);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      responseData.setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
    } catch (IOException e) {
      responseData.setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
      responseData.setStatus(Response.CLIENT_ERROR_PARSE_JSON);
    }
    return mUploadRequest.parseResponseData(responseData);
  }

  private UploadResponse up(String url, File file, boolean isPublishProgress) {
    OutputStream outputStream;
    URL u;
    ResponseData responseData = new ResponseData();
    try {
      u = new URL(url);
      HttpURLConnection mUrlConnection;
      mUrlConnection = (HttpURLConnection) u.openConnection();
      mUrlConnection.setDoOutput(true);
      mUrlConnection.setRequestMethod("POST");
      mUrlConnection.setRequestProperty("Content-Language", "en-US");
      mUrlConnection.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded");
      mUrlConnection.setConnectTimeout(5000);
      mUrlConnection.setReadTimeout(5000);
      outputStream = mUrlConnection.getOutputStream();

      BufferedInputStream bufferedInputStream = new BufferedInputStream(
          new FileInputStream(file));
      long total = file.length();
      long actal = 0;

      int length = 0;
      byte[] buffer = new byte[4096];
      int percent = 0;
      while ((length = bufferedInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, length);
        actal += length;
        percent = (int) (((float) actal / total) * 100);
        if (isPublishProgress) {
          publishProgress(percent);
        }
        LogUtils.d(TAG, String.format("Percent upload %d", percent));
      }
      outputStream.close();
      bufferedInputStream.close();
      InputStreamReader isr = new InputStreamReader(
          mUrlConnection.getInputStream(), "UTF-8");
      BufferedReader buf = new BufferedReader(isr);
      String data = buf.readLine();
      LogUtils.d(TAG, String.format("Upload response %s", data));
      isr.close();
      buf.close();
      responseData.setStatus(Response.CLIENT_SUCCESS);
      responseData.setText(data);
      responseData.makeJSONObject();
    } catch (IOException e) {
      e.printStackTrace();
      responseData.setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
    } catch (JSONException e) {
      e.printStackTrace();
      responseData.setStatus(Response.CLIENT_ERROR_PARSE_JSON);
    } catch (Exception e) {
    }
    return mUploadRequest.parseResponseData(responseData);
  }
}

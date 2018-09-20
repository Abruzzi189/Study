package com.application.uploadmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore.Video.Thumbnails;
import com.application.chat.ChatManager;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.UploadImageRequest;
import com.application.uploader.CountingInputStreamEntity;
import com.application.uploader.UploadRequest;
import com.application.uploader.UploadResponse;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.PhotoUtils;
import com.application.util.StorageUtil;
import com.application.util.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

public class AndGUploadManager extends AbstractUploadManager {

  private final ExecutorService executor;
  private Context mContext;
  private Map<Long, Uploader> mUploaders = new HashMap<Long, Uploader>();

  public AndGUploadManager(Context context, IUpload iUpload) {
    super(iUpload);
    mContext = context;

    // create own executor instead of AsyncTask.SERIAL_EXECUTOR for upload without wait another async done
    executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void execute(long uploadId, IUploadResource uploadResource) {
    Uploader uploader = new Uploader(mContext, mIUpload, uploadId);
    mUploaders.put(uploadId, uploader);

    // #13838 execute async in serial
    uploader.executeOnExecutor(executor, uploadResource);
  }

  @Override
  public void cancel(long uploadId) {
    super.cancel(uploadId);
    Uploader uploader = mUploaders.get(uploadId);
    if (uploader != null) {
      uploader.cancel(true);
    }
  }

  private class Uploader extends
      AsyncTask<IUploadResource, Integer, UploadResponse> {

    protected static final String TAG = "Uploader";

    private IUploadResource mUploadResource;
    private Context mContext;
    private long mUploadId;
    private IUpload mIUpload;

    public Uploader(Context context, IUpload upload, long uploadId) {
      mContext = context;
      mUploadId = uploadId;
      mIUpload = upload;

      // show progress upload intermediately
      publishProgress(0);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mIUpload.onPending(mUploadId);
    }

    @Override
    protected UploadResponse doInBackground(IUploadResource... params) {
      LogUtils.i(TAG, "upload doInBackground()");
      mUploadResource = params[0];
      UploadRequest mUploadRequest = (UploadRequest) mUploadResource
          .getResourceExtras();
      if (mUploadRequest == null || mUploadRequest.getFile() == null) {
        return null;
      }
      if ((mUploadRequest.getType().equals(ChatManager.PHOTO))) {
        int angle = Utility
            .getAngle(mUploadRequest.getFile().getPath());
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT < 11) {
          int width = mContext.getResources().getDisplayMetrics().widthPixels;
          bitmap = PhotoUtils.decodeSampledBitmapFromFile(
              mUploadRequest.getFile().getPath(), width, width);
        } else {
          bitmap = BitmapFactory.decodeFile(mUploadRequest.getFile()
              .getPath());
        }
        Bitmap bitmap2 = Utility.rotateImage(bitmap, angle);
        try {
          FileOutputStream fileOutputStream = new FileOutputStream(mUploadRequest.getFile());
          bitmap2.compress(CompressFormat.JPEG, 100, fileOutputStream);
          fileOutputStream.close();
          bitmap.recycle();
          bitmap2.recycle();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (Exception e) {
        }

      }

      // upload file no matter photo, video, audio
      File fileUpload = new File(mUploadRequest.getFile().getPath());
      // calculate md5
      String md5Encrypted = ImageUtil.getMD5EncryptedString(fileUpload);
      mUploadRequest.setHashSum(md5Encrypted);
      UploadResponse uploadResponse = upload(mUploadRequest,
          mUploadRequest.toURL(), mUploadRequest.getFile(), true);

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
          String md5 = ImageUtil.getMD5EncryptedString(file);
          UploadImageRequest uploadImageRequest = new UploadImageRequest(mUploadRequest.getToken(),
              UploadImageRequest.VIDEO_THUMBNAIL, md5);
          upload(mUploadRequest, uploadImageRequest.toURLWithFileId(uploadResponse.getFileId()),
              file, false);
          file.delete();
        }
      }
      return uploadResponse;
    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
      mIUpload.onCancel(mUploadId);
    }

    @Override
    protected void onPostExecute(UploadResponse result) {
      super.onPostExecute(result);
      if (result.getCode() == Response.SERVER_SUCCESS) {
        mIUpload.onSuccess(mUploadId, UploadResponseCode.NO_RESPONSE,
            result);
      } else {
        mIUpload.onFailed(mUploadId, UploadResponseCode.NO_RESPONSE,
            result);
      }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
      LogUtils.d("UploadProgress", progress[0] + " %");
      mIUpload.onRunnning(mUploadId, progress[0]);
    }

    private UploadResponse upload(UploadRequest mUploadRequest, String url,
        File file, final boolean isPublishProgress) {
      ResponseData responseData = new ResponseData();
      final HttpResponse resp;
      final HttpClient httpClient = new DefaultHttpClient();
      final HttpPost post = new HttpPost(url);

      try {
        CountingInputStreamEntity entity = new CountingInputStreamEntity(
            new FileInputStream(file), file.length());
        entity.setUploadListener(new CountingInputStreamEntity.UploadListener() {
          int bufferPercent = 0;
          boolean isStart = false;

          @Override
          public void onChange(int percent) {
            if (isPublishProgress) {
              // publishProgress only one when percent=0%
              if (!isStart && percent == 0) {
                isStart = true;
                publishProgress(percent);
                return;
              }

              int value = percent - bufferPercent;
              if ((percent == 100 || value % 5 == 0)
                  && percent > bufferPercent) {
                publishProgress(percent);
                bufferPercent = percent;
              }
            }
          }
        });
        post.setEntity(entity);
        resp = httpClient.execute(post);
        String data = EntityUtils.toString(resp.getEntity(), "UTF-8");
        responseData.setStatus(Response.CLIENT_SUCCESS);
        responseData.setText(data);
        responseData.makeJSONObject();
      } catch (IOException e) {
        e.printStackTrace();
        responseData
            .setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
      } catch (JSONException e) {
        e.printStackTrace();
        responseData.setStatus(Response.CLIENT_ERROR_PARSE_JSON);
      } catch (Exception e) {
      }
      return mUploadRequest.parseResponseData(responseData);
    }
  }
}

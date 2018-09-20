package com.application.imageloader;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.response.UploadImageResponse;
import com.application.util.LogUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import org.json.JSONException;

public class ImageUploader extends
    AsyncTask<UploadImageRequest, Integer, UploadImageResponse> {

  private static final String TAG = "ImageUploader";
  private static final int IO_BUFFER_SIZE = 8 * 1024;
  private boolean isUploading = false;
  private UploadImageProgress muUploadImageProgress;

  public ImageUploader() {
  }

  public ImageUploader(UploadImageProgress upProgress) {
    muUploadImageProgress = upProgress;
  }

  @Override
  protected void onPreExecute() {
    if (muUploadImageProgress != null) {
      muUploadImageProgress.uploadImageStart();
    }
    super.onPreExecute();
  }

  @Override
  protected UploadImageResponse doInBackground(UploadImageRequest... params) {
    isUploading = true;
    UploadImageRequest imageRequest = params[0];
    String url = imageRequest.toURL();
    File queueUploadFile = imageRequest.mFile;

    if (imageRequest.mType == UploadImageRequest.UPLOAD_URL) {
      OutputStream outputStream;
      try {
        outputStream = new FileOutputStream(queueUploadFile);
        downloadUrlToStream(imageRequest.mURL, outputStream);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    LogUtils.d(TAG, "URL to upload[" + url + "]");
    return uploadImage(url, queueUploadFile);
  }

  @Override
  protected void onPostExecute(UploadImageResponse result) {
    super.onPostExecute(result);
    isUploading = false;
    if (muUploadImageProgress != null) {
      if (result.getCode() == Response.SERVER_SUCCESS) {
        muUploadImageProgress.uploadImageSuccess(result);
      } else {
        muUploadImageProgress.uploadImageFail(result.getCode());
      }
    }
  }

  private UploadImageResponse uploadImage(String url, File file) {
    UploadImageResponse uploadImageResponse = null;
    ResponseData responseData = new ResponseData();
    StringBuilder result = new StringBuilder();
    try {

      URL u = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) u.openConnection();

      // post method
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Language", "en-US");
      conn.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded");

      FileInputStream fileInputStream = new FileInputStream(file);
      int bytesAvailable = fileInputStream.available();
      byte[] buffer = new byte[bytesAvailable];
      fileInputStream.read(buffer, 0, bytesAvailable);
      fileInputStream.close();
      byte[] postDataByte = buffer;
      try {
        OutputStream out = conn.getOutputStream();
        out.write(postDataByte);
        out.close();
      } catch (IOException ex) {
        ex.printStackTrace();
        responseData
            .setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
      }
      // get data from server
      InputStreamReader isr = new InputStreamReader(conn.getInputStream());
      BufferedReader buf = new BufferedReader(isr);

      String data = null;
      while ((data = buf.readLine()) != null) {
        if (isCancelled()) {
          break;
        }
        result.append(data);
      }
      isr.close();
      buf.close();
      // write
      responseData.setStatus(Response.CLIENT_SUCCESS);
      responseData.setText(result.toString());
      responseData.makeJSONObject();
      LogUtils.i(TAG, "data receive=" + result);

    } catch (FileNotFoundException ex) {
      System.out.println(ex.toString());
      responseData.setStatus(Response.CLIENT_FILE_NOT_FOUND);
    } catch (IOException ex) {
      System.out.println(ex.toString());
      responseData.setStatus(Response.CLIENT_ERROR_CAN_NOT_CONNECTION);
    } catch (JSONException e) {
      responseData.setStatus(Response.CLIENT_ERROR_PARSE_JSON);
      e.printStackTrace();
    }
    uploadImageResponse = new UploadImageResponse(responseData);
    return uploadImageResponse;
  }

  public boolean isUploading() {
    return isUploading;
  }

  /**
   * Download a bitmap from a URL and write the content to an output stream.
   *
   * @param urlString The URL to fetch
   * @return true if successful, false otherwise
   */
  public boolean downloadUrlToStream(String urlString,
      OutputStream outputStream) {
    HttpURLConnection urlConnection = null;
    BufferedOutputStream out = null;
    BufferedInputStream in = null;

    try {
      final URL url = new URL(urlString);
      urlConnection = (HttpURLConnection) url.openConnection();

      in = new BufferedInputStream(urlConnection.getInputStream(),
          IO_BUFFER_SIZE);
      out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

      int b;
      while ((b = in.read()) != -1) {
        out.write(b);
      }
      return true;
    } catch (final IOException e) {
      LogUtils.e(TAG, "Error in downloadBitmap - " + e);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (final IOException e) {
      }
    }
    return false;
  }

  public interface UploadImageProgress {

    public void uploadImageStart();

    public void uploadImageSuccess(UploadImageResponse response);

    public void uploadImageFail(int code);
  }

}

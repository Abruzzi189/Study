package com.application.uploader;

import com.application.connection.ResponseData;
import com.application.uploadmanager.IUploadResource;
import java.io.File;

public interface UploadRequest {

  String toURL();

  File getFile();

  String getFileName();

  String getType();

  UploadResponse parseResponseData(ResponseData responseData);

  String getToken();

  /**
   * set hash sum for upload, cause calculate hash sum is heavy process so set it later inside async
   * task
   *
   * @see com.application.uploadmanager.AndGUploadManager.Uploader#doInBackground(IUploadResource...)
   */
  void setHashSum(String hashSum);
}

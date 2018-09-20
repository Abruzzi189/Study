package com.application.uploader;

public interface UploadProgress {

  void uploadStart();

  void uploadSuccess(UploadRequest uploadRequest, UploadResponse response);

  void uploadProgress(int percentage);

  void uploadFail(int code);
}

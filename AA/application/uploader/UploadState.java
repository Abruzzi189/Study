package com.application.uploader;

public interface UploadState {

  public static final int UPLOAD_SUCCESS = 0;
  public static final int UPLOAD_FAILED = 1;
  public static final int UPLOAD_CANCEL = 2;
  public static final int UPLOAD_PENDING = 3;
  public static final int UPLOAD_PROGRESS = 4;
  public static final int UPLOAD_START = 5;
}

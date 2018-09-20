package com.application.uploadmanager;

public interface UploadState {

  public int UNKNOW = -1;
  public int PENDING = 0;
  public int RUNNING = 1;
  public int SUCCESSFUL = 2;
  public int FAILED = 3;
  public int PAUSED = 4;
  public int INITIAL = 5;
  public int CANCEL = 6;
}

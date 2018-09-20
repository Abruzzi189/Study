package com.application.downloadmanager;

import android.app.DownloadManager;

public interface DownloadState {

  int PENDING = DownloadManager.STATUS_PENDING;
  int RUNNING = DownloadManager.STATUS_RUNNING;
  int SUCCESSFUL = DownloadManager.STATUS_SUCCESSFUL;
  int PAUSED = DownloadManager.STATUS_PAUSED;
  int FAILED = DownloadManager.STATUS_FAILED;
  int UNKNOW = -1;
}

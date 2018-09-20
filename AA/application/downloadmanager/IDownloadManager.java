package com.application.downloadmanager;

public interface IDownloadManager {

  public int getState(long downloadId);

  public int getProgress(long downloadId);

  public void registerDownloadProgressChange(IDownloadProgress progress);

  public void unregisterDownloadProgressChange(IDownloadProgress progress);

  public void appendDownloadId(long downloadId);

  public void removeDownloadId(long downloadId);

  public void terminate();

  public void clearDownloadIds();
}

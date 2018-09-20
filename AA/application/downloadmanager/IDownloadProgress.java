package com.application.downloadmanager;

import android.net.Uri;

public interface IDownloadProgress {

  public void onDownloadPending(long downloadId, int progress);

  public void onDownloadRunning(long downloadId, int progress);

  public void onDownloadPaused(long downloadId, int progress);

  public void onDownloadSuccessful(long downloadId, Uri fileUri);

  public void onDownloadFailed(long downloadId, int progress, int reason);
}

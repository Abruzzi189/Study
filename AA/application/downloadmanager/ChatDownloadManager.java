package com.application.downloadmanager;

public interface ChatDownloadManager {

  public void saveDownload(String messageId, long downloadId);

  public String getMessageId(long downloadId);

  public long getDownloadId(String messageId);
}

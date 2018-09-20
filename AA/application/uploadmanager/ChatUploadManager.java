package com.application.uploadmanager;

public interface ChatUploadManager {

  public void saveUpload(String messageId, long uploadId);

  public String getMessageId(long uploadId);

  public long getUploadId(String messageId);
}

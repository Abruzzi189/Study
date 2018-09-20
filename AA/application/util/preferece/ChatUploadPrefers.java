package com.application.util.preferece;

import android.content.SharedPreferences;
import com.application.uploadmanager.ChatUploadManager;

public class ChatUploadPrefers extends BasePrefers implements ChatUploadManager {

  public static final int INVALID_UPLOAD_ID = -1;

  public ChatUploadPrefers() {
    super();
  }

  @Override
  protected String getFileNamePrefers() {
    return "chatuploadprfers";
  }

  @Override
  public void saveUpload(String messageId, long uploadId) {
    SharedPreferences.Editor editor = getEditor();
    editor.putLong(messageId, uploadId).commit();
    editor.putString(uploadId + "", messageId).commit();
  }

  @Override
  public String getMessageId(long uploadId) {
    return getPreferences().getString(uploadId + "", "");
  }

  @Override
  public long getUploadId(String messageId) {
    return getPreferences().getLong(messageId, INVALID_UPLOAD_ID);
  }
}

package com.application.util.preferece;

import android.content.SharedPreferences;
import com.application.downloadmanager.ChatDownloadManager;

public class ChatDownloadPrefers extends BasePrefers implements
    ChatDownloadManager {

  public ChatDownloadPrefers() {
    super();
  }

  @Override
  public void saveDownload(String messageId, long downloadId) {
    // must check it because exist only pair of messageId and downloadId
    SharedPreferences sharedPreferences = getPreferences();
    if (sharedPreferences.contains(messageId)) {
      long tempId = sharedPreferences.getLong(messageId, -1);
      // remove downloadId which accaciate with messageId
      sharedPreferences.edit().remove(tempId + "").commit();
    }
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(messageId, downloadId).commit();
    editor.putString(downloadId + "", messageId).commit();
  }

  @Override
  public String getMessageId(long downloadId) {
    return getPreferences().getString(downloadId + "", "");
  }

  @Override
  public long getDownloadId(String messageId) {
    return getPreferences().getLong(messageId, -1);
  }

  @Override
  protected String getFileNamePrefers() {
    return "chatdownloadprfers";
  }
}

package com.application.util.preferece;

import android.text.TextUtils;
import com.application.util.StorageUtil;

public class DownloadFileTempPrefers extends BasePrefers {

  private static final Object OBJECT = new Object();

  public DownloadFileTempPrefers() {
    super();
  }

  @Override
  protected String getFileNamePrefers() {
    return "downloadfiletemp";
  }

  public void saveUserIdToSendAndMessageId(String downloadId,
      String userIdToSend, String messsageId, String fileId) {
    getEditor().putString(downloadId,
        userIdToSend + " " + messsageId + " " + fileId).commit();
  }

  public String getUserId(String downloadId) {
    try {
      String[] data = getPreferences().getString(downloadId, "").split(
          " ");
      return data[0];
    } catch (Exception exception) {
      exception.printStackTrace();
      return "";
    }
  }

  public String getMessageId(String downloadId) {
    try {
      String[] data = getPreferences().getString(downloadId, "").split(
          " ");
      return data[1];
    } catch (Exception exception) {
      exception.printStackTrace();
      return "";
    }
  }

  public String getFileId(String downloadId) {
    try {
      String[] data = getPreferences().getString(downloadId, "").split(
          " ");
      return data[2];
    } catch (Exception exception) {
      exception.printStackTrace();
      return "";
    }
  }

  public void removeDownloadId(String downloadId) {
    synchronized (OBJECT) {
      getEditor().remove(downloadId).commit();
    }
  }

  /**
   * Mapping thong tin messageId va file Path dua vao thong tin downloadId da luu
   */
  public void mapMessageIdAndFilePath(long downloadId, String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return;
    }
    // Lay ra cac thong tin messageId va userIdToSend de
    // mapping
    String userId = getUserId(downloadId + "");
    String messageId = getMessageId(downloadId + "");
    // Mapping giua messageId va file duoc download.
    StorageUtil.saveMessageIdAndFilePathByUser(mContext, userId, messageId,
        filePath);
    // Xoa lai thong tin tam khoi file
    removeDownloadId(downloadId + "");
  }
}

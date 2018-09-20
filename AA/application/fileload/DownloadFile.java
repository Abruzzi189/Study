package com.application.fileload;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import com.application.chat.ChatMessage;
import com.application.util.StorageUtil;
import com.application.util.preferece.DownloadFileTempPrefers;
import com.application.util.preferece.UserPreferences;
import java.io.File;

public class DownloadFile {

  private Context mContext;
  private FILETYPE mType;
  private String mUserId;
  private String mMessageId;
  private String mUrl;
  private String mFileId;
  public DownloadFile(Context context, String url, ChatMessage chatMessage,
      FILETYPE type) {
    mContext = context;
    mUrl = url;
    mType = type;
    if (chatMessage.isOwn()) {
      mUserId = UserPreferences.getInstance().getUserId();
    } else {
      mUserId = chatMessage.getUserId();
    }
    mMessageId = chatMessage.getMessageId();
    mFileId = chatMessage.getFileMessage().getFileId();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private Request makeRequest(String url, FILETYPE type) {
    Request request = new Request(Uri.parse(url));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }
    File file = null;
    switch (type) {
      case AUDIO:
        file = StorageUtil.getAudioFileTempByUser(mContext);
        //request.setDestinationUri(UriCompat.fromFile(mContext, file));
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,
            file.getName());
        request.setMimeType("audio/mp3");
        break;
      case VIDEO:
        file = StorageUtil.getVideoFileTempByUser(mContext);
//				request.setDestinationUri(UriCompat.fromFile(mContext, file));
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,
            file.getName());
        request.setMimeType("video/mp4");
        break;
      case PHOTO:
        file = StorageUtil.getPhotoFileTempToDownload(mContext);
//				request.setDestinationUri(UriCompat.fromFile(mContext, file));
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,
            file.getName());
        request.setMimeType("image/jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        }
        break;
      default:
        break;
    }
    return request;
  }

  public String isFileDownloaded() {
    return StorageUtil.getFilePathByUserIdAndFileId(mContext, mUserId,
        mMessageId);
  }

  public long startDownload() {
    DownloadManager downloadManager = (DownloadManager) mContext
        .getSystemService(Context.DOWNLOAD_SERVICE);
    Request request = makeRequest(mUrl, mType);
    long downloadId = downloadManager.enqueue(request);

    // Luu lai thong tin giua file dang duoc download va thong tin:
    // userIdToSend + messageId. De su dung khi download file thanh cong.
    DownloadFileTempPrefers prefers = new DownloadFileTempPrefers();
    prefers.saveUserIdToSendAndMessageId(downloadId + "", mUserId,
        mMessageId, mFileId);
    return downloadId;
  }

  public enum FILETYPE {
    AUDIO, VIDEO, NONE, PHOTO
  }
}

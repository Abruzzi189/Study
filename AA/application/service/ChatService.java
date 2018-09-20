package com.application.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.application.chat.ChatManager;
import com.application.chat.ChatManager.IStartSentMediaMessage;
import com.application.connection.Response;
import com.application.status.StatusConstant;
import com.application.status.StatusController;
import com.application.status.StatusDBManager;
import com.application.uploader.FileUploadRequest;
import com.application.uploader.ImageUploadRequest;
import com.application.uploader.UploadRequest;
import com.application.uploader.UploadResponse;
import com.application.uploadmanager.ChatUploadManager;
import com.application.uploadmanager.CustomUploadService;
import com.application.uploadmanager.IUploadCustom;
import com.application.uploadmanager.IUploadResource;
import com.application.uploadmanager.UploadService;
import com.application.util.LogUtils;
import com.application.util.StorageUtil;
import com.application.util.preferece.ChatUploadPrefers;
import com.application.util.preferece.UserPreferences;

import java.io.File;
import java.util.Date;

import glas.bbsystem.R;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class ChatService extends Service {

  public static final String EXTRA_OPTION = "OPTION";
  public static final int STOP_CHAT = 1;
  public static final int AUTHEN = 0;
  private static final String TAG = "ChatService";
  private final IBinder mBinder = new LocalBinder();
  public CustomUploadService mUploadService;
  private ChatUploadManager mChatUploadManager;
  private boolean mIsBound = false;
  private ServiceConnection mUploadConnection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mUploadService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mUploadService = (CustomUploadService) ((UploadService.UploadServiceBinder) service)
          .getService();
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // send authenticate message
    getChatManager().sendAuthenticationMessage();
    doBindUploadService();
    mChatUploadManager = new ChatUploadPrefers();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      return super.onStartCommand(intent, flags, startId);
    }
    int extra = intent.getIntExtra(EXTRA_OPTION, AUTHEN);
    if (extra == STOP_CHAT) {
      getChatManager().clearChat();
    } else {
      getChatManager().sendAuthenticationMessage();
      // stop service with startId
      stopSelf(startId);
    }
    return super.onStartCommand(intent, flags, startId);
  }

  public void sendPhoto(
      final String messageId, final Date date, final String userIdToSend, final String fileName,
      final File file) {
    String token = UserPreferences.getInstance().getToken();
    ImageUploadRequest imageUploadRequest = new ImageUploadRequest(token, file, fileName,
        ImageUploadRequest.CHAT_IMAGE);
    sendMedia(messageId, date, userIdToSend, fileName, imageUploadRequest, ChatManager.PHOTO,
        0);
  }

  public void sendAudio(
      final String messageId, final Date date, final String userIdToSend, final String fileName,
      final File file, final long audioTime) {
    String token = UserPreferences.getInstance().getToken();
    FileUploadRequest fileUploadRequest = new FileUploadRequest(token, file, fileName,
        ChatManager.AUDIO);
    sendMedia(messageId, date, userIdToSend, fileName, fileUploadRequest, ChatManager.AUDIO,
        audioTime);
  }

  public void sendVideo(
      final String messageId, final Date date, final String userIdToSend, final String fileName,
      final File file) {
    String token = UserPreferences.getInstance().getToken();
    FileUploadRequest fileUploadRequest = new FileUploadRequest(token, file, fileName,
        ChatManager.VIDEO);
    sendMedia(messageId, date, userIdToSend, fileName, fileUploadRequest, ChatManager.VIDEO, 0);
  }

  /**
   * @param fileName In case Audio, fileName is duration of audio
   */
  private void sendMedia(final String msgId, final Date date,
      final String userIdToSend, String fileName,
      final UploadRequest uploadRequest, final String fileType,
      final long audioTime) {

    // Must remove space in fileName
    UserPreferences userPreferences = UserPreferences.getInstance();
    final String name = fileName.replace(" ", "");
    final String userId = userPreferences.getUserId();
    final ChatUploadResource chatUploadResource = new ChatUploadResource(
        msgId, uploadRequest);
    String currentUserId = userPreferences.getUserId();

    // Must use currentUserId to save maps
    StorageUtil.saveMessageIdAndFilePathByUser(ChatService.this,
        currentUserId, msgId, uploadRequest.getFile().getPath());
    getChatManager().sendStartSentMediaMessage(msgId,date, userId, userIdToSend,
        fileType, new IStartSentMediaMessage() {
          @Override
          public void onSentResult(boolean isSuccess, Message message) {
            // message 1 sent, start upload file
            long uploadId = mUploadService
                .executeUpload(chatUploadResource);
            mUploadService.addUploadCustomListener(StatusController
                .getInstance(ChatService.this));

            // update message file in database
            final Bundle bundle = new Bundle();
            bundle.putString(StatusConstant.ARG_MSG_ID, message.id);
            bundle.putString(StatusConstant.ARG_CHAT_MSG_ID, msgId);
            bundle.putLong(StatusConstant.ARG_UPLOAD_ID, uploadId);
            bundle.putString(StatusConstant.ARG_FILE_TYPE, fileType);
            bundle.putString(StatusConstant.ARG_FILE_PATH,
                uploadRequest.getFile().getPath());
            bundle.putLong(StatusConstant.ARG_AUDIO_TIME, audioTime);
            StatusController.getInstance(ChatService.this)
                .updateMsgFile(bundle);
          }
        });
    mUploadService.addUploadCustomListener(new IUploadCustom() {
      @Override
      public void onSuccess(long uploadId,
          IUploadResource uploadResource, int responseCode,
          Object response) {
        if (!chatUploadResource.equals(uploadResource)) {
          return;
        }
        mUploadService.removeUploadCustomListener(this);
        UploadResponse uploadResponse = (UploadResponse) response;
        String fileId = uploadResponse.getFileId();
        if (fileType.equalsIgnoreCase(ChatManager.AUDIO)) {
          // StorageUtil.saveMessageIdAndFilePathByUser(
          // getApplicationContext(), userIdToSend, messageId,
          // uploadRequest.getFile().getPath());
        } else if (fileType.endsWith(ChatManager.VIDEO)) {
          // StorageUtil.saveMessageIdAndFilePathByUser(
          // getApplicationContext(), userIdToSend, messageId,
          // uploadRequest.getFile().getPath());
        }
        LogUtils.d(TAG, "Start send confirm message");
        if (StatusController.getInstance(ChatService.this).isSendMessageStartSuccess(msgId)) {
//          getChatManager().sendConfirmSentFileMessage(date, userId, userIdToSend, fileType, fileId, name);
          getChatManager().sendConfirmSentFileMessage(msgId, userId, userIdToSend, fileType, fileId, name);
        } else {
          StatusDBManager.getInstance(ChatService.this).updateStatus(msgId, StatusConstant.STATUS_ERROR);

        }
      }

      @Override
      public void onPending(long uploadId, IUploadResource uploadResource) {
        LogUtils.i(TAG, "onPending: uploadId=" + uploadId);
      }

      @Override
      public void onInprogress(long uploadId,
          IUploadResource uploadResource, int progress) {
        LogUtils.i(TAG, "onInprogress: uploadId=" + uploadId
            + "\nprogress=" + progress);
      }

      @Override
      public void onFailed(long uploadId, IUploadResource uploadResource,
          int responseCode, Object response) {
        if (!chatUploadResource.equals(uploadResource)) {
          return;
        }
        LogUtils.i(TAG, "onFailed: uploadId=" + uploadId);
        mUploadService.removeUploadCustomListener(this);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager
            .getInstance(ChatService.this);
        Intent intent = new Intent(ChatManager.ACTION_SEND_FILE_ERROR);
        if (responseCode == Response.SERVER_UPLOAD_FILE_ERROR) {
          intent.putExtra(ChatManager.EXTRA_DATA, getString(R.string.upload_fail));
        } else {
          intent.putExtra(ChatManager.EXTRA_DATA,
              getString(R.string.an_error_occurred_while_send_file));
        }

        intent.putExtra(ChatManager.EXTRA_DATA,
            getString(R.string.an_error_occurred_while_send_file));
        broadcastManager.sendBroadcast(intent);
      }

      @Override
      public void onAdded(long uploadId, IUploadResource uploadResource) {
        if (chatUploadResource.equals(uploadResource)) {
          LogUtils.i(TAG, "onAdded: uploadId=" + uploadId);
          mChatUploadManager.saveUpload(msgId, uploadId);
        }
      }

      @Override
      public void onPaused(long uploadId, IUploadResource uploadResource,
          int responseCode) {
        LogUtils.i(TAG, "onPaused: uploadId=" + uploadId);
        // Do nothing
      }

      @Override
      public void onCancel(long uploadId) {
        // Do nothing
        LogUtils.i(TAG, "onCancel: uploadId=" + uploadId);
        //http://10.64.100.201/issues/14181
        //THANGPQ - because call addUploadCustomListener before, so must remove when upload cancel
        mUploadService.removeUploadCustomListener(this);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LogUtils.i(TAG, "onDestroy()");
    getChatManager().clearChat();
    doUnbindUploadService();
  }

  public ChatManager getChatManager() {

    return ChatManager.getInstance(getApplicationContext());
  }

  public void doBindUploadService() {
    bindService(new Intent(this, CustomUploadService.class),
        mUploadConnection, Context.BIND_AUTO_CREATE);
    mIsBound = true;
  }

  public void doUnbindUploadService() {
    if (mIsBound) {
      unbindService(mUploadConnection);
      mIsBound = false;
    }
  }

  public interface OnSendAudioListener {

    public void OnSendSuccess(String userId, String fileId);

    public void OnSendStart();

    public void OnSendFaile();
  }

  public interface OnSendVideoListener {

    public void onSendSuccess(String fileId);
  }

  public class LocalBinder extends Binder {

    public ChatService getService() {
      return ChatService.this;
    }
  }

  private class ChatUploadResource implements IUploadResource {

    UploadRequest uploadRequest;
    String messageId;

    public ChatUploadResource(String messageId, UploadRequest uploadRequest) {
      this.messageId = messageId;
      this.uploadRequest = uploadRequest;
    }

    @Override
    public String getFilePath() {
      return uploadRequest.getFile().getPath();
    }

    @Override
    public Object getResourceExtras() {
      return uploadRequest;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ChatUploadResource) {
        ChatUploadResource resource = (ChatUploadResource) o;
        return messageId.equals(resource.messageId);
      }
      return false;
    }
  }
}

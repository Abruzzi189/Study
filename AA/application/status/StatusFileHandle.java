package com.application.status;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.application.chat.ChatManager;
import com.application.chat.MessageStatus;
import com.application.uploadmanager.IUploadResource;
import com.application.util.LogUtils;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class StatusFileHandle extends StatusHandle {

  private static StatusFileHandle instance;

  public StatusFileHandle(Context context) {
    super(context);
  }

  public static StatusFileHandle getInstance(Context context) {
    if (instance == null) {
      instance = new StatusFileHandle(context);
    }
    return instance;
  }

  @Override
  public void createMsg(Message msg) {
    if (msg.value != null) {
      if (msg.value.length() == 1) {
        // message send file 1, value "a", "v", "p"
        mStatusDBManager.createIfNoExist(msg,
            StatusConstant.STATUS_START);
      } else if (msg.value.length() > 2) {
        // check message 2
        String[] items = msg.value.split("\\|");
        if (items.length == 4) {
          String fileId = items[2];
          MessageInDB messageInDB = mStatusDBManager
              .queryFileId(fileId);

          if (messageInDB != null) {
            mStatusDBManager.updateStatus(messageInDB.getId(),
                StatusConstant.STATUS_SUCCESS);
          }
        }
      }
    }
  }

  @Override
  public void updateMsg(MessageStatus status) {
    // receiver result of message 1
    if (status.isSentSuccess()) {
      // change to sending file, continue check upload
      MessageInDB msgInDB = mStatusDBManager.query(status
          .getMessageCheckedId());
      if (msgInDB.getStatus() != StatusConstant.STATUS_SUCCESS
          && msgInDB.getStatus() != StatusConstant.STATUS_ERROR) {
        mStatusDBManager.updateStatus(status.getMessageCheckedId(),
            StatusConstant.STATUS_SENDING_FILE);
      }
    } else {
      // change to error immediately
      mStatusDBManager.updateStatus(status.getMessageCheckedId(),
          StatusConstant.STATUS_ERROR);
    }
  }

  @Override
  public void resendMsg(String messageId) {
    MessageInDB msgInDB = mStatusDBManager.queryChatMessageId(messageId);
    if (msgInDB != null
        && msgInDB.getStatus() == StatusConstant.STATUS_ERROR) {
      mStatusDBManager.changeFileErrorToResend(msgInDB);
    }
  }

  @Override
  void uploadFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    mStatusDBManager.changeUploadToStatus(uploadId,
        StatusConstant.STATUS_ERROR);
  }

  @Override
  void uploadCancel(long uploadId) {
    mStatusDBManager.changeUploadToStatus(uploadId,
        StatusConstant.STATUS_ERROR);
  }

  @Override
  void updateErrorBySocketDie(String msgId) {
    LogUtils.d(StatusConstant.TAG, "socket die " + msgId);
    mStatusDBManager.updateStatus(msgId, StatusConstant.STATUS_ERROR);
  }

  public void updateMsgFile(Bundle bundle) {
    String msgId = bundle.getString(StatusConstant.ARG_MSG_ID);
    MessageInDB msgInDB = mStatusDBManager.query(msgId);
    if (msgInDB != null) {
      msgInDB.setChatClientId(bundle
          .getString(StatusConstant.ARG_CHAT_MSG_ID));
      msgInDB.setUploadId(bundle.getLong(StatusConstant.ARG_UPLOAD_ID));
      msgInDB.setFileType(bundle.getString(StatusConstant.ARG_FILE_TYPE));
      msgInDB.setFilePath(bundle.getString(StatusConstant.ARG_FILE_PATH));
      msgInDB.setAudioTime(bundle.getLong(StatusConstant.ARG_AUDIO_TIME));
      mStatusDBManager.update(msgInDB);

      // Notify chat fragment.
      LocalBroadcastManager broadcastManager = LocalBroadcastManager
          .getInstance(context);
      Intent intent = new Intent(ChatManager.ACTION_MESSAGE_UPDATE_FILE);
      intent.putExtra(ChatManager.EXTRA_BUNDLE, bundle);
      broadcastManager.sendBroadcast(intent);
    }
  }
}

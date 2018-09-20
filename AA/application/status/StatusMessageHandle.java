package com.application.status;

import android.content.Context;
import com.application.chat.MessageStatus;
import com.application.uploadmanager.IUploadResource;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class StatusMessageHandle extends StatusHandle {

  private static StatusMessageHandle instance;

  public StatusMessageHandle(Context context) {
    super(context);
  }

  public static StatusMessageHandle getInstance(Context context) {
    if (instance == null) {
      instance = new StatusMessageHandle(context);
    }
    return instance;
  }

  @Override
  public void createMsg(Message msg) {
    mStatusDBManager.createIfNoExist(msg, StatusConstant.STATUS_START);
  }

  @Override
  public void updateMsg(MessageStatus status) {
    if (status.isSentSuccess()) {
      if (status.isReaded()) {
        mStatusDBManager.updateStatus(status.getMessageCheckedId(),
            StatusConstant.STATUS_READ);
      } else {
        mStatusDBManager.updateStatus(status.getMessageCheckedId(),
            StatusConstant.STATUS_SUCCESS);
      }
    } else {
      if (status.getPoint() > 0) {
        mStatusDBManager
            .updateStatusError(status.getMessageCheckedId());
        StatusController.getInstance(context).autoResendMsg(
            status.getMessageCheckedId());
      } else {
        // not enough point, change to error, don't retry
        mStatusDBManager.updateStatus(status.getMessageCheckedId(),
            StatusConstant.STATUS_ERROR);
      }
    }
  }

  @Override
  public void resendMsg(String messageId) {
    StatusController.getInstance(context).resendMsg(messageId, false);
  }

  @Override
  void uploadFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    // upload failed, change to error
  }

  @Override
  void uploadCancel(long uploadId) {
  }

  @Override
  void updateErrorBySocketDie(String msgId) {
  }

}

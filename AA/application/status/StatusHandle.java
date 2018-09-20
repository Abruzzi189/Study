package com.application.status;

import android.content.Context;
import com.application.chat.MessageStatus;
import com.application.uploadmanager.IUploadResource;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public abstract class StatusHandle {

  protected StatusDBManager mStatusDBManager;
  protected Context context;

  public StatusHandle(Context context) {
    this.context = context;
    mStatusDBManager = StatusDBManager.getInstance(context);
  }

  abstract void createMsg(Message msg);

  abstract void updateMsg(MessageStatus status);

  abstract void updateErrorBySocketDie(String msgId);

  abstract void resendMsg(String messageId);

  abstract void uploadFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response);

  abstract void uploadCancel(long uploadId);
}

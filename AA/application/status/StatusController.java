package com.application.status;

import android.content.Context;
import android.os.Bundle;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.FileMessage;
import com.application.chat.MessageStatus;
import com.application.uploader.UploadResponse;
import com.application.uploadmanager.IUploadCustom;
import com.application.uploadmanager.IUploadResource;
import com.application.util.EmojiUtils;
import com.application.util.LogUtils;
import com.application.util.preferece.ChatUploadPrefers;
import com.application.util.preferece.UserPreferences;
import java.util.Iterator;
import java.util.List;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class StatusController implements IUploadCustom {

  private static StatusController instance;
  private final String TAG = "StatusController";
  private StatusDBManager mStatusDBManager;
  private TimeoutController mTimeoutController;
  private Context mContext;
  private boolean isOpened = false;

  private StatusController(Context context) {
    mContext = context;
    mStatusDBManager = StatusDBManager.getInstance(context);
    mTimeoutController = new TimeoutController(context);
  }

  public static StatusController getInstance(Context context) {
    if (instance == null) {
      instance = new StatusController(context);
    }
    return instance;
  }

  public void createMessage(Message message) {
    boolean isAllow = filter(message);
    if (!isAllow) {
      return;
    }
    getHandle(message.msgType).createMsg(message);
  }

  public void updateMsg(MessageStatus status) {
    String messageId = status.getMessageCheckedId();
    MessageInDB msgInDB = mStatusDBManager.query(messageId);
    if (msgInDB != null) {
      MessageType msgType = StatusConstant.getMsgType(msgInDB.getType());
      getHandle(msgType).updateMsg(status);
    }
  }

  public void updateErrorBySocketDie(Message message) {
    getHandle(message.msgType).updateErrorBySocketDie(message.id);
  }

  public void updateMsgFile(Bundle bundle) {
    StatusFileHandle.getInstance(mContext).updateMsgFile(bundle);
  }

  void autoResendMsg(String messageId) {
    // resendMsg(messageId, true);
  }

  /**
   * send message via click button "Resend"
   */
  public void resendMsg(ChatMessage chatMessage) {
    getHandle(chatMessage.isFileMessage()).resendMsg(
        chatMessage.getMessageId());
  }

  /**
   * Resend message error
   *
   * @param isAuto true if auto resend, false if resend via click button "Resend"
   */
  void resendMsg(String messageId, boolean isAuto) {
    MessageInDB messageInDb = mStatusDBManager.query(messageId);
    if (messageInDb == null) {
      return;
    }
    if (!isAuto || messageInDb.getStatus() == StatusConstant.STATUS_START
        || messageInDb.getStatus() == StatusConstant.STATUS_RETRY) {
      // implement sendMessage
      ChatManager.getInstance(mContext).sendGenericMessage(
          messageInDb.makeMessage());
      // update time send database
      long currentTime = System.currentTimeMillis();
      if (!isAuto) {
        messageInDb.setTimeStart(currentTime);
        messageInDb.setStatus(StatusConstant.STATUS_START);
      }
      messageInDb.setTimeSend(currentTime);
      mStatusDBManager.update(messageInDb);
      LogUtils.d(TAG, "Resend message " + messageInDb);
    }
  }

  public void deleteMsg(ChatMessage chatMessage) {
    MessageInDB msgInDB = mStatusDBManager.queryChatMessageId(chatMessage
        .getMessageId());
    if (msgInDB != null) {
      mStatusDBManager.detele(msgInDB.getId());
    }
  }

  public void deleteListConversation(String[] listFriendIds) {
    String userId = UserPreferences.getInstance().getUserId();
    mStatusDBManager.deleteListSend(userId, listFriendIds);
  }

  /**
   * Merge message chat from database with message from history
   */
  public void mergeWithHistory(List<ChatMessage> historyList,
      String userIdToSend, boolean isMergeredHistory) {
    LogUtils.i(TAG,
        "Merger with histories: " + String.valueOf(historyList.size()));

    // set status for message file
    Iterator<ChatMessage> ite = historyList.iterator();
    ChatMessage chatMessage;
    ChatUploadPrefers chatUploadManager = new ChatUploadPrefers();
    while (ite.hasNext()) {
      chatMessage = ite.next();
      String msgId = chatMessage.getMessageId();

      // Convert text to emoji character
      String content = chatMessage.getContent();
      chatMessage.setContent(EmojiUtils.convertTag(content));

      // Set status
      long uploadId = chatUploadManager.getUploadId(msgId);
      MessageInDB msgInDB = mStatusDBManager.queryUploadId(uploadId);
      if (msgInDB == null) {
        // Message history don't exist on table status
        if (chatMessage.isFileMessage()) {
          FileMessage fileMessage = chatMessage.getFileMessage();
          if (fileMessage != null && !fileMessage.isStartSent()) {
            // Message file upload success
            String readTime = chatMessage.getReadTime();
            if (readTime != null && readTime.length() > 0) {
              chatMessage
                  .setStatusSend(StatusConstant.STATUS_READ);
            } else {
              chatMessage
                  .setStatusSend(StatusConstant.STATUS_SUCCESS);
            }
          } else {
            // Message file upload failed
            LogUtils.d(TAG, "Upload msg failed: " + chatMessage);
            chatMessage.setStatusSend(StatusConstant.STATUS_UNKNOW);
          }
        }
      } else {
        // This's sure that my message
        int status = msgInDB.getStatus();
        if (status == StatusConstant.STATUS_ERROR
            || status == StatusConstant.STATUS_DELETE
            || status == StatusConstant.STATUS_SENDING_FILE) {
          // Remove file message to move this to the end of list.
          if (chatMessage.getUserId().equals(userIdToSend)) {
            // Message exist on server
            mStatusDBManager.updateStatus(msgId,
                StatusConstant.STATUS_SUCCESS);
          }
          ite.remove();
        } else {
          chatMessage.setStatusSend(status);
        }
      }
    }

    // Clear message sent success
    mStatusDBManager.clearMessageSuccess();

    // Check timeout upload file
    checkTimeoutMessage(userIdToSend);

    if (!isMergeredHistory) {
      // attach message start, retry, sending file to history
      attachMsgWithStatus(historyList, new int[]{
          StatusConstant.STATUS_START, StatusConstant.STATUS_RETRY,
          StatusConstant.STATUS_SENDING_FILE}, userIdToSend);

      // attach message error to history
      attachMsgWithStatus(historyList,
          new int[]{StatusConstant.STATUS_ERROR}, userIdToSend);
    }
  }

  private void checkTimeoutMessage(String userIdToSend) {
    int[] status = new int[]{StatusConstant.STATUS_START,
        StatusConstant.STATUS_RETRY, StatusConstant.STATUS_SENDING_FILE};
    String userId = UserPreferences.getInstance().getUserId();
    List<MessageInDB> listMsgInDB = mStatusDBManager
        .queryMsgFromUserIdWithStatus(userId, userIdToSend, status);
    ChatMessage messageChat;
    for (MessageInDB messageInDB : listMsgInDB) {
      messageChat = messageInDB.makeChatMessage();
      if (messageChat != null && messageChat.isTimeOutForFileMessage()) {
        mStatusDBManager.updateStatus(messageChat.getMessageId(),
            StatusConstant.STATUS_ERROR);
      }
    }
  }

  private void attachMsgWithStatus(List<ChatMessage> historyList,
      int[] status, String userIdToSend) {
    String userId = UserPreferences.getInstance().getUserId();
    List<MessageInDB> listMsgInDB = mStatusDBManager
        .queryMsgFromUserIdWithStatus(userId, userIdToSend, status);
    ChatMessage messageChat;
    for (MessageInDB messageInDB : listMsgInDB) {
      messageChat = messageInDB.makeChatMessage();
      if (messageChat != null) {
        if (!isSuccessFromServer(historyList, messageChat)) {
          historyList.add(0, messageChat);
        }
      }
    }
  }

  public void updateStatus(String currentMessageId){
    LogUtils.i(TAG,String.format("---------------------> updateStatus=[%1$s]",currentMessageId));
    mStatusDBManager.updateStatus2(currentMessageId, StatusConstant.STATUS_ERROR);
    mStatusDBManager.updateStatus(currentMessageId, StatusConstant.STATUS_ERROR);
  }

  /**
   * Check message chat exist from history (from server) and DB
   */
  private boolean isSuccessFromServer(List<ChatMessage> historyList,
      ChatMessage messageChat) {
    String currentMessageId = messageChat.getMessageId();
    for (ChatMessage itemMessage : historyList) {
      if (itemMessage.getMessageId().equals(currentMessageId)) {
        String readTime = messageChat.getReadTime();
        if (readTime != null && readTime.length() > 0) {
          mStatusDBManager.updateStatus(currentMessageId,
              StatusConstant.STATUS_READ);
        } else {
          mStatusDBManager.updateStatus(currentMessageId,
              StatusConstant.STATUS_SUCCESS);
        }
        return true;
      }
    }
    return false;
  }

  public boolean isSendMessageStartSuccess(String messageId){
    MessageInDB messageInDB = mStatusDBManager.query(messageId);
    if(messageInDB==null){
      return false;
    }
    if(messageInDB.getStatus() == StatusConstant.STATUS_READ
            || messageInDB.getStatus() == StatusConstant.STATUS_SUCCESS
            || messageInDB.getStatus() == StatusConstant.STATUS_SENDING_FILE) {
      return true;
    }
    return false;
  }

  public void open() {
    if (!isOpened) {
      isOpened = true;
      mStatusDBManager.open();
      mTimeoutController.start();
    }
  }

  public void close() {
    if (isOpened) {
      isOpened = false;
      mStatusDBManager.close();
      mTimeoutController.stop();
    }
  }

  public void requestTimeout() {
    mTimeoutController.requestTimeout();
  }

  public boolean isOpened() {
    return isOpened;
  }

  public void addStatusChangedListener(IStatusChatChanged listener) {
    mStatusDBManager.addDbChangeListener(listener);
  }

  public void removeStatusChangedListener(IStatusChatChanged listener) {
    mStatusDBManager.removeDbChangeListener(listener);
  }

  public boolean hasMsgErrorWith(String sendId) {
    String userId = UserPreferences.getInstance().getUserId();
    return mStatusDBManager.hasMsgError(userId, sendId);
  }

  private boolean filter(Message message) {
    // message typing
    if (message.msgType == MessageType.PRC) {
      return false;
    } else if (message.msgType == MessageType.CMD) {
      return false;
    } else if (message.msgType == MessageType.CALLREQ) {
      return false;
    } else if (message.msgType == MessageType.SVOICE
        || message.msgType == MessageType.EVOICE
        || message.msgType == MessageType.SVIDEO
        || message.msgType == MessageType.EVIDEO) {
      return false;
    }
    return true;
  }

  private StatusHandle getHandle(MessageType msgType) {
    if (msgType == MessageType.FILE) {
      return StatusFileHandle.getInstance(mContext);
    }
    return StatusMessageHandle.getInstance(mContext);
  }

  private StatusHandle getHandle(boolean isFile) {
    if (isFile) {
      return StatusFileHandle.getInstance(mContext);
    }
    return StatusMessageHandle.getInstance(mContext);
  }

  public void checkUploadingMsg(String userId) {
    String myId = UserPreferences.getInstance().getUserId();
    List<MessageInDB> listMsgInDB = mStatusDBManager
        .queryMsgFromUserIdWithStatus(myId, userId,
            new int[]{StatusConstant.STATUS_SENDING_FILE});
    for (MessageInDB messageInDB : listMsgInDB) {
      mStatusDBManager.updateStatus(messageInDB.getId(),
          StatusConstant.STATUS_ERROR);

    }
  }

  public void clearAllMsg() {
    mStatusDBManager.clearMyMessage();
  }

  public void clearAllMsgFrom(String userId) {
    mStatusDBManager.clearMessageFromId(userId);
  }

  /******************************** Handle upload file ********************************/
  @Override
  public void onAdded(long uploadId, IUploadResource uploadResource) {
  }

  @Override
  public void onPending(long uploadId, IUploadResource uploadResource) {

  }

  @Override
  public void onInprogress(long uploadId, IUploadResource uploadResource,
      int progress) {

  }

  @Override
  public void onSuccess(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    MessageInDB messageInDB = mStatusDBManager.queryUploadId(uploadId);
    if (messageInDB != null) {
      UploadResponse uploadResponse = (UploadResponse) response;
      messageInDB.setFileId(uploadResponse.getFileId());
      mStatusDBManager.update(messageInDB);
    }
  }

  @Override
  public void onFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    getHandle(MessageType.FILE).uploadFailed(uploadId, uploadResource,
        responseCode, response);
  }

  @Override
  public void onPaused(long uploadId, IUploadResource uploadResource,
      int responseCode) {

  }

  @Override
  public void onCancel(long uploadId) {
    getHandle(MessageType.FILE).uploadCancel(uploadId);
  }
}
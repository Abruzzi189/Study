package com.application.chat;

import android.text.TextUtils;
import com.application.status.StatusConstant;
import com.application.uploadmanager.UploadState;
import com.application.util.StringUtils;
import com.application.util.Utility;

public class ChatMessage {

  public static final String WINK = "WINK";
  public static final String PP = "PP";
  public static final String AUDIO = "AUDIO";
  public static final String VIDEO = "VIDEO";
  public static final String PHOTO = "PHOTO";
  public static final String FILE = "FILE";
  public static final String GIFT = "GIFT";
  public static final String LOCATION = "LCT";
  public static final String STICKER = "STK";
  public static final String STARTVIDEO = "SVIDEO";
  public static final String ENDVIDEO = "EVIDEO";
  public static final String STARTVOICE = "SVOICE";
  public static final String ENDVOICE = "EVOICE";
  public static final String CMD = "CMD";
  public static final String TYPING = "TYPING";
  public static final String CALLREQUEST = "CALLREQ";

  // Call request setting the same iOs
  public static final String CALLREQUEST_VOICE = "voip_voice";
  public static final String CALLREQUEST_VIDEO = "voip_video";

  public static final int VoIPActionNone = 0;
  public static final int VoIPActionVoiceStart = 1;
  public static final int VoIPActionVoiceEnd = 2;
  public static final int VoIPActionVoiceEndNoAnswer = 3;
  public static final int VoIPActionVoiceEndBusy = 4;
  public static final int VoIPActionVideoStart = 5;
  public static final int VoIPActionVideoEnd = 6;
  public static final int VoIPActionVideoEndNoAnswer = 7;
  public static final int VoIPActionVideoEndBusy = 8;
  public static final long TIMEOUT = 20 * 60 * 1000;
  protected String messageId = "";
  protected String callId;
  protected String userId;
  protected boolean isOwn;
  protected String content;
  protected String msgType;
  protected FileMessage fileMessage;
  protected String timeStamp;
  protected String readTime = "";
  protected boolean isHeader = false;
  protected boolean isAddHiddenToFavourite = false;
  protected boolean isFileDelete = false;

  public boolean isFileDelete() {
    return isFileDelete;
  }

  public void setFileDelete(boolean fileDelete) {
    isFileDelete = fileDelete;
  }

  public boolean isUnlock() {
    return isUnlock;
  }

  public void setUnlock(boolean unlock) {
    isUnlock = unlock;
  }

  protected boolean isUnlock = false;
  protected boolean isExpired = false;
  protected long timeInMilisecond = 0;
  protected boolean isEnoughPointToSend = true;

  // status of sent message
  protected int statusSend = StatusConstant.STATUS_SUCCESS;

  public ChatMessage() {
  }

  /**
   * Use when this is message received
   *
   * @param timeStamp must in GMT
   */
  public ChatMessage(String messageId, String userId, boolean isOwn,
      String content, String timeStamp, String msgType) {
    super();
    this.messageId = messageId;
    this.userId = userId;
    this.isOwn = isOwn;
    this.content = content;
    this.timeStamp = Utility.convertGMTtoLocale(timeStamp);
    timeInMilisecond = Utility.convertTimeToMilisecond(this.timeStamp);
    this.msgType = msgType;
    updateCallId(content, msgType);
  }

  /**
   * Use when this is message sent
   *
   * @param timeStamp must in GMT
   */
  public ChatMessage(String userId, boolean isOwn, String content,
      String timeStamp, String msgType) {
    super();
    this.messageId = "";
    this.userId = userId;
    this.isOwn = isOwn;
    this.content = content;
    this.timeStamp = Utility.convertGMTtoLocale(timeStamp);
    timeInMilisecond = Utility.convertTimeToMilisecond(this.timeStamp);
    this.msgType = msgType;
    updateCallId(content, msgType);
  }

  /**
   * Use when this is message received
   *
   * @param timeStamp must in GMT
   */
  public ChatMessage(String messageId, String userId, boolean isOwn,
      String content, String timeStamp, String msgType,
      FileMessage fileMessage) {
    this(messageId, userId, isOwn, content, timeStamp, msgType);
    this.fileMessage = fileMessage;
  }

  /**
   * User when parse message from history
   */
  public ChatMessage(String msgId, String userId, boolean isOwn,
      String timeStamp, String msgType, FileMessage fileMessage) {
    this(userId, isOwn, null, timeStamp, msgType);
    this.fileMessage = fileMessage;
    this.messageId = msgId;
  }

  /**
   * Use for video, video, photo when this is message sent
   */
  public ChatMessage(String userId, boolean isOwn, String timeStamp,
      String msgType, FileMessage fileMessage) {
    this(userId, isOwn, null, timeStamp, msgType);
    this.fileMessage = fileMessage;
  }

  public static ChatMessage makeGiftMessage(String userId, boolean owner,
      String giftId, String time) {
    ChatMessage chatMessage = new ChatMessage(userId, owner, giftId
        + "| | ", time, ChatMessage.GIFT);
    return chatMessage;
  }

  public void updateCallId(String content, String msgType) {
    if (ChatMessage.STARTVIDEO.equals(msgType) || ChatMessage.STARTVOICE.equals(msgType)
        || ChatMessage.ENDVIDEO.equals(msgType) || ChatMessage.ENDVOICE.equals(msgType)) {

      String[] tmp = content.split("\\|");
      if (tmp.length == 3) {
        setCallId(tmp[1]);
      } else {
        setCallId("");
      }
    }
  }
  public boolean hasReadMessage() {
    return (readTime != null && readTime.length() > 0);
  }

  public boolean isExpired() {
    return isExpired;
  }

  public void setExpired(boolean expired) {
    isExpired = expired;
  }

  public String getMsgType() {
    return msgType;
  }

  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  public long getTimeInMilisecond() {
    return timeInMilisecond;
  }

  public void setTimeInMilisecond(long timeInMilisecond) {
    if (!isHeader) {
      return;
    }
    this.timeInMilisecond = timeInMilisecond;
  }

  public boolean isHeader() {
    return isHeader;
  }

  public void setHeader(boolean isHeader) {
    this.isHeader = isHeader;
  }

  public boolean isAddHiddenFavourite() {
    return this.isAddHiddenToFavourite;
  }

  public void setAddHiddenFavourite(boolean value) {
    this.isAddHiddenToFavourite = value;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public boolean isOwn() {
    return isOwn;
  }

  public void setOwn(boolean isOwn) {
    this.isOwn = isOwn;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  /**
   * set timeStamp for message
   *
   * @param timeStamp must in Locale time
   */
  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
    timeInMilisecond = Utility.convertTimeToMilisecond(this.timeStamp);
  }

  public String getReadTime() {
    return readTime;
  }

  public void setReadTime(String readTime) {
    if (!TextUtils.isEmpty(readTime)) {
      this.readTime = Utility.convertReadTimeGMTtoLocale(readTime);
    } else {
      this.readTime = "";
    }
  }

  public String getMessageToSend() {
    return ChatUtils.encryptMessageToSend(content);
  }

  public void decryptMessageHistory() {
    this.content = ChatUtils.decryptMessageReceived(content);
  }

  public String getMessageReceived() {
    if (content == null) {
      return null;
    }
    return content;
  }

  public FileMessage getFileMessage() {
    return fileMessage;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public boolean isEnoughPointToSend() {
    return isEnoughPointToSend;
  }

  public void setEnoughPointToSend(boolean isEnoughPointToSend) {
    this.isEnoughPointToSend = isEnoughPointToSend;
  }

  public boolean isUploadSuccess() {
    if (fileMessage != null && fileMessage.getFileId() != null) {
      return true;
    }
    return fileMessage.uploadState == UploadState.SUCCESSFUL ? true : false;
  }

  public boolean isFileMessage() {
    return msgType == PHOTO || msgType == VIDEO || msgType == AUDIO;
  }

  public void setFileMessage(FileMessage fileMessage) {
    this.fileMessage = fileMessage;
  }

  public boolean isTimeOutForFileMessage() {
    long time = System.currentTimeMillis() - timeInMilisecond;
    return time >= TIMEOUT;
  }

  public boolean isTypingMessage() {
    return msgType == TYPING;
  }

  public int getStatusSend() {
    return this.statusSend;
  }

  public void setStatusSend(int statusSend) {
    this.statusSend = statusSend;
  }

  public String getCallId() {
    return callId;
  }

  public void setCallId(String callId) {
    this.callId = callId;
  }

  @Override
  public boolean equals(Object message) {
    if (this == message) return true;
    if (message == null || getClass() != message.getClass()) return false;

    ChatMessage uniqueVal = (ChatMessage) message;
    if (messageId.equals(uniqueVal.messageId)) return true;//messageId equals
    if (StringUtils.nullToEmpty(callId).equals(StringUtils.nullToEmpty(uniqueVal.callId)))
      return true;// callId equals callId
    if (StringUtils.nullToEmpty(content).startsWith(uniqueVal.messageId + "|"))
      return true;// content startsWith messageId
    return StringUtils.nullToEmpty(content).contains("|" + uniqueVal.messageId + "|");

  }

  @Override
  public int hashCode() {
    // if messageId not null and not empty then return has code if messageId
    if (!StringUtils.isEmptyOrNull(messageId)) return messageId.hashCode();
    // if (messageId is empty or null) and callId not null and not empty then return has code if callId
    if (!StringUtils.isEmptyOrNull(callId) && !(StringUtils.nullToEmpty(content)).startsWith(messageId + "|") && !(StringUtils.nullToEmpty(content)).contains("|" + messageId + "|"))
      return callId.hashCode();
    if (!StringUtils.isEmptyOrNull(callId) && StringUtils.nullToEmpty(callId).equals(messageId))
      return callId.hashCode();

    // if (callId is empty or null) and messageId not null and not empty then return has code if messageId
    //if (!isEmptyOrNull(messageId)) return messageId.hashCode();

    int result = messageId.hashCode();
    result = 31 * result + (callId != null ? callId.hashCode() : 0);
    System.out.println("------------------->hashCode().messageId=" + messageId + "\thashCode =" + result + "\t\tcallId =" + callId);
    return result;
  }

  @Override
  public String toString() {
    return "ChatMessage [messageId=" + messageId
        + ", callId=" + callId
        + ", userId=" + userId
        + ", isOwn=" + isOwn
        + ", content=" + content
        + ", msgType=" + msgType
        + ", fileMessage=" + fileMessage
        + ", timeStamp=" + timeStamp
        + ", readTime=" + readTime
        + ", isHeader=" + isHeader
        + ", isAddHiddenToFavourite=" + isAddHiddenToFavourite
        + ", timeInMilisecond=" + timeInMilisecond
        + ", isEnoughPointToSend=" + isEnoughPointToSend
        + ", statusSend=" + statusSend + "]";
  }
}
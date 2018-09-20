package com.application.status;

import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.chat.FileMessage;
import com.application.entity.TimeAudioHold;
import com.application.util.Utility;
import java.util.Date;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class MessageInDB {

  private String id;
  private long timeStart;
  private long timeSend;
  private String from;
  private String to;
  private int type;
  private String value;
  private int status;

  // for message file
  private long uploadId;
  private String chatClientId;
  private String fileId;
  private String fileType;
  private String filePath;
  private long audioTime;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(long timeStart) {
    this.timeStart = timeStart;
  }

  public long getTimeSend() {
    return timeSend;
  }

  public void setTimeSend(long timeSend) {
    this.timeSend = timeSend;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getChatClientId() {
    return chatClientId;
  }

  public void setChatClientId(String chatClientId) {
    this.chatClientId = chatClientId;
  }

  public long getUploadId() {
    return uploadId;
  }

  public void setUploadId(long uploadId) {
    this.uploadId = uploadId;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public long getAudioTime() {
    return audioTime;
  }

  public void setAudioTime(long audioTime) {
    this.audioTime = audioTime;
  }

  public Message makeMessage() {
    MessageType msgType = StatusConstant.getMsgType(this.type);
    Message message = new Message(new Date(timeStart), from, to, msgType,
        value);
    message.id = id;
    return message;
  }

  public ChatMessage makeChatMessage() {
    MessageType msgType = StatusConstant.getMsgType(type);
    Message message = makeMessage();
    ChatMessage chatMessage = null;
    if (msgType == MessageType.WINK) {
      chatMessage = ChatUtils.convertToWinkChatMessage(message);
    } else if (msgType == MessageType.PP) {
      chatMessage = ChatUtils.convertToPPChatMessage(message);
    } else if (msgType == MessageType.FILE) {
      chatMessage = ChatUtils.convertToFileChatMessage(new FileMessage(
          message));
    } else if (msgType == MessageType.GIFT) {
      chatMessage = ChatUtils.convertToGiftChatMessage(message);
    } else if (msgType == MessageType.LCT) {
      chatMessage = ChatUtils.convertToLocationChatMessage(message);
    } else if (msgType == MessageType.STK) {
      chatMessage = ChatUtils.convertToStickerChatMessage(message);
    } else if (msgType == MessageType.SVIDEO
        || msgType == MessageType.EVIDEO
        || msgType == MessageType.SVOICE
        || msgType == MessageType.EVOICE) {
      chatMessage = ChatUtils.convertToCallChatMessage(message, true);
    } else if (msgType == MessageType.CMD) {
      // TODO (hien tai khong can)
    } else if (msgType == MessageType.PRC) {
      if (value.equalsIgnoreCase(ChatManager.START_TYPING)
          || value.equalsIgnoreCase(ChatManager.STOP_TYPING)) {
        chatMessage = ChatUtils.convertToTypingChatMessage(message);
      } else {
        // TODO (hien tai khong can)
      }
    } else if (msgType == MessageType.CALLREQ) {
      chatMessage = ChatUtils.convertToCALLRQChatMessage(message);
    }
    if (chatMessage != null) {
      chatMessage.setTimeStamp(Utility.getTimeStamp(new Date(timeSend)));
      chatMessage.setStatusSend(status);
      chatMessage.setOwn(true);
      if (msgType == MessageType.FILE) {
        chatMessage.setMessageId(chatClientId);
        chatMessage.getFileMessage().setFilePath(filePath);
        chatMessage.getFileMessage().setFileType(fileType);
        String audioTimeStr = Utility.getTimeString(audioTime);
        chatMessage.getFileMessage().setTimeAudioHold(
            new TimeAudioHold(audioTimeStr, 0));
      }
      return chatMessage;
    }
    return null;
  }

  @Override
  public String toString() {
    return "MessageInDB [id=" + id + ", timeStart=" + timeStart
        + ", timeSend=" + timeSend + ", from=" + from + ", to=" + to
        + ", type=" + type + ", value=" + value + ", status=" + status
        + ", uploadId=" + uploadId + ", chatClientId=" + chatClientId
        + ", fileId=" + fileId + ", fileType=" + fileType
        + ", filePath=" + filePath + ", timeAutiod=" + audioTime + "]";
  }

}

package com.application.chat;

import com.application.downloadmanager.DownloadState;
import com.application.entity.TimeAudioHold;
import com.application.uploadmanager.UploadState;
import com.application.util.Utility;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class FileMessage extends MessageClient {

  /**
   *
   */
  private static final long serialVersionUID = -5747057046415119318L;
  public int uploadProgress;
  public int uploadState = UploadState.INITIAL;
  public int downloadProgress;
  public int downloadState = DownloadState.UNKNOW;
  public int errorReason;
  private String fileName;
  private String fileId;
  private String fileType;
  // private String messageId;
  private boolean isStart = true;
  private boolean isPlay;
  private String filePath;
  private TimeAudioHold timeAudioHold;

  public FileMessage(Message message) {
    super(message);
    parseData(message.id, message.value);
  }

  public FileMessage(String fileName, String fileType, String filePath) {
    super(null);
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileType = fileType;
    this.isPlay = false;
  }

  public FileMessage(String fileType, TimeAudioHold timeAudioHold,
      String audioFilePath) {
    super(null);
    this.fileType = fileType;
    this.isPlay = false;
    this.timeAudioHold = timeAudioHold;
    this.filePath = audioFilePath;
  }

  public FileMessage(String fileType, String fileId) {
    super(null);
    this.fileId = fileId;
    this.fileType = fileType;
    this.isPlay = false;
  }

  /**
   * User for History response
   *
   * @param messageId
   * @param fileName
   * @param fileType
   * @param fileId
   */
  // public FileMessage(String fileName, String fileType,
  // String fileId) {
  // super(null);
  // // this.messageId = messageId;
  // this.fileName = fileName;
  // this.fileId = fileId;
  // this.fileType = fileType;
  // }

  /**
   * Use when get history
   */
  public FileMessage(String content) {
    super(null);
    parseData("", content);
  }

  public String getMessageId() {
    try {
      String[] data = message.value.split("\\|");
      return data[0];
    } catch (Exception e) {
    }
    return "0";
  }

  private void parseData(String id, String value) {
    if (!value.contains("|")) {
      setStart(true);
      setFileType(value);
      // setMessageId(id);
    } else {
      setStart(false);
      String[] data = value.split("\\|");
      try {
        // setMessageId(data[0]);
        setFileType(data[1]);
        setFileId(data[2]);
        if (data[1].equals(ChatManager.AUDIO)) {
          double f = Double.parseDouble(data[3]);
          // must *1000 because convert second -> milisecond
          f = f * 1000;
          long l = Math.round(f);
          String time = Utility.getTimeString(l);
          setTimeAudioHold(new TimeAudioHold(time, 0));
        } else {
          if (data.length >= 4) {
            setFileName(data[3]);
          }
        }
      } catch (IndexOutOfBoundsException exception) {
        exception.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  // public void setMessageId(String messageId) {
  // this.messageId = messageId;
  // }
  //
  // public String getMessageId() {
  // return messageId;
  // }

  public boolean isStartSent() {
    return isStart;
  }

  public void setStart(boolean isStart) {
    this.isStart = isStart;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public boolean isPlay() {
    return isPlay;
  }

  public void setPlay(boolean isPlay) {
    this.isPlay = isPlay;
  }

  public TimeAudioHold getTimeAudioHold() {
    if (timeAudioHold == null) {
      timeAudioHold = new TimeAudioHold();
    }
    return timeAudioHold;
  }

  public void setTimeAudioHold(TimeAudioHold timeAudioHold) {
    this.timeAudioHold = timeAudioHold;
  }

}

package com.application.chat;

import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class MessageStatus extends MessageClient {

  public static final String READED_DATA = "rd";
  public static final String READEDALL_DATA = "rd_all";
  private static final long serialVersionUID = -7713787569434956398L;
  public static String SUCCESS_DATA = "st";
  public static String NOT_ENOUGH_POINT_DATA = "us";
  public static String SEND_FAILED_MESS_2= "failed";
  private boolean sentSuccess = false;
  private boolean notEnoughPoint = false;
  private boolean isSendFaildedMess2 = false;
  private boolean readed = false;
  private boolean readALL = false;
  private String messageCheckedId;
  private int point;
  public MessageStatus(Message message) {
    super(message);
    parseData(message.value);
  }

  private void parseData(String value) {
    try {
      String[] data = value.split("\\|");
      messageCheckedId = data[0];
      String status = data[1];
      if (status.equals(SUCCESS_DATA)) {
        sentSuccess = true;
        readed = false;
        readALL = false;
      } else if (status.equals(READED_DATA)) {
        sentSuccess = true;
        readed = true;
        readALL = false;
      } else if (status.equals(READEDALL_DATA)) {
        sentSuccess = true;
        readed = true;
        readALL = true;
      } else {
        sentSuccess = false;
        readed = false;
        readALL = false;
        if (status.equals(NOT_ENOUGH_POINT_DATA)) {
          notEnoughPoint = true;
        } else {
          notEnoughPoint = false;
        }
      }
      if (data.length >= 3) {
        point = Integer.valueOf(data[2]);
      }
      if(data.length>=4 && data[3].equals(SEND_FAILED_MESS_2)){
        isSendFaildedMess2 = true;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public boolean isSentSuccess() {
    return sentSuccess;
  }

  public boolean isNotEnoughPoint() {
    return notEnoughPoint;
  }

  public String getMessageCheckedId() {
    return messageCheckedId;
  }

  public int getPoint() {
    return point;
  }

  public boolean isReaded() {
    return readed;
  }

  public void setReaded(boolean readed) {
    this.readed = readed;
  }

  public boolean isReadALL() {
    return readALL;
  }

  public void setReadALL(boolean readALL) {
    this.readALL = readALL;
  }

  public boolean isSendFaildedMess2() {
    return isSendFaildedMess2;
  }

  public void setSendFaildedMess2(boolean sendFaildedMess2) {
    isSendFaildedMess2 = sendFaildedMess2;
  }

  @Override
  public String toString() {
    return "MessageStatus{" +
        "sentSuccess=" + sentSuccess +
        ", notEnoughPoint=" + notEnoughPoint +
        ", readed=" + readed +
        ", readALL=" + readALL +
        ", messageCheckedId='" + messageCheckedId + '\'' +
        ", point=" + point +
        '}';
  }

}

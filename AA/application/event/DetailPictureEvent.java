package com.application.event;

public class DetailPictureEvent {

  /**
   * The id of buzz that is used to back
   */
  private String buzzId;
  /**
   * Buzz detail option ( Comment, ... )
   */
  private int option;
  /**
   * The user id
   */
  private String userId;


  public DetailPictureEvent(String buzzId, int option) {
    this.buzzId = buzzId;
    this.option = option;
  }

  public DetailPictureEvent(String buzzId, String userId, int option) {
    this.buzzId = buzzId;
    this.option = option;
    this.userId = userId;
  }


  public String getBuzzId() {
    return buzzId;
  }

  public void setBuzzId(String buzzId) {
    this.buzzId = buzzId;
  }

  public int getOption() {
    return option;
  }

  public void setOption(int option) {
    this.option = option;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }


}


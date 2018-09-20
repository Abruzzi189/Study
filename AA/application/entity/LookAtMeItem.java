package com.application.entity;

public class LookAtMeItem {

  private boolean isOnline;
  private int point;
  private String userName;
  private int gender;
  private String userId;
  private int timeRemain;
  private String avatarId;

  public LookAtMeItem(boolean isOnline, int point, String userName,
      int gender, String userId, int timeRemain, String avatarId) {
    super();
    this.isOnline = isOnline;
    this.point = point;
    this.userName = userName;
    this.gender = gender;
    this.userId = userId;
    this.timeRemain = timeRemain;
    this.avatarId = avatarId;
  }

  public String getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getTimeRemain() {
    return timeRemain;
  }

  public void setTimeRemain(int timeRemain) {
    this.timeRemain = timeRemain;
  }

}

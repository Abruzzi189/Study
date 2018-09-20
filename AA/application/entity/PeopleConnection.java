package com.application.entity;

public class PeopleConnection {

  private String userId;
  private String userName;
  private int gender;
  private int age;
  private String avaId;
  private boolean isOnline;
  private int isFriend;
  private double longitute;
  private double latitue;
  private double distance;
  private long checkTime;
  private String status;
  private int unreadMsg;
  private boolean isVoiceWaiting;
  private boolean isVideoWaiting;
  private String lastLogin;
  private String checkOutTime;

  public PeopleConnection(String userId, String userName, int gender,
      int age, String avaId, boolean isOnline, int isFriend,
      double longitute, double latitue, double distance, long chktime,
      String status, int unreadMsg, boolean isVoiceWaiting,
      boolean isVideoWaiting, String lastLogin, String checkOutTime) {
    super();
    this.userId = userId;
    this.userName = userName;
    this.gender = gender;
    this.age = age;
    this.avaId = avaId;
    this.isOnline = isOnline;
    this.isFriend = isFriend;
    this.longitute = longitute;
    this.latitue = latitue;
    this.distance = distance;
    this.checkTime = chktime;
    this.status = status;
    this.unreadMsg = unreadMsg;
    this.isVoiceWaiting = isVoiceWaiting;
    this.isVideoWaiting = isVideoWaiting;
    this.lastLogin = lastLogin;
    this.checkOutTime = checkOutTime;
  }

  public long getCheckTime() {
    return checkTime;
  }

  public void setCheckTime(long checkTime) {
    this.checkTime = checkTime;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
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

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getAvaId() {
    return avaId;
  }

  public void setAvaId(String avaId) {
    this.avaId = avaId;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public int getIsFriend() {
    return isFriend;
  }

  public void setIsFriend(int isFriend) {
    this.isFriend = isFriend;
  }

  public double getLongitute() {
    return longitute;
  }

  public void setLongitute(long longitute) {
    this.longitute = longitute;
  }

  public double getLatitue() {
    return latitue;
  }

  public void setLatitue(long latitue) {
    this.latitue = latitue;
  }

  public double getDistance() {
    return this.distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getUnreadNum() {
    return this.unreadMsg;
  }

  public void setUnreadNum(int unreadMsg) {
    this.unreadMsg = unreadMsg;
  }

  public boolean isVoiceWaiting() {
    return isVoiceWaiting;
  }

  public boolean isVideoWaiting() {
    return isVideoWaiting;
  }

  public String getLastLogin() {
    return lastLogin;
  }

  public String getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
  }
}
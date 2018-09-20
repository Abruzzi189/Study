package com.application.ui.chat;

public class CallLog {

  public static final int STATUS_ANSWER = 0;
  public static final int STATUS_BUSY = 1;
  public static final int STATUS_NO_ANSWER = 2;

  public static final int CALL_TYPE_VOICE = 1;
  public static final int CALL_TYPE_VIDEO = 2;

  private String userId;
  private String userName;
  private int gender;
  private int callType;
  private boolean isOnline;
  private String avatarId;
  private int duration;
  private int response;
  private String startTime;
  private double longitude;
  private double latitude;
  private double distance;
  private String lastLogin;
  private boolean isVoiceWaiting;
  private boolean isVideoWaiting;

  public CallLog(String userId, String userName, int gender, int callType,
      boolean isOnline, String avatarId, int duration, int response,
      String startTime, double longitude, double latitude,
      double distance, String lastLogin, boolean isVoiceWaiting,
      boolean isVideoWaiting) {
    this.userId = userId;
    this.userName = userName;
    this.gender = gender;
    this.callType = callType;
    this.isOnline = isOnline;
    this.avatarId = avatarId;
    this.duration = duration;
    this.response = response;
    this.startTime = startTime;
    this.longitude = longitude;
    this.latitude = latitude;
    this.distance = distance;
    this.lastLogin = lastLogin;
    this.isVoiceWaiting = isVoiceWaiting;
    this.isVideoWaiting = isVideoWaiting;
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

  public int getCallType() {
    return callType;
  }

  public void setCallType(int callType) {
    this.callType = callType;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public String getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getResponse() {
    return response;
  }

  public void setResponse(int response) {
    this.response = response;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public String getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(String lastLogin) {
    this.lastLogin = lastLogin;
  }

  public boolean isVoiceWaiiting() {
    return isVoiceWaiting;
  }

  public void setVoiceWaiting(boolean isVoiceWaiting) {
    this.isVoiceWaiting = isVoiceWaiting;
  }

  public boolean isVideoWaiting() {
    return isVideoWaiting;
  }

  public void setVideoWaiting(boolean isVideoWaiting) {
    this.isVideoWaiting = isVideoWaiting;
  }
}
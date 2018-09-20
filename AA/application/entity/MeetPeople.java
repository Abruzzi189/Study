package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MeetPeople implements Serializable {

  private static final long serialVersionUID = 1L;
  @SerializedName("is_fav")
  public int isFav;
  @SerializedName("user_id")
  private String userId;
  @SerializedName("user_name")
  private String user_name;
  @SerializedName("email")
  private String email;
  @SerializedName("is_online")
  private boolean is_online;
  @SerializedName("long")
  private double longitute;
  @SerializedName("lat")
  private double lat;
  @SerializedName("dist")
  private double distance;
  @SerializedName("ava_id")
  private String ava_id;
  @SerializedName("age")
  private int age;
  @SerializedName("gender")
  private int gender;
  @SerializedName("status")
  private String status;
  @SerializedName("unread_num")
  private int unreadNum;
  @SerializedName("region")
  private int region;
  private String lastLogin;
  private boolean isVoiceCallWaiting;
  private boolean isVideoCallWaiting;
  private String about;
  public MeetPeople() {

  }

  public MeetPeople(String user_name, String email, boolean is_online,
      double longitute, double lat, double distance, String ava_id,
      int age, int gender) {
    super();
    this.user_name = user_name;
    this.email = email;
    this.is_online = is_online;
    this.longitute = longitute;
    this.distance = distance;
    this.lat = lat;
    this.ava_id = ava_id;
    this.age = age;
    this.gender = gender;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean getIs_online() {
    return is_online;
  }

  public void setIs_online(boolean is_online) {
    this.is_online = is_online;
  }

  public double getLongitute() {
    return longitute;
  }

  public void setLongitute(double longitute) {
    this.longitute = longitute;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getDistance() {
    return this.distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public String getAva_id() {
    return ava_id;
  }

  public void setAva_id(String ava_id) {
    this.ava_id = ava_id;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getUnreadNum() {
    return unreadNum;
  }

  public void setUnreadNum(int unreadNum) {
    this.unreadNum = unreadNum;
  }

  public String getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(String lastLogin) {
    this.lastLogin = lastLogin;
  }

  public boolean isVoiceCallWaiting() {
    return isVoiceCallWaiting;
  }

  public void setVoiceCallWaiting(boolean isVoiceCallWaiting) {
    this.isVoiceCallWaiting = isVoiceCallWaiting;
  }

  public boolean isVideoCallWaiting() {
    return isVideoCallWaiting;
  }

  public void setVideoCallWaiting(boolean isVideoCallWaiting) {
    this.isVideoCallWaiting = isVideoCallWaiting;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MeetPeople)) {
      return false;
    }

    MeetPeople that = (MeetPeople) o;

    if (getAge() != that.getAge()) {
      return false;
    }
    if (getGender() != that.getGender()) {
      return false;
    }
    if (!getUserId().equals(that.getUserId())) {
      return false;
    }
    if (!getUser_name().equals(that.getUser_name())) {
      return false;
    }
    if (!getEmail().equals(that.getEmail())) {
      return false;
    }
    return getAva_id().equals(that.getAva_id());
  }

  @Override
  public int hashCode() {
    int result = getUserId().hashCode();
    result = 31 * result + getUser_name().hashCode();
    result = 31 * result + getEmail().hashCode();
    result = 31 * result + getAva_id().hashCode();
    result = 31 * result + getAge();
    result = 31 * result + getGender();
    return result;
  }
}

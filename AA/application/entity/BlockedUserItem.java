package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BlockedUserItem implements Serializable {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = 4642388549787411135L;

  @SerializedName("user_id")
  private String user_id;

  @SerializedName("user_name")
  private String user_name;

  @SerializedName("ava_id")
  private String ava_id;

  @SerializedName("age")
  private int age;

  @SerializedName("gender")
  private int gender;

  @SerializedName("is_blocked")
  private boolean is_blocked;

  public String getUserId() {
    return this.user_id;
  }

  public void setUserId(String user_id) {
    this.user_id = user_id;
  }

  public String getUserName() {
    return this.user_name;
  }

  public void setUserName(String username) {
    this.user_name = username;
  }

  public String getAvatarId() {
    return this.ava_id;
  }

  public void setAvatarId(String ava_id) {
    this.ava_id = ava_id;
  }

  public int getAge() {
    return this.age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getGender() {
    return this.gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public boolean getBlockedStatus() {
    return this.is_blocked;
  }

  public void setBlockedStatus(boolean blockStatus) {
    this.is_blocked = blockStatus;
  }
}

package com.application.model;

import com.application.constant.UserSetting;

public class ChatUser {

  private String id = "";
  private String name = "";
  private String avatar = "";
  private int gender = UserSetting.GENDER_MALE;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Id\t: ");
    builder.append(id);
    builder.append("\nName\t: ");
    builder.append(name);
    builder.append("\nAvatar\t: ");
    builder.append(avatar);
    builder.append("\nGender\t: ");
    builder.append(gender);
    return builder.toString();
  }
}
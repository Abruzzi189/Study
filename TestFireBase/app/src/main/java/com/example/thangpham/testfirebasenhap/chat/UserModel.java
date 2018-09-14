package com.example.thangpham.testfirebasenhap.chat;

import java.io.Serializable;

public class UserModel implements Serializable {
  public String userId;
  public String name;
  public String avatarLink;

  public UserModel(String userId, String name, String avatarLink) {
    this.userId = userId;
    this.name = name;
    this.avatarLink = avatarLink;
  }
  public UserModel(){

  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarLink() {
    return avatarLink;
  }

  public void setAvatarLink(String avatarLink) {
    this.avatarLink = avatarLink;
  }
}

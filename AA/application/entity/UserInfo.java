package com.application.entity;

import com.application.connection.response.UserInfoResponse;
import java.util.ArrayList;

public class UserInfo extends ProfileItem {

  private UserInfoResponse user;
  private ArrayList<String> publicImages;
  private boolean isFirstProfile;
  private boolean isLastProfile;

  public UserInfo() {
  }

  public UserInfo(UserInfoResponse user, ArrayList<String> publicImages) {
    super();
    this.user = user;
    this.publicImages = publicImages;
  }

  // CuongNV : Khoi tao doi tuong UserInfo dau vao la 1 doi tuong UserInfoResponse
  public UserInfo(UserInfoResponse user) {
    super();
    this.user = user;
  }

  // Ket thuc khoi tao
  public UserInfoResponse getUser() {
    return user;
  }

  public void setUser(UserInfoResponse user) {
    this.user = user;
  }

  public ArrayList<String> getPublicImages() {
    return publicImages;
  }

  public void setPublicImages(ArrayList<String> publicImages) {
    this.publicImages = publicImages;
  }

  public boolean isFirstProfile() {
    return isFirstProfile;
  }

  public void setFirstProfile(boolean isFirstProfile) {
    this.isFirstProfile = isFirstProfile;
  }

  public boolean isLastProfile() {
    return isLastProfile;
  }

  public void setLastProfile(boolean isLastProfile) {
    this.isLastProfile = isLastProfile;
  }

}

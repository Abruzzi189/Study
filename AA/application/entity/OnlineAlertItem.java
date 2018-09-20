package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OnlineAlertItem implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_name")
  private String userName;

  @SerializedName("age")
  private int age;

  @SerializedName("gender")
  private int gender;

  @SerializedName("ethn")
  private int ethn;

  @SerializedName("inters_in")
  private int intersIn;

  @SerializedName("ava_id")
  private String avaId;

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

  public int getEthn() {
    return ethn;
  }

  public void setEthn(int ethn) {
    this.ethn = ethn;
  }

  public int getIntersIn() {
    return intersIn;
  }

  public void setIntersIn(int intersIn) {
    this.intersIn = intersIn;
  }

  public String getAvaId() {
    return avaId;
  }

  public void setAvaId(String avaId) {
    this.avaId = avaId;
  }

}

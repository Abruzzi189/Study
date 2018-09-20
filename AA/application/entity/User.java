package com.application.entity;

import com.application.constant.UserSetting;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class User implements Serializable {

  private static final long serialVersionUID = 2706582719334413978L;
  private String name;
  private String email;
  private String password;
  private Date birthday;
  private int gender = UserSetting.GENDER_MALE;
  private int interested = UserSetting.INTERESTED_IN_WOMEN;
  private String avatar;
  private String anotherSystemId;
  private boolean isReceiveEmailNotification = false;

  public User() {
  }

  public User(String name, String email, String password, Date birthday,
      int gender, int interested) {
    super();
    this.name = name;
    this.email = email;
    this.password = password;
    this.birthday = birthday;
    this.gender = gender;
    this.interested = interested;
  }

  public User(String name, Date birthday, int gender, int interested,
      String avatar, String anotherSystemId) {
    super();
    this.name = name;
    this.birthday = birthday;
    this.gender = gender;
    this.interested = interested;
    this.avatar = avatar;
    this.anotherSystemId = anotherSystemId;
  }

  public User(String name, String email, Date birthday, int gender,
      int interested, String avatar, String anotherSystemId) {
    super();
    this.name = name;
    this.email = email;
    this.birthday = birthday;
    this.gender = gender;
    this.interested = interested;
    this.avatar = avatar;
    this.anotherSystemId = anotherSystemId;
  }

  public boolean isReceiveEmailNotification() {
    return isReceiveEmailNotification;
  }

  public void setReceiveEmailNotification(boolean isReceiveEmailNotification) {
    this.isReceiveEmailNotification = isReceiveEmailNotification;
  }

  public String getAnotherSystemId() {
    return anotherSystemId;
  }

  public void setAnotherSystemId(String anotherSystemId) {
    this.anotherSystemId = anotherSystemId;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public int getInterested() {
    return interested;
  }

  public void setInterested(int interested) {
    this.interested = interested;
  }

  public int getAge() {
    Calendar a = Calendar.getInstance(Locale.getDefault());
    a.setTime(birthday);
    Calendar b = Calendar.getInstance(Locale.getDefault());
    int age = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
        || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
        .get(Calendar.DATE) > b.get(Calendar.DATE))) {
      age--;
    }
    return age;
  }

  @Override
  public String toString() {
    String s = "email=" + this.email + ", name=" + this.name + ", gender="
        + this.gender + ", interested=" + this.interested
        + ", birthday=" + this.birthday;
    return s;
  }

}

package com.application.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.UserSetting;

public class UserProfileInfo implements Parcelable {

  public static final Parcelable.Creator<UserProfileInfo> CREATOR = new Creator<UserProfileInfo>() {
    @Override
    public UserProfileInfo[] newArray(int size) {
      return new UserProfileInfo[size];
    }

    @Override
    public UserProfileInfo createFromParcel(Parcel source) {
      return new UserProfileInfo(source);
    }
  };
  private String name = "";
  private String birthday = "";
  private int job = -1;
  private int region = -1;
  private int[] threeSizes;
  private int cupSize = -1;
  private int cuteType = -1;
  private int joinHours = -1;
  private String typeMan = "";
  private String fetish = "";
  private String message = "";
  private String hobby = "";
  private int gender = UserSetting.GENDER_MALE;

  public UserProfileInfo() {

  }

  public UserProfileInfo(UserInfoResponse userInfo) {
    this.name = userInfo.getUserName();
    this.birthday = userInfo.getBirthday();
    this.region = userInfo.getRegion();
    this.threeSizes = userInfo.getThreeSizes();
    this.cupSize = userInfo.getCupSize();
    this.cuteType = userInfo.getCuteType();
    this.joinHours = userInfo.getJoinHours();
    this.typeMan = userInfo.getTypeMan();
    this.fetish = userInfo.getFetish();
    this.message = userInfo.getAbout();
    this.hobby = userInfo.getHobby();
    this.gender = userInfo.getGender();
    this.job = userInfo.getJob();
  }

  public UserProfileInfo(String name, String birthday, int job, int region,
      int[] threeSizes, int cupSize, int cuteType, int indecent,
      int joinHours, String typeMan, String fetish, String message,
      String hobby) {

    this.name = name;
    this.birthday = birthday;
    this.job = job;
    this.region = region;
    this.threeSizes = threeSizes;
    this.cupSize = cupSize;
    this.cuteType = cuteType;
    this.joinHours = joinHours;
    this.typeMan = typeMan;
    this.fetish = fetish;
    this.message = message;
    this.hobby = hobby;
  }

  public UserProfileInfo(Parcel parcel) {
    name = parcel.readString();
    birthday = parcel.readString();
    job = parcel.readInt();
    region = parcel.readInt();
    threeSizes = parcel.createIntArray();
    cupSize = parcel.readInt();
    cuteType = parcel.readInt();
    joinHours = parcel.readInt();
    typeMan = parcel.readString();
    fetish = parcel.readString();
    message = parcel.readString();
    hobby = parcel.readString();
    gender = parcel.readInt();
  }

  public void updateUserInfo(UserInfoResponse userInfo) {
    this.name = userInfo.getUserName();
    this.birthday = userInfo.getBirthday();
    this.region = userInfo.getRegion();
    this.threeSizes = userInfo.getThreeSizes();
    this.cupSize = userInfo.getCupSize();
    this.cuteType = userInfo.getCuteType();
    this.joinHours = userInfo.getJoinHours();
    this.typeMan = userInfo.getTypeMan();
    this.fetish = userInfo.getFetish();
    this.message = userInfo.getAbout();
    this.hobby = userInfo.getHobby();
    this.gender = userInfo.getGender();
    this.job = userInfo.getJob();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(birthday);
    dest.writeInt(job);
    dest.writeInt(region);
    dest.writeIntArray(threeSizes);
    dest.writeInt(cupSize);
    dest.writeInt(cuteType);
    dest.writeInt(joinHours);
    dest.writeString(typeMan);
    dest.writeString(fetish);
    dest.writeString(message);
    dest.writeString(hobby);
    dest.writeInt(gender);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public int getJob() {
    return job;
  }

  public void setJob(int job) {
    this.job = job;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }

  public int[] getThreeSizes() {
    return threeSizes;
  }

  public void setThreeSizes(int[] threeSizes) {
    this.threeSizes = threeSizes;
  }

  public int getCupSize() {
    return cupSize;
  }

  public void setCupSize(int cupSize) {
    this.cupSize = cupSize;
  }

  public int getCuteType() {
    return cuteType;
  }

  public void setCuteType(int cuteType) {
    this.cuteType = cuteType;
  }

  public int getJoinHours() {
    return joinHours;
  }

  public void setJoinHours(int joinHours) {
    this.joinHours = joinHours;
  }

  public String getTypeMan() {
    return typeMan;
  }

  public void setTypeMan(String typeMan) {
    this.typeMan = typeMan;
  }

  public String getFetish() {
    return fetish;
  }

  public void setFetish(String fetish) {
    this.fetish = fetish;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getHobby() {
    return hobby;
  }

  public void setHobby(String hobby) {
    this.hobby = hobby;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

}

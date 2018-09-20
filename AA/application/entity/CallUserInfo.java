package com.application.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CallUserInfo implements Parcelable {

  public static final Parcelable.Creator<CallUserInfo> CREATOR = new Creator<CallUserInfo>() {

    @Override
    public CallUserInfo[] newArray(int size) {
      return new CallUserInfo[size];
    }

    @Override
    public CallUserInfo createFromParcel(Parcel source) {
      return new CallUserInfo(source);
    }
  };
  private String userName;
  private String userId;
  private String avatarId;
  private int gender;

  public CallUserInfo() {
  }

  public CallUserInfo(String userName, String userId, String avatarId,
      int gender) {
    super();
    this.userName = userName;
    this.userId = userId;
    this.avatarId = avatarId;
    this.gender = gender;
  }

  public CallUserInfo(Parcel parcel) {
    userName = parcel.readString();
    userId = parcel.readString();
    avatarId = parcel.readString();
    gender = parcel.readInt();
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(userName);
    dest.writeString(userId);
    dest.writeString(avatarId);
    dest.writeInt(gender);
  }
}

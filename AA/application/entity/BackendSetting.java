package com.application.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HungHN on 5/30/2016.
 */
public class BackendSetting implements Parcelable {

  public static final Creator<BackendSetting> CREATOR = new Creator<BackendSetting>() {
    @Override
    public BackendSetting createFromParcel(Parcel source) {
      return new BackendSetting(source);
    }

    @Override
    public BackendSetting[] newArray(int size) {
      return new BackendSetting[size];
    }
  };
  @SerializedName("day_bns_pnt")
  private int dailyBonusPoints;
  @SerializedName("save_img_pnt")
  private int saveImagePoints;
  @SerializedName("onl_alt_pnt")
  private int onlineAlertPoints;
  @SerializedName("view_img_pnt")
  private int viewImagePoint;
  @SerializedName("bckstg_time")
  private int backstageTime;
  @SerializedName("bckstg_pri")
  private int backstagePrice;
  @SerializedName("comment_buzz_pnt")
  private int commentBuzzPoint;
  @SerializedName("turn_off_show_news_android")
  private boolean isShowPopupNews;
  @SerializedName("get_free_point_android")
  private boolean isEnableGetFreePoint;
  @SerializedName("turn_off_user_info_android")
  private boolean isTurnOffUserInfo;
  @SerializedName("switch_browser_android_version")
  private String switchBrowserVersion;

  public BackendSetting() {
  }

  protected BackendSetting(Parcel in) {
    this.dailyBonusPoints = in.readInt();
    this.saveImagePoints = in.readInt();
    this.onlineAlertPoints = in.readInt();
    this.viewImagePoint = in.readInt();
    this.backstageTime = in.readInt();
    this.backstagePrice = in.readInt();
    this.commentBuzzPoint = in.readInt();
    this.isShowPopupNews = in.readByte() != 0;
    this.isEnableGetFreePoint = in.readByte() != 0;
    this.isTurnOffUserInfo = in.readByte() != 0;
    this.switchBrowserVersion = in.readString();
  }

  public int getBackstagePrice() {
    return backstagePrice;
  }

  public void setBackstagePrice(int backstagePrice) {
    this.backstagePrice = backstagePrice;
  }

  public int getBackstageTime() {
    return backstageTime;
  }

  public void setBackstageTime(int backstageTime) {
    this.backstageTime = backstageTime;
  }

  public int getViewImagePoint() {
    return viewImagePoint;
  }

  public void setViewImagePoint(int viewImagePoint) {
    this.viewImagePoint = viewImagePoint;
  }

  public int getDailyBonusPoints() {
    return dailyBonusPoints;
  }

  public void setDailyBonusPoints(int dailyBonusPoints) {
    this.dailyBonusPoints = dailyBonusPoints;
  }

  public boolean isEnableGetFreePoint() {
    return isEnableGetFreePoint;
  }

  public void setEnableGetFreePoint(boolean enableGetFreePoint) {
    isEnableGetFreePoint = enableGetFreePoint;
  }

  public boolean isShowPopupNews() {
    return isShowPopupNews;
  }

  public void setShowPopupNews(boolean showPopupNews) {
    isShowPopupNews = showPopupNews;
  }

  public boolean isTurnOffUserInfo() {
    return isTurnOffUserInfo;
  }

  public void setTurnOffUserInfo(boolean turnOffUserInfo) {
    isTurnOffUserInfo = turnOffUserInfo;
  }

  public int getOnlineAlertPoints() {
    return onlineAlertPoints;
  }

  public void setOnlineAlertPoints(int onlineAlertPoints) {
    this.onlineAlertPoints = onlineAlertPoints;
  }

  public int getSaveImagePoints() {
    return saveImagePoints;
  }

  public void setSaveImagePoints(int saveImagePoints) {
    this.saveImagePoints = saveImagePoints;
  }

  public String getSwitchBrowserVersion() {
    return switchBrowserVersion;
  }

  public void setSwitchBrowserVersion(String switchBrowserVersion) {
    this.switchBrowserVersion = switchBrowserVersion;
  }

  public int getCommentBuzzPoint() {
    return commentBuzzPoint;
  }

  public void setCommentBuzzPoint(int commentBuzzPoint) {
    this.commentBuzzPoint = commentBuzzPoint;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.dailyBonusPoints);
    dest.writeInt(this.saveImagePoints);
    dest.writeInt(this.onlineAlertPoints);
    dest.writeInt(this.viewImagePoint);
    dest.writeInt(this.backstageTime);
    dest.writeInt(this.backstagePrice);
    dest.writeInt(this.commentBuzzPoint);
    dest.writeByte(this.isShowPopupNews ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isEnableGetFreePoint ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isTurnOffUserInfo ? (byte) 1 : (byte) 0);
    dest.writeString(this.switchBrowserVersion);
  }
}

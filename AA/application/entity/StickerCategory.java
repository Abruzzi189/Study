package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StickerCategory {

  @SerializedName("cat_d")
  private String id;
  @SerializedName("cat_name")
  private String name;
  @SerializedName("cat_des")
  private String description;
  @SerializedName("cat_pri")
  private int price;
  @SerializedName("download_time")
  private String downloadTime;
  @SerializedName("is_down")
  private int hasDown;
  @SerializedName("live_time")
  private int liveTime;
  @SerializedName("cat_type")
  private int type;
  @SerializedName("new_flag")
  private int newFlag;
  @SerializedName("google_id")
  private String googleId;
  @SerializedName("lst_stk_code")
  private List<String> stickerCodeList;

  public StickerCategory(String id, String name, String description,
      int price, String downloadTime, int hasDown, int liveTime,
      int type, int newFlag, String googleId, List<String> stickerCodeList) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.downloadTime = downloadTime;
    this.hasDown = hasDown;
    this.liveTime = liveTime;
    this.type = type;
    this.newFlag = newFlag;
    this.googleId = googleId;
    this.stickerCodeList = stickerCodeList;
  }

  public StickerCategory() {
  }

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public Date getDownloadTime() {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(
          "yyyyMMddHHmmss", Locale.getDefault());
      return dateFormat.parse(downloadTime);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new Date();
  }

  public void setDownloadTime(String downloadTime) {
    this.downloadTime = downloadTime;
  }

  public boolean hasDown() {
    return hasDown == 1 ? true : false;
  }

  public void setHasDown(int hasDown) {
    this.hasDown = hasDown;
  }

  public int getLiveTime() {
    return liveTime;
  }

  public void setLiveTime(int liveTime) {
    this.liveTime = liveTime;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean hasNewFlag() {
    return newFlag == 1 ? true : false;
  }

  public void setNewFlag(int newFlag) {
    this.newFlag = newFlag;
  }

  public String getGoogleId() {
    return googleId;
  }

  public void setGoogleId(String googleId) {
    this.googleId = googleId;
  }

  public List<String> getStickerCodeList() {
    return stickerCodeList;
  }

  public void setStickerCodeList(List<String> stickerCodeList) {
    this.stickerCodeList = stickerCodeList;
  }

  public interface Type {

    int DEFAULT = 0;
    int FREE = 1;
    int CASH_FOREVER = 2;
    int CASH_EXPIRE = 3;
  }
}

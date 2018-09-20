package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NotificationItem implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 2275788889412740541L;
  @SerializedName("time")
  private String time;
  @SerializedName("noti_user_id")
  private String userId;
  @SerializedName("noti_type")
  private int type;
  @SerializedName("noti_user_name")
  private String userName;
  @SerializedName("noti_buzz_id")
  private String buzzId;
  @SerializedName("noti_buzz_owner_name")
  private String buzzOwnerName;
  @SerializedName("dist")
  private double dist;
  @SerializedName("point")
  private int point;
  @SerializedName("rank_look")
  private int rankLook;
  @SerializedName("noti_image_id")
  private String imageId;

  @SerializedName("content")
  private String content;

  @SerializedName("url")
  private String url;

  @SerializedName("ownerid")
  private String ownerId;

  public NotificationItem() {
    super();
  }

  public NotificationItem(String time, String userId, int type,
      String userName, String buzzID, String buzzOwnerName, double dist,
      int point, int rankLook, String imageId) {
    super();
    this.time = time;
    this.userId = userId;
    this.type = type;
    this.userName = userName;
    this.dist = dist;
    this.buzzOwnerName = buzzOwnerName;
    this.buzzId = buzzID;
    this.point = point;
    this.imageId = imageId;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String user_name) {
    this.userName = user_name;
  }

  public double getDist() {
    return dist;
  }

  public void setDist(double dist) {
    this.dist = dist;
  }

  public String getBuzzOwnerName() {
    return buzzOwnerName;
  }

  public void setBuzzOwnerName(String buzzOwnerName) {
    this.buzzOwnerName = buzzOwnerName;
  }

  public String getBuzzId() {
    return buzzId;
  }

  public void setBuzzId(String buzzId) {
    this.buzzId = buzzId;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public int getRankLook() {
    return rankLook;
  }

  public void setRankLook(int rankLook) {
    this.rankLook = rankLook;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }
}

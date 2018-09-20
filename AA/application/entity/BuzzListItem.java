/**
 *
 */
package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author TuanPQ
 */
public class BuzzListItem implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -5957857516096760474L;

  /**
   * User Id
   */
  @SerializedName("user_id")
  private String user_id;

  /**
   * User Name
   */
  @SerializedName("user_name")
  private String user_name;

  /**
   * Avatar Id
   */
  @SerializedName("ava_id")
  private String ava_id;

  /**
   * Gender
   */
  @SerializedName("gender")
  private int gender;

  /**
   * Longitude
   */
  @SerializedName("long")
  private double lon;

  /**
   * Latitude
   */
  @SerializedName("lat")
  private double lat;

  /**
   * Distance
   */
  @SerializedName("dist")
  private double distance;

  /**
   * Is like
   */
  @SerializedName("is_like")
  private int is_like;

  /**
   * Buzz Time
   */
  @SerializedName("buzz_time")
  private String buzz_time;

  /**
   * View Number
   */
  @SerializedName("seen_num")
  private int seen_num;

  /**
   * Like Number
   */
  @SerializedName("like_num")
  private int like_num;

  /**
   * Comment Number
   */
  @SerializedName("cmt_num")
  private int cmt_num;

  /**
   * Buzz Id
   */
  @SerializedName("buzz_id")
  private String buzz_id;

  /**
   * Buzz Value
   */
  @SerializedName("buzz_val")
  private String buzz_val;

  /**
   * Buzz Type
   */
  @SerializedName("buzz_type")
  private int buzz_type;

  /**
   * Select To Delete flag
   */
  @SerializedName("select_to_delete")
  private boolean select_to_delete;

  @SerializedName("is_app")
  private int isApproved;

  @SerializedName("is_online")
  private boolean isOnline;

  @SerializedName("region")
  private int region;

  private int comment_buzz_point;

  /**
   * Comment List
   */
  @SerializedName("comment")
  private ArrayList<BuzzListCommentItem> comment;

  public String getUserId() {
    return this.user_id;
  }

  public void setUserId(String user_id) {
    this.user_id = user_id;
  }

  public String getUserName() {
    return this.user_name;
  }

  public void setUserName(String user_name) {
    this.user_name = user_name;
  }

  public String getAvatarId() {
    return this.ava_id;
  }

  public void setAvatarId(String ava_id) {
    this.ava_id = ava_id;
  }

  public int getGender() {
    return this.gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public double getLogitude() {
    return this.lon;
  }

  public void setLogitude(double lon) {
    this.lon = lon;
  }

  public double getLatitude() {
    return this.lat;
  }

  public void setLatitude(double lat) {
    this.lat = lat;
  }

  public double getDistance() {
    return this.distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public int getIsLike() {
    return this.is_like;
  }

  public void setIsLike(int is_like) {
    this.is_like = is_like;
  }

  public String getBuzzTime() {
    return this.buzz_time;
  }

  public void setBuzzTime(String buzz_time) {
    this.buzz_time = buzz_time;
  }

  public int getSeenNumber() {
    return this.seen_num;
  }

  public void setSeenNumber(int seen_num) {
    this.seen_num = seen_num;
  }

  public int getLikeNumber() {
    return this.like_num;
  }

  public void setLikeNumber(int like_num) {
    this.like_num = like_num;
  }

  public int getCommentNumber() {
    return this.cmt_num;
  }

  public void setCommentNumber(int cmt_num) {
    this.cmt_num = cmt_num;
  }

  public String getBuzzId() {
    return this.buzz_id;
  }

  public void setBuzzId(String buzz_id) {
    this.buzz_id = buzz_id;
  }

  public String getBuzzValue() {
    return this.buzz_val;
  }

  public void setBuzzValue(String buzz_val) {
    this.buzz_val = buzz_val;
  }

  public int getBuzzType() {
    return this.buzz_type;
  }

  public void setBuzzType(int buzz_type) {
    this.buzz_type = buzz_type;
  }

  public boolean getSelectToDelete() {
    return this.select_to_delete;
  }

  public void setSelectToDelete(boolean select_to_delete) {
    this.select_to_delete = select_to_delete;
  }

  public ArrayList<BuzzListCommentItem> getCommentList() {
    return this.comment;
  }

  public void setCommentList(ArrayList<BuzzListCommentItem> comment) {
    if (this.comment != null) {
      this.comment.clear();
      this.comment = null;
    }
    this.comment = comment;
  }

  public int getIsApproved() {
    return this.isApproved;
  }

  public void setIsApproved(int isApproved) {
    this.isApproved = isApproved;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }

  public int getCommentPoint() {
    return comment_buzz_point;
  }

  public void setCommentPoint(int point) {
    this.comment_buzz_point = point;
  }
}
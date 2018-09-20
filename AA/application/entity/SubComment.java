package com.application.entity;

import com.google.gson.annotations.SerializedName;

public class SubComment {

  /**
   * Sub comment Id
   */
  @SerializedName("sub_comment_id")
  public String sub_comment_id;

  /**
   * Flag used to check whether or not this comment can be deleted
   */
  @SerializedName("can_delete")
  public boolean can_delete;

  /**
   * Id of user who sent sub comment
   */
  @SerializedName("user_id")
  public String user_id;

  /**
   * Content of sub comment
   */
  @SerializedName("value")
  public String value;

  /**
   * The time when user sent sub comment
   */
  @SerializedName("time")
  public String time;

  /**
   * Avatar Id
   */
  @SerializedName("ava_id")
  public String ava_id;

  /**
   * Gender: 0 female, 1 male
   */
  @SerializedName("gender")
  public int gender;

  @SerializedName("is_online")
  public boolean is_online;

  /**
   * User Name
   */
  @SerializedName("user_name")
  public String user_name;

  @SerializedName("is_app")
  public int isApprove;
}

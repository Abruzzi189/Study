/**
 *
 */
package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * @author TuanPQ
 */
public class BuzzListCommentItem {

  /**
   * Number of sub comment
   */
  @SerializedName("sub_comment_number")
  public int sub_comment_number;

  @SerializedName("sub_comment_point")
  public int sub_comment_point;

  /**
   * Flag used to check whether or not this comment can be deleted
   */
  @SerializedName("can_delete")
  public int can_delete;

  /**
   * Comment Id
   */
  @SerializedName("cmt_id")
  public String cmt_id;

  /**
   * User Id
   */
  @SerializedName("user_id")
  public String user_id;

  /**
   * User Name
   */
  @SerializedName("user_name")
  public String user_name;

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

  /**
   * Comment value
   */
  @SerializedName("cmt_val")
  public String cmt_val;

  /**
   * Comment time: yyyyMMddHHmmss
   */
  @SerializedName("cmt_time")
  public String cmt_time;

  @SerializedName("is_online")
  public boolean is_online;

  @SerializedName("is_app")
  public int isApproved;

  @SerializedName("sub_comment")
  public ArrayList<SubComment> sub_comments;

  public boolean isNoMoreSubComment;
}

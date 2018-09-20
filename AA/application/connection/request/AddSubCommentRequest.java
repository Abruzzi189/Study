package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddSubCommentRequest extends RequestParams {

  private static final long serialVersionUID = -5236697757258503838L;

  @SerializedName("cmt_id")
  private String commentId;
  @SerializedName("value")
  private String value;
  @SerializedName("buzz_id")
  private String buzzId;

  public AddSubCommentRequest(String token, String commentId, String value, String buzzId) {
    super();
    this.api = "add_sub_comment";
    this.token = token;
    this.commentId = commentId;
    this.value = value;
    this.buzzId = buzzId;
  }
}

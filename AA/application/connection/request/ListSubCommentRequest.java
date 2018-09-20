package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListSubCommentRequest extends RequestParams {

  private static final long serialVersionUID = 2230047531362916659L;

  @SerializedName("buzz_id")
  private String buzzId;
  @SerializedName("cmt_id")
  private String commentId;
  @SerializedName("skip")
  private int skip;
  @SerializedName("take")
  private int take;

  public ListSubCommentRequest(String token, String buzzId, String commentId, int skip, int take) {
    super();
    this.api = "list_sub_comment";
    this.token = token;
    this.commentId = commentId;
    this.buzzId = buzzId;
    this.skip = skip;
    this.take = take;
  }

  public String getCommentId() {
    return commentId;
  }

  public String getBuzzId() {
    return buzzId;
  }

  public int getSkip() {
    return skip;
  }

  public int getTake() {
    return take;
  }

}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeleteSubCommentRequest extends RequestParams {

  private static final long serialVersionUID = -1052498163934790664L;

  @SerializedName("buzz_id")
  private String buzzId;
  @SerializedName("cmt_id")
  private String commentId;
  @SerializedName("sub_comment_id")
  private String subCommentId;

  public DeleteSubCommentRequest(String token, String buzzId, String cmtId, String subCommentId) {
    super();
    this.api = "delete_sub_comment";
    this.token = token;
    this.buzzId = buzzId;
    this.commentId = cmtId;
    this.subCommentId = subCommentId;
  }
}

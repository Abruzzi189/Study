package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeleteCommentRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = -1700859535156213977L;

  /**
   * Buzz Id
   */
  @SerializedName("buzz_id")
  private String buzz_id;

  /**
   * Comment Id
   */
  @SerializedName("cmt_id")
  private String comment_id;

  public DeleteCommentRequest(String token, String buzz_id, String comment_id) {
    super();
    this.api = "del_cmt";
    this.token = token;
    this.buzz_id = buzz_id;
    this.comment_id = comment_id;
  }

}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddCommentRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = 916773044915966309L;

  @SerializedName("buzz_id")
  private String buzz_id;

  @SerializedName("cmt_val")
  private String cmt_val;

  public AddCommentRequest(String token, String buzz_id, String cmt_val) {
    super();
    this.api = "add_cmt_version_2";
    this.token = token;
    this.buzz_id = buzz_id;
    this.cmt_val = cmt_val;
  }
}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeactivateAccountRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -5711298868807760456L;

  @SerializedName("cmt")
  private String comment;

  public DeactivateAccountRequest(String token, String comment) {
    super();
    this.api = "de_act";
    this.token = token;
    this.comment = comment;
  }
}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class LikeBuzzRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = -5227585586906864759L;

  @SerializedName("buzz_id")
  private String buzz_id;

  @SerializedName("like_type")
  private int like_type;

  public LikeBuzzRequest(String token, String buzz_id, int like_type) {
    super();
    this.api = "like_buzz";
    this.token = token;
    this.buzz_id = buzz_id;
    this.like_type = like_type;
  }

}

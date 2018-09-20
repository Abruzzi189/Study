package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class BuzzDetailRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = -2032935348375742288L;

  /**
   * Buzz Id
   */
  @SerializedName("buzz_id")
  private String buzz_id;

  /**
   * Taken comments
   */
  @SerializedName("cmt_take")
  private int cmt_take;

  public BuzzDetailRequest(String token, String buzz_id, int cmt_take) {
    super();
    this.api = "get_buzz_detail";
    this.token = token;
    this.buzz_id = buzz_id;
    this.cmt_take = cmt_take;
  }

}

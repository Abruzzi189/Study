package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeleteBuzzRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = 6021902774226853879L;

  /**
   * Buzz Id
   */
  @SerializedName("buzz_id")
  private String buzz_id;

  public DeleteBuzzRequest(String token, String buzz_id) {
    super();
    this.api = "del_buzz";
    this.token = token;
    this.buzz_id = buzz_id;
  }
}

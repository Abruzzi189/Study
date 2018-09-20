package com.application.connection.request;

import com.google.gson.annotations.SerializedName;


public class ShakeToChatRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -1295176073920032366L;

  @SerializedName("message")
  private String message;

  public ShakeToChatRequest(String token, String message) {
    super();
    this.api = "shake_chat";
    this.token = token;
    this.message = message;
  }
}

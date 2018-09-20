package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class CheckCallRequest extends RequestParams {

  private static final long serialVersionUID = 8744030392515324102L;
  @SerializedName("rcv_id")
  private String rcv_id;
  @SerializedName("type")
  private int type;

  public CheckCallRequest(String token, String userId, int type) {
    super();
    this.api = "check_call";
    this.token = token;
    this.rcv_id = userId;
    this.type = type;
  }
}
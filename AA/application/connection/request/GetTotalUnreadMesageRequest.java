package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class GetTotalUnreadMesageRequest extends RequestParams {

  @SerializedName("user_id")
  private String user_id;

  public GetTotalUnreadMesageRequest(String token, String user_id) {
    this.api = "total_unread";
    this.token = token;
    this.user_id = user_id;
  }
}
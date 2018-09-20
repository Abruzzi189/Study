package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class GetOnlineAlertRequest extends RequestParams {


  @SerializedName("req_user_id")
  private String userId;

  public GetOnlineAlertRequest(String token) {
    super();
    this.api = "get_onl_alt";
    this.token = token;
  }

  public GetOnlineAlertRequest(String token, String userId) {
    super();
    this.api = "get_onl_alt";
    this.token = token;
    this.userId = userId;
  }
}
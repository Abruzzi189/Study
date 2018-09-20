package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class UserInfoRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -5937202813582044228L;
  @SerializedName("req_user_id")
  private String userid;

  public UserInfoRequest(String token) {
    super();
    this.api = "get_user_inf";
    this.token = token;
  }

  public UserInfoRequest(String token, String userId) {
    super();
    this.api = "get_user_inf";
    this.token = token;
    this.userid = userId;
  }
}

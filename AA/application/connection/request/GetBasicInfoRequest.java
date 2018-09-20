package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class GetBasicInfoRequest extends RequestParams {

  private static final long serialVersionUID = -5543796839003391448L;

  @SerializedName("req_user_id")
  private String userId;

  public GetBasicInfoRequest(String token) {
    super();
    this.api = "get_basic_inf";
    this.token = token;
  }

  public GetBasicInfoRequest(String token, String userId) {
    super();
    this.api = "get_basic_inf";
    this.token = token;
    this.userId = userId;
  }
}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class RateBackstageRequest extends RequestParams {

  private static final long serialVersionUID = 8744030392515324102L;

  @SerializedName("req_user_id")
  private String req_user_id;

  @SerializedName("rate_point")
  private int rate_point;

  public RateBackstageRequest(String token, String userId, int rate) {
    super();
    this.api = "rate_bckstg";
    this.token = token;
    this.req_user_id = userId;
    this.rate_point = rate;
  }
}
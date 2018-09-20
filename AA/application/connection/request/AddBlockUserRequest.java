package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddBlockUserRequest extends RequestParams {

  private static final long serialVersionUID = -5710543060284730108L;

  @SerializedName("req_user_id")
  private String req_user_id;

  public AddBlockUserRequest(String token, String req_user_id) {
    super();
    this.api = "add_blk";
    this.token = token;
    this.req_user_id = req_user_id;
  }
}
package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class RemoveBlockUserRequest extends RequestParams {

  private static final long serialVersionUID = 7951376381332508415L;


  @SerializedName("blk_user_id")
  private String blk_user_id;

  public RemoveBlockUserRequest(String token, String blk_user_id) {
    super();
    this.api = "rmv_blk";
    this.token = token;
    this.blk_user_id = blk_user_id;
  }
}

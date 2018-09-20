package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddFavoriteRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1466194191366455809L;
  @SerializedName("req_user_id")
  private String userId;

  public AddFavoriteRequest(String token, String userId) {
    super();
    this.api = "add_fav";
    this.token = token;
    this.userId = userId;
  }

}

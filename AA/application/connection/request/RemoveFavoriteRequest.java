package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class RemoveFavoriteRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -3001347033544025434L;
  @SerializedName("fav_id")
  private String userId;

  public RemoveFavoriteRequest(String token, String userId) {
    super();
    this.api = "rmv_fav";
    this.token = token;
    this.userId = userId;
  }

}

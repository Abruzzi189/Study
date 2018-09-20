package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class RemoveFriendRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -8662209626688443770L;
  @SerializedName("frd_id")
  private String userId;

  public RemoveFriendRequest(String token, String userId) {
    super();
    this.api = "rmv_frd";
    this.token = token;
    this.userId = userId;
  }

}

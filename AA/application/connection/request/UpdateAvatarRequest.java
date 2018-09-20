package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class UpdateAvatarRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1614811053787262003L;
  @SerializedName("img_id")
  private String imgId;

  public UpdateAvatarRequest(String token, String imgId) {
    super();
    this.api = "upd_ava";
    this.token = token;
    this.imgId = imgId;
  }

}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class RemoveBackstageImageRequest extends RequestParams {

  private static final long serialVersionUID = 6021902774226853879L;

  @SerializedName("img_id")
  private String img_id;

  public RemoveBackstageImageRequest(String token, String img_id) {
    super();
    this.api = "rmv_bckstg";
    this.token = token;
    this.img_id = img_id;
  }
}

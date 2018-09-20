package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AllCategoriesGiftRequest extends RequestParams {

  private static final long serialVersionUID = 6927979190520316351L;

  @SerializedName("language")
  private String language;

  public AllCategoriesGiftRequest(String token, String language) {
    super();
    this.api = "get_all_gift_cat";
    this.token = token;
    this.language = language;
  }
}

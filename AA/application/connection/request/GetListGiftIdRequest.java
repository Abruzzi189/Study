package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class GetListGiftIdRequest extends RequestParams {

  public static final int TYPE_A_CATEGORY = 0;
  public static final int TYPE_ALL = 1;
  private static final long serialVersionUID = -2666693294852150838L;
  @SerializedName("cat_id")
  private String categoryId;

  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  @SerializedName("language")
  private String language;

  public GetListGiftIdRequest(String token, String categoryId, String language) {
    super();
    this.api = "get_gift_cat";
    this.token = token;
    this.categoryId = categoryId;
    this.language = language;
  }

  public GetListGiftIdRequest(String token, int skip, int take, String language) {
    super();
    this.api = "get_all_gift";
    this.token = token;
    this.skip = skip;
    this.take = take;
    this.language = language;
  }

}

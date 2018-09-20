package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ThoNh on 12/12/2017.
 */

public class ListNewPostRequest extends RequestParams {

  public static final int TAKE = 20;

  @SerializedName("skip")
  public int skip;

  @SerializedName("take")
  public int take = TAKE;

  public ListNewPostRequest(String token, int skip) {
    super();
    this.api = "get_list_user_top";
    this.token = token;
    this.skip = skip;
  }
}

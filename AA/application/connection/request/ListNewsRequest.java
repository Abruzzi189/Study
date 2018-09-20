package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HungHN on 4/7/2016.
 */
public class ListNewsRequest extends RequestParams {

  @SerializedName("userid")
  private String userid;
  @SerializedName("gender")
  private int gender;
  @SerializedName("device_type")
  // Integer : 0 : IOS | 1: Android
  private int device_type;
  //@SerializedName("skip")
  //private int skip;
  //@SerializedName("take")
  //private int take;

  public ListNewsRequest(String token, int gender, String userid) {
    super();
    this.api = "list_news_client";
    this.token = token;
    this.gender = gender;
    this.device_type = 1;
    this.userid = userid;
    //this.skip = skip;
    //this.take = take;
  }
}

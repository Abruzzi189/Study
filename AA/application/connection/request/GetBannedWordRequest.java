package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class GetBannedWordRequest extends RequestParams {

  @SerializedName("version")
  private int version;

  public GetBannedWordRequest(String token) {
    this.api = "get_banned_word";
    this.token = token;
  }

  public GetBannedWordRequest(String token, int version) {
    this(token);
    this.version = version;
  }
}
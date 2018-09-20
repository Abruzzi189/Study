package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class GetCallLogRequest extends RequestParams {

  public static final int TYPE_SEND = 0;
  public static final int TYPE_RECEIVER = 1;
  private static final long serialVersionUID = 1L;
  @SerializedName("type")
  private int type;

  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  public GetCallLogRequest(String token, int type, int skip, int take) {
    this.api = "get_call_log";
    this.token = token;
    this.type = type;
    this.skip = skip;
    this.take = take;
  }
}
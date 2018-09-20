package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ConversationRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -5465377980387287519L;
  @SerializedName("time_stamp")
  private String timeStamp;
  @SerializedName("take")
  private int take;

  public ConversationRequest(String token, int take) {
    super();
    this.api = "list_conversation";
    this.token = token;
    this.take = take;
    this.timeStamp = "";
  }

  public ConversationRequest(String token, String timeStamp, int take) {
    super();
    this.api = "list_conversation";
    this.token = token;
    this.timeStamp = timeStamp;
    this.take = take;
  }
}

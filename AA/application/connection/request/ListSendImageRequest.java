package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListSendImageRequest extends RequestParams {

  private static final long serialVersionUID = -3548592354092023195L;
  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("skip")
  private int skip;
  @SerializedName("take")
  private int take;

  public ListSendImageRequest(String token, String id, int skip, int take) {
    super();
    this.api = "lst_sent_img_with_user";
    this.token = token;
    this.req_user_id = id;
    this.skip = skip;
    this.take = take;
  }
}
package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListCommentRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -7885666262521871550L;

  @SerializedName("buzz_id")
  private String buzz_id;

  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  public ListCommentRequest(String token, String buzz_id, int skip, int take) {
    super();
    this.api = "lst_cmt";
    this.token = token;
    this.buzz_id = buzz_id;
    this.skip = skip;
    this.take = take;
  }
}

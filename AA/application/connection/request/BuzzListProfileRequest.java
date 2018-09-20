package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class BuzzListProfileRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 8099342744696469221L;

  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("buzz_kind")
  private int buzz_kind;
  @SerializedName("skip")
  private int skip;
  @SerializedName("take")
  private int take;

  public BuzzListProfileRequest(String token, String req_user_id, int skip,
      int take) {
    super();
    this.api = "get_buzz";
    this.token = token;
    this.req_user_id = req_user_id;
    this.skip = skip;
    this.take = take;
    this.buzz_kind = 0;
  }

}

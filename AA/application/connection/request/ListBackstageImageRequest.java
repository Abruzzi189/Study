package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListBackstageImageRequest extends RequestParams {

  private static final long serialVersionUID = -3548592354092023195L;
  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("skip")
  private int skip;
  @SerializedName("take")
  private int take;

  public ListBackstageImageRequest(String token, int skip, int take) {
    super();
    this.api = "lst_bckstg";
    this.token = token;
    this.req_user_id = null;
    this.skip = skip;
    this.take = take;
  }

  public ListBackstageImageRequest(String token, String req_user_id, int skip, int take) {
    super();
    this.api = "lst_bckstg";
    this.token = token;
    this.req_user_id = req_user_id;
    this.skip = skip;
    this.take = take;
  }
}

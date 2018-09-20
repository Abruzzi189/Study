package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class UpdatePriceRequest extends RequestParams {

  private static final long serialVersionUID = 8744030392515324102L;

  @SerializedName("bckstg_pri")
  private int bckstg_pri;

  public UpdatePriceRequest(String token, int unlockPrice) {
    super();
    this.api = "upd_bckstg_pri";
    this.token = token;
    this.bckstg_pri = unlockPrice;
  }
}
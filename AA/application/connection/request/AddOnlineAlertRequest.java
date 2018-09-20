package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddOnlineAlertRequest extends RequestParams {

  private static final long serialVersionUID = 8264027815619259187L;

  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("is_alt")
  private int isAlert;
  @SerializedName("alt_fre")
  private int num;

  public AddOnlineAlertRequest(String token, String req_user_id, int isAlert,
      int num) {
    super();
    this.api = "add_onl_alt";
    this.token = token;
    this.req_user_id = req_user_id;
    this.isAlert = isAlert;
    this.num = num;
  }
}
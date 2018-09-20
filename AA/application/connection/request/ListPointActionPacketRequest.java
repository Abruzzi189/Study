package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListPointActionPacketRequest extends RequestParams {

  private int pro_type = 1;
  @SerializedName("action_type")
  private int actionType;

  public ListPointActionPacketRequest(String token, int actionType) {
    this.api = "lst_action_point_pck";
    this.token = token;
    this.actionType = actionType;
  }
}

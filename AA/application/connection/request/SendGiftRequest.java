package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SendGiftRequest extends RequestParams {

  private static final long serialVersionUID = -2905678680442204168L;

  @SerializedName("gift_id")
  private String giftId;
  @SerializedName("rcv_id")
  private String receiveId;

  public SendGiftRequest(String token, String giftId, String receiveId) {
    super();
    this.api = "send_gift";
    this.token = token;
    this.giftId = giftId;
    this.receiveId = receiveId;
  }
}
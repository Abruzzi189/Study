package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class LogPurchaseRequest extends RequestParams {

  private static final long serialVersionUID = -6503160397851000661L;

  @SerializedName("pck_id")
  private String packetId;

  @SerializedName("device_type")
  private int deviceType = 1; // Android

  @SerializedName("flagCancel")
  private int flagCancel;

  @SerializedName("internalTransactionId")
  private String transactionId;

  /**
   * to get transaction id
   */
  public LogPurchaseRequest(String token, String packetId) {
    this.api = "log_before_purchase";
    this.token = token;
    this.packetId = packetId;
  }

  /**
   * cancel transaction request
   */
  public LogPurchaseRequest(String token, String packetId, String transactionId) {
    this.api = "log_before_purchase";
    this.token = token;
    this.packetId = packetId;
    this.flagCancel = 1;
    this.transactionId = transactionId;
  }
}
package com.application.connection.request;

import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;

public class ConfirmPurchaseRequest extends RequestParams {

  private static final long serialVersionUID = 1849977520335174462L;
  String pck_id;
  String signature;
  String pur_data;
  String transaction_id;
  @SerializedName("is_sandbox")
  int sandbox;
  @SerializedName("application")
  private int application;

  public ConfirmPurchaseRequest(String token, String pck_id,
      String purchaseData, String signature, String transactionId, int sandboxPurchase) {
    this.api = "cnf_purchase_and";
    this.token = token;
    this.pck_id = pck_id;
    this.signature = signature;
    this.pur_data = purchaseData;
    this.transaction_id = transactionId;
    this.application = Constants.APPLICATION_TYPE;
    this.sandbox = sandboxPurchase;
  }
}
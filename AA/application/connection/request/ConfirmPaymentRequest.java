package com.application.connection.request;

public class ConfirmPaymentRequest extends RequestParams {

  /**
   */
  private static final long serialVersionUID = 6204921141033581795L;
  String pck_id;
  String signature;
  String sig_data;

  public ConfirmPaymentRequest(String token, String pck_id, String signature,
      String sig_data) {
    this.api = "cnf_purchase_and";
    this.token = token;
    this.pck_id = pck_id;
    this.signature = signature;
    this.sig_data = sig_data;
  }
}

package com.application.payment.extra;

import com.android.billingclient.api.Purchase;

public interface BillingCallback {

  /**
   * error when purchase
   */
  void onPurchaseError(int errorCode);

  /**
   * cancel by user, so call API "log_before_purchase" with parameters <b>flagCancel=1</b> and
   * <b>internalTransactionId=transactionId</b>
   */
  void onCancelPurchaseByUser();

  /**
   * flow play billing success => confirm purchase to add point, API "cnf_purchase_and"
   */
  void onConfirmPurchase(Purchase purchase, String transactionId);
}

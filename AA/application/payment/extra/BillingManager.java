package com.application.payment.extra;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import java.util.List;

/**
 * <p>1. setup billing client</p> <p>2. get transactionId via API "log_before_purchase"</p> <p>3.
 * {@link #purchase(String, String, String)}</p> <p>4. {@link #onPurchasesUpdated(int, List)}</p>
 * <p>4.1 -> ITEM_ALREADY_OWNED -> {@link BillingClient#queryPurchaseHistoryAsync(String,
 * PurchaseHistoryResponseListener)} -> {@link BillingClient#consumeAsync(String,
 * ConsumeResponseListener)} -> retry purchase </p> <p>4.2 -> USER_CANCELED -> {@link
 * BillingCallback#onCancelPurchaseByUser()} </p> <p>4.3 -> OK -> {@link
 * BillingCallback#onConfirmPurchase(Purchase, String)} </p> <p>dependency:
 * "com.android.billingclient:billing"</p> <p>permission: "com.android.vending.BILLING"</p> <p> how
 * to get pckId?
 * <pre>
 * {
 *   "application": 6,
 *   "pro_type": 1,
 *   "api": "lst_point_pck",
 *   "token": "31e71234-e0d4-4324-ba6e-99226821f439"
 * }
 * </pre>
 */
public class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener,
    PurchaseHistoryResponseListener, ConsumeResponseListener {

  private static final int BILLING_SERVICE_DISCONNECTED = 330;
  private static final int ON_PURCHASE_HISTORY_ERROR = 331;
  private static final int ON_CONSUME_ERROR = 332;
  private static final String TAG = "BillingManager";
  /**
   * configure on google play console store presence -> in-app products -> Managed products
   */
  private String skuItem;
  /**
   * InApp or Subscription
   *
   * @see com.android.billingclient.api.BillingClient.SkuType#INAPP
   * @see com.android.billingclient.api.BillingClient.SkuType#SUBS
   */
  private String type;
  /**
   * store transaction for retry {@link #onConsumeResponse(int, String)}
   *
   * @see #purchase(String, String, String)
   */
  private String transactionId;

  /**
   * store activity for use later
   */
  private Activity activity;

  /**
   * billing client for purchase
   */
  private BillingClient mBillingClient;

  /**
   * callback for billing process
   */
  private BillingCallback callback;

  /**
   * should call only one (onCreate)
   *
   * @param activity to bind purchase service
   * @see #release()
   */
  public void init(@NonNull Activity activity, @NonNull BillingCallback callback) {
    this.activity = activity;
    this.callback = callback;

    mBillingClient = BillingClient.newBuilder(activity).setListener(this).build();
    mBillingClient.startConnection(this);
    Log.d(TAG, "call init: ");
  }

  /**
   * don not forget to release (onDestroy)
   *
   * @see #init(Activity, BillingCallback)
   */
  public void release() {
    if (callback != null) {
      callback = null;
    }
    if (mBillingClient != null) {
      mBillingClient.endConnection();
    }
    Log.d(TAG, "release");
  }

  /**
   * purchase item
   *
   * @param skuItem {@link #skuItem}
   * @param type inApp or subscription
   * @param transactionId check API "log_before_purchase"
   * @see #onPurchasesUpdated(int, List)
   */
  public void purchase(@NonNull String skuItem, @BillingClient.SkuType String type,
      @NonNull String transactionId) {
    this.skuItem = skuItem;
    this.type = type;
    this.transactionId = transactionId;

    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
        .setSku(skuItem)
        .setType(type) // SkuType.SUB for subscription
        .build();
    mBillingClient.launchBillingFlow(activity, flowParams);
  }

  @Override
  public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
    // callback on each  purchase
    Log.d(TAG, "onPurchasesUpdated: responseCode=" + responseCode + ", purchases=" + purchases);
    switch (responseCode) {
      case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
      case BillingClient.BillingResponse.DEVELOPER_ERROR:
      case BillingClient.BillingResponse.ERROR:
      case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
      case BillingClient.BillingResponse.ITEM_NOT_OWNED:
      case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
      case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
      case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
        callback.onPurchaseError(responseCode);
        break;
      case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
        // purchased => get history => consume => purchase again
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this);
        break;
      case BillingClient.BillingResponse.USER_CANCELED:
        callback.onCancelPurchaseByUser();
        break;
      case BillingClient.BillingResponse.OK:
        if (purchases != null && !purchases.isEmpty()) {
          // get first item
          Purchase purchase = purchases.get(0);
          callback.onConfirmPurchase(purchase, transactionId);
        }
        break;
    }
  }

  @Override
  public void onBillingSetupFinished(int responseCode) {
    // when purchase service connected
    Log.d(TAG, "onBillingSetupFinished: " + responseCode);
  }

  @Override
  public void onBillingServiceDisconnected() {
    // when account remove
    callback.onPurchaseError(BILLING_SERVICE_DISCONNECTED);
    Log.d(TAG, "onBillingServiceDisconnected: ");
  }

  @Override
  public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
    if (responseCode == BillingClient.BillingResponse.OK) {
      consumeItem(mBillingClient, purchasesList);
    } else {
      callback.onPurchaseError(ON_PURCHASE_HISTORY_ERROR);
    }
    Log.d(TAG, "onPurchaseHistoryResponse: responseCode=" + responseCode + ", purchasesList="
        + purchasesList);
  }

  /**
   * request consume item => check callback
   *
   * @see #onConsumeResponse(int, String)
   */
  private void consumeItem(BillingClient mBillingClient, List<Purchase> purchasesList) {
    if (mBillingClient == null || purchasesList == null) {
      return;
    }
    String purchaseToken = null;
    for (Purchase purchase : purchasesList) {
      if (purchase.getSku().equals(skuItem)) {
        purchaseToken = purchase.getPurchaseToken();
      }
    }

    Log.d(TAG, "consumeItem: purchaseToken=" + purchaseToken);
    if (purchaseToken != null) {
      mBillingClient.consumeAsync(purchaseToken, this);
    }
  }

  @Override
  public void onConsumeResponse(int responseCode, String purchaseToken) {
    if (responseCode == BillingClient.BillingResponse.OK) {
      // retry purchase
      purchase(skuItem, type, transactionId);
    } else {
      callback.onPurchaseError(ON_CONSUME_ERROR);
    }
    Log.d(TAG,
        "onConsumeResponse: responseCode=" + responseCode + ", purchaseToken=" + purchaseToken);
  }
}
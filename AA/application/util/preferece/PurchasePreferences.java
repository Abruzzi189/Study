package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import com.application.util.LogUtils;
import java.util.Map;

public class PurchasePreferences {

  public static final String ENCODE_SOURCE = "|";
  public static final String DECODE_SOURCE = "\\|";
  private static final String FILE_PREFERENCES = "andG_purchase";
  private Context mContext;

  public PurchasePreferences(Context context) {
    mContext = context;
  }

  /**
   * Get editor for edit preference in andG
   *
   * @return {@link SharedPreferences.Editor}
   */
  private SharedPreferences.Editor getEditor() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE).edit();
  }

  /**
   * Get SharedPreference for get data from Preferences
   */
  private SharedPreferences getSharedPreferences() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE);
  }

  /**
   * Save order detail data
   *
   * @param oderId Id of the order
   */
  public boolean saveOrderDetail(String oderId, String packageId,
      String purchaseData, String signature, String transactionId) {
    String value = encode(oderId, packageId, purchaseData, signature,
        transactionId);
    LogUtils.i(FILE_PREFERENCES, value);
    return getEditor().putString(oderId, value).commit();
  }

  private String encode(String orderId, String packageId,
      String purchaseData, String signature, String transactionId) {
    StringBuilder value = new StringBuilder();
    // Append order id send from Google Play service
    value.append(orderId).append(ENCODE_SOURCE);

    // Append package id
    value.append(packageId).append(ENCODE_SOURCE);

    value.append(purchaseData).append(ENCODE_SOURCE);

    // Append signature
    value.append(signature).append(ENCODE_SOURCE);

    // Append transaction Id
    value.append(transactionId);
    return value.toString();
  }

  public String[] getOrderDetailList() {
    @SuppressWarnings("unchecked")
    Map<String, String> map = (Map<String, String>) getSharedPreferences()
        .getAll();
    String[] data = new String[map.size()];
    int i = 0;
    for (String key : map.keySet()) {
      data[i] = map.get(key);
      i++;
    }
    return data;
  }

  public void removeOrderId(String oderId) {
    getEditor().remove(oderId).commit();
  }
}

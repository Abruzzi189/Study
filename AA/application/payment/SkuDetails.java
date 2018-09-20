package com.application.payment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails {

  String mSku;
  String mType;
  String mPrice;
  double mAmount;
  String mCurrency;
  String mTitle;
  String mJson;
  String mDescription;

  public SkuDetails(String jsonSkuDetails) throws JSONException {
    mJson = jsonSkuDetails;
    JSONObject o = new JSONObject(mJson);
    mSku = o.optString("productId");
    mType = o.optString("type");
    mPrice = o.optString("price");
    // Price in micro-units, where 1,000,000 micro-units equal one unit of the currency.
    mAmount = o.optDouble("price_amount_micros") / 1000000;
    mCurrency = o.optString("price_currency_code");
    mTitle = o.optString("title");
    mDescription = o.optString("description");
  }

  public String getSku() {
    return mSku;
  }

  public String getType() {
    return mType;
  }

  public String getPrice() {
    return mPrice;
  }

  public double getAmount() {
    return mAmount;
  }

  public String getCurrency() {
    return mCurrency;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getDescription() {
    return mDescription;
  }

  @Override
  public String toString() {
    return "SkuDetails:" + mJson;
  }
}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class LogPurchaseResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -1258183915540912108L;

  private String transactionId;

  public LogPurchaseResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }

      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        if (dataJson.has("transaction_id")) {
          this.transactionId = dataJson.getString("transaction_id");
        }
      }
    } catch (JSONException exception) {
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

}

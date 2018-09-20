package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmPaymentResponse extends Response {

  private int point;

  public ConfirmPaymentResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      setCode(jsonObject.getInt("code"));
      JSONObject object = jsonObject.getJSONObject("data");
      if (object.has("point")) {
        point = object.getInt("point");
      }
    } catch (JSONException exception) {
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getPoint() {
    return point;
  }
}

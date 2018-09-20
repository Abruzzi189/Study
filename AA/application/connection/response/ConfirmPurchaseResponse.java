package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmPurchaseResponse extends Response {

  private int point;

  public ConfirmPurchaseResponse(ResponseData responseData) {
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
      if (jsonObject.has("data")) {
        JSONObject object = jsonObject.getJSONObject("data");
        JSONObject object2 = new JSONObject(object.toString());
        point = object2.getInt("point");
      }
    } catch (JSONException exception) {
      exception.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getPoint() {
    return point;
  }

}

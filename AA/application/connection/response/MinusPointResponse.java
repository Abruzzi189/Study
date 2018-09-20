package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class MinusPointResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -4225300063726878400L;
  private int point;

  public MinusPointResponse(ResponseData responseData) {
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
      JSONObject jsonObject2 = jsonObject.optJSONObject("data");
      if (jsonObject2 != null) {
        point = jsonObject2.optInt("point");
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getPoint() {
    return point;
  }
}

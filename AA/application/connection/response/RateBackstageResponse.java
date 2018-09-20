package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class RateBackstageResponse extends Response {

  private int rate;

  public RateBackstageResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getRate() {
    return this.rate;
  }

  private void setRate(int rate) {
    this.rate = rate;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }

      JSONObject data = (JSONObject) jsonObject.get("data");

      if (data.has("rate")) {
        setRate(data.getInt("rate"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}

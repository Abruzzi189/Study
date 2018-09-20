package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportResponse extends Response {

  private boolean isAppear;

  public ReportResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONObject dataJson = jsonObject.getJSONObject("data");
          if (dataJson.has("is_appear")) {
            setAppear(dataJson.getInt("is_appear") == 1);
          } else {
            setAppear(true);
          }

        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public boolean isAppear() {
    return isAppear;
  }

  public void setAppear(boolean isAppear) {
    this.isAppear = isAppear;
  }

}

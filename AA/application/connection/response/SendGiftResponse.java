package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class SendGiftResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -5788619810760956323L;
  private int point;

  public SendGiftResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
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
          if (dataJson.has("point")) {
            setPoint(dataJson.getInt("point"));
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}

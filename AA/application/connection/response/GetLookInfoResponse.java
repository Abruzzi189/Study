package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class GetLookInfoResponse extends Response {

  private int max;
  private int min;
  private int points;

  public GetLookInfoResponse(ResponseData responseData) {
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
        JSONObject object = jsonObject.getJSONObject("data");
        if (object.has("max")) {
          setMax(object.getInt("max"));
        }
        if (object.has("min")) {
          setMin(object.getInt("min"));
        }
      }

    } catch (JSONException exception) {
      exception.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }

  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class GetOnlineAlertResponse extends Response {

  private int is_alt;
  private int altNumber;

  public GetOnlineAlertResponse(ResponseData responseData) {
    super(responseData);
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

      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");

        if (dataJson.has("is_alt")) {
          setIs_alt(dataJson.getInt("is_alt"));
        }
        if (dataJson.has("alt_fre")) {
          setAltNumber(dataJson.getInt("alt_fre"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getIs_alt() {
    return is_alt;
  }

  public void setIs_alt(int is_alt) {
    this.is_alt = is_alt;
  }

  public int getAltNumber() {
    return this.altNumber;
  }

  public void setAltNumber(int altNumber) {
    this.altNumber = altNumber;
  }
}
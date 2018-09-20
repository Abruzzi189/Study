package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class GetTotalUnreadMesageResponse extends Response {

  @SerializedName("unread_num")
  private int unread_num;

  public GetTotalUnreadMesageResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getNumber() {
    return this.unread_num;
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

        if (dataJson.has("unread_num")) {
          this.unread_num = dataJson.getInt("unread_num");
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}
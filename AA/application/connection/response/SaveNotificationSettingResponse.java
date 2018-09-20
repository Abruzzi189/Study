package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class SaveNotificationSettingResponse extends Response {

  public SaveNotificationSettingResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    if (jsonObject.has("code")) {
      try {
        setCode(jsonObject.getInt("code"));
      } catch (JSONException e) {
        e.printStackTrace();
        setCode(CLIENT_ERROR_PARSE_JSON);
      }

    }
  }

}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SetMeetPeopleSearchSettingResponse extends Response {

  public SetMeetPeopleSearchSettingResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        // Get status code from server
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
      }
    } catch (JSONException ex) {
      LogUtils.e("Response: parseData",
          "GetMeetPeopleSettingResponse: " + ex.getMessage());
      setCode(CLIENT_ERROR_NO_DATA);
    }
  }
}
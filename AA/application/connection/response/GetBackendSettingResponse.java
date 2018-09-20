package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.BackendSetting;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HungHN on 5/30/2016.
 */
public class GetBackendSettingResponse extends Response {

  private BackendSetting backendSetting;

  public GetBackendSettingResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    Gson gson = new Gson();
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONObject jsonData = jsonObject.getJSONObject("data");
          backendSetting = gson.fromJson(jsonData.toString(), BackendSetting.class);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public BackendSetting getBackendSetting() {
    return backendSetting;
  }
}

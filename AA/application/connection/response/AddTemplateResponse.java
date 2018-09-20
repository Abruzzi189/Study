package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class AddTemplateResponse extends Response {

  private static final long serialVersionUID = 8149058751746374682L;
  @SerializedName("template_id")
  private String tempId;

  public AddTemplateResponse(ResponseData responseData) {
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
          if (dataJson.has("template_id")) {
            tempId = dataJson.getString("template_id");
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getTempId() {
    return tempId;
  }
}

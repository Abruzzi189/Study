package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class GetStaticPageResponse extends Response {

  @SerializedName("content")
  private String content;

  public GetStaticPageResponse(ResponseData responseData) {
    super(responseData);
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
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
        setContent(jsonObject.getString("data"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}

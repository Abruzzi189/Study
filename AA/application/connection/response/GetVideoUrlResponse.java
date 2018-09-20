package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class GetVideoUrlResponse extends Response {

  private static final long serialVersionUID = 2940071428983607482L;

  private String url;

  public GetVideoUrlResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    int code = jsonObject.optInt("code");
    setCode(code);

    if (jsonObject.has("data")) {
      try {
        JSONObject data = jsonObject.getJSONObject("data");
        if (data.has("url")) {
          url = data.getString("url");
        }
      } catch (JSONException e) {
      }
    }
  }

  public String getURL() {
    return url;
  }
}
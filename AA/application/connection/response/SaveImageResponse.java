package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class SaveImageResponse extends Response {

  private int point;
  private int save_image_point;

  public SaveImageResponse(ResponseData responseData) {
    super(responseData);
  }

  public void setPrice(int point) {
    this.point = point;
  }

  public int getPoint() {
    return this.point;
  }

  public int getSavePoint() {
    return save_image_point;
  }

  public void setSavePoint(int point) {
    this.save_image_point = point;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      setCode(jsonObject.getInt("code"));
      JSONObject dataJson = jsonObject.getJSONObject("data");
      if (dataJson.has("point")) {
        setPrice(dataJson.getInt("point"));
      }
      if (dataJson.has("save_image_point")) {
        setSavePoint(dataJson.getInt("save_image_point"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}

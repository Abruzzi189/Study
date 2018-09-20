package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadImageResponse extends Response {

  private String imgId;
  private int isApproved;
  // api: 25
  public UploadImageResponse(ResponseData responseData) {
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
          setImgId(dataJson.getString("image_id"));
          if (dataJson.has("is_app")) {
            setIsApproved(dataJson.getInt("is_app"));
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public String getImgId() {
    return imgId;
  }

  public void setImgId(String imgId) {
    this.imgId = imgId;
  }


  public int getIsApproved() {
    return isApproved;
  }

  public void setIsApproved(int isApproved) {
    this.isApproved = isApproved;
  }
}
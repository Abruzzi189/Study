package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAvatarResponse extends Response {

  @SerializedName("img_id")
  private String avatarId;

  public UpdateAvatarResponse(ResponseData responseData) {
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
        if (jsonObject.has("img_id")) {
          setAvatarId(jsonObject.getString("img_id"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getAvatarId() {
    return avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

}

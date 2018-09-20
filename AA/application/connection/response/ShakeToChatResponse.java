package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class ShakeToChatResponse extends Response {

  private static final String TAG = "ShakeToChatResponse";

  @SerializedName("user_type")
  private int userType;

  @SerializedName("ava_id")
  private String avatarId;

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_name")
  private String userName;

  @SerializedName("gender")
  private int userGender;

  public ShakeToChatResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "ShakeToChatResponse Started");
    LogUtils.d(TAG, "ShakeToChatResponse Ended");
  }

  public int getUserType() {
    return this.userType;
  }

  public void setUserType(int userType) {
    this.userType = userType;
  }

  public String getAvatarId() {
    return this.avatarId;
  }

  public void setAvatarId(String avatarId) {
    this.avatarId = avatarId;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getUserGender() {
    return this.userGender;
  }

  public void setUserGender(int userGender) {
    this.userGender = userGender;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    LogUtils.d(TAG, "parseData Started");

    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        LogUtils.d(TAG, "parseData Ended (1)");
        return;
      }

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }

      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        if (dataJson.has("user_type")) {
          setUserType(dataJson.getInt("user_type"));
        }
        if (dataJson.has("ava_id")) {
          setAvatarId(dataJson.getString("ava_id"));
        }
        if (dataJson.has("user_id")) {
          setUserId(dataJson.getString("user_id"));
        }
        if (dataJson.has("user_name")) {
          setUserName(dataJson.getString("user_name"));
        }
        if (dataJson.has("gender")) {
          setUserGender(dataJson.getInt("gender"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

}

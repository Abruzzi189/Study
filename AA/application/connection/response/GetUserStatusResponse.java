package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class GetUserStatusResponse extends Response {

  public static final int ACTIVE = 1;
  public static final int DEACTIVE = 0;
  public static final int INACTIVE = -1;
  private static final long serialVersionUID = -6503217388567505201L;
  @SerializedName("user_status")
  private int userStatus;

  public GetUserStatusResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        JSONObject object = jsonObject.getJSONObject("data");
        if (object.has("user_status")) {
          LogUtils.d("STATUS", "parse : " + object.getInt("user_status"));
          userStatus = object.getInt("user_status");
        }
      }
    } catch (JSONException exception) {
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getUserStatus() {
    return userStatus;
  }
}

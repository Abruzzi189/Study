package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class LikeBuzzResponse extends Response {

  private static final String TAG = "LikeBuzzResponse";

  public LikeBuzzResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "LikeBuzzResponse Started");
    LogUtils.d(TAG, "LikeBuzzResponse Ended");
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
    } catch (JSONException e) {
      e.printStackTrace();
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class DeleteCommentResponse extends Response {

  private static final String TAG = "DeleteCommentResponse";

  public DeleteCommentResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "DeleteCommentResponse Started");
    LogUtils.d(TAG, "DeleteCommentResponse Ended");
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

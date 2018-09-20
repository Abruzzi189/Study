package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class AddCommentResponse extends Response {

  private static final String TAG = "AddCommentResponse";

  @SerializedName("cmt_id")
  private String cmt_id;

  @SerializedName("point")
  private int point;

  private int comment_buzz_point;

  @SerializedName("is_app")
  private int isApprove;

  public AddCommentResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "AddCommentResponse Started");
    LogUtils.d(TAG, "AddCommentResponse Ended");
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
        if (dataJson.has("cmt_id")) {
          setCommentId(dataJson.getString("cmt_id"));
        }

        if (dataJson.has("point")) {
          setPoint(dataJson.getInt("point"));
        }

        if (dataJson.has("is_app")) {
          setIsApprove(dataJson.getInt("is_app"));
        } else {
          setIsApprove(Constants.IS_APPROVED);
        }

        if (dataJson.has("comment_buzz_point")) {
          setCommentPoint(dataJson.getInt("comment_buzz_point"));
        }

      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

  public String getCommentId() {
    return cmt_id;
  }

  public void setCommentId(String cmt_id) {
    this.cmt_id = cmt_id;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public int getCommentPoint() {
    return comment_buzz_point;
  }

  public void setCommentPoint(int point) {
    this.comment_buzz_point = point;
  }

  public int getIsApprove() {
    return isApprove;
  }

  public void setIsApprove(int isApprove) {
    this.isApprove = isApprove;
  }
}
package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class AddSubCommentResponse extends Response {

  private static final long serialVersionUID = -3261520432475081025L;

  @SerializedName("sub_comment_id")
  private String subCommentId;

  @SerializedName("point")
  private int point;

  @SerializedName("sub_comment_point")
  private int subCommentPoint;

  @SerializedName("is_app")
  private int isApprove;

  public AddSubCommentResponse(ResponseData responseData) {
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
          if (dataJson.has("sub_comment_id")) {
            subCommentId = dataJson.getString("sub_comment_id");
          }
          if (dataJson.has("point")) {
            point = dataJson.getInt("point");
          }
          if (dataJson.has("is_app")) {
            isApprove = dataJson.getInt("is_app");
          } else {
            setIsApprove(Constants.IS_APPROVED);
          }
          if (dataJson.has("sub_comment_point")) {
            subCommentPoint = dataJson.getInt("sub_comment_point");
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getSubCommentId() {
    return subCommentId;
  }

  public int getPoint() {
    return point;
  }

  public int getSubCommentPoint() {
    return subCommentPoint;
  }

  public int getIsApprove() {
    return isApprove;
  }

  public void setIsApprove(int isApprove) {
    this.isApprove = isApprove;
  }
}

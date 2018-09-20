package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class AddBuzzResponse extends Response {

  @SerializedName("buzz_id")
  private String buzzId;
  @SerializedName("is_app")
  private int isApprove;
  @SerializedName("comment_app")
  private int comment_app;

  public AddBuzzResponse(ResponseData responseData) {
    super(responseData);
  }

  public String getBuzzId() {
    return buzzId;
  }

  public void setBuzzId(String buzzId) {
    this.buzzId = buzzId;
  }

  public int getIsApprove() {
    return isApprove;
  }

  public void setIsApprove(int isApprove) {
    this.isApprove = isApprove;
  }

  public int getComment_app() {
    return comment_app;
  }

  public void setComment_app(int comment_app) {
    this.comment_app = comment_app;
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
          if (dataJson.has("buzz_id")) {
            setBuzzId(dataJson.getString("buzz_id"));
          }
          if (dataJson.has("is_app")) {
            setIsApprove(dataJson.getInt("is_app"));
          } else {
            setIsApprove(Constants.IS_APPROVED);
          }
          if (dataJson.has("comment_app")) {
            setComment_app(dataJson.getInt("comment_app"));
          } else {
            setComment_app(Constants.IS_APPROVED);
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoUpdateRespone extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -7073142051887387794L;

  @SerializedName("email")
  private String email;
  @SerializedName("finish_register_flag")
  private int finishRegisterFlag;

  @SerializedName("verification_flag")
  private int verificationFlag;

  @SerializedName("is_reviewed")
  private boolean isReview;

  public UserInfoUpdateRespone(ResponseData responseData) {
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
        LogUtils.e("UserInfoResponse",
            "UserInfoResponse=" + jsonObject.toString());

        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }

        if (jsonObject.has("data")) {
          JSONObject dataJson = jsonObject.getJSONObject("data");

          if (dataJson.has("email")) {
            setEmail(dataJson.getString("email"));
          }

          if (dataJson.has("finish_register_flag")) {
            setFinishRegisterFlag(dataJson.getInt("finish_register_flag"));
          }

          if (dataJson.has("verification_flag")) {
            setVerificationFlag(dataJson.getInt("verification_flag"));
          }
          if (dataJson.has("is_reviewed")) {
            setIsReview(dataJson
                .getBoolean("is_reviewed"));
          } else {
            setIsReview(false);
          }

        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getFinishRegisterFlag() {
    return finishRegisterFlag;
  }

  public void setFinishRegisterFlag(int finishRegisterFlag) {
    this.finishRegisterFlag = finishRegisterFlag;
  }

  public int getVerificationFlag() {
    return verificationFlag;
  }

  public void setVerificationFlag(int verificationFlag) {
    this.verificationFlag = verificationFlag;
  }

  public boolean isReview() {
    return isReview;
  }

  public void setIsReview(boolean isReview) {
    this.isReview = isReview;
  }
}

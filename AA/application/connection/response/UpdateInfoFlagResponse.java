package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.preferece.UserPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateInfoFlagResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private boolean isUpdateEmail;
  private boolean isFinishRegister;
  private int verificationFlag;

  public UpdateInfoFlagResponse(ResponseData responseData) {
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
          setUpdateEmail(dataJson.getInt("update_email_flag") == 1);
          UserPreferences.getInstance().saveUpdateEmail(isUpdateEmail);
          setFinishRegister(dataJson.getInt("finish_register_flag") == 1);
          setVerificationFlag(dataJson.getInt("verification_flag"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public boolean isUpdateEmail() {
    return isUpdateEmail;
  }

  public void setUpdateEmail(boolean isUpdateEmal) {
    this.isUpdateEmail = isUpdateEmal;
  }

  public boolean isFinishRegister() {
    return isFinishRegister;
  }

  public void setFinishRegister(boolean isFinishRegister) {
    this.isFinishRegister = isFinishRegister;
  }

  public int getVerificationFlag() {
    return verificationFlag;
  }

  public void setVerificationFlag(int verificationFlag) {
    this.verificationFlag = verificationFlag;
  }

}

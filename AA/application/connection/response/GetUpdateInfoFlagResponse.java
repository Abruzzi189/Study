package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class GetUpdateInfoFlagResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 8850857336302108398L;

  @SerializedName("update_email_flag")
  private int updateEmailFlag;

  @SerializedName("verification_flag")
  private int verificationFlag;

  @SerializedName("finish_register_flag")
  private int finishRegisterFlag;

  public GetUpdateInfoFlagResponse(ResponseData data) {
    super(data);
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

        if (object.has("update_email_flag")) {
          setUpdateEmailFlag(object.getInt("update_email_flag"));
        }

        if (object.has("verification_flag")) {
          setVerificationFlag(object.getInt("verification_flag"));
        }

        if (object.has("finish_register_flag")) {
          setFinishRegisterFlag(object.getInt("finish_register_flag"));
        }
      }

    } catch (JSONException exception) {
      exception.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getUpdateEmailFlag() {
    return updateEmailFlag;
  }

  public void setUpdateEmailFlag(int updateEmailFlag) {
    this.updateEmailFlag = updateEmailFlag;
  }

  public int getVerificationFlag() {
    return verificationFlag;
  }

  public void setVerificationFlag(int verificationFlag) {
    this.verificationFlag = verificationFlag;
  }

  public int getFinishRegisterFlag() {
    return finishRegisterFlag;
  }

  public void setFinishRegisterFlag(int finishRegisterFlag) {
    this.finishRegisterFlag = finishRegisterFlag;
  }

}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

public class GetAttendtionNumberResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 1654654324684387L;
  @SerializedName("checkout_num")
  private int checkout_num;
  @SerializedName("fvt_num")
  private int fvt_num;
  @SerializedName("new_checkout_num")
  private int new_checkout_num;
  @SerializedName("new_fvt_num")
  private int new_fvt_num;

  public GetAttendtionNumberResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getCheckout_num() {
    return checkout_num;
  }

  public int getFvt_num() {
    return fvt_num;
  }

  public int getNew_checkout_num() {
    return new_checkout_num;
  }

  public int getNew_fvt_num() {
    return new_fvt_num;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");

        if (dataJson.has("checkout_num")) {
          this.checkout_num = dataJson.getInt("checkout_num");
        }
        if (dataJson.has("fvt_num")) {
          this.fvt_num = dataJson.getInt("fvt_num");
        }
        if (dataJson.has("new_checkout_num")) {
          this.new_checkout_num = dataJson.getInt("new_checkout_num");
        }
        if (dataJson.has("new_fvt_num")) {
          this.new_fvt_num = dataJson.getInt("new_fvt_num");
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }

  }

}

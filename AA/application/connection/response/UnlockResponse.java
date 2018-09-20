package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class UnlockResponse extends Response {

  private static final long serialVersionUID = -4638876834266312819L;

  @SerializedName("point")
  private int point;
  @SerializedName("price")
  private int price;

  public UnlockResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getPoint() {
    return this.point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public int getPrice() {
    return this.price;
  }

  public void setPrice(int price) {
    this.price = price;
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
        if (dataJson.has("point")) {
          setPoint(dataJson.getInt("point"));
        }
        if (dataJson.has("price")) {
          setPrice(dataJson.getInt("price"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}

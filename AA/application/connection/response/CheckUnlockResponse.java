package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUnlockResponse extends Response implements Serializable {

  private static final long serialVersionUID = -5397649281333266963L;

  private int isUnlock;
  private int point;
  private int price;
  private int receivedPoint;
  private double backstageRate;
  private int backstageNumber;
  private int userRateNumber;
  private int ratePoint;
  private int bckstg_pri;
  private int bckstg_bonus;

  public CheckUnlockResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getIsUnlock() {
    return this.isUnlock;
  }

  public void setIsUnlock(int isUnlock) {
    this.isUnlock = isUnlock;
  }

  public int getPoint() {
    return this.point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public double getBackstageRate() {
    return this.backstageRate;
  }

  public void setBackstageRate(double backstageRate) {
    this.backstageRate = backstageRate;
  }

  public int getUserRateNumber() {
    return this.userRateNumber;
  }

  public void setUserRateNumber(int userRateNumber) {
    this.userRateNumber = userRateNumber;
  }

  public int getBackstageNumber() {
    return this.backstageNumber;
  }

  public void setBackstageNumber(int backstageNumber) {
    this.backstageNumber = backstageNumber;
  }

  public void setRatePoint(int ratePoint) {
    this.ratePoint = ratePoint;
  }

  public int getRatepoint() {
    return this.ratePoint;
  }

  public int getBackstagePrice() {
    return this.bckstg_pri;
  }

  public void setBackstagePrice(int point) {
    this.bckstg_pri = point;
  }

  public int getBonus() {
    return this.bckstg_bonus;
  }

  public void setBonus(int point) {
    this.bckstg_bonus = point;
  }

  public int getReceivedPoint() {
    return receivedPoint;
  }

  public void setReceivedPoint(int receivedPoint) {
    this.receivedPoint = receivedPoint;
  }

  public int getPrice() {
    return price;
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
        if (dataJson.has("is_unlck")) {
          setIsUnlock(dataJson.getInt("is_unlck"));
        }
        if (dataJson.has("point")) {
          setPoint(dataJson.getInt("point"));
        }
        if (dataJson.has("bckstg_rate")) {
          setBackstageRate(dataJson.getDouble("bckstg_rate"));
        }
        if (dataJson.has("bckstg_num")) {
          setBackstageNumber(dataJson.getInt("bckstg_num"));
        }
        if (dataJson.has("user_rate_num")) {
          setUserRateNumber(dataJson.getInt("user_rate_num"));
        }
        if (dataJson.has("rate_point")) {
          setRatePoint(dataJson.getInt("rate_point"));
        }
        if (dataJson.has("bckstg_pri")) {
          setBackstagePrice(dataJson.getInt("bckstg_pri"));
        }
        if (dataJson.has("bckstg_bonus")) {
          setBonus(dataJson.getInt("bckstg_bonus"));
        }
        if (dataJson.has("received_point")) {
          setReceivedPoint(dataJson.getInt("received_point"));
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

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class GetMeetPeopleSearchSettingResponse extends Response {

  @SerializedName("show_me")
  private String showMe;
  @SerializedName("inters_in")
  private String intersIn;
  @SerializedName("lower_age")
  private String lowerAge;
  @SerializedName("upper_age")
  private String upperAge;
  @SerializedName("ethn")
  private String ethn;
  @SerializedName("distance")
  private String location;

  public GetMeetPeopleSearchSettingResponse(ResponseData responseData) {
    super(responseData);
  }

  /* @return the showMe */
  public String getShowMe() {
    return showMe;
  }

  /* @return the intersIn */
  public String getIntersIn() {
    return intersIn;
  }

  /* @return the lowerAge */
  public String getLowerAge() {
    return lowerAge;
  }

  /* @return the upperAge */
  public String getUpperAge() {
    return upperAge;
  }

  /* @return the ethn */
  public String getEthn() {
    return ethn;
  }

  /* @return the distance */
  public String getLocation() {
    return location;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        // Get status code from server
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        // Get data response from jsonObject
        if (jsonObject.has("data")) {
          JSONObject dataJson = jsonObject.getJSONObject("data");
          // Set data get from jsonObject
          if (dataJson.has("show_me")) {
            this.showMe = dataJson.getString("show_me");
          }
          if (dataJson.has("inters_in")) {
            this.intersIn = dataJson.getString("inters_in");
          }
          if (dataJson.has("distance")) {
            this.location = dataJson.getString("distance");
          }
          if (dataJson.has("lower_age")) {
            this.lowerAge = dataJson.getString("lower_age");
          }
          if (dataJson.has("upper_age")) {
            this.upperAge = dataJson.getString("upper_age");
          }
          if (dataJson.has("ethn")) {
            this.ethn = dataJson.getString("ethn");
          }
        }
      }
    } catch (JSONException ex) {
      LogUtils.e("Response: parseData", "GetMeetPeopleSettingResponse: " + ex.getMessage());
    }
  }

  public String toString() {
    String result = "{";
    result += "\"Show me: " + this.showMe + "\" ";
    result += "\"Inters in: " + this.intersIn + "\" ";
    result += "\"Location: " + this.location + "\" ";
    result += "\"Age min: " + this.lowerAge + "\" ";
    result += "\"Age max: " + this.upperAge + "\" ";
    result += "\"Ethnicity: " + this.ethn + "\" ";
    result.trim();
    return result + "}";
  }
}
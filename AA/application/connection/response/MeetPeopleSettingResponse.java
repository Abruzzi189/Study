package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MeetPeopleSettingResponse extends Response implements Serializable {

  private static final long serialVersionUID = 4465870185554500938L;

  private int lower_age;
  private int upper_age;
  private int distance;
  private int[] region;
  private int filter;
  private int sort_type;
  private boolean is_new_login;

  public MeetPeopleSettingResponse(ResponseData responseData) {
    super(responseData);
  }

  public MeetPeopleSettingResponse(int minAge, int maxAge, int distance,
      int[] region, int filter, int sort_type, boolean is_new_login) {
    this.lower_age = minAge;
    this.upper_age = maxAge;
    this.distance = distance;
    this.region = region;
    this.filter = filter;
    this.sort_type = sort_type;
    this.is_new_login = is_new_login;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONObject dataJson = jsonObject.getJSONObject("data");
          if (dataJson.has("lower_age")) {
            setLower_age(dataJson.getInt("lower_age"));
          }
          if (dataJson.has("upper_age")) {
            setUpper_age(dataJson.getInt("upper_age"));
          }
          if (dataJson.has("distance")) {
            setDistance(dataJson.getInt("distance"));
          }
          if (dataJson.has("region")) {
            JSONArray region = dataJson.getJSONArray("region");
            if (region != null && region.length() > 0) {
              this.region = new int[region.length()];
              for (int i = 0; i < region.length(); i++) {
                this.region[i] = region.getInt(i);
              }
            }
          }
        }
      }
    } catch (JSONException e) {
      setCode(Response.SERVER_WRONG_DATA_FORMAT);
      e.printStackTrace();
    }
  }

  public int getLowerAge() {
    return lower_age;
  }

  public void setLower_age(int lower_age) {
    if (lower_age < Constants.SEARCH_SETTING_AGE_MIN_LIMIT) {
      lower_age = Constants.SEARCH_SETTING_AGE_MIN_LIMIT;
    }
    if (lower_age > Constants.SEARCH_SETTING_AGE_MAX_LIMIT) {
      lower_age = Constants.SEARCH_SETTING_AGE_MAX_LIMIT;
    }
    this.lower_age = lower_age;
  }

  public int getUpperAge() {
    return upper_age;
  }

  public void setUpper_age(int upper_age) {
    if (upper_age < Constants.SEARCH_SETTING_AGE_MIN_LIMIT) {
      upper_age = Constants.SEARCH_SETTING_AGE_MIN_LIMIT;
    }
    if (upper_age > Constants.SEARCH_SETTING_AGE_MAX_LIMIT) {
      upper_age = Constants.SEARCH_SETTING_AGE_MAX_LIMIT;
    }
    this.upper_age = upper_age;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public int[] getRegion() {
    return this.region;
  }

  public void setRegion(int[] region) {
    this.region = region;
  }

  public int getSortType() {
    return sort_type;
  }

  public void setSort_type(int sort_type) {
    this.sort_type = sort_type;
  }

  public int getFilter() {
    return filter;
  }

  public void setFilter(int filter) {
    this.filter = filter;
  }

  public boolean isNewLogin() {
    return is_new_login;
  }

  public void setIs_new_login(boolean is_new_login) {
    this.is_new_login = is_new_login;
  }
}
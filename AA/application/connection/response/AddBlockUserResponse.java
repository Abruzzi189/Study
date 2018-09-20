package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class AddBlockUserResponse extends Response {

  private int favouriteFriendsNum;
  private int friendsNum;

  public AddBlockUserResponse(ResponseData responseData) {
    super(responseData);
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
      JSONObject jsonObject2 = jsonObject.optJSONObject("data");
      if (jsonObject2 != null) {
        favouriteFriendsNum = jsonObject2.optInt("fav_num");
        friendsNum = jsonObject2.optInt("frd_num");
      }

    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getFavouriteFriendsNum() {
    return favouriteFriendsNum;
  }

  public int getFriendsNum() {
    return friendsNum;
  }

}

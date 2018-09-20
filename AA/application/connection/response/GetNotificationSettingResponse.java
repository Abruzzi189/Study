package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetNotificationSettingResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -5882704252381149906L;
  private int notifyBuzz;
  private int notifyAlert;
  private int notifyChat;
  private int notifyCheckout;
  private String[] mFavList;
  private String[] mFriendList;

  public GetNotificationSettingResponse(ResponseData responseData) {
    super(responseData);
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
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject setting = data.getJSONObject("setting");
        JSONArray arrayFav = data.getJSONArray("fav_lst");
//				JSONArray arrayFriend = data.getJSONArray("frd_lst");
        // get setting
        setNotifyAlert(setting.getInt("andg_alt"));
        setNotifyBuzz(setting.getInt("noti_buzz"));
        setNotifyChat(setting.getInt("chat"));
        setNotifyCheckout(setting.getInt("noti_chk_out"));

        // get fav
        mFavList = new String[arrayFav.length()];
        for (int i = 0; i < arrayFav.length(); i++) {
          mFavList[i] = arrayFav.getString(i);
        }
        // get frineds
//				mFriendList = new String[arrayFriend.length()];
//				for (int index = 0; index < arrayFriend.length(); index++) {
//					mFriendList[index] = arrayFriend.getString(index);
//				}
      }
    } catch (Exception e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }

  }

  public int getNotifyBuzz() {
    return notifyBuzz;
  }

  public void setNotifyBuzz(int notifyBuzz) {
    this.notifyBuzz = notifyBuzz;
  }

  public int getNotifyAlert() {
    return notifyAlert;
  }

  public void setNotifyAlert(int notifyAlert) {
    this.notifyAlert = notifyAlert;
  }

  public int getNotifyChat() {
    return notifyChat;
  }

  public void setNotifyChat(int notifyChat) {
    this.notifyChat = notifyChat;
  }

  public int getNotifyCheckout() {
    return notifyCheckout;
  }

  public void setNotifyCheckout(int notifyCheckout) {
    this.notifyCheckout = notifyCheckout;
  }

  public String[] getFavList() {
    return mFavList;
  }

  public String[] getFriendList() {
    return mFriendList;
  }

}

package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.ui.account.AuthenticationData;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse extends Response {

  private static final long serialVersionUID = -8445666679255121953L;
  private AuthenticationData authenData;
  private String blockedUserList;

  private boolean isEnableGetFreePoint;
  private boolean isTurnOffUserInfo;
  private boolean isShowNews;
  private String switchBrowserVersion;

  public LoginResponse(ResponseData responseData) {
    super(responseData);
  }

  public AuthenticationData getAuthenticationData() {
    return authenData;
  }

  public void setBlokedUsersList(String blockedUserList) {
    this.blockedUserList = blockedUserList;
  }

  public String getBlockedUsersList() {
    return this.blockedUserList;
  }

  public boolean isEnableGetFreePoint() {
    return isEnableGetFreePoint;
  }

  public String getSwitchBrowserVersion() {
    return switchBrowserVersion;
  }

  public boolean isTurnOffUserInfo() {
    return isTurnOffUserInfo;
  }

  public boolean isShowNews() {
    return isShowNews;
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
          authenData = new AuthenticationData();
          if (dataJson.has("user_id")) {
            authenData.setUserId(dataJson.getString("user_id"));
          }
          if (dataJson.has("user_name")) {
            authenData.setUserName(dataJson.getString("user_name"));
          }
          if (dataJson.has("ava_id")) {
            authenData.setAvartarId(dataJson.getString("ava_id"));
          }
          if (dataJson.has("fb_id")) {
            authenData.setFacebookId(dataJson.getString("fb_id"));
          }
          if (dataJson.has("token")) {
            authenData.setToken(dataJson.getString("token"));
          }
          if (dataJson.has("user_name")) {
            authenData.setUserName(dataJson.getString("user_name"));
          }
          if (dataJson.has("noti_num")) {
            authenData.setNumNotification(dataJson
                .getInt("noti_num"));
          }
          if (dataJson.has("chat_num")) {
            authenData.setNumMyChat(dataJson.getInt("chat_num"));
          }
          if (dataJson.has("point")) {
            authenData.setNumPoint(dataJson.getInt("point"));
          }
          if (dataJson.has("frd_num")) {
            authenData.setNumFriend(dataJson.getInt("frd_num"));
          }
          if (dataJson.has("fav_num")) {
            authenData.setNumFavourite(dataJson.getInt("fav_num"));
          }
          if (dataJson.has("fvt_num")) {
            authenData
                .setNumFavouriteMe(dataJson.getInt("fvt_num"));
          }
          if (dataJson.has("gender")) {
            authenData.setGender(dataJson.getInt("gender"));
          }
          // Blocked Users List
          if (dataJson.has("backlst")) {
            String tmp = dataJson.getString("backlst");
            tmp = tmp.subSequence(1, tmp.length() - 1).toString();
            setBlokedUsersList(tmp);
          }
          if (dataJson.has("unlck_fvt")) {
            authenData.setUnlockFavoritePoints(dataJson
                .getInt("unlck_fvt"));
          }
          if (dataJson.has("checkout_num")) {
            authenData.setUnlockWhoCheckMeOutPoints(dataJson
                .getInt("checkout_num"));
          }
          if (dataJson.has("day_bns_pnt")) {
            authenData.setDailyBonusPoints(dataJson
                .getInt("day_bns_pnt"));
          }
          if (dataJson.has("save_img_pnt")) {
            authenData.setSaveImagePoints(dataJson
                .getInt("save_img_pnt"));
          }
          if (dataJson.has("unlck_chk_out_pnt")) {
            authenData.setUnlockWhoCheckMeOutPoints(dataJson
                .getInt("unlck_chk_out_pnt"));
          }
          if (dataJson.has("unlck_fvt_pnt")) {
            authenData.setUnlockFavoritePoints(dataJson
                .getInt("unlck_fvt_pnt"));
          }
          if (dataJson.has("onl_alt_pnt")) {
            authenData.setOnlineAlertPoints(dataJson
                .getInt("onl_alt_pnt"));
          }
          if (dataJson.has("wink_bomb_pnt")) {
            authenData.setWinkBombPoints(dataJson
                .getInt("wink_bomb_pnt"));
          }
          if (dataJson.has("ivt_frd_pnt")) {
            authenData.setInviteFriendPoints(dataJson
                .getInt("ivt_frd_pnt"));
          }
          if (dataJson.has("rate_distri_point")) {
            authenData.setRateDistributionPoints(dataJson
                .getDouble("rate_distri_point"));
          }
          if (dataJson.has("wink_bomb_num")) {
            authenData.setWinkBombPoints(dataJson
                .getInt("wink_bomb_num"));
          }
          if (dataJson.has("ivt_url")) {
            authenData.setInviteUrl(dataJson.getString("ivt_url"));
          }
          if (dataJson.has("look_time")) {
            authenData.setLookAtMeTime(String.valueOf(dataJson
                .getInt("look_time")));
          }
          if (dataJson.has("finish_register_flag")) {
            authenData.setFinishRegister(dataJson
                .getInt("finish_register_flag"));
          }

          if (dataJson.has("verification_flag")) {
            authenData.setVerificationFlag(dataJson
                .getInt("verification_flag"));
          }

          if (dataJson.has("update_email_flag")) {
            authenData.setUpdateEmail(dataJson
                .getInt("update_email_flag") == 1);
          }

          if (dataJson.has("bckstg_pri")) {
            authenData.setBackstagePrice(dataJson
                .getInt("bckstg_pri"));
          }

          if (dataJson.has("bckstg_bonus")) {
            authenData.setBackstageBonus(dataJson
                .getInt("bckstg_bonus"));
          }

          if (dataJson.has("comment_buzz_pnt")) {
            authenData.setCommentPoint(dataJson
                .getInt("comment_buzz_pnt"));
          }

          if (dataJson.has("chat_pnt")) {
            authenData.setChatPoint(dataJson.getInt("chat_pnt"));
          }

          if (dataJson.has("get_free_point_android")) {
            this.isEnableGetFreePoint = !dataJson
                .getBoolean("get_free_point_android");
          }

          if (dataJson.has("switch_browser_android_version")) {
            this.switchBrowserVersion = dataJson
                .getString("switch_browser_android_version");
          }

          if (dataJson.has("turn_off_user_info_android")) {
            this.isTurnOffUserInfo = !dataJson.
                getBoolean("turn_off_user_info_android");
          }

          if (dataJson.has("turn_off_show_news_android")) {
            this.isShowNews = !dataJson.getBoolean("turn_off_show_news_android");
          } else {
            this.isShowNews = true;
          }

          authenData.setShowPopupNews(this.isShowNews);
          authenData.setCheckoutTime(dataJson.optInt("chk_out_time"));
          authenData.setFavouriteTime(dataJson.optInt("fav_time"));
          authenData.setBackstageTime(dataJson.optInt("bckstg_time"));

          authenData.setEnableVideo(dataJson.optBoolean(
              "video_call_waiting", true));
          authenData.setEnableVoice(dataJson.optBoolean(
              "voice_call_waiting", true));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}
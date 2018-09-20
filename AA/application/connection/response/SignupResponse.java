package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.ui.account.AuthenticationData;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupResponse extends Response {

  private static final long serialVersionUID = -2161701300158961654L;
  private AuthenticationData authenticationData;
  private String blockedUserList;
  private String homePage;
  private String email;
  private String password;

  private boolean isEnableGetFreePoint;
  private String switchBrowserVersion;
  private boolean isTurnOffUserInfo;
  private boolean isShowNews;

  public SignupResponse(ResponseData responseData) {
    super(responseData);
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public AuthenticationData getAuthenticationData() {
    return authenticationData;
  }

  public void setAuthenticationData(AuthenticationData authenticationData) {
    this.authenticationData = authenticationData;
  }

  public String getBlockedUserList() {
    return blockedUserList;
  }

  public void setBlockedUserList(String blockedUserList) {
    this.blockedUserList = blockedUserList;
  }

  public String getHomePage() {
    return homePage;
  }

  public void setHomePage(String homePage) {
    this.homePage = homePage;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        mCode = jsonObject.getInt("code");
      }
      if (jsonObject.has("data")) {
        authenticationData = new AuthenticationData();

        JSONObject dataJson = jsonObject.getJSONObject("data");
        if (dataJson.has("token")) {
          authenticationData.setToken(dataJson.getString("token"));
        }
        if (dataJson.has("user_id")) {
          authenticationData.setUserId(dataJson.getString("user_id"));
        }
        if (dataJson.has("user_name")) {
          authenticationData.setUserName(dataJson
              .getString("user_name"));
        }
        if (dataJson.has("ava_id")) {
          authenticationData.setAvartarId(dataJson
              .getString("ava_id"));
        }
        if (dataJson.has("user_name")) {
          authenticationData.setUserName(dataJson
              .getString("user_name"));
        }
        if (dataJson.has("noti_num")) {
          authenticationData.setNumNotification(dataJson
              .getInt("noti_num"));
        }
        if (dataJson.has("chat_num")) {
          authenticationData
              .setNumMyChat(dataJson.getInt("chat_num"));
        }
        if (dataJson.has("point")) {
          authenticationData.setNumPoint(dataJson.getInt("point"));
        }
        if (dataJson.has("frd_num")) {
          authenticationData.setNumFriend(dataJson.getInt("frd_num"));
        }
        if (dataJson.has("fav_num")) {
          authenticationData.setNumFavourite(dataJson
              .getInt("fav_num"));
        }
        if (dataJson.has("fvt_num")) {
          authenticationData.setNumFavouriteMe(dataJson
              .getInt("fvt_num"));
        }
        if (dataJson.has("gender")) {
          authenticationData.setGender(dataJson.getInt("gender"));
        }
        // Blocked Users List
        if (dataJson.has("bcklst")) {
          String tmp = dataJson.getString("bcklst");
          tmp = tmp.subSequence(1, tmp.length() - 1).toString();
          setBlockedUserList(tmp);
        }
        if (dataJson.has("unlck_fvt")) {
          authenticationData.setUnlockFavoritePoints(dataJson
              .getInt("unlck_fvt"));
        }
        if (dataJson.has("unlck_chk_out")) {
          authenticationData.setUnlockWhoCheckMeOutPoints(dataJson
              .getInt("unlck_chk_out"));
        }
        if (dataJson.has("day_bns_pnt")) {
          authenticationData.setDailyBonusPoints(dataJson
              .getInt("day_bns_pnt"));
        }
        if (dataJson.has("save_img_pnt")) {
          authenticationData.setSaveImagePoints(dataJson
              .getInt("save_img_pnt"));
        }
        if (dataJson.has("unlck_chk_out_pnt")) {
          authenticationData.setUnlockWhoCheckMeOutPoints(dataJson
              .getInt("unlck_chk_out_pnt"));
        }
        if (dataJson.has("unlck_fvt_pnt")) {
          authenticationData.setUnlockFavoritePoints(dataJson
              .getInt("unlck_fvt_pnt"));
        }
        if (dataJson.has("onl_alt_pnt")) {
          authenticationData.setOnlineAlertPoints(dataJson
              .getInt("onl_alt_pnt"));
        }
        if (dataJson.has("wink_bomb_pnt")) {
          authenticationData.setWinkBombPoints(dataJson
              .getInt("wink_bomb_pnt"));
        }
        if (dataJson.has("ivt_frd_pnt")) {
          authenticationData.setInviteFriendPoints(dataJson
              .getInt("ivt_frd_pnt"));
        }
        if (dataJson.has("rate_distri_point")) {
          authenticationData.setRateDistributionPoints(dataJson
              .getDouble("rate_distri_point"));
        }
        if (dataJson.has("wink_bomb_num")) {
          authenticationData.setWinkBombPoints(dataJson
              .getInt("wink_bomb_num"));
        }
        if (dataJson.has("ivt_url")) {
          authenticationData.setInviteUrl(dataJson
              .getString("ivt_url"));
        }
        if (dataJson.has("home_page_url")) {
          setHomePage(dataJson.getString("home_page_url"));
        }
        if (dataJson.has("look_time")) {
          authenticationData.setLookAtMeTime(String.valueOf(dataJson
              .getInt("look_time")));
        }

        if (dataJson.has("email")) {
          setEmail(dataJson.getString("email"));
        }

        if (dataJson.has("pwd")) {
          setPassword(dataJson.getString("pwd"));
        }

        if (dataJson.has("finish_register_flag")) {
          authenticationData.setFinishRegister(dataJson
              .getInt("finish_register_flag"));
        }

        if (dataJson.has("verification_flag")) {
          authenticationData.setVerificationFlag(dataJson
              .getInt("verification_flag"));
        }

        if (dataJson.has("update_email_flag")) {
          authenticationData.setUpdateEmail(dataJson
              .getInt("update_email_flag") == 1);
        }

        if (dataJson.has("bckstg_pri")) {
          authenticationData.setBackstagePrice(dataJson
              .getInt("bckstg_pri"));
        }

        if (dataJson.has("bckstg_bonus")) {
          authenticationData.setBackstageBonus(dataJson
              .getInt("bckstg_bonus"));
        }

        if (dataJson.has("comment_buzz_pnt")) {
          authenticationData.setCommentPoint(dataJson
              .getInt("comment_buzz_pnt"));
        }

        if (dataJson.has("chat_pnt")) {
          authenticationData
              .setChatPoint(dataJson.getInt("chat_pnt"));
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

        authenticationData.setShowPopupNews(this.isShowNews);
        authenticationData.setCheckoutTime(dataJson
            .optInt("chk_out_time"));
        authenticationData
            .setFavouriteTime(dataJson.optInt("fav_time"));
        authenticationData.setBackstageTime(dataJson
            .optInt("bckstg_time"));
        authenticationData.setEnableVideo(dataJson.optBoolean(
            "video_call_waiting", true));
        authenticationData.setEnableVoice(dataJson.optBoolean(
            "voice_call_waiting", true));
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}
package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.ui.account.AuthenticationData;
import org.json.JSONException;
import org.json.JSONObject;

public class SendCodeResponse extends Response {

  private static final long serialVersionUID = 189435677194591252L;
  private AuthenticationData authenticationData;
  private String blockedUserList;
  private boolean isShowNews;

  public SendCodeResponse(ResponseData responseData) {
    super(responseData);
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public boolean isShowNews() {
    return isShowNews;
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

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          authenticationData = new AuthenticationData();

          JSONObject dataJson = jsonObject.getJSONObject("data");
          if (dataJson.has("user_id")) {
            authenticationData.setUserId(dataJson
                .getString("user_id"));
          }

          if (dataJson.has("user_name")) {
            authenticationData.setUserName(dataJson
                .getString("user_name"));
          }

          if (dataJson.has("ava_id")) {
            authenticationData.setAvartarId(dataJson
                .getString("ava_id"));
          }

          if (dataJson.has("token")) {
            authenticationData
                .setToken(dataJson.getString("token"));
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
            authenticationData.setNumMyChat(dataJson
                .getInt("chat_num"));
          }

          if (dataJson.has("point")) {
            authenticationData
                .setNumPoint(dataJson.getInt("point"));
          }

          if (dataJson.has("frd_num")) {
            authenticationData.setNumFriend(dataJson
                .getInt("frd_num"));
          }

          if (dataJson.has("fav_num")) {
            authenticationData.setNumFavourite(dataJson
                .getInt("fav_num"));
          }

          if (dataJson.has("gender")) {
            authenticationData.setGender(dataJson.getInt("gender"));
          }

          // Blocked Users List
          if (dataJson.has("backlst")) {
            String tmp = dataJson.getString("backlst");
            tmp = tmp.subSequence(1, tmp.length() - 1).toString();
            setBlockedUserList(tmp);
          }

          if (dataJson.has("unlck_fvt")) {
            authenticationData.setUnlockFavoritePoints(dataJson
                .getInt("unlck_fvt"));
          }

          if (dataJson.has("unlck_chk_out")) {
            authenticationData
                .setUnlockWhoCheckMeOutPoints(dataJson
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
            authenticationData
                .setUnlockWhoCheckMeOutPoints(dataJson
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

          if (dataJson.has("look_time")) {
            authenticationData.setLookAtMeTime(String
                .valueOf(dataJson.getInt("look_time")));
          }

          if (dataJson.has("update_email_flag")) {
            authenticationData.setUpdateEmail(dataJson
                .getInt("update_email_flag") == 1);
          }
          if (dataJson.has("finish_register_flag")) {
            authenticationData.setFinishRegister(dataJson
                .getInt("finish_register_flag"));
          }

          if (dataJson.has("turn_off_show_news_android")) {
            this.isShowNews = !dataJson.getBoolean("turn_off_show_news_android");
          } else {
            this.isShowNews = true;
          }

          authenticationData.setShowPopupNews(this.isShowNews);
          authenticationData.setCheckoutTime(dataJson
              .optInt("chk_out_time"));
          authenticationData.setFavouriteTime(dataJson
              .optInt("fav_time"));
          authenticationData.setBackstageTime(dataJson
              .optInt("bckstg_time"));
          authenticationData.setEnableVideo(dataJson.optBoolean(
              "video_call_waiting", true));
          authenticationData.setEnableVoice(dataJson.optBoolean(
              "voice_call_waiting", true));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }

  }
}
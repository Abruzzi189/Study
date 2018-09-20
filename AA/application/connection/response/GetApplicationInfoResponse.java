package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONObject;

public class GetApplicationInfoResponse extends Response {

  private static final long serialVersionUID = -5461597122287912609L;
  private String switchBrowserVersion;
  private boolean isSwitchBrowser;
  private boolean isLoginByAnotherSystem;
  private boolean isGetFreePoint;
  private boolean isTurnOffUserInfo;
  private boolean isShowNews;

  //#8583 : update by ThoNH
  private boolean force_updating;
  private String url_web;
  private String app_version;

  public GetApplicationInfoResponse(ResponseData responseData) {
    super(responseData);
  }

  public boolean isForce_updating() {
    return force_updating;
  }

  public void setForce_updating(boolean force_updating) {
    this.force_updating = force_updating;
  }

  public String getUrl_web() {
    return url_web;
  }

  public void setUrl_web(String url_web) {
    this.url_web = url_web;
  }

  public String getApp_version() {
    return app_version;
  }

  public void setApp_version(String app_version) {
    this.app_version = app_version;
  }

  public String getSwitchBrowserVersion() {
    return switchBrowserVersion;
  }

  public boolean isSwitchBrowser() {
    return isSwitchBrowser;
  }

  public boolean isLoginByAnotherSystem() {
    return isLoginByAnotherSystem;
  }

  public boolean isGetFreePoint() {
    return isGetFreePoint;
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
      if (jsonObject == null) {
        return;
      }
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        if (dataJson.has("switch_browser_android_version")) {
          this.switchBrowserVersion = dataJson
              .getString("switch_browser_android_version");
        }

        if (dataJson.has("switch_browser_android")) {
          this.isSwitchBrowser = !dataJson
              .getBoolean("switch_browser_android");
        }

        if (dataJson.has("login_by_mocom_android")) {
          this.isLoginByAnotherSystem = !dataJson
              .getBoolean("login_by_mocom_android");
        }

        if (dataJson.has("get_free_point_android")) {
          this.isGetFreePoint = !dataJson
              .getBoolean("get_free_point_android");
        }

        if (dataJson.has("turn_off_user_info_android")) {
          this.isTurnOffUserInfo = !dataJson.
              getBoolean("turn_off_user_info_android");
        }
        if (dataJson.has("turn_off_show_news_android")) {
          this.isShowNews = !dataJson.getBoolean("turn_off_show_news_android");
        }

        if (dataJson.has("url_web")) {
          this.url_web = dataJson.getString("url_web");
        }

        if (dataJson.has("app_version")) {
          this.app_version = dataJson.getString("app_version");
        }

        if (dataJson.has("force_updating")) {
          this.force_updating = dataJson.getBoolean("force_updating");
        } else {
          this.isShowNews = true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}
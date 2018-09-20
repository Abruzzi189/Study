package com.application.connection.request;

import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;

public class SignupRequest extends RequestParams {

  private static final long serialVersionUID = 5652668618094102885L;
  @SerializedName("bir")
  private String birthday;
  @SerializedName("gender")
  private int gender;
  @SerializedName("device_id")
  private String device_id;
  @SerializedName("device_type")
  // Integer : 0 : IOS | 1: Android
  private int device_type;
  @SerializedName("login_time")
  private String login_time;
  @SerializedName("notify_token")
  private String notify_token;
  @SerializedName("application_version")
  private String application_version;
  @SerializedName("gps_adid")
  private String gps_adid;
  @SerializedName("device_name")
  private String device_name;
  @SerializedName("os_version")
  private String os_version;
  @SerializedName("adid")
  private String adid;
  @SerializedName("application_name")
  private String appName;
  @SerializedName("application")
  private int application;


  public SignupRequest(String birthday, int gender, String deviceId,
      String notify_token, String loginTime, String appVersion, String gps_adid, String device_name,
      String os_version, String adid) {
    this.api = "reg";
    this.birthday = birthday;
    this.gender = gender;
    this.device_id = deviceId;
    this.notify_token = notify_token;
    this.login_time = loginTime;
    this.device_type = 1;
    this.application_version = appVersion;
    this.gps_adid = gps_adid;
    this.device_name = device_name;
    this.os_version = os_version;
    this.adid = adid;
    this.application = Constants.APPLICATION_TYPE;

  }

  public SignupRequest(String birthday, int gender, String deviceId,
      String notify_token, String loginTime, String appVersion, String gps_adid, String device_name,
      String os_version, String adid, String appName) {
    this.api = "reg";
    this.birthday = birthday;
    this.gender = gender;
    this.device_id = deviceId;
    this.notify_token = notify_token;
    this.login_time = loginTime;
    this.device_type = 1;
    this.application_version = appVersion;
    this.gps_adid = gps_adid;
    this.device_name = device_name;
    this.os_version = os_version;
    this.adid = adid;
    this.appName = appName;
    this.application = Constants.APPLICATION_TYPE;
  }
}
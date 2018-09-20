package com.application.connection.request;

import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;

public abstract class LoginRequest extends RequestParams {

  private static final long serialVersionUID = -8768157153263016151L;
  @SerializedName("device_id")
  private String device_id;
  @SerializedName("notify_token")
  private String notify_token;
  // Integer : 0 : IOS | 1: Android
  @SerializedName("device_type")
  private int device_type;
  @SerializedName("login_time")
  private String login_time;
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
  private String application_name;
  @SerializedName("application")
  private int application;


  public LoginRequest(String device_id, String notifyToken, String logTime,
      String appVer, String gps_adid, String device_name, String os_version, String adid,
      String application_name) {
    this.device_id = device_id;
    this.notify_token = notifyToken;
    this.device_type = 1;
    this.login_time = logTime;
    this.application_version = appVer;
    this.gps_adid = gps_adid;
    this.device_name = device_name;
    this.os_version = os_version;
    this.adid = adid;
    this.application_name = application_name;
    this.application = Constants.APPLICATION_TYPE;

  }
}
package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SendCodeRequest extends RequestParams {

  private static final long serialVersionUID = 1214675793636212559L;

  @SerializedName("device_id")
  private String device_id;
  @SerializedName("email")
  private String email;
  @SerializedName("vrf_code")
  private String vrf_code;
  @SerializedName("new_pwd")
  private String new_pwd;
  @SerializedName("login_time")
  private String login_time;
  @SerializedName("notify_token")
  private String notify_token;
  @SerializedName("device_type")
  private int device_type;
  @SerializedName("original_pwd")
  private String originalPwd;
  @SerializedName("application_version")
  private String application_version;

  public SendCodeRequest(String device_id, String email, String vrf_code,
      String originalPwd, String new_pwd, String login_time,
      String notify_token, String appVersion) {
    super();
    this.api = "chg_pwd_fgt";
    this.device_id = device_id;
    this.email = email;
    this.vrf_code = vrf_code;
    this.new_pwd = new_pwd;
    this.login_time = login_time;
    this.notify_token = notify_token;
    this.device_type = 1;
    this.originalPwd = originalPwd;
    this.application_version = appVersion;
  }
}
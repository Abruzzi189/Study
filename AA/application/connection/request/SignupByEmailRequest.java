package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SignupByEmailRequest extends SignupRequest {

  private static final long serialVersionUID = 5652668618094102885L;
  @SerializedName("email")
  private String email;
  @SerializedName("pwd")
  private String pwd;
  @SerializedName("original_pwd")
  private String original_pwd;

  public SignupByEmailRequest(String email, String pwd, String originalPwd,
      String birthday, int gender, String device_id, String login_time,
      String notify_token, String inviteCode, String appVersion, String gps_adid,
      String device_name, String os_version, String adid) {
    super(birthday, gender, device_id, notify_token, login_time, appVersion, gps_adid, device_name,
        os_version, adid);
    this.email = email;
    this.pwd = pwd;
    this.original_pwd = originalPwd;
  }
}
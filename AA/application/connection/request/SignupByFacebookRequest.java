package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SignupByFacebookRequest extends SignupRequest {

  private static final long serialVersionUID = 5652668618094102885L;
  @SerializedName("fb_id")
  private String fb_id;
  @SerializedName("ivt_code")
  private String inviteCode;

  public SignupByFacebookRequest(String fb_id, String inviteCode,
      String birthday, int gender, String device_id, String login_time,
      String notify_token, String appVersion, String gps_adid, String device_name,
      String os_version, String adid) {
    super(birthday, gender, device_id, login_time, notify_token, appVersion, gps_adid, device_name,
        os_version, adid);
    this.fb_id = fb_id;
    this.inviteCode = inviteCode;
  }

  public SignupByFacebookRequest(String fb_id, String birthday, int gender,
      String device_id, String login_time, String notify_token,
      String appVersion, String gps_adid, String device_name, String os_version, String adid) {
    super(birthday, gender, device_id, login_time, notify_token, appVersion, gps_adid, device_name,
        os_version, adid);
    this.fb_id = fb_id;
  }

  public SignupByFacebookRequest(String fb_id, String birthday, int gender,
      String device_id, String login_time, String notify_token,
      String appVersion, String gps_adid, String device_name, String os_version, String adid,
      String appName) {
    super(birthday, gender, device_id, login_time, notify_token, appVersion, gps_adid, device_name,
        os_version, adid, appName);
    this.fb_id = fb_id;
  }
}
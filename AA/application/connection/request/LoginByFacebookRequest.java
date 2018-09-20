package com.application.connection.request;

public final class LoginByFacebookRequest extends LoginRequest {

  private static final long serialVersionUID = -7060477858271184742L;
  protected String fb_id;

  public LoginByFacebookRequest(String fb_id, String device_id,
      String notify_token, String login_time, String appVersion, String gps_adid,
      String device_name, String os_version, String adid, String application_name) {
    super(device_id, notify_token, login_time, appVersion, gps_adid, device_name, os_version, adid,
        application_name);
    this.api = "login";
    this.fb_id = fb_id;
  }
}
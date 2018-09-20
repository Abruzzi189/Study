package com.application.connection.request;

public final class LoginByEmailRequest extends LoginRequest {

  private static final long serialVersionUID = -7060477858271184742L;
  protected String email;
  protected String pwd;

  public LoginByEmailRequest(String email, String password, String device_id,
      String notify_token, String login_time, String appVersion, String gps_adid,
      String device_name, String os_version, String adid, String application_name) {
    super(device_id, notify_token, login_time, appVersion, gps_adid, device_name, os_version, adid,
        application_name);
    this.api = "login";
    this.email = email;
    this.pwd = password;
  }
}
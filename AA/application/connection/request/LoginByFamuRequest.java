package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class LoginByFamuRequest extends LoginRequest {

  private static final long serialVersionUID = -3548592354092023195L;
  @SerializedName("famu_id")
  private String famu_id;

  public LoginByFamuRequest(String famu_id, String device_id,
      String notify_token, String login_time, String appVersion, String gps_adid,
      String device_name, String os_version, String adid, String application_name) {
    super(device_id, notify_token, login_time, appVersion, gps_adid, device_name, os_version, adid,
        application_name);
    this.api = "login_by_famu";
    this.famu_id = famu_id;
  }
}
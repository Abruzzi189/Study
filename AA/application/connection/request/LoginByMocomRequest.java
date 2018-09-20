package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class LoginByMocomRequest extends LoginRequest {

  private static final long serialVersionUID = -3548592354092023195L;
  @SerializedName("mocom_id")
  private String mocom_id;

  public LoginByMocomRequest(String mocom_id, String device_id,
      String notify_token, String login_time, String appVersion, String gps_adid,
      String device_name, String os_version, String adid, String application_name) {
    super(device_id, notify_token, login_time, appVersion, gps_adid, device_name, os_version, adid,
        application_name);
    this.api = "login_by_mocom";
    this.mocom_id = mocom_id;
  }
}
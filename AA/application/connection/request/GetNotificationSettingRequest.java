package com.application.connection.request;

public class GetNotificationSettingRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -2092057341028504733L;

  public GetNotificationSettingRequest(String token) {
//		this.api = "get_noti_set_ver_2";
    this.api = "get_noti_set";
    this.token = token;
  }
}

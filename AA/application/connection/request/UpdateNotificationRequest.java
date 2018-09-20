package com.application.connection.request;

public class UpdateNotificationRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6510229527681915274L;
  public String notify_token;
  public int device_type;

  public UpdateNotificationRequest(String token, String notify_token) {
    this.api = "upd_noti_token";
    this.notify_token = notify_token;
    this.device_type = 1;
    this.token = token;
  }
}

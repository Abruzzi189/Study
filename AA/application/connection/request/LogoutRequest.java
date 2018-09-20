package com.application.connection.request;

public class LogoutRequest extends RequestParams {

  private static final long serialVersionUID = -7060477858271184742L;

  public String notify_token;
  public int device_type;

  public LogoutRequest(String token, String notify_token) {
    super();
    this.api = "logout";
    this.token = token;
    this.device_type = 1;
    this.notify_token = notify_token;
  }
}

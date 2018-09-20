package com.application.connection.request;

public class ListOnlineAlertRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ListOnlineAlertRequest(String token) {
    super();
    this.api = "list_online_alert";
    this.token = token;
  }

}

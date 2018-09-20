package com.application.connection.request;

public class GetLookInfoRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1670078909765474500L;

  public GetLookInfoRequest(String token) {
    this.api = "get_look_inf";
    this.token = token;
  }
}

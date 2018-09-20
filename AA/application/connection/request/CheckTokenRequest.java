package com.application.connection.request;

public class CheckTokenRequest extends RequestParams {

  private static final long serialVersionUID = 8744030392515324102L;

  public CheckTokenRequest(String token) {
    super();
    this.api = "check_token";
    this.token = token;
  }
}
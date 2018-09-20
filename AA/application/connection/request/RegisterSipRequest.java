package com.application.connection.request;

public class RegisterSipRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6923223615036570975L;
  public String pwd;

  public RegisterSipRequest(String token, String pwd) {
    this.token = token;
    this.pwd = pwd;
    this.api = "reg_sip";
  }
}

package com.application.connection.request;

public class SendEmailRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 7682747025609049924L;

  private String email;

  public SendEmailRequest(String email) {
    this.api = "fgt_pwd";
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}

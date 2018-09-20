package com.application.connection.request;


public class MyPageInfoRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -1891599814555623894L;

  public MyPageInfoRequest(String token) {
    super();
    this.api = "get_my_page_inf";
    this.token = token;
  }
}

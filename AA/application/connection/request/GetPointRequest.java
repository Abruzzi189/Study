package com.application.connection.request;

public class GetPointRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -5694895759073629892L;

  public GetPointRequest(String token) {
    this.token = token;
    this.api = "get_point";
  }

}

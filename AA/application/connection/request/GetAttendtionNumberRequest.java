package com.application.connection.request;

public class GetAttendtionNumberRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 13546835154653L;

  public GetAttendtionNumberRequest(String token) {
    super();
    this.api = "get_att_num";
    this.token = token;
  }

}

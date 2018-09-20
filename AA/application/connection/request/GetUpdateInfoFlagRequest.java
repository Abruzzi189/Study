package com.application.connection.request;

public class GetUpdateInfoFlagRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1851388413733139720L;

  public GetUpdateInfoFlagRequest(String token) {
    super();
    this.api = "get_update_info_flag";
    this.token = token;
  }
}

package com.application.connection.request;

public class UpdateInfoFlagRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UpdateInfoFlagRequest(String token) {
    super();
    this.api = "get_update_info_flag";
    this.token = token;
  }
}

package com.application.connection.request;

public class GetExtraPageRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6052868934701937706L;

  public GetExtraPageRequest(String token) {
    this.api = "get_ext_page";
    this.token = token;
  }
}

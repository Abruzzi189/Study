package com.application.connection.request;

public class WhoCheckOutRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 6103569310830650465L;
  public int skip;
  public int take;

  public WhoCheckOutRequest(String token, int skip, int take) {
    this.api = "lst_chk_out";
    this.token = token;
    this.skip = skip;
    this.take = take;
  }

}

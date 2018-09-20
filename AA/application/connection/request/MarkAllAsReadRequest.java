package com.application.connection.request;

public class MarkAllAsReadRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -7200630616845768341L;

  public MarkAllAsReadRequest(String token) {
    super();
    this.api = "mark_all_read";
    this.token = token;
  }
}

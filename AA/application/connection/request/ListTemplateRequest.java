package com.application.connection.request;

public class ListTemplateRequest extends RequestParams {

  private static final long serialVersionUID = 6131211619955774043L;

  public ListTemplateRequest(String token) {
    super();
    this.api = "list_template";
    this.token = token;
  }
}

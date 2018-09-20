package com.application.connection.request;

/**
 * Created by HungHN on 5/30/2016.
 */
public class GetBackendSettingRequest extends RequestParams {

  public GetBackendSettingRequest(String token) {
    super();
    this.api = "get_backend_setting";
    this.token = token;
  }
}

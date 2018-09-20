package com.application.connection.request;

public class PointPackageRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6695690405532435267L;

  int pro_type = 1;

  public PointPackageRequest(String token) {
    this.api = "lst_point_pck";
    this.token = token;
  }
}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class InstallCountRequest extends RequestParams {

  private static final long serialVersionUID = 8648373406565729778L;

  // int : 0 : IOS | 1: Android
  @SerializedName("device_type")
  private int device_type;
  @SerializedName("unique_number")
  private String unique_number;

  public InstallCountRequest(String androidId) {
    super();
    this.api = "install_application";
    this.unique_number = androidId;
    this.device_type = 1;
  }
}
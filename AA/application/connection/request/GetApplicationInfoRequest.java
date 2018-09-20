package com.application.connection.request;

import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;

public class GetApplicationInfoRequest extends RequestParams {

  private static final long serialVersionUID = -3927447634714744951L;

  @SerializedName("application")
  private int application;

  public GetApplicationInfoRequest() {
    super();
    this.api = "get_inf_for_application";
    this.application = Constants.APPLICATION_TYPE;
  }
}
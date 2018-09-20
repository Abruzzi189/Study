package com.application.connection.request;

public class GetMeetPeopleSearchSettingRequest extends RequestParams {

  private static final long serialVersionUID = 7801151002210382411L;

  // TODO The value of the field GetMeetPeopleSettingRequest.token is not used

  public GetMeetPeopleSearchSettingRequest(String token) {
    super();
    this.api = "get_mp_setting";
    this.token = token;
  }
}
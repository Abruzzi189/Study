package com.application.connection.request;


public class MeetPeopleSettingRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 3719346390147935758L;

  public MeetPeopleSettingRequest(String token) {
    super();
    this.api = "get_mp_setting";
    this.token = token;
  }
}

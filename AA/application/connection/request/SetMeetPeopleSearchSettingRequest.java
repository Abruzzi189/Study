package com.application.connection.request;

import com.application.constant.Constants;

public class SetMeetPeopleSearchSettingRequest extends RequestParams {

  private static final long serialVersionUID = 7801151002210382411L;
  private int show_me = 0;
  private int inters_in = 0;
  private int lower_age = Constants.SEARCH_SETTING_AGE_MIN_LIMIT;
  private int upper_age = Constants.SEARCH_SETTING_AGE_MAX_LIMIT;
  private int[] ethn;
  private int distance = 0;

  public SetMeetPeopleSearchSettingRequest(String token, int show_me,
      int inters_in, int lower_age, int upper_age, int distance) {
    super();
    this.api = "up_mp_setting";
    this.token = token;
    this.show_me = show_me;
    this.inters_in = inters_in;
    this.lower_age = lower_age;
    this.upper_age = upper_age;
    this.distance = distance;
  }

  public SetMeetPeopleSearchSettingRequest(String token, int show_me,
      int inters_in, int location, int lower_age, int upper_age,
      int[] ethn) {
    super();
    this.api = "up_mp_setting";
    this.token = token;
    this.show_me = show_me;
    this.inters_in = inters_in;
    this.distance = location;
    this.lower_age = lower_age;
    this.upper_age = upper_age;
    this.ethn = ethn;
  }
}
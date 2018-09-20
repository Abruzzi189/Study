package com.application.connection.request;

/**
 * @author tungdx
 */
public class PeopleListRequest extends RequestParams {
  // for API 18,20,21,22
  public static final String FRIEND = "lst_frd";
  public static final String FAVORITE = "lst_fav";
  public static final String FAVORITED = "lst_fvt";
  public static final String WHO_CHECK_YOU_OUT = "lst_chk_out";
  /**
   *
   */
  private static final long serialVersionUID = -5057958178302677856L;
  private String req_user_id = null;
  private int skip;
  private int take;

  public PeopleListRequest(String api, String token, int skip, int take) {
    super();
    this.api = api;
    this.token = token;
    this.skip = skip;
    this.take = take;
  }

  public PeopleListRequest(String api, String token, String req_user_id, int skip, int take) {
    super();
    this.api = api;
    this.token = token;
    this.req_user_id = req_user_id;
    this.skip = skip;
    this.take = take;
  }

  public int getSkip() {
    return skip;
  }

  public void setSkip(int skip) {
    this.skip = skip;
  }

  public int getTake() {
    return take;
  }

  public void setTake(int take) {
    this.take = take;
  }

  public String getReq_user_id() {
    return req_user_id;
  }

  public void setReq_user_id(String req_user_id) {
    this.req_user_id = req_user_id;
  }
}

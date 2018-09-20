package com.application.connection.request;

public class ConnectionRequest extends RequestParams {

  public static final String FAVORITE = "lst_fav";
  public static final String WHO = "lst_chk_out";
  /**
   *
   */
  private static final long serialVersionUID = -5057958178302677856L;
  private int skip;
  private int take;
//	private int req_user_id;

  public ConnectionRequest(String api, String token, int skip, int take) {
    super();
    this.api = api;
    this.token = token;
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

//	public int getReq_user_id() {
//		return req_user_id;
//	}
//
//	public void setReq_user_id(int req_user_id) {
//		this.req_user_id = req_user_id;
//	}

}

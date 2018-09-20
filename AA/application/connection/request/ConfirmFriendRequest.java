package com.application.connection.request;

public class ConfirmFriendRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 173649463233828277L;

  private String req_user_id;
  private int cnf_stt;

  public ConfirmFriendRequest(String token, String req_user_id, int cnf_stt) {
    super();
    this.api = "cnf_frd";
    this.token = token;
    this.req_user_id = req_user_id;
    this.cnf_stt = cnf_stt;
  }

  public String getReq_user_id() {
    return req_user_id;
  }

  public void setReq_user_id(String req_user_id) {
    this.req_user_id = req_user_id;
  }

  public int getCnf_stt() {
    return cnf_stt;
  }

  public void setCnf_stt(int cnf_stt) {
    this.cnf_stt = cnf_stt;
  }

}

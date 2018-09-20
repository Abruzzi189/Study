package com.application.connection.request;

public class ChangePasswordRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 5348959062852609958L;
  private String old_pwd;
  private String new_pwd;
  private String original_pwd;

  public ChangePasswordRequest(String token, String old_pwd,
      String originalpwd, String new_pwd) {
    super();
//		this.api = "chg_pwd_ver_2";
    this.api = "chg_pwd";
    this.token = token;
    this.old_pwd = old_pwd;
    this.new_pwd = new_pwd;
    this.original_pwd = originalpwd;
  }

  public String getOld_pwd() {
    return old_pwd;
  }

  public String getNew_pwd() {
    return new_pwd;
  }

  public String getOriginal_pwd() {
    return original_pwd;
  }

}

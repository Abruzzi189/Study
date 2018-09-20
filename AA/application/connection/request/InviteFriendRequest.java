package com.application.connection.request;

public class InviteFriendRequest extends RequestParams {

  public static final int TYPE_EMAIL = 1;
  public static final int TYPE_SMS = 2;
  public static final int TYPE_FACEBOOK = 3;
  /**
   *
   */
  private static final long serialVersionUID = 6083549923313062348L;
  private int ivt_type;
  private String[] lst_ivt_user;

  public InviteFriendRequest(String token, int ivt_type, String[] lst_ivt_user) {
    super();
    this.api = "ivt_frd";
    this.ivt_type = ivt_type;
    this.lst_ivt_user = lst_ivt_user;
  }

  public int getIvt_type() {
    return ivt_type;
  }

  public void setIvt_type(int ivt_type) {
    this.ivt_type = ivt_type;
  }

  public String[] getLst_ivt_user() {
    return lst_ivt_user;
  }

  public void setLst_ivt_user(String[] lst_ivt_user) {
    this.lst_ivt_user = lst_ivt_user;
  }


}

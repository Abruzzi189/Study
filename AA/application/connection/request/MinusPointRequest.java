package com.application.connection.request;

public class MinusPointRequest extends RequestParams {

  public static final int CHAT = 1;
  public static final int VOICE = 2;
  public static final int VIDEO = 3;

  public static final int CHECK_POINT = 1;
  public static final int NOT_CHECK_POINT = 0;

  /**
   *
   */
  private static final long serialVersionUID = 8517099334955826631L;
  public int type;
  public String partner_id;
  public int check;

  public MinusPointRequest(String token, int type, String partnerId, int check) {
    this.api = "minus_point";
    this.token = token;
    this.type = type;
    this.partner_id = partnerId;
    this.check = check;
  }
}

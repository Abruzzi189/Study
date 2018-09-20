package com.application.connection.request;

public class SaveNotificationSettingRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 3262536255457660838L;
  public int noti_buzz;
  public int andg_alt;
  public int chat;
  public int noti_chk_out;

  public SaveNotificationSettingRequest(String token, int noti_buzz,
      int andg_alt, int chat, int noti_check_out) {
    super();
    this.api = "noti_set";
    this.token = token;
    this.noti_buzz = noti_buzz;
    this.andg_alt = andg_alt;
    this.chat = chat;
    this.noti_chk_out = noti_check_out;
  }

}

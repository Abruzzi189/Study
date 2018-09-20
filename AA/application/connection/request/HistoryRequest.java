package com.application.connection.request;

public class HistoryRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -2116589942681642684L;
  public String frd_id;
  public String time_stamp;
  public int take;

  public HistoryRequest(String token, String friendId, String timeStamp,
      int take) {
    this.api = "get_chat_history";
    this.token = token;
    this.frd_id = friendId;
    this.time_stamp = timeStamp;
    this.take = take;
  }

}

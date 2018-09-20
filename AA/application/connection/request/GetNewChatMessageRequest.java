package com.application.connection.request;

public class GetNewChatMessageRequest extends RequestParams {

  private static final long serialVersionUID = -2116589942681642684L;
  public String frd_id;
  public String time_stamp;

  public GetNewChatMessageRequest(String token, String friendId,
      String timeStamp) {
    this.api = getApi();
    this.token = token;
    this.frd_id = friendId;
    this.time_stamp = timeStamp;
  }

  public GetNewChatMessageRequest(String token, String friendId) {
    this.api = getApi();
    this.token = token;
    this.frd_id = friendId;
  }

  @Override
  public String getApi() {
    return "get_new_chat_message";
  }
}
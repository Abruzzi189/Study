package com.application.chat;

import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class AuthenticationMessage extends MessageClient {

  /**
   *
   */
  public static final long serialVersionUID = -4183830579030268317L;
  private boolean authen = false;

  public AuthenticationMessage(Message message) {
    super(message);
    String value = message.value;
    if (value.equals("s")) {
      authen = true;
    } else if (value.equals("f")) {
      authen = false;
    }
  }

  public boolean isSuccess() {
    return authen;
  }

  public Message getMessage() {
    return message;
  }

}

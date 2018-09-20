package com.application.chat;

import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class PresentationMessage extends MessageClient {

  /**
   *
   */
  private static final long serialVersionUID = -204458159008122281L;

  public PresentationMessage(Message message) {
    super(message);
  }

  public boolean isOnline() {
    String value = message.value;
    if (value.equals("on")) {
      return true;
    }
    return false;
  }
}

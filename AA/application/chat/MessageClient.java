package com.application.chat;

import java.io.Serializable;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class MessageClient implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -508478908138453199L;

  protected Message message;

  public MessageClient(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }
}

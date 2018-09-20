package com.application.chat;

public class TextMessage extends ChatMessage {

  private String content;

  public TextMessage(String userId, String content) {
    super();
    this.userId = userId;
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}

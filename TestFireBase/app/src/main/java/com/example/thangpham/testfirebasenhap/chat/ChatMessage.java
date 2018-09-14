package com.example.thangpham.testfirebasenhap.chat;

public class ChatMessage {
  public String content;
  public long time;

  public ChatMessage(String content, long time) {
    this.content = content;
    this.time = time;
  }
  public ChatMessage(){

  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }
}

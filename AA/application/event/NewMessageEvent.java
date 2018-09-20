package com.application.event;

/**
 * Created by HungHN on 1/4/2016.
 */
public class NewMessageEvent {

  public static final int SHOW = 0;
  public static final int UPDATE = 1;
  public static final int CLEAR = 2;
  public int mode;
  public int mUnreadNumber = -1;
  public String userId = "";

  public NewMessageEvent(int mode, String userId, int mUnreadMessage) {
    this.mode = mode;
    this.mUnreadNumber = mUnreadMessage;
    this.userId = userId;
  }

  public NewMessageEvent(int mode) {
    this.mode = mode;
    this.mUnreadNumber = 0;
    this.userId = "";
  }

  public NewMessageEvent(int mode, int mUnreadMessage) {
    this.mode = mode;
    this.mUnreadNumber = mUnreadMessage;
    this.userId = "";
  }

  public NewMessageEvent(int mode, String userId) {
    this.mode = mode;
    this.userId = userId;
    this.mUnreadNumber = 0;
  }
}

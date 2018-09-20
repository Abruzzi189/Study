package com.application.event;

import com.application.entity.ConversationItem;
import java.util.List;

public class ConversationEvent {

  public static final int UPDATE = 0; // Update Conversation List
  public static final int REMOVE = 1; // Remove
  public static final int CHANGE = 2; // setDatachange
  public static final int CLEAR = 3;

  public List<ConversationItem> items;
  public int mode;
  public String userId;

  public ConversationEvent(int mode) {
    this.mode = mode;
  }

  public ConversationEvent(int mode, List<ConversationItem> items) {
    this.mode = mode;
    this.items = items;
  }

  public ConversationEvent(int mode, String userId) {
    this.mode = mode;
    this.userId = userId;
  }

}

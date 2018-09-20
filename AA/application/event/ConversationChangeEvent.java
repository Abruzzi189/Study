package com.application.event;

import com.application.entity.ConversationItem;
import java.util.List;

public class ConversationChangeEvent {

  private List<ConversationItem> items;

  public ConversationChangeEvent(List<ConversationItem> items) {
    this.items = items;
  }

  public List<ConversationItem> getConversationList() {
    return items;
  }
}

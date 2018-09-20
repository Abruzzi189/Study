package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import glas.bbsystem.R;


public class HeaderChatView extends LinearLayout {

  public HeaderChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public HeaderChatView(Context context) {
    super(context);
  }

  public void fillData(final ChatMessage chatMessage,
      HeaderViewHolder viewHolder) {
    if (viewHolder == null) {
      return;
    }
    if (chatMessage != null) {
      viewHolder.mTxtHeader.setText(chatMessage.getContent());
    }
  }

  public HeaderViewHolder fillHolder() {
    HeaderViewHolder headerHolder = new HeaderViewHolder();
    headerHolder.mTxtHeader = (TextView) findViewById(R.id.item_list_chat_header_txt);
    return headerHolder;
  }

}

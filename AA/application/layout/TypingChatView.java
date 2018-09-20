package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.ui.ChatFragment;
import glas.bbsystem.R;


public class TypingChatView extends BaseChatView {

  private TextView txtUserName;

  public TypingChatView(Context context, int contentRes) {
    super(context, contentRes, false);
    hideAllStuffView();
  }

  public TypingChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    txtUserName = (TextView) findViewById(R.id.name);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }
}
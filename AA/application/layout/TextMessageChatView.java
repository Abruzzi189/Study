package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.ui.ChatFragment;
import com.application.ui.customeview.EmojiTextView;
import glas.bbsystem.R;


public class TextMessageChatView extends BaseChatView {

  private EmojiTextView emojiTextView;
  private TextView txtUserName;

  public TextMessageChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TextMessageChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    txtUserName = (TextView) findViewById(R.id.name);
    emojiTextView = (EmojiTextView) findViewById(R.id.message);
    emojiTextView.setMaxWidth(mMaxWidthContent);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    Context appContext = chatFragment.getActivity().getApplicationContext();

    if (chatMessage.getMsgType().equals(ChatMessage.WINK)) {
      // wink
      String value = chatMessage.getContent();
      if (null != value && !"".equals(value)) {
        emojiTextView.setEmojiText(value, true);
        return;
      }
      if (chatMessage.isOwn()) {
        value = appContext.getString(R.string.message_wink_2,
            chatFragment.getNameUserToSend());
      } else {
        value = appContext.getString(R.string.message_wink,
            chatFragment.getNameUserToSend());
      }
      emojiTextView.setEmojiText(value, true);
    } else {
      // message
      if (!chatMessage.isOwn()) {
        emojiTextView.setEmojiText(chatMessage.getMessageReceived(),
            true);
      } else {
        emojiTextView.setEmojiText(chatMessage.getContent(), true);
      }
    }

    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }
}
package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.ui.ChatFragment;
import glas.bbsystem.R;

/**
 * Created by HungHN on 1/13/2016.
 */
public class RequestCallView extends BaseChatView {

  private TextView txtUserName;
  private TextView callContent;

  public RequestCallView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RequestCallView(Context context, int contentRes, boolean isSendMessage) {
    super(context, contentRes, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    txtUserName = (TextView) findViewById(R.id.name);
    callContent = (TextView) findViewById(R.id.message);
    callContent.setMaxWidth(mMaxWidthContent);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    String content = chatMessage.getContent();
    String msg;
    String name;
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
      name = chatFragment.getUserName();
    } else {
      name = chatFragment.getNameUserToSend();
    }

    if (content.equals(ChatMessage.CALLREQUEST_VIDEO)) {
      msg = getContext().getString(R.string.message_video_call_request, name);
    } else {
      msg = getContext().getString(R.string.message_voice_call_request,
          name);
    }
    // message
    callContent.setText(msg);
  }
}

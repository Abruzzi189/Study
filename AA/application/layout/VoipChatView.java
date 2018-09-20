package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.chat.ChatUtils.CallInfo;
import com.application.ui.ChatFragment;
import glas.bbsystem.R;


public class VoipChatView extends BaseChatView {

  private ImageView callIcon;
  private TextView callContent;
  private TextView txtUserName;

  public VoipChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VoipChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    txtUserName = (TextView) findViewById(R.id.name);
    callIcon = (ImageView) findViewById(R.id.image);
    callContent = (TextView) findViewById(R.id.title);
    callContent.setMaxWidth(mMaxWidthContent);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    Context appContext = chatFragment.getActivity().getApplicationContext();
    // parse info
    String content = chatMessage.getContent();
    CallInfo callInfo = ChatUtils.getCallInfo(content);
    // set info to view
    callIcon.setImageResource(ChatUtils.getVoipIcon(callInfo.voipType,
        chatMessage.isOwn()));
    callContent.setText(ChatUtils.getCallDuration(appContext, callInfo));
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }

  @Override
  public void fillData1(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData1(chatFragment, chatMessage);
    Context appContext = chatFragment.getActivity().getApplicationContext();
    // parse info
    String content = chatMessage.getContent();
    CallInfo callInfo = ChatUtils.getCallInfo(content);
    // set info to view
    callIcon.setImageResource(ChatUtils.getVoipIcon(callInfo.voipType,
        chatMessage.isOwn()));
    callContent.setText(ChatUtils.getCallDuration1(appContext, callInfo));
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }

}

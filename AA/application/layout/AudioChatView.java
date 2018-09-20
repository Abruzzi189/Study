package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.ui.ChatFragment;
import glas.bbsystem.R;


public class AudioChatView extends BaseFileChatView {

  private ImageView imageAudio;
  private TextView audioTime;
  private TextView txtUserName;
  LinearLayout audioLayout;
  public AudioChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AudioChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    imageAudio = (ImageView) findViewById(R.id.image);
    audioTime = (TextView) findViewById(R.id.timeAudio);
    txtUserName = (TextView) findViewById(R.id.name);
    audioLayout = (LinearLayout) findViewById(R.id.audio_layout);
  }

  @Override
  public void fillData(final ChatFragment chatFragment,
      final ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    audioTime.setText(chatMessage.getFileMessage().getTimeAudioHold()
        .getDisplay());
    // display state button audio
    if (chatMessage.getFileMessage().isPlay()) {
      imageAudio.setImageResource(R.drawable.ic_btn_pause_audio_chat);
    } else {
      imageAudio.setImageResource(R.drawable.ic_btn_play_audio_chat);
    }
    imageAudio.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (chatMessage.getFileMessage().isPlay()) {
          chatFragment.stopAdapterPlayAudio(chatMessage);
        } else {
          chatFragment.startAdapterPlayAudio(chatMessage);
        }
      }
    });

    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
//    audioLayout.setOnLongClickListener(v -> {
//      displayControlRemoveItem.onDisplayControlRemoveItem();
//      return true;
//    });

    imageAudio.setOnClickListener(v -> {
      if (chatMessage.getFileMessage().isPlay())
        chatFragment.stopAdapterPlayAudio(chatMessage);
      else
        chatFragment.startAdapterPlayAudio(chatMessage);
    });
  }
}

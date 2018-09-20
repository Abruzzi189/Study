package com.application.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.ui.notification.ManageOnlineAlertFragment;
import com.application.util.preferece.FavouritedPrefers;
import glas.bbsystem.R;


public class ChatMoreLayout extends LinearLayout implements OnClickListener {

  public static boolean isFavorited;
  private OnChatMoreListener chatMoreListener;
  private boolean isVoiceCallWaiting = false;
  private boolean isVideoCallWaiting = false;
  private boolean isChat = true;
  private int isAlt;
  private TextView mTxtVoiceCall;
  private TextView mTxtVideoCall;
  private TextView mTvAlertOnline;
  private FrameLayout flvideocall;
  private FrameLayout flvoicecall;

  public ChatMoreLayout(Context context, OnChatMoreListener chatMoreListener,
      String userId, boolean isVoiceCallWaiting,
      boolean isVideoCallWaiting, boolean isChat, int isAlt) {
    super(context);

    this.chatMoreListener = chatMoreListener;
    this.isVoiceCallWaiting = isVoiceCallWaiting;
    this.isVideoCallWaiting = isVideoCallWaiting;
    this.isChat = isChat;
    this.isAlt = isAlt;

    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.layout_chat_more_options, this, true);

    mTxtVoiceCall = (TextView) findViewById(R.id.btn_chat_more_voice_call);
    mTxtVideoCall = (TextView) findViewById(R.id.btn_chat_more_video_call);
    mTvAlertOnline = (TextView) findViewById(R.id.btn_chat_more_alert_online);
    flvideocall = (FrameLayout) findViewById(R.id.fl_chat_more_video_call);
    flvoicecall = (FrameLayout) findViewById(R.id.fl_chat_more_voice_call);

    if (!isVoiceCallWaiting) {
      mTxtVoiceCall.setCompoundDrawablesWithIntrinsicBounds(
          0, R.drawable.ic_communication_call_inactive, 0, 0);
      mTxtVoiceCall.setText(getResources().getString(R.string.request_voice_call));
    }

    if (!isVideoCallWaiting) {
      mTxtVideoCall.setCompoundDrawablesWithIntrinsicBounds(
          0, R.drawable.ic_av_videocam_inactive, 0, 0);
      mTxtVideoCall.setText(getResources().getString(R.string.request_video_call));
    }

    if (isAlt == ManageOnlineAlertFragment.ALERT_YES) {
      mTvAlertOnline.setText(R.string.profile_online_alerted);
    } else {
      mTvAlertOnline.setText(R.string.profile_online_alert);
    }

    findViewById(R.id.btn_chat_more_block).setOnClickListener(this);
    findViewById(R.id.btn_chat_more_report).setOnClickListener(this);
    findViewById(R.id.btn_chat_more_send_gift).setOnClickListener(this);
    mTxtVideoCall.setOnClickListener(this);
    flvoicecall.setOnClickListener(this);
    TextView txtFavorite = (TextView) findViewById(R.id.btn_chat_more_favorite);
    txtFavorite.setOnClickListener(this);

    isFavorited = FavouritedPrefers.getInstance().hasContainFav(
        userId);
    if (isFavorited) {
      txtFavorite.setText(R.string.profile_favorites_title);
      txtFavorite.setCompoundDrawablesWithIntrinsicBounds(0,
          R.drawable.ic_action_favorited, 0, 0);
    } else {
      txtFavorite.setText(R.string.profile_add_to_favorites_title);
      txtFavorite.setCompoundDrawablesWithIntrinsicBounds(0,
          R.drawable.ic_submenu_favorite_outline, 0, 0);
    }
    //hiepuh
    setUpViews();
    //end
    chatMoreListener.onShown();
  }

  //hiepuh
  private void setUpViews() {
    if (!isChat) {
      mTxtVoiceCall.setVisibility(View.GONE);
      flvideocall.setClickable(false);
      mTxtVideoCall.setVisibility(View.INVISIBLE);
      mTvAlertOnline.setVisibility(View.VISIBLE);
    }
  }
  //end

  @Override
  public void onClick(View v) {
    switch (v.getId()) {

      case R.id.btn_chat_more_block:
        if (chatMoreListener != null) {
          chatMoreListener.onBlock();
        }
        break;

      case R.id.btn_chat_more_favorite:
        if (chatMoreListener != null) {
          chatMoreListener.onFavorite();
        }
        break;

      case R.id.btn_chat_more_report:
        if (chatMoreListener != null) {
          chatMoreListener.onReport();
        }
        break;

      case R.id.btn_chat_more_send_gift:
        if (chatMoreListener != null) {
          chatMoreListener.onSendGift();
        }
        break;

      case R.id.btn_chat_more_video_call:
//				if (isVideoCallWaiting && chatMoreListener != null) {
        if (chatMoreListener != null) {
          chatMoreListener.onVideoCall();
        }
        break;

      case R.id.fl_chat_more_voice_call:
//				if (isVoiceCallWaiting && chatMoreListener != null) {
        if (chatMoreListener != null) {
          if (isChat) {
            chatMoreListener.onVoiceCall();
          } else {
            chatMoreListener.onAlertOnline();
          }
        }
        break;

      default:
        break;
    }
  }

}
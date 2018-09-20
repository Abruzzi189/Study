package com.application.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.connection.request.CircleImageRequest;
import com.application.status.StatusConstant;
import com.application.ui.ChatFragment;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public abstract class BaseChatView extends LinearLayout {

  protected static final String TAG = "BaseChatView";

  protected ImageView avatar;
  protected TextView time;
  protected ImageView warning;
  protected View contentView;
  protected ProgressBar mProgressBar;
  protected View mErrorStatusView;
  protected View mSendingStatusView;
  protected TextView read;
    protected int mMaxWidthContent;
  private int mAvatarSize;
  private int mPadding;
  private String userName = "";

  public BaseChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mAvatarSize = getResources().getDimensionPixelSize(
        R.dimen.item_list_chat_avatar);
    paddingWhenShowView();
  }

  public BaseChatView(Context context, int contentRes, boolean isSendMessage) {
    super(context);
    mAvatarSize = getResources().getDimensionPixelSize(
        R.dimen.item_list_chat_avatar);
    paddingWhenShowView();
    setOrientation(LinearLayout.HORIZONTAL);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (isSendMessage) {
      setEmptyView();
      setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
      setupStatusView(inflater, isSendMessage);
      setupMessageContentView(inflater, contentRes);
      setupAvatarView(inflater, isSendMessage);
    } else {
      setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
      setupAvatarView(inflater, isSendMessage);
      setupMessageContentView(inflater, contentRes);
      setupStatusView(inflater, isSendMessage);
      setEmptyView();
    }
    avatar = (ImageView) findViewById(R.id.item_list_chat_img);
    warning = (ImageView) findViewById(R.id.warning);
    time = (TextView) findViewById(R.id.time);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mErrorStatusView = findViewById(R.id.error_status);
    mSendingStatusView = findViewById(R.id.sending_status);
    read = (TextView) findViewById(R.id.read);
    calculateMaxWidthContent(isSendMessage);
    onFindId();
  }

  private void calculateMaxWidthContent(boolean isSendMessage) {

    Resources resources = getResources();
    int widthStatus = resources
        .getDimensionPixelSize(R.dimen.chat_width_time);

    if (isSendMessage) {
      mMaxWidthContent = getResources().getDisplayMetrics().widthPixels
          - widthStatus - getWidthEmptyViewSize();
    } else {
      mMaxWidthContent = getResources().getDisplayMetrics().widthPixels
          - mAvatarSize - widthStatus - getWidthEmptyViewSize();
    }

  }

  public void onFindId() {
  }

  private void setEmptyView() {
    View view = new View(getContext());
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.width = getWidthEmptyViewSize();
    addView(view, params);
  }

  protected int getWidthEmptyViewSize() {
    return getResources().getDisplayMetrics().widthPixels / 16;
  }

  private void setupStatusView(LayoutInflater layoutInflater,
      boolean isSendMessage) {
    View view = layoutInflater.inflate(R.layout.item_list_chat_status,
        this, false);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    if (isSendMessage) {
      params.gravity = Gravity.BOTTOM | Gravity.START;
    } else {
      params.gravity = Gravity.BOTTOM | Gravity.END;
    }
    addView(view, params);
  }

  private void setupAvatarView(LayoutInflater inflater, boolean isSendMessage) {
    View view = inflater.inflate(R.layout.item_chat_avatar, this, false);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.width = mAvatarSize;
    params.height = mAvatarSize;
    if (isSendMessage) {
      params.gravity = Gravity.TOP | Gravity.START;
    } else {
      params.gravity = Gravity.TOP | Gravity.END;
    }
    addView(view, params);
  }

  private void setupMessageContentView(LayoutInflater inflater,
      int contentLayout) {
    contentView = inflater.inflate(contentLayout, this, false);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.setMargins(0, 0, 10, 0);
    addView(contentView, params);
  }

  private void fillDataToAvatarView(final ChatFragment chatFragment,
      final ChatMessage chatMessage) {
    String token = UserPreferences.getInstance().getToken();
    String avataId;
    if (chatMessage.isOwn()) {
      avataId = chatFragment.getAvatar();
    } else {
      avataId = chatFragment.getAvatarToSend();
    }
    CircleImageRequest imageRequest = new CircleImageRequest(token, avataId);
    ImageUtil.loadCircleAvataImage(getContext(), imageRequest.toURL(), avatar);
    avatar.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.hideSoftKeyboard(chatFragment.getActivity());
        if (chatMessage.isOwn()) {
          // avatar of message send
          chatFragment.getItemChatClickListener()
              .onItemMyprofileClick();
        } else {
          // avatar of message receiver
          chatFragment.getItemChatClickListener()
              .onItemUserProfileClick();
        }
      }
    });
    avatar.setOnTouchListener(new OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        Utility.hideSoftKeyboard(chatFragment.getActivity());
        return false;
      }
    });
    avatar.setVisibility(chatMessage.isOwn() ? View.GONE : View.VISIBLE);
  }

  private void fillDataToSendTimeView(final ChatMessage chatMessage) {
    // time send or receiver
    String timeStr = chatMessage.getTimeStamp();
    timeStr = Utility.convertFormatDateTime(timeStr, "yyyyMMddHHmmssSSS",
        "HH:mm");
    time.setText(timeStr);
  }

  private void fillDataToReadTimeView(final ChatMessage chatMessage) {
    // time send or receiver
    if (chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (chatMessage.hasReadMessage()) {
        read.setVisibility(View.VISIBLE);
      } else {
        read.setVisibility(View.INVISIBLE);
      }
    } else {
      read.setVisibility(View.INVISIBLE);
    }
  }

  private void fillDataToStatusView(final ChatFragment fragment,
      final ChatMessage chatMessage) {
    if (chatMessage.isOwn()) {
      switch (chatMessage.getStatusSend()) {
        case StatusConstant.STATUS_START:
          mSendingStatusView.setVisibility(View.VISIBLE);
          mErrorStatusView.setVisibility(View.GONE);
          time.setVisibility(View.VISIBLE);
          read.setVisibility(View.INVISIBLE);
          break;
        case StatusConstant.STATUS_SENDING_FILE:
          mSendingStatusView.setVisibility(View.VISIBLE);
          mErrorStatusView.setVisibility(View.GONE);
          time.setVisibility(View.VISIBLE);
          read.setVisibility(View.INVISIBLE);
          break;
        case StatusConstant.STATUS_ERROR:
          mSendingStatusView.setVisibility(View.GONE);
          mErrorStatusView.setVisibility(View.VISIBLE);
          time.setVisibility(View.GONE);
          read.setVisibility(View.INVISIBLE);
          break;
        case StatusConstant.STATUS_SUCCESS:
          mSendingStatusView.setVisibility(View.GONE);
          mErrorStatusView.setVisibility(View.GONE);
          time.setVisibility(View.VISIBLE);
          String readTime = chatMessage.getReadTime();
          if (readTime != null && readTime.length() > 0) {
            read.setVisibility(View.VISIBLE);
          } else {
            read.setVisibility(View.INVISIBLE);
          }
          break;
        case StatusConstant.STATUS_READ:
          mSendingStatusView.setVisibility(View.GONE);
          mErrorStatusView.setVisibility(View.GONE);
          time.setVisibility(View.VISIBLE);
          read.setVisibility(View.VISIBLE);
          break;
        case StatusConstant.STATUS_RETRY:
          mSendingStatusView.setVisibility(View.VISIBLE);
          mErrorStatusView.setVisibility(View.GONE);
          time.setVisibility(View.VISIBLE);
          read.setVisibility(View.INVISIBLE);
          break;
        default:
          break;
      }
    }
    mErrorStatusView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (fragment.isShowDialogResend()) {
          fragment.dismissDialog();
        }
        fragment.showDialogResendMessage(chatMessage);

      }
    });
  }

  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    if (chatMessage.getStatusSend() == StatusConstant.STATUS_UNKNOW) {
      hideChildViews();
      return;
    }
    showChildViews();
    fillDataToAvatarView(chatFragment, chatMessage);
    fillDataToSendTimeView(chatMessage);
    fillDataToReadTimeView(chatMessage);
    fillDataToStatusView(chatFragment, chatMessage);
  }

  public void fillData1(ChatFragment chatFragment, ChatMessage chatMessage) {
    if (chatMessage.getStatusSend() == StatusConstant.STATUS_UNKNOW) {
      hideChildViews();
      return;
    }
    showChildViews();
    fillDataToAvatarView(chatFragment, chatMessage);
    fillDataToSendTimeView(chatMessage);
    fillDataToReadTimeView(chatMessage);
    fillDataToStatusView(chatFragment, chatMessage);
  }

  public void hideAllStuffView() {
    time.setVisibility(View.GONE);
    mProgressBar.setVisibility(View.GONE);
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Hide child views if this is first message (for send file)
   */
  protected void hideChildViews() {
    int count = getChildCount();
    for (int i = 0; i < count; i++) {
      getChildAt(i).setVisibility(View.GONE);
    }
    paddingWhenHideView();
  }

  /**
   * Show child views if this is second message (for send file)
   */
  protected void showChildViews() {
    int count = getChildCount();
    for (int i = 0; i < count; i++) {
      getChildAt(i).setVisibility(View.VISIBLE);
    }
    paddingWhenShowView();
  }

  private void paddingWhenShowView() {
    if (mPadding == 0) {
      mPadding = getResources().getDimensionPixelSize(
          R.dimen.item_chat_padding);
    }
    setPadding(0, mPadding, 0, mPadding);
  }

  private void paddingWhenHideView() {
    setPadding(0, 0, 0, 0);
  }

}

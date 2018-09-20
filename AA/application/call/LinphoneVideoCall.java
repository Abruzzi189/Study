package com.application.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.call.VideoCallFragment.OnVideoButtonClickListener;
import com.application.chat.ChatMessage;
import com.application.connection.request.CircleImageRequest;
import com.application.entity.CallUserInfo;
import com.application.ui.MainActivity;
import com.application.util.DesignUtils;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;
import org.linphone.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class LinphoneVideoCall extends LinphoneActivity implements
    OnClickListener, LinphoneOnCallStateChangedListener,
    OnVideoButtonClickListener {

  public static final String TURN_ON_CAMERA = "voip_video_on";
  public static final String TURN_OFF_CAMERA = "voip_video_off";
  private static final String TAG = "LinphoneVideoCall";
  protected ImageView mDimLayer;
  boolean mHasVideo = true;
  private View mRootLayout;
  private LinphoneCall mCall;
  // Outgoing
  private View mFrmOutgoing;
  private TextView mtxtName;
  private TextView mtxtStatus;
  private ImageView mImgAvatar;
  // Incomming
  private LinearLayout mCallLayoutController;
  private View mIgnore;
  private View mAnswer;
  private View mAnswerVoiceOnly;
  private View mEndCall;
  private ImageView mAvatarBg;
  private Animation mAvatarBgAnimation;
  // Calling
  private ImageView mCallingAvatar;
  private TextView mCallingName;
  private TextView mCallingStatus;
  private TextView mBtnAnswer;
  private TextView mBtnEnd;
  private AudioJackReceiver audioReceiver;
  private VideoCallFragment mVideoCallFragment;
  private OnSingleClickListener onSingleClickListener = new OnSingleClickListener() {

    @Override
    public void onOneClick(View view) {
      if (LinphoneManager.getLc().getCurrentCall() == null) {
        finishWithAnimation();
        return;
      }
      switch (view.getId()) {
        case R.id.end_call:
          mEndCallButtonClicked = true;
          LinphoneManager.getLc().terminateAllCalls();
          LinphoneManager.getLc().terminateCall(mCall);
          finishWithAnimation();
          break;
        case R.id.ignore:
          mEndCallButtonClicked = true;
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          LinphoneManager.getLc().terminateAllCalls();
          finishWithAnimation();
          break;
        case R.id.answer_voice_only:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          mHasVideo = false;
          mInviteAnswered = true;
          answer(true);
          addVideoCallFragment(false);
          break;
        case R.id.answer:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          mHasVideo = true;
          mInviteAnswered = true;
          answer(true);
          addVideoCallFragment(true);
          break;
        default:
          break;
      }
    }
  };

  public static void startInCommingCall(Context context,
      CallUserInfo callUserInfo) {
    synchronized (sCalling) {
      if (!sCalling) {
        sCalling = true;
        Intent intent = new Intent(context, LinphoneVideoCall.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra(USER_INFO, callUserInfo);
        intent.putExtra(LinphoneActivity.CALL_TYPE,
            LinphoneActivity.TYPE_INCOMING);
        context.startActivity(intent);
      }
    }
  }

  public static void startOutGoingCall(Context context,
      CallUserInfo callUserInfo) {
    synchronized (sCalling) {
      if (!sCalling) {
        sCalling = true;
        Intent intent = new Intent(context, LinphoneVideoCall.class);
        intent.putExtra(LinphoneActivity.CALL_TYPE,
            LinphoneActivity.TYPE_OUTGOING);
        intent.putExtra(LinphoneActivity.USER_INFO, callUserInfo);
        context.startActivity(intent);
      }
    }
  }

  @Override
  protected void doOnCreate(Bundle onsaveInstance) {
    super.doOnCreate(onsaveInstance);
    initView();
    LinphoneManager.addListener(this);
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    audioReceiver = new AudioJackReceiver();
    IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    registerReceiver(audioReceiver, filter);
    // Add on streaming error listener
    IOnStreamingError streamingError = new IOnStreamingError() {
      @Override
      public void onStreamingError() {
        LinphoneVideoCall.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Intent intent = new Intent(
                MainActivity.ACTION_STREAMING_ERROR);
            LocalBroadcastManager.getInstance(
                getApplicationContext()).sendBroadcast(intent);
            LinphoneManager.getLc().terminateAllCalls();
            finishWithAnimation();
          }
        });
      }
    };
    LinphoneManager.addListener(streamingError);
  }

  private void addVideoCallFragment(boolean hasVideo) {
    missCallable = false;
    mVideoCallFragment = VideoCallFragment.newInstance(mUserInfo, hasVideo);

    FragmentTransaction transaction = getSupportFragmentManager()
        .beginTransaction();
    transaction.replace(R.id.frame, mVideoCallFragment);
    try {
      transaction.commitAllowingStateLoss();
    } catch (Exception e) {
    }
    startCallingDuration();
  }

  private void in() {
    mDimLayer = (ImageView) findViewById(R.id.dim_layer);
    // Outgoing
    mFrmOutgoing = findViewById(R.id.frm_in_out);
    mtxtName = (TextView) findViewById(R.id.name);
    mtxtStatus = (TextView) findViewById(R.id.status);
    mImgAvatar = (ImageView) findViewById(R.id.avatar);

    // Incomming
    mCallLayoutController = (LinearLayout) findViewById(R.id.calling_controller);
    mIgnore = findViewById(R.id.ignore);
    mAnswer = findViewById(R.id.answer);
    mBtnAnswer = (TextView) findViewById(R.id.tv_activity_voice_call_new_answer);
    mAnswerVoiceOnly = findViewById(R.id.answer_voice_only);
    mEndCall = findViewById(R.id.end_call);
    mBtnEnd = (TextView) findViewById(R.id.tv_activity_voice_call_new_end_call);
    mAvatarBg = (ImageView) findViewById(R.id.avatar_background);
    mAvatarBgAnimation = AnimationUtils.loadAnimation(
        getApplicationContext(), R.anim.anim_bg_avatar_video_call_new);
    mIgnore.setOnClickListener(onSingleClickListener);
    mAnswer.setOnClickListener(onSingleClickListener);
    mEndCall.setOnClickListener(onSingleClickListener);
    mAnswerVoiceOnly.setOnClickListener(onSingleClickListener);

    // Calling
    mCallingAvatar = (ImageView) findViewById(R.id.calling_avatar);
    mCallingName = (TextView) findViewById(R.id.calling_name);
    mCallingStatus = (TextView) findViewById(R.id.calling_status);

    // Animation
    mAvatarBgAnimation = AnimationUtils.loadAnimation(
        getApplicationContext(), R.anim.anim_bg_avatar_video_call_new);

    mEndCall.getBackground()
        .setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);
    DesignUtils.setTextViewDrawableColor(mBtnAnswer, Color.WHITE);
    DesignUtils.setTextViewDrawableColor(mBtnEnd, Color.WHITE);
  }

  protected void initView() {
    super.initView();
    in();
    mRootLayout = findViewById(R.id.rootLayout);
    mRootLayout.setBackgroundColor(getResources().getColor(
        android.R.color.black));
    mRootLayout.setKeepScreenOn(true);
  }

  @Override
  public void onClick(View v) {

  }

  private void switchToOutgoing() {
    LogUtils.d(TAG, "switchToOutgoing Begin");
    missCallable = false;
    mCallLayoutController.setVisibility(View.GONE);
    mFrmOutgoing.setVisibility(View.VISIBLE);
    mEndCall.setVisibility(View.VISIBLE);
    LogUtils.d(TAG, "switchToOutgoing End");
  }

  private void switchToAnswer() {
    LogUtils.d(TAG, "switch to Answer Begin");
    missCallable = false;
    mRootLayout.setBackgroundColor(Color.BLACK);
    mFrmOutgoing.setVisibility(View.GONE);
    LogUtils.d(TAG, "switch to Answer End");
  }

  private void switchToIncomming() {
    LogUtils.d(TAG, "switchToIncomming Begin");
    if (LinphoneManager.getLc().getCurrentCall() == null) {
      finishWithAnimation();
      return;
    }
    missCallable = true;
    mCallLayoutController.setVisibility(View.VISIBLE);
    mFrmOutgoing.setVisibility(View.VISIBLE);
    mEndCall.setVisibility(View.GONE);
    LogUtils.d(TAG, "switchToIncomming End");
  }

  @Override
  public String getMissCallNotiBarMsg(String userName) {
    return super.getMissCallNotiBarMsg(userName);
  }

  @Override
  protected void doOnResume() {
    super.doOnResume();
    // Only one call ringing at a time is allowed
    if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
      List<LinphoneCall> calls = LinphoneUtils
          .getLinphoneCalls(LinphoneManager.getLc());
      for (LinphoneCall call : calls) {
        if (State.IncomingReceived == call.getState()) {
          mCall = call;
          break;
        }
      }
    }
    if (mInviteAnswered) {
      switchToAnswer();
    }
  }

  @Override
  protected void doOnDestroy() {
    super.doOnDestroy();
    LinphoneManager.removeListener(this);
    mAvatarBg.clearAnimation();
    mAvatarBgAnimation.cancel();
    mAvatarBgAnimation = null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(audioReceiver);
    // should stop linphone service and release linphone manager for save energy
//    Intent intent = new Intent(this, LinphoneService.class);
//    stopService(intent);
  }

  @Override
  protected String getCodeActionStart() {
    String messageValue = "" + ChatMessage.VoIPActionVideoStart;
    return messageValue;
  }

  @Override
  protected String getCodeActionEndNoAnswer() {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    String messageValue = "" + ChatMessage.VoIPActionVideoEndNoAnswer;
    return messageValue;
  }

  @Override
  protected String getCodeActionEnd(int callDuration) {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    String messageValue = ChatMessage.VoIPActionVideoEnd + "|"
        + startedMessageId + "|" + callDuration;
    return messageValue;
  }

  @Override
  protected String getCodeActionCalling(String callDuration) {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    StringBuilder builder = new StringBuilder();
    builder.append("call");
    builder.append("|");
    builder.append(startedMessageId);
    builder.append("|");
    builder.append(callDuration);
    return builder.toString();
  }

  @Override
  protected String getCodeActionEndBusy() {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    String messageValue = "" + ChatMessage.VoIPActionVideoEndBusy;
    return messageValue;
  }

  @Override
  protected int getContentView() {
    return R.layout.activity_video_call;
  }

  @Override
  protected MessageType getMessageTypeEnd() {
    return MessageType.EVIDEO;
  }

  @Override
  protected MessageType getMessageTypeStart() {
    return MessageType.SVIDEO;
  }

  @Override
  protected void showInComingScreen() {
    mtxtStatus.setText(R.string.andG_call);
    mAvatarBg.startAnimation(mAvatarBgAnimation);
    switchToIncomming();
  }

  @Override
  protected void showOutGoingScreen() {
    mtxtStatus.setText(R.string.ringing);
    mAvatarBg.startAnimation(mAvatarBgAnimation);
    switchToOutgoing();
  }

  @Override
  public boolean isVoiceCall() {
    return false;
  }

  @Override
  protected void setUserInfo(CallUserInfo userInfo) {
    mtxtName.setText(userInfo.getUserName());
    mCallingName.setText(userInfo.getUserName());
    String token = UserPreferences.getInstance().getToken();
    String avatarId = userInfo.getAvatarId();
    CircleImageRequest circleImageRequest = new CircleImageRequest(token,
        avatarId);
    String url = circleImageRequest.toURL();
    ImageUtil.loadCircleAvataImage(this, url, mImgAvatar);
    ImageUtil.loadCircleAvataImage(this, url, mCallingAvatar);

    // update userInfo to Fragment if need
    if (mVideoCallFragment != null) {
      mVideoCallFragment.updateUserInfo(mUserInfo);
    }
  }

  @Override
  protected void updateDuration(String durationMilis) {
    mtxtStatus.setText(durationMilis);
    mCallingStatus.setText(durationMilis);
    if (mVideoCallFragment != null) {
      mVideoCallFragment.updateDuration(durationMilis);
    }
    LogUtils.i(TAG, "duration=" + durationMilis);
  }

  @Override
  public View getDimView() {
    return mDimLayer;
  }

  @Override
  protected String getMessageNotEnoughPoint() {
    int point = UserPreferences.getInstance().getNumberPoint();
    return getString(R.string.not_enough_point_msg_video_call, point);
  }

  @Override
  protected void makeOutgoingCall() {
    mAndGCallManager.makeVideoCall();
  }

  private void answer(boolean hasVideo) {
    LinphoneCallParams params = LinphoneManager.getLc()
        .createCallParams(null);
    params.setVideoEnabled(hasVideo);
    boolean isLowBandwidthConnection = !LinphoneUtils
        .isHightBandwidthConnection(this);
    if (isLowBandwidthConnection) {
      params.enableLowBandwidth(true);
    }
    LinphoneCall mCall = null;
    // Only one call ringing at a time is allowed
    if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
      List<LinphoneCall> calls = LinphoneUtils
          .getLinphoneCalls(LinphoneManager.getLc());
      for (LinphoneCall call : calls) {
        if (State.IncomingReceived == call.getState()) {
          mCall = call;
          break;
        }
      }
    }
    LinphoneManager.getInstance().acceptCallWithParams(mCall, params);
  }

  @Override
  public void onMuteClick() {
    mAndGCallManager.toggleMute();
  }

  @Override
  public void onSpeakerClick() {
    mAndGCallManager.toggleSpeaker();
    LogUtils.d("JACK",
        "onSpeakerClick : " + mAndGCallManager.isEnableSpeaker());
  }

  @Override
  public void onEndClick() {
    mAndGCallManager.hangup();
    LinphoneManager.getLc().terminateAllCalls();
    finishWithAnimation();
  }

  @Override
  public void onOffVideo(boolean enableVideo) {
    String message;
    if (enableVideo) {
      mAndGCallManager.turnOnVideo();
      message = TURN_ON_CAMERA;
    } else {
      mAndGCallManager.turnOffVideo();
      message = TURN_OFF_CAMERA;
    }

    // send status of VoIP to CHAT server
    String userId = UserPreferences.getInstance().getUserId();
    String friendId = mUserInfo.getUserId();
    getChatService().getChatManager().sendVideoOnOffMessage(userId,
        friendId, message);
  }

  @Override
  protected AndGCallManager initAndGCallManager(String userId, String userName) {
    mAndGCallManager = new LinphoneCallManager(userId, userName);
    mAndGCallManager.enableSpeaker();
    return mAndGCallManager;
  }

  @Override
  protected void onAnswered() {
    if (mVideoCallFragment == null) {
      addVideoCallFragment(mHasVideo);
      if (!isSaveInstace()) {
        switchToAnswer();
      }
      // Stop Sound
      mCallSoundManager.stopSound();
    }
  }

  @Override
  protected void onEnded() {
    mCallSoundManager.stopSound();
    mVibrationManager.stop();
    LinphoneManager.getLc().terminateAllCalls();
    finishWithAnimation();
  }

  private class AudioJackReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
        int state = intent.getIntExtra("state", -1);
        switch (state) {
          case 0:
            LogUtils.d("JACK", "Headset is unplugged");
            mAndGCallManager.enableSpeaker();
            break;
          case 1:
            LogUtils.d("JACK", "Headset is plugged");
            mAndGCallManager.disableSpeaker();
            break;
        }
      }
    }
  }
}

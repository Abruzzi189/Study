package com.application.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.application.Config;
import com.application.chat.ChatMessage;
import com.application.connection.request.CircleImageRequest;
import com.application.entity.CallUserInfo;
import com.application.util.DesignUtils;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import org.linphone.LinphoneManager;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;


public class LinphoneVoiceCall extends LinphoneActivity {

  private static final String TAG = "LinphoneVoiceCall";
  protected ImageView mDimLayer;
  // Infomation
  private ImageView mImgAvatar;
  private TextView mtxtName;
  private TextView mtxtStatus;

  // CuongNV : Hiển thị nút end từ bên gọi và bên nhận khác nhau
  private TextView mBtnEnd;
  private TextView mBtnAnswer;

  // Microphone, Speaker
  private View mMuteMicrophone;
  private View mSpeak;
  private View mCallLayoutController;
  private ToggleButton mToggleSpeaker;
  private ToggleButton mToggleMic;

  // Calling
  private View mAnswer;
  private View mEnd;

  private SensorManager mSensorManager;

  private boolean isDimable = false;
  private float lightData = 0;
  private float proximityData = 0;

  private AudioJackReceiver audioReceiver;
  private SensorEventListener brightnessSensor = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      lightData = event.values[0];
      if (!isDimable) {
        if (isScreenDimmed()) {
          undimScreen();
        }
        return;
      }
      notifySensorChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };
  private SensorEventListener proximitySensor = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      proximityData = event.values[0];
      if (!isDimable) {
        if (isScreenDimmed()) {
          undimScreen();
        }
        return;
      }
      notifySensorChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };
  private OnSingleClickListener onSingleClickListener = new OnSingleClickListener() {

    @Override
    public void onOneClick(View view) {
      if (LinphoneManager.getLc().getCurrentCall() == null) {
        finishWithAnimation();
        return;
      }
      switch (view.getId()) {
        case R.id.ll_activity_voice_call_new_end_call:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          // sendMessageCallState();
          mAndGCallManager.hangup();
          finishWithAnimation();
          break;
        case R.id.ll_activity_voice_call_new_answer:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          switchToAnswer();
          mInviteAnswered = true;
          mAndGCallManager.answer();
          // Start calling duration
          startCallingDuration();
          break;
        default:
          break;
      }
    }
  };
  private View.OnClickListener inComingClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      if (LinphoneManager.getLc().getCurrentCall() == null) {
        finishWithAnimation();
        return;
      }
      switch (v.getId()) {
        case R.id.ll_activity_voice_call_new_end_call:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          // sendMessageCallState();
          mAndGCallManager.hangup();
          finishWithAnimation();
          break;
        case R.id.ll_activity_voice_call_new_answer:
          // Stop Sound
          mCallSoundManager.stopSound();
          mVibrationManager.stop();
          switchToAnswer();
          mInviteAnswered = true;
          mAndGCallManager.answer();
          // Start calling duration
          startCallingDuration();
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
        Intent intent = new Intent(context, LinphoneVoiceCall.class);
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
        Intent intent = new Intent(context, LinphoneVoiceCall.class);
        intent.putExtra(LinphoneActivity.CALL_TYPE,
            LinphoneActivity.TYPE_OUTGOING);
        intent.putExtra(LinphoneActivity.USER_INFO, callUserInfo);
        context.startActivity(intent);
      }
    }
  }

  private void in() {
    mImgAvatar = (ImageView) findViewById(R.id.iv_activity_voice_call_new_avatar);
    mtxtName = (TextView) findViewById(R.id.tv_activity_voice_call_new_user_name);
    mtxtStatus = (TextView) findViewById(R.id.tv_activity_voice_call_new_status);
    mtxtStatus.startAnimation(AnimationUtils.loadAnimation(
        getApplicationContext(), R.anim.push_up_in_out));

    // CuongNV : Hiển thị nút end từ bên gọi và bên nhận khác nhau
    mBtnEnd = (TextView) findViewById(R.id.mBtnEnd);

    mMuteMicrophone = findViewById(R.id.ll_activity_voice_call_new_mute);
    mSpeak = findViewById(R.id.ll_activity_voice_call_new_end_speaker);
    mCallLayoutController = findViewById(R.id.ll_activity_call_new_setting_calling);
    mToggleMic = (ToggleButton) findViewById(R.id.microphone);
    mToggleSpeaker = (ToggleButton) findViewById(R.id.speaker);
    mToggleMic.setOnClickListener(this);
    mToggleSpeaker.setOnClickListener(this);
    mSpeak.setOnClickListener(this);
    mMuteMicrophone.setOnClickListener(this);

    mBtnAnswer = (TextView) findViewById(R.id.tv_activity_voice_call_new_answer);
    mAnswer = findViewById(R.id.ll_activity_voice_call_new_answer);
    mAnswer.setOnClickListener(onSingleClickListener);

    mEnd = findViewById(R.id.ll_activity_voice_call_new_end_call);
    if (mCallType == TYPE_OUTGOING) {
      mEnd.setOnClickListener(this);
    } else {
      mEnd.setOnClickListener(inComingClickListener);
    }

    mDimLayer = (ImageView) findViewById(R.id.dim_layer);

    DesignUtils.setTextViewDrawableColor(mBtnEnd, Color.WHITE);
    DesignUtils.setTextViewDrawableColor(mBtnAnswer, Color.WHITE);
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    audioReceiver = new AudioJackReceiver();
    IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    registerReceiver(audioReceiver, filter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(audioReceiver);
  }

  private void switchToOutgoing() {
    missCallable = false;
    isDimable = false;
    mCallLayoutController.setVisibility(View.GONE);
    mAnswer.setVisibility(View.GONE);
    mEnd.setVisibility(View.VISIBLE);
    mEnd.setBackground(getResources().getDrawable(R.drawable.bg_call_button_base_color));

  }

  private void switchToIncomming() {
    if (LinphoneManager.getLc().getCurrentCall() == null) {
      finishWithAnimation();
      return;
    }
    missCallable = true;
    isDimable = false;
    mCallLayoutController.setVisibility(View.GONE);
    mAnswer.setVisibility(View.VISIBLE);
    mEnd.setVisibility(View.VISIBLE);
  }

  private void switchToAnswer() {
    missCallable = false;
    isDimable = true;
    mtxtStatus.clearAnimation();
    mCallLayoutController.setVisibility(View.VISIBLE);
    mAnswer.setVisibility(View.GONE);
    mBtnEnd.setText(R.string.activity_voip_end);
    mBtnEnd.setTextColor(getResources().getColor(R.color.white));
    mBtnEnd.setCompoundDrawablesWithIntrinsicBounds(
        getResources().getDrawable(R.drawable.ic_voip_end_answer), null, null, null);
    mEnd.setBackground(getResources().getDrawable(R.drawable.bg_call_button_base_color));
    DesignUtils.setTextViewDrawableColor(mBtnEnd, Color.WHITE);
  }

  @Override
  public String getMissCallNotiBarMsg(String userName) {
    return super.getMissCallNotiBarMsg(userName);
  }

  @Override
  protected void doOnCreate(Bundle onsaveInstance) {
    super.doOnCreate(onsaveInstance);
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
  }

  @Override
  protected void doOnResume() {
    super.doOnResume();
    Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    mSensorManager.registerListener(brightnessSensor, lightSensor,
        SensorManager.SENSOR_DELAY_NORMAL);
    Sensor proximity = mSensorManager
        .getDefaultSensor(Sensor.TYPE_PROXIMITY);
    mSensorManager.registerListener(proximitySensor, proximity,
        SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void doOnPause() {
    super.doOnPause();
    mSensorManager.unregisterListener(brightnessSensor);
    mSensorManager.unregisterListener(proximitySensor);
  }

  /**
   * Xu ly khi co cuoc goi toi'
   */

  @Override
  protected void showInComingScreen() {
    // mStartCallController.setVisibility(View.VISIBLE);
    switchToIncomming();
    mtxtStatus.setText(getString(R.string.andG_call));
  }

  /**
   * Xu ly khi co cuoc goi di
   */
  @Override
  protected void showOutGoingScreen() {
    // mStartCallController.setVisibility(View.GONE);
    switchToOutgoing();
    mtxtStatus.setText(getString(R.string.ringing));

    // CuongNV : Hiển thị nút end từ bên gọi và bên nhận khác nhau
    mBtnEnd.setText(getString(R.string.activity_voip_end_from));
  }

  @Override
  public boolean isVoiceCall() {
    return true;
  }

  /**
   * Khoi tao cac view trong man hinh nay, view chung da duoc khoi tao tren base class
   */
  @Override
  protected void initView() {
    super.initView();
    in();
    View mRootLayout = findViewById(R.id.rootLayout);
    mRootLayout.setBackgroundColor(getResources().getColor(
        android.R.color.black));
    mRootLayout.setKeepScreenOn(false);
  }

  @Override
  protected String getCodeActionStart() {
    String messageValue = "" + ChatMessage.VoIPActionVoiceStart;
    return messageValue;
  }

  @Override
  protected MessageType getMessageTypeStart() {
    return MessageType.SVOICE;
  }

  @Override
  protected String getCodeActionEndNoAnswer() {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    String messageValue = "" + ChatMessage.VoIPActionVoiceEndNoAnswer;
    return messageValue;
  }

  @Override
  protected String getCodeActionEnd(int callDuration) {
    String startedMessageId = mStartedMessageId;
    if (startedMessageId.length() == 0) {
      startedMessageId = UserPreferences.getInstance()
          .getStartedCallMessageId();
    }
    String messageValue = ChatMessage.VoIPActionVoiceEnd + "|"
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
    String messageValue = "" + ChatMessage.VoIPActionVoiceEndBusy;
    return messageValue;
  }

  /**
   * Get layout cua activity
   */
  @Override
  protected int getContentView() {
    return R.layout.activity_voice_call;
  }

  @Override
  protected MessageType getMessageTypeEnd() {
    return MessageType.EVOICE;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.speaker:
        mAndGCallManager.toggleSpeaker();
        break;
      case R.id.microphone:
        mAndGCallManager.toggleMute();
        break;
      case R.id.ll_activity_voice_call_new_end_call:
        // Stop Sound
        mEndCallButtonClicked = true;
        mCallSoundManager.stopSound();
        mVibrationManager.stop();
        // sendMessageCallState();
        mAndGCallManager.hangup();
        finishWithAnimation();
        break;
      default:
        break;
    }
  }

  @Override
  protected void setUserInfo(CallUserInfo userInfo) {
    LogUtils.d(TAG, "setUserInfo Started");
    mtxtName.setText(userInfo.getUserName());
    String token = UserPreferences.getInstance().getToken();
    String avatarId = userInfo.getAvatarId();
    CircleImageRequest circleImageRequest = new CircleImageRequest(token,
        avatarId);
    ImageUtil.loadCircleAvataImage(this, circleImageRequest.toURL(), mImgAvatar);
    LogUtils.d(TAG, "setUserInfo Ended");
  }

  @Override
  protected void updateDuration(String durationMilis) {
    mtxtStatus.setText(durationMilis);
  }

  public View getDimView() {
    return mDimLayer;
  }

  @Override
  protected String getMessageNotEnoughPoint() {
    int point = UserPreferences.getInstance().getNumberPoint();
    return getString(R.string.not_enough_point_msg_voice_call, point);
  }

  private void notifySensorChanged() {
    if (isDimable) {
      LogUtils.i(TAG, "BRIGHTNESS: " + String.valueOf(lightData));
      LogUtils.i(TAG, "PROXIMITY: " + String.valueOf(proximityData));
      if (lightData <= Config.THRESHOLD_BRIGHTNESS
          && proximityData <= Config.THRESHOLD_PROXIMITY) {
        dimScreen();
      } else {
        undimScreen();
      }
    } else {
      undimScreen();
    }
  }

  private void dimScreen() {
    if (this.mDimLayer.getVisibility() != View.VISIBLE) {
      this.mDimLayer.setVisibility(View.VISIBLE);
    }
  }

  private void undimScreen() {
    if (this.mDimLayer.getVisibility() == View.VISIBLE) {
      this.mDimLayer.setVisibility(View.GONE);
    }
  }

  private boolean isScreenDimmed() {
    return this.mDimLayer.getVisibility() == View.VISIBLE;
  }

  @Override
  protected void makeOutgoingCall() {
    mAndGCallManager.makeVoiceCall();
  }

  @Override
  protected AndGCallManager initAndGCallManager(String userId, String userName) {
    mAndGCallManager = new LinphoneCallManager(userId, userName);
    mAndGCallManager.disableSpeaker();
    return mAndGCallManager;
  }

  @Override
  protected void onAnswered() {
    startCallingDuration();
    // Stop Sound
    mCallSoundManager.stopSound();
    // Start calling duration
    switchToAnswer();
  }

  @Override
  protected void onEnded() {
    // Stop Sound
    mCallSoundManager.stopSound();
    mVibrationManager.stop();
    finishWithAnimation();
  }

  private class AudioJackReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
        int state = intent.getIntExtra("state", -1);
        switch (state) {
          case 0:
            LogUtils.d("JACK", "Headset is unplugged : " + mAndGCallManager.isEnableSpeaker());
            break;
          case 1:
            LogUtils.d("JACK", "Headset is plugged");
            mAndGCallManager.disableSpeaker();
            if (mToggleSpeaker != null) {
              mToggleSpeaker.setChecked(false);
            }
            break;
        }
      }
    }
  }
}

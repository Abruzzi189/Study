package com.application.call;

import static com.application.fcm.NotificationUtils.NOTIFICATION_CHANEL_ID;
import static com.application.fcm.NotificationUtils.NOTIFICATION_COLOR;
import static com.application.fcm.NotificationUtils.createChanel;
import static com.application.fcm.NotificationUtils.showNotificationCompat;

import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import com.application.call.CallSoundManager.CallSoundManagerImpl;
import com.application.call.CallVibrationManager.CallVibrationManagerImpl;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.MessageClient;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.entity.CallUserInfo;
import com.application.service.ApplicationNotificationManager;
import com.application.service.ChatService;
import com.application.service.DataFetcherService;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.ChatBindableActivity;
import com.application.ui.MainActivity;
import com.application.util.LogUtils;
import com.application.util.NetworkListener;
import com.application.util.NetworkListener.OnNetworkListener;
import com.application.util.NotificationUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public abstract class LinphoneActivity extends ChatBindableActivity implements
    ResponseReceiver, OnClickListener, OnNetworkListener,
    LinphoneOnCallStateChangedListener {

  public static final String TURN_ON_CAMERA = "voip_video_on";
  public static final String TURN_OFF_CAMERA = "voip_video_off";
  public final static String CALL_TYPE = "call_type";
  public final static String USER_INFO = "user_info";
  public final static int TYPE_INCOMING = 0;
  public final static int TYPE_OUTGOING = 1;
  public static final String ACTION_RETRY_CALL = "call_retry";
  private static final String TAG = "LinphoneActivity";
  private static final int LOADER_ID_BASIC_USER_INFO = 1051;
  private static final int FINISH = 100;
  private static final int UPDATE_DURATION = 200;
  public static LinphoneActivity instance;
  public volatile static Boolean sCalling = false;
  public static boolean missCallable = false;
  private final int DELAY_CALLING_COUNT = 60;
  protected int mCallType = TYPE_INCOMING;
  protected CallUserInfo mUserInfo;
  protected CallSoundManager mCallSoundManager;
  protected CallVibrationManager mVibrationManager;
  protected boolean mInviteAnswered = false;
  protected boolean mEnded = false;
  protected String mStartedMessageId = "";
  protected AndGCallManager mAndGCallManager;
  protected boolean mEndCallButtonClicked = false;
  private CallHandler mCallHandler;
  private Animation mEndCallAnimation;
  private WakeLock mWakeLock;
  private NetworkListener mNetworkListener;
  private int mDuration = 0;
  private long mStartTime = 0;
  private boolean isInPhoneCall = false;

  /**
   * Code when start call, code in {@link ChatMessage}
   */
  protected abstract String getCodeActionStart();

  /**
   * Code when end call with no answer, code in {@link ChatMessage}
   */
  protected abstract String getCodeActionEndNoAnswer();

  /**
   * Code when end call normally, code in {@link ChatMessage}
   */
  protected abstract String getCodeActionEnd(int callDuration);

  /**
   * Code when calling normally, code in {@link ChatMessage}
   */
  protected abstract String getCodeActionCalling(String callDuration);

  /**
   * Code when end call with busy, code in {@link ChatMessage}
   */
  protected abstract String getCodeActionEndBusy();

  /**
   * id of layout
   */
  protected abstract int getContentView();

  /**
   * Code when sent end call, code in {@link MessageType}
   */
  protected abstract MessageType getMessageTypeEnd();

  /**
   * Code when sent start call, code in {@link MessageType}
   */
  protected abstract MessageType getMessageTypeStart();

  /**
   * Control incomming
   */
  protected abstract void showInComingScreen();

  /**
   * Control outgoing
   */
  protected abstract void showOutGoingScreen();

  /**
   * @return true if voicecall, otherwise return false (ex: video call)
   */
  public abstract boolean isVoiceCall();

  protected abstract void onAnswered();

  protected abstract void onEnded();

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    Window win = getWindow();
    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    if (isInPhoneCall = Utility.isInPhoneCall(getApplicationContext())) {
      sendTerminateLinphoneCallByRealCall();
      Intent intent = getIntent();
      if (intent != null) {
        mCallType = intent.getIntExtra(CALL_TYPE, -1);
        mUserInfo = intent.getParcelableExtra(USER_INFO);
      }
      LinphoneManager.getLc().terminateAllCalls();
      int resMessage = -1;
      if (this instanceof LinphoneVideoCall) {
        missCallable = true;
        resMessage = R.string.canot_use_videocall;
      } else {
        missCallable = true;
        resMessage = R.string.canot_use_voicecall;
      }
      showMissCallToNotiBar();
      Builder builder = new CenterButtonDialogBuilder(this, false);
      builder.setMessage(resMessage);
      builder.setPositiveButton(R.string.ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          });
      builder.setCancelable(false);
      builder.show();
      return;
    } else {
      Intent intent = getIntent();
      if (intent != null) {
        mCallType = intent.getIntExtra(CALL_TYPE, -1);
        mUserInfo = intent.getParcelableExtra(USER_INFO);
      }
      setContentView(getContentView());
      initView();
      mCallSoundManager = new CallSoundManagerImpl(this);
      mVibrationManager = new CallVibrationManagerImpl(this);
      // Must call after super.onCreate(...) because this setCalling=false
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.setInCallingProcess(true);
      mNetworkListener = new NetworkListener(this, this);
      mNetworkListener.register();
      mCallHandler = new CallHandler(this);

      // Save current calling user id.
      userPreferences.setCallingUserId(mUserInfo.getUserId());
      // Cancel all notifications
      NotificationManager notificationManager = (NotificationManager) getSystemService(
          Context.NOTIFICATION_SERVICE);
      notificationManager.cancelAll();
      mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
          .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
      mWakeLock.acquire();
      setUnbindChatOnStop(false);
      initAndGCallManager(mUserInfo.getUserId(), mUserInfo.getUserName());
      performCallAction();
      LinphoneManager.addListener(this);
      instance = this;
      doOnCreate(bundle);
    }
  }

  private void performCallAction() {
    if (mCallType == TYPE_OUTGOING) {
      setUserInfo(mUserInfo);
      showOutGoingScreen();
      makeOutgoingCall();
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mCallSoundManager.playOutgoingSound();
    } else {
      showInComingScreen();

      // Start incoming call sound
      setVolumeControlStream(AudioManager.STREAM_RING);
      mCallSoundManager.playIncommingSound();
      mVibrationManager.vibrate();
      // Get user information
      try {
        if (Utility.isBlockedWithUser(this, mUserInfo.getUserId())) {
          finishWithAnimation();
          return;
        }

        String token = UserPreferences.getInstance().getToken();
        String userId = mUserInfo.getUserId();
        GetBasicInfoRequest basicInfoRequest = new GetBasicInfoRequest(
            token, userId);
        restartRequestServer(LOADER_ID_BASIC_USER_INFO,
            basicInfoRequest);
      } catch (Exception e) {
        LogUtils.e(TAG, String.valueOf(e.getMessage()));
      }
    }
  }

  public void lostConnectScreen() {
    showOutGoingScreen();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!isInPhoneCall) {
      instance = this;
      if (mEnded) {
        finishWithAnimation();
      }
      doOnResume();
    }

  }

  /**
   * Initialize common view
   */
  protected void initView() {
    mEndCallAnimation = AnimationUtils.loadAnimation(this, R.anim.end_call);
    mEndCallAnimation.setAnimationListener(new AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        finish();
      }
    });
  }

  private void startCountCalling(String duration) {
    String userId = UserPreferences.getInstance().getUserId();
    String friendId = mUserInfo.getUserId();
    String value = getCodeActionCalling(duration);
    getChatService().getChatManager().sendCallingMessage(userId, friendId,
        value);
  }

  public void startEndAnimation() {
    View mDimLayer = getDimView();
    if (mDimLayer != null) {
      mDimLayer.setVisibility(View.VISIBLE);
      mDimLayer.startAnimation(mEndCallAnimation);
    }
  }

  public void finishWithAnimation() {
    if (mCallHandler != null) {
      mCallHandler.sendEmptyMessage(FINISH);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    switch (loaderID) {
      case LOADER_ID_BASIC_USER_INFO:
        return new GetBasicInfoResponse(getApplicationContext(), data);
      default:
        break;
    }
    return null;
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (response == null) {
      return;
    }
    switch (loader.getId()) {
      case LOADER_ID_BASIC_USER_INFO:
        handleGetBasicInfoResponse((GetBasicInfoResponse) response);
        break;
      default:
        break;
    }
  }

  private void handleGetBasicInfoResponse(GetBasicInfoResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      String userName = response.getUserName();
      String userId = response.getUserId();
      String avatarId = response.getAvataId();
      int gender = response.getGender();
      mUserInfo = new CallUserInfo(userName, userId, avatarId, gender);
      setUserInfo(mUserInfo);
    } else {
      // finish();
      finishWithAnimation();
    }
  }

  protected abstract AndGCallManager initAndGCallManager(String userId,
      String userName);

  /**
   * Broadcast message to conversation list
   */
  private void broadcastMessageToConversationList(MessageType messageType,
      String voiceType) {
    Date date = Utility.getDateTimeInGMT();
    String userId = UserPreferences.getInstance().getUserId();
    String friendId = mUserInfo.getUserId();
    vn.com.ntqsolution.chatserver.pojos.message.Message message = new vn.com.ntqsolution.chatserver.pojos.message.Message(
        date, userId, friendId, messageType, String.valueOf(voiceType));

    // sent broadcast to ConversationList
    getChatService().getChatManager().sendBroadcastMessage(
        ChatManager.ACTION_LOCAL_MESSAGE_CALL,
        new MessageClient(message));
  }

  /**
   * add by HungHN Synchronize messageId message send call and message broadcast to
   * conversationList
   */
  private void broadcastMessageToConversationList(String messageId, MessageType messageType,
      String voiceType) {
    Date date = Utility.getDateTimeInGMT();
    String userId = UserPreferences.getInstance().getUserId();
    String friendId = mUserInfo.getUserId();
    vn.com.ntqsolution.chatserver.pojos.message.Message message = new vn.com.ntqsolution.chatserver.pojos.message.Message(
        date, userId, friendId, messageType, String.valueOf(voiceType));
    message.id = messageId;
    // sent broadcast to ConversationList
    getChatService().getChatManager().sendBroadcastMessage(
        ChatManager.ACTION_LOCAL_MESSAGE_CALL,
        new MessageClient(message));
  }

  @Override
  public boolean hasShowNotificationView() {
    return false;
  }

  @Override
  public boolean hasNotificationSound() {
    return false;
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Window win = getWindow();
    win.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    sCalling = false;
    if (!isInPhoneCall) {
      DataFetcherService.startGetPoint(getApplicationContext());
      // Stop calling duration
      stopCallingDuration();
      // Start ending call sound
      mCallSoundManager.playEndSound();
      mCallSoundManager.stopSound();
      mVibrationManager.stop();
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.setStartedCallMessageId("");
      userPreferences.setCallingUserId("");
      userPreferences.setInCallingProcess(false);
      mNetworkListener.unRegister();
      LinphoneManager.removeListener(this);
      if (instance == this) {
        mAndGCallManager.hangup();
        instance = null;
      }

      showMissCallToNotiBar();
      doOnDestroy();
    }

  }

  public String getMissCallNotiBarMsg(String userName) {
    if (userName != null && userName.length() > 0) {
      return String.format(
          getString(R.string.voip_miss_call_msg_at_noti_bar),
          userName);
    } else {
      return getString(R.string.voip_miss_call_msg_at_noti_bar_someone);
    }
  }

  public void showMissCallToNotiBar() {
    if (missCallable) {
      missCallable = false;
      String userName = mUserInfo.getUserName();
      showMissCallToNotiBar(userName);
    }
  }

  public void showMissCallToNotiBar(String userName) {
    String message = getMissCallNotiBarMsg(userName);
    String title = getString(R.string.common_app_name);

    // Data message send to MainActivity
    Intent notificationIntent = new Intent(this, MainActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_NEW_TASK);

    // Setting notification view
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    createChanel(this);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
        NOTIFICATION_CHANEL_ID)
        .setTicker(message)
        .setOngoing(false)
        .setUsesChronometer(false)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentTitle(title)
        .setAutoCancel(true)
        .setColor(Color.parseColor(NOTIFICATION_COLOR))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentIntent(pendingIntent);

    // Update sound and vibrate
    if (UserPreferences.getInstance().isSoundOn()) {
      String packageName = getPackageName();
      builder.setSound(Uri.parse("android.resource://" + packageName
          + File.separator + R.raw.notice));
    }
    NotificationUtils.vibarateNotification(this);

    // Get notification manager and push
    showNotificationCompat(this, ApplicationNotificationManager.KEY_PUSH_NOTIFICATION,
        builder.build());
  }

  protected void startCallingDuration() {
    LogUtils.d(TAG, "initializeTimerTask Started");
    mCallHandler.sendEmptyMessage(UPDATE_DURATION);
    mStartTime = System.currentTimeMillis();
    LogUtils.d(TAG, "initializeTimerTask Ended");
  }

  protected abstract void updateDuration(String durationMilis);

  private void performUpdateDuration() {
    long len = (System.currentTimeMillis() - mStartTime) / 1000;
    // accept convert long -> int
    mDuration = (int) len;
    String duration = Utility.getCallingDuration((int) len);
    // String duration = Utility.getCallingDuration(mDuration);
    LogUtils.i(TAG, String.format("Calling duration=[%s]", duration));
    updateDuration(duration);

    /**
     * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
                /*if (mDuration % DELAY_CALLING_COUNT == 0) {
                        startCountCalling(String.valueOf(mDuration));
                }
                }*/
    /**
     * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
  }

  private void stopCallingDuration() {
    LogUtils.d(TAG, "stopCallingDuration Started");
    if (mCallHandler != null) {
      mCallHandler.removeMessages(FINISH);
      mCallHandler.removeMessages(UPDATE_DURATION);
      mCallHandler = null;
    }
    LogUtils.d(TAG, "stopCallingDuration Ended");
  }

  /**
   * Sent information of call to chat server
   */
  public void sendMessageCallState() {

    if (getChatService() == null
        || getChatService().getChatManager() == null) {
      return;
    }

    switch (mCallType) {
      case TYPE_OUTGOING:
        String userId = UserPreferences.getInstance().getUserId();
        String friendId = mUserInfo.getUserId();
        MessageType type = getMessageTypeEnd();
        if (mInviteAnswered) {
          // On cancel call
                    /*String code = getCodeActionEnd(mDuration);
                    String messageId = getChatService().getChatManager().sendCallMessage(userId,
                            friendId, isVoiceCall(), false, code);
                    broadcastMessageToConversationList(messageId, type, code);*/
        } else {
                    /*if (mEndCallButtonClicked) {
                        // On timeout call
                        String code = getCodeActionEndNoAnswer();
                        String messageId = getChatService().getChatManager().sendCallMessage(
                                userId, friendId, isVoiceCall(), false, code);
                        broadcastMessageToConversationList(messageId, type, code);
                    } else {
                        // On busy call
                        String code = getCodeActionEndBusy();
                        String messageId = getChatService().getChatManager().sendCallMessage(
                                userId, friendId, isVoiceCall(), false, code);
                        broadcastMessageToConversationList(messageId, type, code);
                    }*/
        }
        break;
      default:
        break;
    }
  }

  public void sendTerminateLinphoneCallByRealCall() {
    ChatService chatService = getChatService();
    if (chatService == null) {
      return;
    }

    ChatManager chatManager = chatService.getChatManager();
    if (chatManager == null) {
      return;
    }

    String userId = UserPreferences.getInstance().getUserId();
    String friendId = mUserInfo.getUserId();
    chatManager.sendTerminateCallByRealCall(userId, friendId);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (isFinishing()) {
      sCalling = false;
      instance = null;
    }

    if (!isInPhoneCall) {
      if (mWakeLock != null && mWakeLock.isHeld()) {
        mWakeLock.release();
        LogUtils.d(TAG, "wake release");
      }
      doOnPause();
    }
  }

  // chat service connected
  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    super.onServiceConnected(name, service);
    // send ping message to partner
    if (mCallType == TYPE_OUTGOING) {
      String userId = UserPreferences.getInstance().getUserId();
      String friendId = mUserInfo.getUserId();
      String code = getCodeActionStart();
      // Ping message
      mChatService.getChatManager().sendCallPingMessage(userId, friendId);

      /**
       * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
       * http://10.64.100.201/issues/10589
       * http://10.64.100.201/issues/10577
       */
			/*// Save send call message
			mStartedMessageId = mChatService.getChatManager().sendCallMessage(
					userId, friendId, isVoiceCall(), true, code);

			// Broadcast local start voice
			broadcastMessageToConversationList(mStartedMessageId,getMessageTypeStart(), code);*/

      /**
       * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
       * http://10.64.100.201/issues/10589
       * http://10.64.100.201/issues/10577
       */
    }

  }

  @Override
  public void onNetworkDisconnected() {
    mEnded = true;
    finishWithAnimation();
  }

  @Override
  public void onNetworkConnected() {
  }

  public abstract View getDimView();

  protected abstract void makeOutgoingCall();

  protected abstract String getMessageNotEnoughPoint();

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    sendMessageCallState();
  }

  public CallUserInfo getUserInfo() {
    return mUserInfo;
  }

  protected abstract void setUserInfo(CallUserInfo userInfo);

  @Override
  public void onCallStateChanged(LinphoneCall call, State state,
      String message) {
    LogUtils.i(TAG, String.format("CallStateChanged=[%s]", state));
    if (state == State.CallEnd || state == State.CallReleased) {
      onEnded();
      if (!mEnded) {
        mEnded = true;
        if (mCallType == TYPE_OUTGOING) {
          sendMessageCallState();
        }
      }
    }
    if (state == State.Connected) {
      runOnUiThread(new Runnable() {

        @Override
        public void run() {
          if (!mInviteAnswered) {
            mInviteAnswered = true;
            onAnswered();
            mEnded = false;
          }
        }
      });
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    LogUtils.d("Volume", "onKeyDown");
    if (LinphoneUtils.onKeyVolumeAdjust(keyCode)) {
      return true;
    }

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  public int getCallType() {
    return mCallType;
  }

  protected void doOnCreate(Bundle onsaveInstance) {

  }

  protected void doOnResume() {

  }

  protected void doOnPause() {

  }

  protected void doOnDestroy() {

  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }

  private static final class CallHandler extends Handler {

    private final WeakReference<LinphoneActivity> weakReference;

    public CallHandler(LinphoneActivity activity) {
      weakReference = new WeakReference<LinphoneActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      LinphoneActivity activity = weakReference.get();
      if (activity == null) {
        return;
      }
      switch (msg.what) {
        case FINISH:
          if (activity.isSaveInstace()) {
            activity.finish();
          } else {
            activity.startEndAnimation();
          }
          break;
        case UPDATE_DURATION:
          activity.performUpdateDuration();
          sendEmptyMessageDelayed(UPDATE_DURATION, 1000);
          break;
        default:
          break;
      }
    }
  }
}

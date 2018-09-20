package com.application.call;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.application.CallConfig;
import com.application.chat.ChatManager;
import com.application.chat.MessageClient;
import com.application.connection.request.ImageRequest;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.CircleImageView;
import com.application.util.AnimationUtils;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import glas.bbsystem.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.linphone.CallManager;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;


public class VideoCallFragment extends BaseFragment implements OnClickListener {

  private static final String EXTRA_HAS_VIDEO = "has_video";
  private static final String EXTRA_USER_INFO = "user_info";

  private SurfaceView mVideoView;
  private SurfaceView mCaptureView;
  private AndroidVideoWindowImpl androidVideoWindowImpl;
  private View mMute, mEndCall;
  private View mCameraSwitcher;
  private ToggleButton mOnOffVideoCall;
  private TextView mtxtName;
  private TextView mtxtTime;
  private CircleImageView mAvatar;
  private View mBgAvatar;
  private View mControlBottom;
  private View mControlTop;

  private CallUserInfo mUserInfo;
  private boolean mHasVideo;
  private OnVideoButtonClickListener mButtonClickListener;
  private BroadcastReceiver mBroadcastReceiver;
  private boolean mShowControl = true;
  private AnimationListener animationListener = new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
      if (mShowControl) {
        mControlBottom.setVisibility(View.VISIBLE);
        mControlTop.setVisibility(View.VISIBLE);
      } else {
        mControlBottom.setVisibility(View.INVISIBLE);
        mControlTop.setVisibility(View.INVISIBLE);
      }
    }
  };

  public static VideoCallFragment newInstance(CallUserInfo userInfo,
      boolean hasVideo) {
    VideoCallFragment callFragment = new VideoCallFragment();
    Bundle bundle = new Bundle();
    bundle.putBoolean(EXTRA_HAS_VIDEO, hasVideo);
    bundle.putParcelable(EXTRA_USER_INFO, userInfo);
    callFragment.setArguments(bundle);
    return callFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    registerReceiveMessage();
    mUserInfo = getArguments().getParcelable(EXTRA_USER_INFO);
    mHasVideo = getArguments().getBoolean(EXTRA_HAS_VIDEO);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.video, container, false);
  }

  ;

  @SuppressWarnings("deprecation")
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init(view);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      mCaptureView.getHolder().setType(
          SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    fixZOrder(mVideoView, mCaptureView);

    androidVideoWindowImpl = new AndroidVideoWindowImpl(mVideoView,
        mCaptureView);
    androidVideoWindowImpl
        .setListener(new AndroidVideoWindowImpl.VideoWindowListener() {
          public void onVideoRenderingSurfaceReady(
              AndroidVideoWindowImpl vw, SurfaceView surface) {
            LinphoneManager.getLc().setVideoWindow(vw);
            mVideoView = surface;
          }

          public void onVideoRenderingSurfaceDestroyed(
              AndroidVideoWindowImpl vw) {
            LinphoneCore lc = LinphoneManager.getLc();
            if (lc != null) {
              lc.setVideoWindow(null);
            }
          }

          public void onVideoPreviewSurfaceReady(
              AndroidVideoWindowImpl vw, SurfaceView surface) {

            CallManager.getInstance().updateCall();

            mCaptureView = surface;
            LinphoneManager.getLc().setPreviewWindow(mCaptureView);
          }

          public void onVideoPreviewSurfaceDestroyed(
              AndroidVideoWindowImpl vw) {
            // Remove references kept in jni code and restart camera
            LinphoneManager.getLc().setPreviewWindow(null);
          }
        });
    androidVideoWindowImpl.init();

    mVideoView.setOnTouchListener(new OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      public boolean onTouch(View v, MotionEvent event) {
        // Disable touch on this view
        return true;
      }
    });

    mCaptureView.setOnTouchListener(new OnTouchCaptureView());
    if (mCaptureView.getLayoutParams() instanceof LayoutParams
        && CallConfig.isSaveVideoCaptureLocation) {
      UserPreferences pre = UserPreferences.getInstance();
      int location = pre.getVideoLocation();
      LayoutParams param = (LayoutParams) mCaptureView.getLayoutParams();
      if (location == UserPreferences.TOP_RIGHT) {
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      } else if (location == UserPreferences.BOTTOM_RIGHT) {
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      } else if (location == UserPreferences.BOTTOM_LEFT) {
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      }
    }

    if (!mHasVideo) {
      mButtonClickListener.onOffVideo(false);
    }
  }

  private void init(View view) {
    mMute = view.findViewById(R.id.mute);
    mEndCall = view.findViewById(R.id.end);
    mCameraSwitcher = view.findViewById(R.id.cameraswitcher);
    mOnOffVideoCall = (ToggleButton) view.findViewById(R.id.onoffvideo);
    mtxtName = (TextView) view.findViewById(R.id.name);
    mtxtTime = (TextView) view.findViewById(R.id.time);
    mAvatar = (CircleImageView) view.findViewById(R.id.avatar);
    mBgAvatar = view.findViewById(R.id.bg_avatar);
    mControlBottom = view.findViewById(R.id.control_bottom);
    mControlTop = view.findViewById(R.id.control);
    mVideoView = (SurfaceView) view.findViewById(R.id.videosurface);
    mCaptureView = (SurfaceView) view.findViewById(R.id.capturesurface);

    view.findViewById(R.id.touch).setOnClickListener(this);
    mMute.setOnClickListener(this);
    mEndCall.setOnClickListener(this);
    mCameraSwitcher.setOnClickListener(this);
    mOnOffVideoCall.setOnClickListener(this);
    mControlBottom.setOnClickListener(this);

    bindUserInfo();
  }

  private void bindUserInfo() {
    if (mUserInfo == null) {
      return;
    }
    if (mtxtName != null) {
      mtxtName.setText(mUserInfo.getUserName());
    }
    String token = UserPreferences.getInstance().getToken();
    String avatarId = mUserInfo.getAvatarId();
    ImageRequest circleImageRequest = new ImageRequest(token, avatarId,
        ImageRequest.THUMBNAIL);

    RequestOptions option = new RequestOptions().circleCrop()
        .placeholder(
            mUserInfo.getGender() == Constants.GENDER_TYPE_MAN ? R.drawable.dummy_avatar_male
                : R.drawable.dummy_avatar_female);
    Glide.with(this)
        .load(circleImageRequest.toURL())
        .apply(option)
        .into(mAvatar);
  }

  public void updateUserInfo(CallUserInfo userInfo) {
    mUserInfo = userInfo;
    bindUserInfo();
  }

  private void fixZOrder(SurfaceView video, SurfaceView preview) {
    video.setZOrderOnTop(false);
    preview.setZOrderOnTop(true);
    preview.setZOrderMediaOverlay(true);
  }

  public void switchCamera() {
    try {
      int videoDeviceId = LinphoneManager.getLc().getVideoDevice();
      videoDeviceId = (videoDeviceId + 1)
          % AndroidCameraConfiguration.retrieveCameras().length;
      LinphoneManager.getLc().setVideoDevice(videoDeviceId);
      CallManager.getInstance().updateCall();

      // previous call will cause graph reconstruction -> regive preview
      // window
      if (mCaptureView != null) {
        LinphoneManager.getLc().setPreviewWindow(mCaptureView);
      }
    } catch (ArithmeticException ae) {
      Log.e("Cannot swtich camera : no camera");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mVideoView != null) {
      ((GLSurfaceView) mVideoView).onResume();
    }
    if (androidVideoWindowImpl != null) {
      synchronized (androidVideoWindowImpl) {
        LinphoneManager.getLc().setVideoWindow(androidVideoWindowImpl);
      }
    }
    syncSwitchCameraButton();
    syncOnOffCameraButton();
  }

  @Override
  public void onPause() {
    if (androidVideoWindowImpl != null) {
      synchronized (androidVideoWindowImpl) {
        /*
         * this call will destroy native opengl renderer which is used
         * by androidVideoWindowImpl
         */
        LinphoneManager.getLc().setVideoWindow(null);
      }
    }

    if (mVideoView != null) {
      ((GLSurfaceView) mVideoView).onPause();
    }

    super.onPause();
  }

  @Override
  public void onDestroy() {

    mCaptureView = null;
    if (mVideoView != null) {
      mVideoView.setOnTouchListener(null);
      mVideoView = null;
    }
    if (androidVideoWindowImpl != null) {
      // Prevent linphone from crashing if correspondent hang up while you
      // are rotating
      androidVideoWindowImpl.release();
      androidVideoWindowImpl = null;
    }
    unregisterReceiveMessage();
    super.onDestroy();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mButtonClickListener = (OnVideoButtonClickListener) activity;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.mute:
        mButtonClickListener.onMuteClick();
        break;
      case R.id.end:
        mButtonClickListener.onEndClick();
        break;
      case R.id.onoffvideo:
        final LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
        if (call == null) {
          return;
        }
        boolean onoff = !call.cameraEnabled();
        mHasVideo = onoff;
        mButtonClickListener.onOffVideo(onoff);
        if (onoff) {
          mCaptureView.setVisibility(View.VISIBLE);
        } else {
          mCaptureView.setVisibility(View.GONE);
        }
        syncSwitchCameraButton();
        break;
      case R.id.cameraswitcher:
        switchCamera();
        break;
      case R.id.touch:
        toggleControl();
        break;
      default:
        break;
    }
  }

  public void switchToAudio() {
    mVideoView.setVisibility(View.GONE);
    mBgAvatar.setVisibility(View.VISIBLE);
  }

  public void switchToVideo() {
    mVideoView.setVisibility(View.VISIBLE);
    mBgAvatar.setVisibility(View.GONE);
  }

  private void registerReceiveMessage() {
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ChatManager.ACTION_MESSAGE_CMD)) {
          String patternVideoOn = "\\Avoip_video_on";
          String patternVideoOff = "\\Avoip_video_off";
          MessageClient compat = (MessageClient) intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          vn.com.ntqsolution.chatserver.pojos.message.Message message = compat
              .getMessage();
          String videoOnOffMessage = "";

          do {
            Pattern ppp = Pattern.compile(patternVideoOn,
                Pattern.DOTALL);
            Matcher m = ppp.matcher(message.value);
            if (m.find()) {
              videoOnOffMessage = LinphoneActivity.TURN_ON_CAMERA;
              break;
            }

            ppp = Pattern.compile(patternVideoOff, Pattern.DOTALL);
            m = ppp.matcher(message.value);
            if (m.find()) {
              videoOnOffMessage = LinphoneActivity.TURN_OFF_CAMERA;
              break;
            }
          } while (false);

          if (videoOnOffMessage.length() > 0) {
            if (videoOnOffMessage == LinphoneActivity.TURN_ON_CAMERA) {
              switchToVideo();
            } else {
              switchToAudio();
            }
          }
        }
      }
    };

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CMD);
    LocalBroadcastManager
        .getInstance(getActivity().getApplicationContext())
        .registerReceiver(mBroadcastReceiver, intentFilter);
  }

  private void unregisterReceiveMessage() {
    LocalBroadcastManager
        .getInstance(getActivity().getApplicationContext())
        .unregisterReceiver(mBroadcastReceiver);
  }

  public void updateDuration(String duration) {
    if (mtxtTime != null) {
      mtxtTime.setText(duration);
    }
  }

  @SuppressWarnings("deprecation")
  private void syncSwitchCameraButton() {
    if (mHasVideo && android.hardware.Camera.getNumberOfCameras() > 1) {
      mCameraSwitcher.setEnabled(true);
    } else {
      mCameraSwitcher.setEnabled(false);
    }
  }

  private void syncOnOffCameraButton() {
    mOnOffVideoCall.setChecked(!mHasVideo);
  }

  private void toggleControl() {
    if (mShowControl) {
      hideControl();
      mShowControl = false;
    } else {
      showControl();
      mShowControl = true;
    }
  }

  private void hideControl() {
    Animation animBottom = AnimationUtils.animationSlide(1,
        mControlBottom.getHeight(), false);
    animBottom.setAnimationListener(animationListener);
    mControlBottom.startAnimation(animBottom);
    Animation aniTop = AnimationUtils.animationSlide(-1,
        mControlBottom.getHeight(), false);
    aniTop.setAnimationListener(animationListener);
    mControlTop.startAnimation(aniTop);
  }

  private void showControl() {
    Animation animBottom = AnimationUtils.animationSlide(1,
        mControlBottom.getHeight(), true);
    animBottom.setAnimationListener(animationListener);
    mControlBottom.startAnimation(animBottom);
    Animation aniTop = AnimationUtils.animationSlide(-1,
        mControlBottom.getHeight(), true);
    aniTop.setAnimationListener(animationListener);
    mControlTop.startAnimation(aniTop);
  }

  public interface OnVideoButtonClickListener {

    public void onMuteClick();

    public void onSpeakerClick();

    public void onEndClick();

    public void onOffVideo(boolean enableVideo);
  }

  class OnTouchCaptureView implements OnTouchListener {

    private final float corX, corY;
    private final int DELAY = 250;
    private float dX, dY;
    private float sizeX, sizeY;
    private float edgeTop, edgeBottom, edgeLeft, edgeRight;
    private boolean isSettingDone = false;

    public OnTouchCaptureView() {
      Resources res = getResources();
      DisplayMetrics displayMetrics = res.getDisplayMetrics();
      corX = displayMetrics.widthPixels / 2;
      corY = displayMetrics.heightPixels / 2;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
      float rawX = event.getRawX();
      float rawY = event.getRawY();
      float posX = rawX + dX;
      float posY = rawY + dY;

      int action = event.getAction();
      switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
          // Setup start location
          setting(v);
          dX = v.getX() - rawX;
          dY = v.getY() - rawY;
          break;
        case MotionEvent.ACTION_UP:
          if (posX + sizeX / 2 > corX) {
            posX = edgeRight;
          } else {
            posX = edgeLeft;
          }

          if (posY + sizeY / 2 > corY) {
            posY = edgeBottom;
            if (CallConfig.isSaveVideoCaptureLocation) {
              UserPreferences pre = UserPreferences.getInstance();
              if (posX == edgeRight) {
                pre.saveVideoLocation(UserPreferences.BOTTOM_RIGHT);
              } else {
                pre.saveVideoLocation(UserPreferences.BOTTOM_LEFT);
              }
            }
          } else {
            posY = edgeTop;
            if (CallConfig.isSaveVideoCaptureLocation) {
              UserPreferences pre = UserPreferences.getInstance();
              if (posX == edgeRight) {
                pre.saveVideoLocation(UserPreferences.TOP_RIGHT);
              } else {
                pre.saveVideoLocation(UserPreferences.TOP_LEFT);
              }
            }
          }

          v.animate().x(posX).y(posY).setDuration(DELAY)
              .setInterpolator(new AccelerateInterpolator()).start();
          break;
        case MotionEvent.ACTION_MOVE:
          if (CallConfig.isCaptureVideoMovable) {
            if (posX < edgeLeft) {
              posX = edgeLeft;
            } else if (posX > edgeRight) {
              posX = edgeRight;
            }

            if (posY < edgeTop) {
              posY = edgeTop;
            } else if (posY > edgeBottom) {
              posY = edgeBottom;
            }
            v.animate().cancel();
            v.animate().x(posX).y(posY).setDuration(0).start();
          }
          break;
        default:
          return false;
      }
      return true;
    }

    private void setting(View v) {
      if (isSettingDone) {
        return;
      }
      isSettingDone = true;
      sizeX = v.getWidth();
      sizeY = v.getHeight();
      Resources res = getResources();
      int margin = res.getDimensionPixelSize(R.dimen.voip_padding);
      DisplayMetrics displayMetrics = res.getDisplayMetrics();
      edgeTop = margin;
      edgeLeft = margin;
      edgeBottom = displayMetrics.heightPixels - margin - sizeY;
      edgeRight = displayMetrics.widthPixels - margin - sizeX;
    }
  }
}

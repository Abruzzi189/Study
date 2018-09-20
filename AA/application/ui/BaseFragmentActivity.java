package com.application.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.application.AndGApp;
import com.application.common.webview.ActionParam;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.ServerRequest;
import com.application.connection.request.GetUserStatusRequest;
import com.application.connection.request.LoginByEmailRequest;
import com.application.connection.request.LoginByFacebookRequest;
import com.application.connection.request.LoginRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.constant.Constants;
import com.application.entity.NotificationMessage;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.imageloader.ImageCache.ImageCacheParams;
import com.application.imageloader.ImageFetcher;
import com.application.service.ApplicationNotificationManager;
import com.application.ui.account.AuthenticationData;
import com.application.ui.account.AuthenticationUtil;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.account.LoginActivity;
import com.application.ui.account.RegisterActivity;
import com.application.ui.account.SignUpActivity;
import com.application.ui.backstage.DetailPictureBackstageApproveActivity;
import com.application.ui.customeview.NavigationBar;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.point.BuyPointActivity;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.LogUtils;
import com.application.util.NotificationUtils;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ntq.api.DfeApi;
import com.ntq.fragments.PageFragmentHost;
import glas.bbsystem.R;
import org.linphone.LinphoneService;


/**
 * Base FragmentActivity for all FragmentActivity use in application
 *
 * @author tungdx
 */
public abstract class BaseFragmentActivity extends AppCompatActivity implements
    OnSharedPreferenceChangeListener, ResponseReceiver, PageFragmentHost {

  public static final int LOADER_RETRY_LOGIN = 1000;
  public static final int LOADER_GET_USER_STATUS = 9999;
  public static final String ACTION_INVALID_API = "invalid_api";
  public static final String ACTION_INVALID_VERSION = "invalid_version";
  public static final String ACTION_INVALID_EMAIl = "invalid_email";
  public static final String ACTION_INVALID_PASSWORD = "invalid_pass";
  public static final String ACTION_ACCOUNT_DISABLED = "acc_disabled";
  public static final int RESULT_EXIT = 100;
  public static final int REQUEST_EXIT = 101;
  public static final String TAB_BACKSTACK = "backstack_frg";
  protected static final String TAG = "BaseFragmentActivity";
  private static final String KEY_IS_SHOW_NOTIFICATION_VIEW = "IS_SHOW_NOTIFICATION_VIEW";
  protected NotificationMessage mNotificationMessage;
  private ResponseReceiver responseReceiver;
  private ServerRequest serverRequest;
  private NavigationBar navigationBar;
  private OnNavigationClickListener mOnNavigationClickListener;
  private RelativeLayout mRlNotification;
  private ImageView imgType;
  private TextView tvContent;
  private LocalBroadcastManager mLocalBroadcastManager;
  private BroadcastReceiver mBroadcastReceiver;
  private BroadcastReceiver mBlockDeactiveReceiver;
  private BroadcastReceiver mInvalidPasswordReceiver;
  private ImageFetcher mImageFetcher;
  private View mLoadingLayout;
  private boolean mIsShowNotificationView = true;
  private boolean isShowNotificationMessage = true;
  private boolean isShowNotificationSound = true;
  private boolean isShowNotificationVibrate = true;
  private AlertDialog mAlertBlookDeactive;
  private AlertDialog mDialogFlowMessage;
  private boolean mIsSaveInstance = false;
  private boolean isNavigable = true;
  private ProgressDialog mProgressDialogRetryLogin;

  protected void setResponseReceiver(ResponseReceiver responseReceiver) {
    this.responseReceiver = responseReceiver;
  }

  public void setOnNavigationClickListener(
      OnNavigationClickListener onNavigationClickListener) {
    mOnNavigationClickListener = onNavigationClickListener;
    if (navigationBar != null) {
      navigationBar
          .setOnNaviagtionClickListener(onNavigationClickListener);
    }
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (hasImageFetcher()) {
      initImageFetcher();
    }
    if (isNoTitle()) {
      supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    setupActionbar();
    mLocalBroadcastManager = LocalBroadcastManager
        .getInstance(getApplicationContext());
    if (this instanceof OnNavigationClickListener) {
      setOnNavigationClickListener((OnNavigationClickListener) this);
    }
    if (this instanceof ResponseReceiver) {
      setResponseReceiver((ResponseReceiver) this);
    }
    serverRequest = new ServerRequest(getSupportLoaderManager(),
        getApplicationContext(), responseReceiver);
    if (hasShowNotificationView()) {
      registerBroadcastGCM();
    }
    registerBlockAndDeactive();
    // tracking calling or not (Neu' khong phai la voice call + video call
    // -> khong phai trong cuoc goi
    UserPreferences.getInstance().setInCallingProcess(false);
    isShowNotificationSound = hasNotificationSound();
    mIsSaveInstance = false;
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState != null
        && savedInstanceState
        .containsKey(KEY_IS_SHOW_NOTIFICATION_VIEW)) {
      mIsShowNotificationView = savedInstanceState
          .getBoolean(KEY_IS_SHOW_NOTIFICATION_VIEW);
    }
  }

  private void registerBroadcastGCM() {
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE)) {

          NotificationMessage notiMessage = (NotificationMessage) intent
              .getParcelableExtra(WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE);
          LogUtils.e(TAG, "Notification Message Body.toString=" + notiMessage.toString());
          handleNotification(notiMessage);
          mNotificationMessage = notiMessage;
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(
        WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
        intentFilter);
  }

  private void registerBlockAndDeactive() {
    mBlockDeactiveReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Response.ACTION_BLOCK_DEACTIVE)) {
          int code = intent.getIntExtra(Response.EXTRA_CODE, -1);
          if (BaseFragmentActivity.this instanceof MainActivity) {
            displayDialogBlockAndDeative(code);
          } else {
            finish();
          }
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(
        Response.ACTION_BLOCK_DEACTIVE);
    mLocalBroadcastManager.registerReceiver(mBlockDeactiveReceiver,
        intentFilter);
  }

  private void unregisterBlockAndDeactive() {
    if (mBlockDeactiveReceiver != null) {
      LocalBroadcastManager.getInstance(getApplicationContext())
          .unregisterReceiver(mBlockDeactiveReceiver);
    }
  }

  private void handleNotification(NotificationMessage notiMsg) {
    if (!mIsShowNotificationView) {
      return;
    }
    LogUtils
        .e(TAG, "-->handleNotification().NotificationMessage.NotiType=" + notiMsg.getNotiType());
    // if type is'nt text notification then always notify
    UserPreferences userPreferences = UserPreferences.getInstance();
    if (notiMsg.getNotiType() != Constants.NOTI_CHAT_TEXT) {
      showNotificationView(notiMsg);
      if (notiMsg.getNotiType() == Constants.NOTI_ONLINE_ALERT) {
        Preferences preferences = Preferences.getInstance();
        int alertPoint = preferences.getOnlineAlertPoints();
        int currentPoint = userPreferences.getNumberPoint();
        userPreferences.saveNumberPoint(currentPoint - alertPoint);
      }
      // else if only not in Chat screen then notify
    } else if (TextUtils.isEmpty(userPreferences.getCurentFriendChat())) {
      showNotificationView(notiMsg);
    }
  }

  /**
   * Show notificationview or not, default=true
   */
  public void setEnableShowNotificationView(boolean isShow) {
    mIsShowNotificationView = isShow;
  }

  private void unregisterBroadcastGCM() {
    if (mLocalBroadcastManager != null) {
      mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
  }

  private void initImageFetcher() {
    mImageFetcher = new ImageFetcher(getApplicationContext());
    mImageFetcher.setLoadingImage(R.drawable.dummy_avatar,
        R.drawable.dummy_circle_avatar, R.drawable.dummy_avatar_male,
        R.drawable.dummy_avatar_female);
    mImageFetcher.addImageCache(getSupportFragmentManager(),
        getImageCache());
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (hasImageFetcher()) {
      mImageFetcher.setExitTasksEarly(false);
    }
    AndGApp.get().activityResume();

    if (!(this instanceof LoginActivity)) {
      registerInValidPassword();
    }
    mIsSaveInstance = false;

    if (!(this instanceof SplashActivity)) {
      requestGetUserStatus(Utility.isNeededGetUserStatus());
    }

    LogUtils.i(TAG, "onResume() finish");
  }

  public void requestGetUserStatus(int type) {
    LogUtils.d(TAG, "requestGetUserStatus - " + type);
    UserPreferences prefers = UserPreferences.getInstance();
    String data = null;
    switch (type) {
      case GetUserStatusRequest.TYPE_EMAIL:
        data = prefers.getRegEmail();
        break;
      case GetUserStatusRequest.TYPE_FACEBOOK:
        data = prefers.getFacebookId();
        break;
      case GetUserStatusRequest.TYPE_FAMU:
        data = prefers.getFamuId();
        break;
      case GetUserStatusRequest.TYPE_MOCOM:
        data = prefers.getMocomId();
        break;
      default:
        break;
    }

    if (data == null) {
      return;
    }

    GetUserStatusRequest request = new GetUserStatusRequest(type, data);
    restartRequestServer(LOADER_GET_USER_STATUS, request);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Preferences.getInstance().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
    mIsSaveInstance = false;
  }

  @Override
  protected void onStop() {
    super.onStop();
    Preferences.getInstance().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    AndGApp.get().activityPause();
    if (hasImageFetcher()) {
      mImageFetcher.setPauseWork(false);
      mImageFetcher.setExitTasksEarly(true);
      mImageFetcher.flushCache();
    }
    unregisterInvalidPassword();
    if (mDialogFlowMessage != null && mDialogFlowMessage.isShowing()) {
      mDialogFlowMessage.dismiss();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (hasImageFetcher()) {
      mImageFetcher.closeCache();
    }
    if (hasShowNotificationView()) {
      unregisterBroadcastGCM();
    }
    mNotificationMessage = null;
    unregisterBlockAndDeactive();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
    super.onActivityResult(requestCode, resultCode, arg2);
    if (requestCode == REQUEST_EXIT) {
      if (resultCode == RESULT_EXIT) {
        setResult(RESULT_EXIT);
        finish();
      }
      return;
    }
  }

  /**
   * Use when start Activity in account
   */
  protected void startCustomeActivityForResult(Intent intent) {
    startActivityForResult(intent, REQUEST_EXIT);
  }

  protected void customeFinishActivity() {
    setResult(RESULT_EXIT);
    finish();
  }

  protected boolean isNavigable() {
    return isNavigable;
  }

  protected void setNavigable(boolean isNavigable) {
    this.isNavigable = isNavigable;
  }

  protected void initNavigationBar() {
    navigationBar = (NavigationBar) findViewById(R.id.actionbar);
    if (navigationBar != null) {
      navigationBar.reset();
      navigationBar
          .setOnNaviagtionClickListener(mOnNavigationClickListener);
      Utility.hideKeyboard(this, navigationBar);
    }
  }

  protected void initialNotificationVew() {
    mRlNotification = (RelativeLayout) findViewById(R.id.rl_notification);
    imgType = (ImageView) findViewById(R.id.img_notification_type);
    tvContent = (TextView) findViewById(R.id.tv_notification_content);
    mRlNotification.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        performClickNotificationView(v, mNotificationMessage);
      }
    });
  }

  protected RelativeLayout getRlNotification() {
    return mRlNotification;
  }

  /**
   * Show notification
   */
  protected void showNotificationView(NotificationMessage notificationMessage) {
    LogUtils.i("TamNV",
        "Notification from free page --- showNotificationView   "
            + isShowNotificationMessage);
    if (isShowNotificationSound) {
      NotificationUtils.playNotificationSound(getApplicationContext());
    }
    if (isShowNotificationVibrate) {
      NotificationUtils.vibarateNotification(getApplicationContext());
    }
    //TODO show notification
    if (isShowNotificationMessage) {
      if (mRlNotification.getVisibility() == View.VISIBLE) {
        mRlNotification.removeCallbacks(null);
        mRlNotification.setVisibility(View.GONE);
      }
      mRlNotification.setEnabled(true);
      mRlNotification.setVisibility(View.VISIBLE);
      String message = ApplicationNotificationManager.getMessageNotification(
          getApplicationContext(), notificationMessage);
      int iconNotification = ApplicationNotificationManager
          .getIconNotification(notificationMessage);
      imgType.setImageResource(iconNotification);
      tvContent.setText(message);
      mRlNotification.postDelayed(new Runnable() {

        @Override
        public void run() {
          mRlNotification.setVisibility(View.GONE);
        }
      }, Constants.NOTI_SHOW_TIME);
    } else {
      mRlNotification.setVisibility(View.GONE);
    }
  }

  //TODO dismiss Notification
  public void dismissNotificationView() {
    if (mRlNotification != null) {
      mRlNotification.setVisibility(View.GONE);
    }
  }

  public NavigationBar getNavigationBar() {
    return navigationBar;
  }

  protected boolean isNoTitle() {
    return true;
  }

  protected void requestServer(int loaderID, int requestType,
      RequestParams data) {
    serverRequest.initLoader(loaderID, requestType, data);
  }

  protected void restartRequestServer(int loaderID, int requestType,
      RequestParams data) {
    serverRequest.restartLoader(loaderID, requestType, data);
  }

  protected void requestServer(int loaderId, RequestParams data) {
    serverRequest.initLoader(loaderId, RequestType.JSON, data);
  }

  public void restartRequestServer(int loaderID, RequestParams data) {
    serverRequest.restartLoader(loaderID, RequestType.JSON, data);
  }

  protected void clearAllFocusableFields() {
    // NOP
  }

  protected ImageCacheParams getImageCache() {
    ImageCacheParams mImageCacheParams = new ImageCacheParams(
        getApplicationContext(), ImageFetcher.LARGE_CACHE_DIR);
    mImageCacheParams.setMemCacheSizePercent(0.25f);
    return mImageCacheParams;
  }

  public ImageFetcher getImageFetcher() {
    return mImageFetcher;
  }

  /**
   * Indicate that use ImageFetcher in activity, default: false
   */
  public boolean hasImageFetcher() {
    return false;
  }

  public boolean hasShowNotificationView() {
    return false;
  }

  /**
   * In this activity has sound or not (VideoCall vs VoiceCall hasn't sound)
   */
  public boolean hasNotificationSound() {
    return true;
  }

  /**
   * This activity has sound or not to notify (VideoCall and VoiceCall hasn't sound)
   */
  public void setNotificationSound(boolean isSound) {
    isShowNotificationSound = isSound;
  }

  /**
   * This activity has bottom message or not to notify(Shake To Chat hasn't bottom message)
   */
  public void setNotificationMessage(boolean isMessage) {
    this.isShowNotificationMessage = isMessage;
  }

  /**
   * This activity has vibration or not to notify
   */
  public void setNotificationVibrate(boolean isVibrate) {
    this.isShowNotificationMessage = isVibrate;
  }

  public void resetHandleShowNotification() {
    this.isShowNotificationMessage = true;
    this.isShowNotificationSound = true;
    this.isShowNotificationVibrate = true;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    // do nothing, maybe inherit class use
  }

  public void performClickNotificationView(View view,
      NotificationMessage notificationMessage) {
    if (!(this instanceof MainActivity)) {
      return;
    }

    UserPreferences userPreferences = UserPreferences.getInstance();

    if (userPreferences.getInRecordingProcess()) {
      return;
    }

    // Hide keyboard
    Utility.hideSoftKeyboard(this);

    MainActivity mainActivity = (MainActivity) this;
    int type = notificationMessage.getNotiType();

    if (type != Constants.NOTI_CHAT_TEXT && type != Constants.NOTI_REQUEST_CALL) {
      userPreferences.decreaseNumberNotification();
    }

    switch (type) {
      case Constants.NOTI_LIKE_BUZZ:
      case Constants.NOTI_LIKE_OTHER_BUZZ:
      case Constants.NOTI_COMMENT_BUZZ:
      case Constants.NOTI_COMMENT_OTHER_BUZZ:
      case Constants.NOTI_BUZZ_APPROVED:
      case Constants.NOTI_FAVORITED_CREATE_BUZZ:
      case Constants.NOTI_APPROVE_BUZZ_TEXT:
        mainActivity.showBuzzDetail(notificationMessage, true);
        break;
      case Constants.NOTI_REPLY_YOUR_COMMENT:
      case Constants.NOTI_APPROVE_COMMENT:
      case Constants.NOTI_DENIED_COMMENT:
      case Constants.NOTI_APPROVE_SUB_COMMENT:
      case Constants.NOTI_DENI_SUB_COMMENT:
        mainActivity.showBuzzDetail(notificationMessage, false);
        break;
      case Constants.NOTI_CHECK_OUT_UNLOCK:
      case Constants.NOTI_FAVORITED_UNLOCK:
      case Constants.NOTI_UNLOCK_BACKSTAGE:
      case Constants.NOTI_FRIEND:
      case Constants.NOTI_ONLINE_ALERT:
        mainActivity.showProfile(notificationMessage);
        break;
      case Constants.NOTI_APPROVE_USERINFO:
      case Constants.NOTI_DENIED_USERINFO:
      case Constants.NOTI_APART_OF_USERINFO:
        mainActivity.showProfile(notificationMessage.getOwnerId());
        break;
      case Constants.NOTI_DENIED_BUZZ_IMAGE:
      case Constants.NOTI_DENIED_BUZZ_TEXT:
        mainActivity.showMyPost(notificationMessage);
        break;
      case Constants.NOTI_CHAT_TEXT:
        mainActivity.replaceFragment(ChatFragment.newInstance(
            notificationMessage.getUserid(), true),
            MainActivity.TAG_FRAGMENT_CHAT);
        break;
      case Constants.NOTI_DAYLY_BONUS:
        BaseFragment fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
        mainActivity.replaceFragment(fragment, null);
        break;
      case Constants.NOTI_BACKSTAGE_APPROVED:
        Intent intent = new Intent(this,
            DetailPictureBackstageApproveActivity.class);
        intent.putExtras(ProfilePictureData
            .parseDataToBundle(notificationMessage.getImageId()));
        startActivity(intent);
        break;
      case Constants.NOTI_DENIED_BACKSTAGE:
        mainActivity.showListBackstage(notificationMessage.getOwnerId());
        break;
      case Constants.NOTI_FROM_FREE_PAGE:
        String url = notificationMessage.getUrl();

        if (url != null && url.contains("open_browser=1")) {
          String mUrl = url.replace("open_browser=1", "");
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
          startActivity(browserIntent);
          return;
        }

        String param = null;
        if (url.split("=").length > 1) {
          param = url.split("=")[1];
        }
        Fragment activePage = null;
        if (mainActivity != null
            && mainActivity.getNavigationManager() != null) {
          activePage = mainActivity.getNavigationManager()
              .getActivePage();
        }

        ActionParam actionParam = new ActionParam();
        if (url.contains(actionParam.USER_PROFILE)) {
          int idIndex = url.lastIndexOf(actionParam.USER_PROFILE_ID)
              + actionParam.USER_PROFILE_ID.length();
          String id = url.substring(idIndex, url.length());
          if (!TextUtils.isEmpty(id)) {
            if ((activePage instanceof MyProfileFragment && !((MyProfileFragment) activePage)
                .getUserId().equals(id))
                || !(activePage instanceof MyProfileFragment)) {
              MyProfileFragment myProfileFragment = MyProfileFragment
                  .newInstance(id);
              mainActivity.replaceFragment(myProfileFragment, "");
            }
          }
        } else if (actionParam.TOP.equals(param)) {
          // Action for top page. Open top page
          if (!(activePage instanceof MeetPeopleFragment)) {
            HomeFragment meetPeopleFragment = HomeFragment
                .newInstance(HomeFragment.TAB_MEET_PEOPLE);
            mainActivity.replaceFragment(meetPeopleFragment, "");
          }
        } else if (actionParam.MY_PROFILE.equals(param)) {
          // Open my profile
          if (!(activePage instanceof EditProfileFragment)) {
            EditProfileFragment editProfileFragment = EditProfileFragment
                .newInstance();
            mainActivity.replaceFragment(editProfileFragment, "");
          }
        } else if (actionParam.MY_PAGE.equals(param)) {
          // Open my page
          if (!(activePage instanceof MyPageFragment)) {
            MyPageFragment myPageFragment = new MyPageFragment();
            mainActivity.replaceFragment(myPageFragment, "");
          }
        } else if (actionParam.TERMS.equals(param)) {
          // Open the term of service
          if ((activePage instanceof WebViewFragment
              && !(((WebViewFragment) activePage).getPageType()
              != WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE))
              || !(activePage instanceof WebViewFragment)) {
            WebViewFragment webViewFragment = WebViewFragment
                .newInstance(WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE);
            mainActivity.replaceFragment(webViewFragment, "");
          }
        } else if (actionParam.PRIVACY.equals(param)) {
          // Open privacy
          if ((activePage instanceof WebViewFragment
              && !(((WebViewFragment) activePage).getPageType()
              != WebViewFragment.PAGE_TYPE_PRIVACY_POLICY))
              || !(activePage instanceof WebViewFragment)) {
            WebViewFragment webViewFragment = WebViewFragment
                .newInstance(WebViewFragment.PAGE_TYPE_PRIVACY_POLICY);
            mainActivity.replaceFragment(webViewFragment, "");
          }
        } else if (actionParam.GOOGLE_PLAY_PAGE.equals(param)) {
          // Open buy point page
          startActivity(new Intent(this, BuyPointActivity.class));
        } else {
          String content = notificationMessage.getContent();
          WebViewFragment webViewFragment = WebViewFragment.newInstance(
              url, content);
          mainActivity.replaceFragment(webViewFragment, "");
        }
        break;
      case Constants.NOTI_REQUEST_CALL:
        //TODO process click noti request call
        mainActivity.showProfile(notificationMessage);
        break;
      default:
        break;
    }
    view.setVisibility(View.GONE);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(KEY_IS_SHOW_NOTIFICATION_VIEW,
        mIsShowNotificationView);
    mIsSaveInstance = true;
  }

  private void displayDialogBlockAndDeative(int code) {
    if (mAlertBlookDeactive != null && mAlertBlookDeactive.isShowing()) {
      return;
    }

    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.common_error);
    builder.setCustomTitle(customTitle);
    //builder.setTitle(R.string.common_error);
    if (code == Response.SERVER_BLOCKED_USER) {
      builder.setMessage(R.string.user_blocked);
    } else if (code == Response.SERVER_USER_NOT_EXIST) {
      builder.setMessage(R.string.user_deactive);
    } else {
      return;
    }

    builder.setCancelable(false);
    builder.setPositiveButton(R.string.ok,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            MainActivity mainActivity = (MainActivity) BaseFragmentActivity.this;
            mainActivity.handleMenu(
                MainActivity.FUNCTION_HOME_FRAGMENT, true);
          }
        });
    mAlertBlookDeactive = builder.create();
    mAlertBlookDeactive.show();

    int dividerId = mAlertBlookDeactive.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertBlookDeactive.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    getSupportLoaderManager().destroyLoader(loader.getId());
    if (response == null) {
      return;
    }
    if (mProgressDialogRetryLogin != null) {
      mProgressDialogRetryLogin.dismiss();
    }

    switch (loader.getId()) {
      case LOADER_RETRY_LOGIN:
        if (response != null) {
          int code = response.getCode();
          if (code == Response.SERVER_SUCCESS) {
            // Save info when login success
            performTasksWhenLoginSuccess((LoginResponse) response,
                false);
            // start MainActivity
            startMainScreen();
          } else {
            RequestBuilder.getInstance().performErrorCodeFromServer(
                code);
          }
        }
        break;
      case LOADER_GET_USER_STATUS:
        if (response != null) {
          if (response.getCode() == Response.SERVER_SUCCESS) {
            onResponseGetUserStatus((GetUserStatusResponse) response);
          } else {
            RequestBuilder.getInstance().performErrorCodeFromServer(
                response.getCode());
          }
        }
        break;
      default:
        break;
    }
  }

  private void onResponseGetUserStatus(GetUserStatusResponse response) {
    try {
      int status = response.getUserStatus();
      LogUtils.d(TAG, "onResponseGetUserStatus -- status : "
          + status);
      if (status == GetUserStatusResponse.ACTIVE) {
        autoLogin();
      } else {
        if (this instanceof SplashActivity) {
          startAuthenScreen();
        }
      }
    } catch (Exception e) {
      LogUtils.e(TAG, "Get user status error");
      if (this instanceof SplashActivity) {
        startAuthenScreen();
      }
    }
  }

  public void startAuthenScreen() {
    if (this instanceof SignUpActivity) {
      return;
    }

    if (isNavigable()) {
      startActivity(new Intent(this, SignUpActivity.class));
      finish();
    }
  }

  public void startMainScreen() {
    if (this instanceof MainActivity) {
      return;
    }
    // Send active action to ApptizerF
    UserPreferences userPreferences = UserPreferences.getInstance();
    int gender = userPreferences.getGender();
    String userId = userPreferences.getUserId();

    // Start main activity

    if (isNavigable()) {
      startActivity(new Intent(this, MainActivity.class));
      finish();
    }
  }

  public void autoLogin() {
    UserPreferences userPreferences = UserPreferences.getInstance();

    // Authentication data
    String email = userPreferences.getEmail();
    String facebookId = userPreferences.getFacebookId();
    String mocomId = userPreferences.getMocomId();
    String famuId = userPreferences.getFamuId();
    String pass = userPreferences.getPassword();

    // Get basic login data
    String device_id = Utility.getDeviceId(this);
//        String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    String login_time = Utility.getLoginTime();
    String appVersion = Utility.getAppVersionName(this);
    LoginRequest loginRequest = null;
    String adjustAdid = "";
    adjustAdid = Preferences.getInstance().getAdjustAdid();
    String applicationName = Utility.getApplicationName(this);
    if (!TextUtils.isEmpty(email)) {
      loginRequest = new LoginByEmailRequest(email, pass, device_id,
          notify_token, login_time, appVersion, AndGApp.advertId, AndGApp.device_name,
          AndGApp.os_version, adjustAdid, applicationName);
    } else if (!TextUtils.isEmpty(facebookId)) {
      loginRequest = new LoginByFacebookRequest(facebookId, device_id,
          notify_token, login_time, appVersion, AndGApp.advertId, AndGApp.device_name,
          AndGApp.os_version, adjustAdid, applicationName);
    } else if (!TextUtils.isEmpty(mocomId)) {
//            loginRequest = new LoginByMocomRequest(mocomId, device_id,
//                    notify_token, login_time, appVersion);
    } else if (!TextUtils.isEmpty(famuId)) {
//            loginRequest = new LoginByFamuRequest(famuId, device_id,
//                    notify_token, login_time, appVersion);
    } else {
      if (this instanceof SplashActivity) {
        startAuthenScreen();
      }
    }

    if (loginRequest != null) {
      restartRequestServer(LOADER_RETRY_LOGIN, loginRequest);
    }

  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void startRequest(int loaderId) {

  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_RETRY_LOGIN) {
      return new LoginResponse(data);
    } else if (loaderID == LOADER_GET_USER_STATUS) {
      return new GetUserStatusResponse(data);
    }

    return null;
  }

  protected void performTasksWhenLoginSuccess(LoginResponse loginResponse,
      boolean isFirstLogin) {
    // Save info when login success
    AuthenticationData authenData = loginResponse.getAuthenticationData();

    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.saveSuccessLoginData(authenData, isFirstLogin);

    Preferences preferences = Preferences.getInstance();
    preferences.saveTimeSetting(authenData);
    preferences.savePointSetting(authenData);

    // Login time
    preferences.saveTimeRelogin(System.currentTimeMillis());

    // Start LinphoneCall
    LinphoneService.startLogin(getApplicationContext());

    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    googleReviewPreference.saveTurnOffVersion(loginResponse
        .getSwitchBrowserVersion());
    googleReviewPreference.saveEnableGetFreePoint(loginResponse
        .isEnableGetFreePoint());
    googleReviewPreference.saveIsTurnOffUserInfo(loginResponse.isTurnOffUserInfo());
  }

  private void registerInValidPassword() {
    mInvalidPasswordReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (mDialogFlowMessage != null
            && mDialogFlowMessage.isShowing()) {
          return;
        }
        String action = intent.getAction();
        AlertDialog.Builder builder = new CenterButtonDialogBuilder(BaseFragmentActivity.this,
            false);
        if (action.equals(ACTION_INVALID_PASSWORD) || action.equals(ACTION_INVALID_EMAIl)) {
          if (BaseFragmentActivity.this instanceof SignUpActivity
              || BaseFragmentActivity.this instanceof RegisterActivity

              // don't do anything if activity is web view activity #11491
              || BaseFragmentActivity.this instanceof WebViewActivity) {
            return;
          }
          if (BaseFragmentActivity.this.isNavigable()) {
            BaseFragmentActivity.this.setNavigable(false);
          }

          // delay auto logout
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              if (!BaseFragmentActivity.this.isNavigable()) {
                BaseFragmentActivity.this.setNavigable(true);
              }
              logout();
              removeInfoWhenLogout();
            }
          }, 1500);

          if (action.equals(ACTION_INVALID_PASSWORD)) {
            builder.setMessage(R.string.password_invalid);
          } else {
            builder.setMessage(R.string.email_invalid);
          }
          builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                int which) {
              mDialogFlowMessage.dismiss();
            }
          });
        } else if (action.equals(ACTION_ACCOUNT_DISABLED)) {
          if (BaseFragmentActivity.this.isNavigable()) {
            BaseFragmentActivity.this.setNavigable(false);
          }

          // delay auto logout
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              if (!BaseFragmentActivity.this
                  .isNavigable()) {
                BaseFragmentActivity.this
                    .setNavigable(true);
              }

              logout();
            }
          }, 1500);

          builder.setMessage(R.string.account_locked_user);
          builder.setPositiveButton(R.string.common_ok,
              new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  if (!BaseFragmentActivity.this.isNavigable()) {
                    BaseFragmentActivity.this.setNavigable(true);
                  }
                  mDialogFlowMessage.dismiss();
                }
              });
        } else if (action.equals(ACTION_INVALID_VERSION)) {
          if (BaseFragmentActivity.this.isNavigable()) {
            BaseFragmentActivity.this.setNavigable(false);
          }
          builder.setMessage(R.string.application_version_invalid);
          builder.setPositiveButton(R.string.common_ok,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  if (!BaseFragmentActivity.this
                      .isNavigable()) {
                    BaseFragmentActivity.this
                        .setNavigable(true);
                  }

                  final String packageName = getPackageName();
                  Uri URI;
                  try {
                    URI = Uri.parse(WebViewFragment
                        .getGooglePlayLink(packageName));
                  } catch (android.content.ActivityNotFoundException anfe) {
                    URI = Uri.parse(WebViewFragment
                        .getGoogleMaketLink(packageName));
                  }

                  startActivity(new Intent(
                      Intent.ACTION_VIEW, URI));
                }
              });
        } else if (action.equals(ACTION_INVALID_API)) {
          if (BaseFragmentActivity.this.isNavigable()) {
            BaseFragmentActivity.this.setNavigable(false);
          }
          builder = new CenterButtonDialogBuilder(BaseFragmentActivity.this, true);
          builder.setMessage(R.string.application_version_invalid);
          builder.setPositiveButton(R.string.go_to_google_play,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  if (!BaseFragmentActivity.this
                      .isNavigable()) {
                    BaseFragmentActivity.this
                        .setNavigable(true);
                  }

                  final String packageName = getPackageName();
                  Uri URI;
                  try {
                    URI = Uri.parse(WebViewFragment
                        .getGooglePlayLink(packageName));
                  } catch (android.content.ActivityNotFoundException anfe) {
                    URI = Uri.parse(WebViewFragment
                        .getGoogleMaketLink(packageName));
                  }
                  startActivity(new Intent(
                      Intent.ACTION_VIEW, URI));
                }
              });
          builder.setNegativeButton(R.string.later,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  if (!BaseFragmentActivity.this.isNavigable()) {
                    BaseFragmentActivity.this.setNavigable(true);
                  }
                  finish();
                  System.exit(0);
                }
              });
        }
        builder.setCancelable(false);
        mDialogFlowMessage = builder.create();
        mDialogFlowMessage.setCanceledOnTouchOutside(false);
        mDialogFlowMessage.show();
      }
    };
    IntentFilter filter = new IntentFilter(ACTION_INVALID_PASSWORD);
    filter.addAction(ACTION_INVALID_EMAIl);
    filter.addAction(ACTION_ACCOUNT_DISABLED);
    filter.addAction(ACTION_INVALID_VERSION);
    filter.addAction(ACTION_INVALID_API);
    LocalBroadcastManager.getInstance(getApplicationContext())
        .registerReceiver(mInvalidPasswordReceiver, filter);
  }

  private void unregisterInvalidPassword() {
    if (mInvalidPasswordReceiver != null) {
      LocalBroadcastManager.getInstance(getApplicationContext())
          .unregisterReceiver(mInvalidPasswordReceiver);
    }
  }

  private void logout() {
    // removeInfoWhenLogout();
    UserPreferences.getInstance().setIsLogout(true);

    // Stop linphone service
    Intent linphone = new Intent(getApplicationContext(),
        LinphoneService.class);
    stopService(linphone);
    // Goto LoginActivity
    Intent login = new Intent(getApplicationContext(), SignUpActivity.class);
    startActivity(login);
    finish();
  }

  private void removeInfoWhenLogout() {
    Log.d(TAG, "removeInfoWhenLogout");
    AuthenticationUtil.clearAuthenticationData();
  }

  public void replaceFragment(int placeHolder, BaseFragment fragment,
      String tag) {
    String backstack = TAB_BACKSTACK;
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,
        R.anim.fragment_exit, R.anim.fragment_pop_enter,
        R.anim.fragment_pop_exit);
    fragmentTransaction.replace(placeHolder, fragment, tag);
    fragmentTransaction.addToBackStack(backstack);
    fragmentTransaction.commitAllowingStateLoss();

  }

  @Override
  public ImageLoader getImageLoader() {
    return AndGApp.get().getImageLoader();
  }

  @Override
  public DfeApi getDfeApi() {
    return AndGApp.get().getDfeApi();
  }

  @Override
  public void showErrorDialog(String s, String s1, boolean flag) {

  }

  private void handlerLoading(boolean isShow) {
    if (mLoadingLayout == null) {
      ViewGroup root = (ViewGroup) this.getWindow().getDecorView()
          .findViewById(android.R.id.content);

      // create view for loading
      mLoadingLayout = LayoutInflater.from(this).inflate(
          R.layout.view_loading, null);
      root.addView(mLoadingLayout);
    }
    if (isShow) {
      mLoadingLayout.setVisibility(View.VISIBLE);
    } else if (mLoadingLayout != null) {
      mLoadingLayout.setVisibility(View.GONE);
    }
  }

  public void showLoading() {
    handlerLoading(true);
  }

  public void hideLoading() {
    handlerLoading(false);
  }

  public boolean isSaveInstace() {
    return mIsSaveInstance;
  }

  protected boolean isActionbarShowed() {
    return true;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupActionbar() {
    if (!isActionbarShowed()) {
      if (Build.VERSION.SDK_INT < 11) {
        if (getSupportActionBar() != null) {
          getSupportActionBar().hide();
          getSupportActionBar().getCustomView().setVisibility(View.GONE);
        }
      } else {
        if (getActionBar() != null) {
          getActionBar().hide();
          getActionBar().getCustomView().setVisibility(View.GONE);
        }
      }
    }
  }

  public void showActionBar() {
    if (Build.VERSION.SDK_INT < 11) {
      if (getSupportActionBar() != null) {
        getSupportActionBar().show();
        getSupportActionBar().getCustomView().setVisibility(View.VISIBLE);
      }
    } else {
      if (getActionBar() != null) {
        getActionBar().show();
        getActionBar().getCustomView().setVisibility(View.VISIBLE);
      }
    }
  }

  public void hideActionBar() {
    if (Build.VERSION.SDK_INT < 11) {
      if (getSupportActionBar() != null) {
        getSupportActionBar().hide();
        getSupportActionBar().getCustomView().setVisibility(View.GONE);
      }
    } else {
      if (getActionBar() != null) {
        getActionBar().hide();
        getActionBar().getCustomView().setVisibility(View.GONE);
      }
    }
  }
}
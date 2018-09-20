package com.application.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.AndGApp;
import com.application.BadgeConfig;
import com.application.Config;
import com.application.actionbar.CustomActionBar;
import com.application.actionbar.CustomActionBarFactory;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.chat.ChatUtils.CallInfo;
import com.application.chat.MessageClient;
import com.application.common.webview.ActionParam;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.ConversationRequest;
import com.application.connection.request.DeleteConversationRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.GetExtraPageRequest;
import com.application.connection.request.GetTotalUnreadMesageRequest;
import com.application.connection.request.GetUpdateInfoFlagRequest;
import com.application.connection.request.InstallCountRequest;
import com.application.connection.request.MarkReadsRequest;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.ConversationResponse;
import com.application.connection.response.DeleteConversationResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.GetExtraPageResponse;
import com.application.connection.response.GetTotalUnreadMesageResponse;
import com.application.connection.response.GetUpdateInfoFlagResponse;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.InstallCountResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.MarkReadsResponse;
import com.application.connection.response.UnlockResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.ConversationItem;
import com.application.entity.NotificationMessage;
import com.application.event.ConversationChangeEvent;
import com.application.event.ConversationEvent;
import com.application.event.DetailPictureEvent;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.navigationmanager.NavigationManager;
import com.application.payment.RetryPurchaseHandler;
import com.application.payment.RetryPurchaseHandler.OnRetryPurchaseListener;
import com.application.service.ApplicationNotificationManager;
import com.application.service.ChatService;
import com.application.service.DataFetcherService;
import com.application.status.IStatusChatChanged;
import com.application.status.MessageInDB;
import com.application.status.StatusController;
import com.application.ui.account.ProfileRegisterActivity;
import com.application.ui.backstage.DetailPictureBackstageApproveActivity;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.ui.buzz.BuzzDetail;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.buzz.ShareMyBuzzFragment;
import com.application.ui.connection.ConnectionFragment;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.EmojiTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.hotpage.HotPagePeopleFragment;
import com.application.ui.notification.NotificationFragment;
import com.application.ui.profile.AccountStatus;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.ui.profile.SliderProfileFragment;
import com.application.ui.settings.OnLogoutTask;
import com.application.uploadmanager.CustomUploadService;
import com.application.uploadmanager.UploadService;
import com.application.util.ConversationComparator;
import com.application.util.FreePageUtil;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.NetworkListener;
import com.application.util.NetworkListener.OnNetworkListener;
import com.application.util.NotificationUtils;
import com.application.util.Utility;
import com.application.util.preferece.AgeVerificationPrefers;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.leolin.shortcutbadger.ShortcutBadger;
import org.linphone.LinphoneService;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;


public class MainActivity extends SlidingFragmentActivity implements OnClickListener,
    ResponseReceiver, OnItemClickListener, OnRetryPurchaseListener, OnNetworkListener, OnLogoutTask,
    CustomActionBarActivity {

  public static final int UPDATE_NAME = 0;
  public static final int UPDATE_CONNECTIONS = 1;
  public static final int UPDATE_NOTIFICATIONS = 3;
  public static final int UPDATE_POINTS = 4;
  public static final int UPDATE_AVATAR = 5;
  public static final String TAG_FRAGMENT_SHARE_MY_BUZZ = "share_my_buzz";
  public static final String TAG_FRAGMENT_MEETPEOPLE = "meet_people";
  public static final String TAG_FRAGMENT_HOT_PAGE = "hot_page";
  public static final String TAG_FRAGMENT_MY_BUZZ = "buzz";
  public static final String TAG_FRAGMENT_MY_PROFILE = "my_profile";
  public static final String TAG_FRAGMENT_MY_PAGE = "my_page";
  public static final String TAG_FRAGMENT_CONNECTION = "connection";
  public static final String TAG_FRAGMENT_SETTING = "settings";
  public static final String TAG_FRAGMENT_SETTING_BLOCKED_USERS = "settings_blocked_users";
  public static final String TAG_FRAGMENT_SETTING_DEACTIVATE_ACCOUNT_CONFIRM = "settings_deactivate_account_confirm";
  public static final String TAG_FRAGMENT_SETTING_DEACTIVATE_ACCOUNT = "settings_deactivate_account";
  public static final String TAG_FRAGMENT_CHAT = "chat";
  public static final String TAG_FRAGMENT_BUZZ_DETAIL = "buzz_detail";
  public static final String TAG_FRAGMENT_NOTIFICATION = "notification";
  public static final String TAG_FRAGMENT_POINT = "point";
  public static final String TAG_FRAGMENT_HOW_TO_USE = "how_to_use";
  public static final String TAG_FRAGMENT_SUPPORT = "support";
  public static final String TAG_FRAGMENT_URL_CMCODE = "url_cmcode";
  public static final String TAG_HOT_PAGE = "hot_page_fragment";
  public static final String TAG_HOME_FRAGMENT = "home_fragment";
  public static final String TAG_FRAGMENT_FOOTPRINT = "footprint";
  public static final String TAG_FRAGMENT_MY_CHATS = "my_chat";
  public static final String TAG_FRAGMENT_CONTACT = "contact";
  public static final String TAG_FRAGMENT_INFORMATION = "information";
  public static final int FUNCTION_MEET_PEOPLE = 1;
  public static final int FUNCTION_BUZZ = 2;
  public static final int FUNCTION_MY_PROFILE = 5;
  public static final int FUNCTION_CONNECTIONS = 6;
  public static final int FUNCTION_MY_CHATS = 7;
  public static final int FUNCTION_NOTIFICATIONS = 8;
  public static final int FUNCTION_POINTS = 9;
  public static final int FUNCTION_SETTINGS = 10;
  public static final int FUNCTION_SHARE_MY_BUZZ = 15;
  public static final int FUNCTION_EXTRA_PAGE = 16;
  public static final int FUNCTION_HOW_TO_USE = 17;
  public static final int FUNCTION_SUPPORT = 18;
  public static final int FUNCTION_MY_PAGE = 19;
  public static final int FUNCTION_FOOT_PRINT = 20;
  public static final int FUNCTION_HOME_FRAGMENT = 22;
  public static final int FUNCTION_INFORMATION = 24;
  public static final int FUNCTION_CONTACT = 25;
  public static final int FUNCTION_URL_CMCODE = 26;
  public static final int FUNCTION_HOT_PAGE = 27;
  public static final String ACTION_STREAMING_ERROR = "andg.main.streaming.error";
  public static final String ACTION_KEYBOARD_CHANGE = "andg.main.keyboard.change";
  public static final String EXTRA_KEYBOARD_STATUS = "andg_main_keyboard_status";
  public static final String EXTRA_KEYBOARD_HEIGHT = "andg_main_keyboard_height";
  public static final int LOADER_CONVERSATION = 0;
  public static final int LOADER_ID_BASIC_USER_INFO = 1;
  public static final int LOADER_DELETE_CONVERSATION = 2;
  public static final int LOADER_ID_TOTAl_UNREAD_MSG = 3;
  public static final int LOADER_ID_MARK_AS_READ = 4;
  public final static int LOADER_ID_CHECK_UNLOCK = 5;
  public final static int LOADER_ID_UNLOCK_TYPE = 6;
  public final static int LOADER_ID_EXTRA_PAGE = 7;
  public final static int LOADER_GET_UPDATE_INFO_FLAG = 8;
  public final static int LOADER_GET_MY_INFO = 9;
  public final static int LOADER_INSTALL_COUNT = 10;
  public final static int LOADER_ID_USER_INFO_CALL = 11;
  public static final int TAKE = 20;
  private static final String TAG = "MainActivity";
  private final static int HIDE_USER_TIME_IN_SECONDS = 40;
  //end
  private static final String KEY_TIME_REMAIN_HIDDEN = "time_remain_hidden";
  private static final int MAX_LINE = 1;
  private static final int MAX_SHOW_PENDING = 2;
  /**
   * Update Main Menu Item
   */
  public static boolean checkactionbar = true;
  private final int REQUEST_WEB_VIEW = -1;
  public int mRemain = 0;
  public String mHiddenUserId;
  public String mHiddenUserName;
  public String mHiddenUserAvatarId;
  public int mHiddenUserGender;
  public List<ChatMessage> mHiddenChatMessageList;
  public int selectedIndex = 0;
  public boolean isBackPressed = false;
  public CustomUploadService mUploadService;
  String[] listMarkAsReadUser = null;
  /**************************
   * SHAKE TO CHAT
   **************************/
  private LinearLayout mLlInformation;
  private MainHandler mMainHandler;
  private List<ChatFragment.ChatWithHiddenUserListener> mHiddenUserListeners;
  private Timer mHiddenUserTimer;
  private TextView mtxtHowToUse;
  private TextView mtxtSupport;
  private TextView mtxtContact;
  //View in left menu
  private RelativeLayout mLlMe;
  private TextView mTxtHome;
  private LinearLayout mLlNotifications;
  private TextView mTxtCheckTimeline;
  private LinearLayout mLlChats;
  private TextView mTxtProfile;
  private LinearLayout mLlPoints;
  private TextView mTxtSettings;
  private TextView mtxtEmptyChatView;
  private TextView mtxtShake;
  private TextView mtxtAddFriend;
  private TextView mtxtEdit;
  private TextView mtxtDone;
  private LinearLayout mlnHeadNormal;
  private LinearLayout mlnHeadEdit;
  private PullToRefreshListView pullToRefreshListView;
  private ListView mlvConversation;
  private LinearLayout mMenuLeftRoot;
  private ConversationAdapter mConversationAdapter;
  private ArrayList<ConversationItem> mConversationList = new ArrayList<ConversationItem>();
  private boolean isRequestDeleteAll = false;
  private String mTimeSpan = "";
  private String mFirstTime = "";
  private BroadcastReceiver mBroadcastReceiver;
  private boolean isHasOnConversationList = false;
  private String mNewMessage;
  private String mNewDate;
  private Message mMessage;
  // get finish register flag
  private int finishRegisterFlag = Constants.FINISH_REGISTER_NO;
  @SuppressWarnings("unused")
  private boolean isAddFriend = false;
  private String messageType = ChatMessage.PP;
  private String messageFromUserId;
  private ProgressDialog progressDialog;
  private AlertDialog dialogCanNotStreaming;
  private boolean mNeedRefreshConversationList = false;
  private ExtraPage mExtraPageSelected;
  // AdView
  private LinearLayout mAdLayout;
  private NetworkListener mNetworkListener;
  private TextView mTxtUserName;
  private TextView mTxtPointsInTop;
  private TextView mTxtPointsInLeft;
  private BadgeTextView mTxtNotisInLeft;
  private BadgeTextView mTxtChatsInLeft;
  private ImageView mImgAvatarInLeft;


  /**
   * update badge when save unread message only update badge on chat text cause it's not show
   * notification => ignore xiaomi badge
   */
  private void updateBadgeNumber(int unreadMessage) {
    ShortcutBadger.applyCount(getApplicationContext(), unreadMessage);
  }

  private TextView mTxtRightSetting;
  private CustomActionBar mActionBar;
  private NavigationManager mNavigationManager;
  private boolean mStateSaved;
  private String mBuzzDetailId = "";
  private String mUserId = "";
  private LinearLayout mLinLayFreePoints;
  private ProgressDialog mDialog;
  private int numberShowPending = 0;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      String userIdToSend = UserPreferences.getInstance()
          .getCurentFriendChat();
      if (userIdToSend != null) {
        String[] userId = new String[]{userIdToSend};
        markAsRead(userId);
      }
      mConversationList.clear();
      mConversationAdapter.notifyDataSetChanged();
      showUnreadMessage();
      requestConversation(mFirstTime);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      requestConversation(mTimeSpan);
    }
  };
  private boolean mIsBound = false;
  private ServiceConnection mUploadConnection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mUploadService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mUploadService = (CustomUploadService) ((UploadService.UploadServiceBinder) service)
          .getService();
      Fragment fragment = getSupportFragmentManager().findFragmentById(
          R.id.activity_main_content);
      if (fragment instanceof ChatFragment) {
        ((ChatFragment) fragment)
            .onUploadServiceConnected(mUploadService);
      }
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    EventBus.getDefault().register(this);

    // Check finish register
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      finishRegisterFlag = bundle
          .getInt(Constants.INTENT_FINISH_REGISTER_FLAG);
      // remove extra key flag after get data
      getIntent().removeExtra(Constants.INTENT_FINISH_REGISTER_FLAG);
    }

    // Setup action bar
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    initNavigationManager(savedInstanceState);
    initCustomActionBar();

    // Bind upload service
    doBindUploadService();

    UserPreferences userPreferences = UserPreferences.getInstance();

    // Clear previous data
    userPreferences.removeCurentFriendChat();

    // Setting view
    initView();

    // Check finish register
    checkFinishRegister();

    // Setup default fragment
    Fragment fragment = handleMenu(FUNCTION_HOME_FRAGMENT, false);
    if (fragment != null) {
      mActionBar.syncActionBar(fragment);
    }

    // Initial conversation view
    initConversationView();
    mNeedRefreshConversationList = false;

    // Set keyboard controller
    setKeyboardController();

    // Retry purchase
    RetryPurchaseHandler retryPurchaseHandler = new RetryPurchaseHandler(
        getApplicationContext(), this);
    retryPurchaseHandler.retryPurchase();
    registerReceiveMessage();

    // Check and sync default sticker from server
    requestSticker();
    // Check and sync default banned word from server
    requestDirtyWord();
    // Request notification setting register GCM
    initialNotificationVew();
    DataFetcherService
        .startLoadNotificationSetting(getApplicationContext());
    NotificationMessage notiMessage = (NotificationMessage) getIntent()
        .getParcelableExtra(
            WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE);
    if (notiMessage != null) {
      mNotificationMessage = notiMessage;
      actionNotification(notiMessage);
      LogUtils.i(TAG, "Noti type: " + notiMessage.getNotiType());
    }

    // Request extra page from sliding menu left
    int gender = userPreferences.getGender();
    int ageVerification = userPreferences.getAgeVerification();
    if (gender == UserSetting.GENDER_MALE
        || (gender == UserSetting.GENDER_FEMALE
        && ageVerification == Constants.AGE_VERIFICATION_VERIFIED)) {
      requestExtraPage();
    }

    // Register network
    mNetworkListener = new NetworkListener(this, this);
    mNetworkListener.register();
    mMainHandler = new MainHandler(this);

    // Update status controller
    StatusController.getInstance(getApplicationContext())
        .addStatusChangedListener(mConversationAdapter);

    // Always login Linphone when main activity opened.
    LinphoneService.startLogin(getApplicationContext());

  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState != null) {
      mRemain = savedInstanceState.getInt(KEY_TIME_REMAIN_HIDDEN);
    }
  }

  private void requestSticker() {
    String token = UserPreferences.getInstance().getToken();
    DataFetcherService.startCheckSticker(this, token);
  }

  private void requestDirtyWord() {
    DataFetcherService.startLoadDirtyWord(this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    String buzzId = intent.getStringExtra(TAG_FRAGMENT_BUZZ_DETAIL);

    if (buzzId != null && !buzzId.equals("")) {
      BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(buzzId,
          Constants.BUZZ_TYPE_NONE);
      getNavigationManager().addPage(buzzDetailFragment);
    }
    NotificationMessage notiMessage = (NotificationMessage) intent
        .getParcelableExtra(WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE);
    if (notiMessage != null) {
      mNotificationMessage = notiMessage;
      actionNotification(notiMessage);
    }

    if (intent.hasExtra(FreePageUtil.ACT_INTENT)) {
      String url = intent.getStringExtra(FreePageUtil.ACT_INTENT);
      WebViewFragment fragment = WebViewFragment.newInstance(url, "");
      mNavigationManager.addPage(fragment);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    mStateSaved = false;
    if (mNeedRefreshConversationList) {
      mConversationAdapter.clear();
      requestConversation(mFirstTime);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    mStateSaved = false;
  }

  @Override
  protected void onResume() {
    super.onResume();
    LogUtils.i(TAG, "onResume() start");
    requestUserinfo();
    Preferences preferences = Preferences.getInstance();
    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    if (preferences.isInstall()) {
      if (!preferences.isAttachCmcode() && googleReviewPreference.isEnableBrowser()) {
        preferences.saveIsAttachCmcode();
        String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        String url = String.format(Config.COUNTER_SERVER, androidId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
      }
    } else {
      // Request to count and check user info after count
      String android_id = Secure.getString(getContentResolver(),
          Secure.ANDROID_ID);
      InstallCountRequest data = new InstallCountRequest(android_id);
      restartRequestServer(LOADER_INSTALL_COUNT, data);
    }
    LogUtils.i(TAG, "onResume() finish");
  }

  private void setUpSlidingMenu() {
    SlidingMenu slidingMenu = getSlidingMenu();
    slidingMenu.setShadowWidthRes(R.dimen.activity_main_shadow_width);
//        slidingMenu.setShadowDrawable(R.drawable.activity_main_shadow);
//        slidingMenu
//                .setSecondaryShadowDrawable(R.drawable.activity_main_shadow);
    slidingMenu
        .setBehindOffsetRes(R.dimen.activity_main_slidingmenu_offset);
    slidingMenu.setFadeDegree(0.35f);
    slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    slidingMenu.setMode(SlidingMenu.RIGHT);
    slidingMenu.setOnOpenedListener(new OnOpenedListener() {
      @Override
      public void onOpened() {
        Utility.hideSoftKeyboard(MainActivity.this);
        clearAllFocus();
        mConversationAdapter.notifyDataSetChanged();
        requestUserinfo();
        if (mNavigationManager.getActivePage() instanceof ChatFragment) {
          ChatFragment chatFragment = (ChatFragment) mNavigationManager.getActivePage();
          chatFragment.hidePanel();
          chatFragment.hideChatMoreOptions();
        }
//                if (mNavigationManager.getActivePage() instanceof SliderProfileFragment){
//                    SliderProfileFragment sliderProfileFragment = (SliderProfileFragment) mNavigationManager.getActivePage();
//                    sliderProfileFragment.hideChatMoreOptions();
//                }
      }
    });

    slidingMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
      @Override
      public void onOpen() {
        Fragment fragment = mNavigationManager.getActivePage();
        if (fragment instanceof ChatFragment) {
          ((ChatFragment) fragment).hideChatMoreOptions();
          ((ChatFragment) fragment).hidePanel();
        }
        if (fragment instanceof SliderProfileFragment) {
          ((SliderProfileFragment) fragment).hideChatMoreOptions();
        }
      }
    });

  }

  private void requestUserinfo() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String userId = userPreferences.getUserId();
    String token = userPreferences.getToken();

    UserInfoRequest infoRequest = null;
    if (userId == null || userId.length() == 0) {
      infoRequest = new UserInfoRequest(token);
    } else {
      infoRequest = new UserInfoRequest(token, userId);
    }

    restartRequestServer(LOADER_GET_MY_INFO, infoRequest);
  }

  private void clearAllFocus() {
    Fragment buzzFragment = getSupportFragmentManager().findFragmentByTag(
        TAG_FRAGMENT_MY_BUZZ);
    if (buzzFragment != null) {
      if (buzzFragment instanceof BuzzFragment) {
        ((BuzzFragment) buzzFragment).clearAllFocusableFields();
      }
    }
  }

  private void setKeyboardController() {
    View menu_left = findViewById(R.id.activity_main_menu_left_root);
    View menu_right = findViewById(R.id.activity_main_menu_right_root);
    View center = findViewById(R.id.activity_main_content);
    Utility.hideKeyboard(this, menu_left);
    Utility.hideKeyboard(this, menu_right);
    Utility.hideKeyboard(this, center);
  }

  @SuppressLint("CutPasteId")
  private void initView() {
    // set layout for menu left
    setBehindContentView(R.layout.activity_main_menu_left);
    // set layout for center
    setContentView(R.layout.activity_main);
    // set layout for menu right
    getSlidingMenu().setSecondaryMenu(R.layout.activity_main_menu_right);
    // setup SldingMenu
    setUpSlidingMenu();
    initNavigationBar();
    setUpNavigationBar();
    mDialog = new ProgressDialog(this);
    mDialog.setMessage(getString(R.string.waiting));
    mDialog.setCancelable(false);
    mDialog.setCanceledOnTouchOutside(false);

    mMenuLeftRoot = (LinearLayout) findViewById(R.id.activity_main_menu_left_layout);
    mLinLayFreePoints = (LinearLayout) findViewById(R.id.activity_main_menu_left_ll_free_points);

    UserPreferences userPreferences = UserPreferences.getInstance();
    GoogleReviewPreference reviewPreference = new GoogleReviewPreference();
    if (reviewPreference.isEnableGetFreePoint()
        && userPreferences.getGender() == UserSetting.GENDER_MALE) {
      mLinLayFreePoints.setVisibility(View.GONE);
/*            findViewById(R.id.activity_main_menu_left_underline_free_points)
                    .setVisibility(View.VISIBLE);*/
    } else {
      mLinLayFreePoints.setVisibility(View.GONE);
/*            findViewById(R.id.activity_main_menu_left_underline_free_points)
                    .setVisibility(View.GONE);*/
    }

    mImgAvatarInLeft = (ImageView) findViewById(R.id.activity_main_menu_left_avatar);
    mTxtUserName = (TextView) findViewById(R.id.activity_main_menu_left_username);
    mTxtUserName.setMaxLines(MAX_LINE);
    mTxtPointsInTop = (TextView) findViewById(R.id.activity_main_menu_left_points_number);
    mTxtPointsInLeft = (TextView) findViewById(R.id.activity_main_menu_left_text_points_number);
    mTxtNotisInLeft = (BadgeTextView) findViewById(R.id.activity_main_menu_left_image_notis_number);
    mTxtChatsInLeft = (BadgeTextView) findViewById(R.id.activity_main_menu_left_image_chats_number);

    mLlMe = (RelativeLayout) findViewById(R.id.activity_main_menu_left_me);
    mTxtHome = (TextView) findViewById(R.id.activity_main_menu_left_text_top);
    mLlNotifications = (LinearLayout) findViewById(R.id.activity_main_menu_left_ll_notifications);
    mTxtCheckTimeline = (TextView) findViewById(R.id.activity_main_menu_left_text_check_timeline);
    mLlChats = (LinearLayout) findViewById(R.id.activity_main_menu_left_ll_chats);
    mTxtProfile = (TextView) findViewById(R.id.activity_main_menu_left_text_profile);
    mLlPoints = (LinearLayout) findViewById(R.id.activity_main_menu_left_ll_points);
    mTxtSettings = (TextView) findViewById(R.id.activity_main_menu_left_text_settings);
    mtxtHowToUse = (TextView) findViewById(R.id.activity_main_menu_left_text_how_to_use);
    mtxtSupport = (TextView) findViewById(R.id.activity_main_menu_left_text_support);
    mtxtContact = (TextView) findViewById(R.id.activity_main_menu_left_contact_us);
    mTxtRightSetting = (TextView) findViewById(R.id.iv_main_menu_right_settings);

    mLlInformation = (LinearLayout) findViewById(R.id.activity_main_menu_left_ll_infomation);
    mLlInformation.setOnClickListener(this);

    mLinLayFreePoints.setOnClickListener(this);

    mtxtHowToUse.setOnClickListener(this);
    mtxtSupport.setOnClickListener(this);
    mtxtContact.setOnClickListener(this);

    mLlMe.setOnClickListener(this);
    mTxtHome.setOnClickListener(this);
    mLlNotifications.setOnClickListener(this);
    mTxtCheckTimeline.setOnClickListener(this);
    mLlChats.setOnClickListener(this);
    mTxtProfile.setOnClickListener(this);
    mLlPoints.setOnClickListener(this);
    mTxtSettings.setOnClickListener(this);
    mTxtRightSetting.setOnClickListener(this);

    if (userPreferences != null) {
      loadAvatar();
      String username = userPreferences.getUserName();
      mTxtUserName.setText(username);
      int points = userPreferences.getNumberPoint();
      String format = getResources().getString(R.string.point_suffix);
      String pointStr = MessageFormat.format(format, points);
      mTxtPointsInTop.setText(pointStr);
      mTxtPointsInLeft.setText(pointStr);

      int numNotifs = userPreferences.getNumberNotification();
      int numChats = userPreferences.getNumberUnreadMessage();
      // display notification
      mTxtNotisInLeft.setTextNumber(numNotifs);
      // display unreadmessage
      mTxtChatsInLeft.setTextNumber(numChats);
//            mActionBar.displayUnreadChatMessage(numChats);
    }
    final View rootView = findViewById(R.id.activity_main_root);
    final int size = getResources().getDimensionPixelSize(
        R.dimen.height_keyboard);
    rootView.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          private boolean wasOpened;

          @Override
          public void onGlobalLayout() {
            int heightDiff = rootView.getRootView().getHeight()
                - rootView.getHeight();
            boolean isOpen = heightDiff > size;
            if (isOpen == wasOpened) {
              return;
            }
            wasOpened = isOpen;
            Intent intent = new Intent(ACTION_KEYBOARD_CHANGE);
            intent.putExtra(EXTRA_KEYBOARD_STATUS, isOpen);
            intent.putExtra(EXTRA_KEYBOARD_HEIGHT, heightDiff);
            LocalBroadcastManager.getInstance(
                getApplicationContext()).sendBroadcast(intent);
          }
        });

  }

  /**
   * HoanDC: initial view in Conversation
   */
  private void initConversationView() {
    mtxtShake = (TextView) findViewById(R.id.tvMarkAsRead);
    mtxtAddFriend = (TextView) findViewById(R.id.tvDeleteSelected);
    mtxtEdit = (TextView) findViewById(R.id.tvDeleteAll);
    mtxtDone = (TextView) findViewById(R.id.sliding_menu_right_top_control_edit_txt_done);
    mlnHeadNormal = (LinearLayout) findViewById(R.id.sliding_menu_right_head_normal);
    mlnHeadEdit = (LinearLayout) findViewById(R.id.sliding_menu_right_head_edit);
    pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.sliding_menu_right_list_chat);
    mlvConversation = pullToRefreshListView.getRefreshableView();
    mtxtEmptyChatView = new TextView(this);
    mtxtEmptyChatView.setBackgroundColor(getResources().getColor(
        android.R.color.transparent));
    mtxtEmptyChatView.setText("");
    mtxtEmptyChatView.setLayoutParams(new AbsListView.LayoutParams(
        AbsListView.LayoutParams.MATCH_PARENT,
        AbsListView.LayoutParams.MATCH_PARENT));
    mtxtEmptyChatView.setGravity(Gravity.CENTER);
    mlvConversation.setEmptyView(mtxtEmptyChatView);
    pullToRefreshListView.setOnRefreshListener(onRefreshListener);
    pullToRefreshListView.setMode(Mode.BOTH);
    Resources resource = getResources();
    pullToRefreshListView.setPullLabelFooter(resource
        .getString(R.string.pull_to_load_more_pull_label));
    pullToRefreshListView.setReleaseLabelFooter(resource
        .getString(R.string.pull_to_load_more_release_label));
    mlvConversation.setCacheColorHint(getResources().getColor(R.color.transparent));
    // register event onclick
    mtxtShake.setOnClickListener(this);
    mtxtEdit.setOnClickListener(this);
    mtxtDone.setOnClickListener(this);
    mtxtAddFriend.setOnClickListener(this);
    mlvConversation.setOnItemClickListener(this);
    mConversationAdapter = new ConversationAdapter(this,
        R.layout.activity_main_menu_right, mConversationList);
    mlvConversation.setAdapter(mConversationAdapter);
    requestConversation(mTimeSpan);
    requestExtraPage();
  }

  /**
   * Receive notification gcm and handle.
   */
  private void actionNotification(NotificationMessage notificationMessage) {
    int type = notificationMessage.getNotiType();
    getSlidingMenu().showContent(true);

    UserPreferences userPreferences = UserPreferences.getInstance();
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
        showBuzzDetail(notificationMessage, true);
        break;
      case Constants.NOTI_REPLY_YOUR_COMMENT:
      case Constants.NOTI_APPROVE_COMMENT:
      case Constants.NOTI_DENIED_COMMENT:
      case Constants.NOTI_APPROVE_SUB_COMMENT:
      case Constants.NOTI_DENI_SUB_COMMENT:
        showBuzzDetail(notificationMessage, false);
        break;
      case Constants.NOTI_CHECK_OUT_UNLOCK:
      case Constants.NOTI_FAVORITED_UNLOCK:
      case Constants.NOTI_UNLOCK_BACKSTAGE:
      case Constants.NOTI_FRIEND:
      case Constants.NOTI_ONLINE_ALERT:
        showProfile(notificationMessage);
        break;
      case Constants.NOTI_APPROVE_USERINFO:
      case Constants.NOTI_APART_OF_USERINFO:
      case Constants.NOTI_DENIED_USERINFO:
        showProfile(notificationMessage.getOwnerId());
        break;
      case Constants.NOTI_DENIED_BUZZ_IMAGE:
      case Constants.NOTI_DENIED_BUZZ_TEXT:
        showMyPost(notificationMessage);
        break;
      case Constants.NOTI_CHAT_TEXT:
        replaceFragment(ChatFragment.newInstance(
            notificationMessage.getUserid(), true), TAG_FRAGMENT_CHAT);
        break;
      case Constants.NOTI_DAYLY_BONUS:
        Fragment fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
        mNavigationManager.addPage(fragment);
        // Intent buyPoint = new Intent(this, BuyPointActivity.class);
        // startActivity(buyPoint);
        break;
      case Constants.NOTI_BACKSTAGE_APPROVED:
        Intent intent = new Intent(this,
            DetailPictureBackstageApproveActivity.class);
        intent.putExtras(ProfilePictureData
            .parseDataToBundle(notificationMessage.getImageId()));
        startActivity(intent);
        break;
      case Constants.NOTI_DENIED_BACKSTAGE:
        showListBackstage(notificationMessage.getOwnerId());
        break;

      case Constants.NOTI_FROM_FREE_PAGE:
        String url = notificationMessage.getUrl();
        ActionParam actionParam = new ActionParam();
        if (url.contains(actionParam.USER_PROFILE)) {
          int idIndex = url.lastIndexOf(actionParam.USER_PROFILE_ID)
              + actionParam.USER_PROFILE_ID.length();
          String id = url.substring(idIndex, url.length());
          if (!TextUtils.isEmpty(id)) {
            MyProfileFragment myProfileFragment = MyProfileFragment
                .newInstance(id);
            replaceFragment(myProfileFragment, "");
          }
        } else {
          String content = notificationMessage.getContent();
          WebViewFragment webViewFragment = WebViewFragment.newInstance(
              url, content);
          replaceFragment(webViewFragment, "");
        }
        break;
      case Constants.NOTI_REQUEST_CALL:
        //TODO process click noti request call
        showProfile(notificationMessage);
        break;
      default:
        break;
    }
  }

  private void markAsRead(String[] userId) {
    String token = UserPreferences.getInstance().getToken();
    listMarkAsReadUser = userId;
    MarkReadsRequest markReadsRequest = new MarkReadsRequest(token, userId);
    restartRequestServer(LOADER_ID_MARK_AS_READ, markReadsRequest);
  }

  private void restartRequestBasicUserInfo(String userID) {
    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getUserId().equals(userID)) {
      return;
    }
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, userID);
    restartRequestServer(LOADER_ID_USER_INFO_CALL, request);
  }

  public void requestTotalUnreadMsg() {
    String token = UserPreferences.getInstance().getToken();
    GetTotalUnreadMesageRequest request = new GetTotalUnreadMesageRequest(
        token, mUserId);
    restartRequestServer(LOADER_ID_TOTAl_UNREAD_MSG, request);
  }

  /**
   *
   */
  private void requestConversation(String timeSpan) {
    String token = UserPreferences.getInstance().getToken();
    ConversationRequest conversationRequest = null;
    // conversationRequest = new ConversationRequest(token, TAKE);
    LogUtils.i(TAG, "requestConversation, timeSpan=" + timeSpan.length());
    if (timeSpan.length() == 0) {
      mtxtEmptyChatView.setText(R.string.common_loading);
      conversationRequest = new ConversationRequest(token, TAKE);
    } else {
      conversationRequest = new ConversationRequest(token, timeSpan, TAKE);
    }
    restartRequestServer(LOADER_CONVERSATION, conversationRequest);
  }

  private void requestExtraPage() {
    String token = UserPreferences.getInstance().getToken();
    GetExtraPageRequest extraPageRequest = new GetExtraPageRequest(token);
    restartRequestServer(LOADER_ID_EXTRA_PAGE, extraPageRequest);
  }

  /**
   * Setup Navigation bar for application
   */
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setVisibility(View.GONE);
  }

  public Fragment handleMenuAddUrl(int function, boolean hasAnimation, String url) {
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager
        .findFragmentById(R.id.activity_main_content);
    String tag = null;
    boolean isReplace = false;
    switch (function) {
      case FUNCTION_URL_CMCODE:
        tag = TAG_FRAGMENT_URL_CMCODE;
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_URL_CMCODE, url, "");
        isReplace = true;
        break;
      default:
        break;
    }
    if (null == fragment || null == tag) {
      throw new IllegalArgumentException("Function invalid:" + function);
    }
    mNavigationManager.switchPage(fragment, true);
    showContent();
    return fragment;
  }

  public Fragment handleMenu(int function, boolean hasAnimation) {
    LogUtils.i(TAG, "Add function,id=[" + function + "]");
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager
        .findFragmentById(R.id.activity_main_content);
    String tag = null;
    boolean isReplace = false;
    switch (function) {

      case FUNCTION_HOME_FRAGMENT:
        tag = TAG_HOME_FRAGMENT;
        if (!(fragment instanceof HomeFragment)) {
          fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE, finishRegisterFlag);
          if (finishRegisterFlag == Constants.FINISH_REGISTER_YES) {
            finishRegisterFlag = Constants.FINISH_REGISTER_NO;
          }
        }
        break;

      case FUNCTION_HOT_PAGE:
        tag = TAG_HOT_PAGE;
        if (!(fragment instanceof HotPagePeopleFragment)) {
          fragment = new HotPagePeopleFragment();
        }
        break;

      case FUNCTION_FOOT_PRINT:
        tag = TAG_FRAGMENT_FOOTPRINT;
        if (!(fragment instanceof WhoCheckYouOutFragment)) {
          fragment = new WhoCheckYouOutFragment();
        }
        break;
//            case FUNCTION_MY_CHATS:
//                tag = TAG_FRAGMENT_MY_CHATS;
//                if (!(fragment instanceof ConversationsFragment)) {
//                    fragment = new ConversationsFragment();
//                }
//                break;

      case FUNCTION_SHARE_MY_BUZZ:
        tag = TAG_FRAGMENT_SHARE_MY_BUZZ;
        if (!(fragment instanceof ShareMyBuzzFragment)) {
          fragment = new ShareMyBuzzFragment();
        }
        break;
      case FUNCTION_MEET_PEOPLE:
        tag = TAG_FRAGMENT_MEETPEOPLE;
        if (!(fragment instanceof MeetPeopleFragment)) {
          Utility.hideSoftKeyboard(this);
          fragment = MeetPeopleFragment.newInstance();
          if (finishRegisterFlag == Constants.FINISH_REGISTER_YES) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.ARGUMENT_FINISH_REGISTER_FLAG,
                finishRegisterFlag);
            fragment.setArguments(bundle);
            finishRegisterFlag = Constants.FINISH_REGISTER_NO;
          }
        }
        break;
      case FUNCTION_BUZZ:
        tag = TAG_FRAGMENT_MY_BUZZ;
        if (!(fragment instanceof BuzzFragment)) {
          fragment = new BuzzFragment();
        }
        break;
      case FUNCTION_MY_PAGE:
      case FUNCTION_MY_PROFILE:
        tag = TAG_FRAGMENT_MY_PAGE;
        if (!(fragment instanceof MyPageFragment)) {
          fragment = new MyPageFragment();
        }
        break;
      case FUNCTION_CONNECTIONS:
        tag = TAG_FRAGMENT_CONNECTION;
        if (!(fragment instanceof ConnectionFragment)) {
          fragment = ConnectionFragment.newInstance(false,
              ConnectionFragment.TAB_FAVORITE_INDEX);
        }
        break;
      case FUNCTION_SETTINGS:
        tag = TAG_FRAGMENT_SETTING;
        if (!(fragment instanceof SettingsFragment)) {
          fragment = new SettingsFragment();
        }
        break;
      case FUNCTION_NOTIFICATIONS:
        tag = TAG_FRAGMENT_NOTIFICATION;
        if (fragment instanceof NotificationFragment) {
          NotificationFragment notificationFragment = ((NotificationFragment) fragment);
          boolean isLike = notificationFragment.isOnlyLike();
          if (isLike) {
            notificationFragment.setOnlyLike(false);
            notificationFragment.refresh();
          }
          showContent();
          return null;
        } else {
          fragment = new NotificationFragment();
        }
        break;
      case FUNCTION_POINTS:
        fragment = handleMenuToFragmentPoint();
        if (fragment != null) {
          tag = TAG_FRAGMENT_POINT;
        } else {
          return null;
        }
        isReplace = true;
        break;
      case FUNCTION_EXTRA_PAGE:
        tag = "extras";
        Pattern p = Pattern.compile("%sid%");
        String token = UserPreferences.getInstance().getToken();
        Matcher m = p.matcher(mExtraPageSelected.url);
        String url = m.replaceAll(token);
        fragment = WebViewFragment.newInstance(
            WebViewFragment.PAGE_TYPE_WEB_VIEW, url,
            mExtraPageSelected.title);
        isReplace = true;
        break;

      //hiepuh
      case FUNCTION_INFORMATION:
        tag = TAG_FRAGMENT_INFORMATION;
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_INFORMATION);
        isReplace = true;
        break;
      //end

      case FUNCTION_HOW_TO_USE:
        tag = TAG_FRAGMENT_HOW_TO_USE;
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_HOW_TO_USE);
        isReplace = true;
        break;

      case FUNCTION_SUPPORT:
        tag = TAG_FRAGMENT_SUPPORT;
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_SUPPORT);
        isReplace = true;
        break;

      case FUNCTION_CONTACT:
        tag = TAG_FRAGMENT_CONTACT;
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_CONTACT);
        isReplace = true;
        break;

      default:
        break;
    }
    if (null == fragment || null == tag) {
      throw new IllegalArgumentException("Function invalid:" + function);
    }
    mNavigationManager.switchPage(fragment, isReplace);
    showContent();
    return fragment;
  }

  private Fragment handleMenuToFragmentPoint() {
    Fragment fragment = WebViewFragment
        .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
    return fragment;
  }

  @Override
  public void onClick(View v) {
    MainActivity.checkactionbar = false;
    switch (v.getId()) {
      //hiepuh
      case R.id.activity_main_menu_left_ll_infomation:
        handleMenu(FUNCTION_INFORMATION, true);
        break;
      //end
      case R.id.activity_main_menu_left_me:
        handleMenu(FUNCTION_MY_PAGE, true);
        break;
      case R.id.activity_main_menu_left_text_top:
        MainActivity.checkactionbar = true;
        changeTabActive(HomeFragment.TAB_MEET_PEOPLE);
        break;
      case R.id.activity_main_menu_left_ll_notifications:
        MainActivity.checkactionbar = false;
        handleMenu(FUNCTION_NOTIFICATIONS, true);
        break;
      case R.id.activity_main_menu_left_text_check_timeline:
        changeTabActive(HomeFragment.TAB_TIMELINE);
        break;
      // CUONGNV : Button setting left menu
      case R.id.iv_main_menu_right_settings:
        handleMenu(FUNCTION_SETTINGS, true);
        break;
      case R.id.activity_main_menu_left_ll_chats:
        changeTabActive(HomeFragment.TAB_CHAT);
        break;
      case R.id.activity_main_menu_left_text_profile:
        handleMenu(FUNCTION_MY_PROFILE, true);
        break;
      case R.id.activity_main_menu_left_ll_points:
        handleMenu(FUNCTION_POINTS, true);
        break;
      case R.id.activity_main_menu_left_text_settings:
        handleMenu(FUNCTION_SETTINGS, true);
        break;
      case R.id.tvMarkAsRead:
        if (mConversationList.size() > 0) {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              this, "", getString(R.string.confirm_mark_all_readed),
              true)
              .setPositiveButton("", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  String[] friends = new String[mConversationList.size()];
                  for (int i = 0; i < mConversationList.size(); i++) {
                    friends[i] = mConversationList.get(i).getFriendId();
                  }

                  markAsRead(friends);
                }
              })
              .create();
          confirmDialog.show();

          int dividerId = confirmDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = confirmDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(
                confirmDialog.getContext().getResources().getColor(R.color.transparent));
          }
        } else {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              this, "", getString(R.string.confirm_mark_all_readed),
              false)
              .setPositiveButton(0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
              })
              .create();
          confirmDialog.show();

          int dividerId = confirmDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = confirmDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(
                confirmDialog.getContext().getResources().getColor(R.color.transparent));
          }
        }
        break;
      case R.id.tvDeleteSelected:
        // Handle delete selected conversation
        if (mConversationList.size() > 0) {
          mtxtEdit.setEnabled(true);
          changeEditConversationStatus(true);
        } else {
          mtxtEdit.setEnabled(false);
        }

        break;
      case R.id.tvDeleteAll:
        if (mConversationList.size() >= 0) {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              this, "",
              getString(R.string.confirm_delete_all_messages), true)
              .setPositiveButton(0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  List<String> userIdList = new ArrayList<String>();
                  for (int i = 0; i < mConversationList.size(); i++) {
                    String userId = mConversationList.get(i)
                        .getFriendId();
                    userIdList.add(userId);
                  }
                  requestDeleteConversation(userIdList);
                }
              })
              .create();
          confirmDialog.show();

          int dividerId = confirmDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = confirmDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(
                confirmDialog.getContext().getResources().getColor(R.color.transparent));
          }
        } else {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              this, "",
              getString(R.string.confirm_delete_all_messages), false)
              .setPositiveButton(0, null)
              .create();
          confirmDialog.show();

          int dividerId = confirmDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = confirmDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(
                confirmDialog.getContext().getResources().getColor(R.color.transparent));
          }
        }
        break;

      case R.id.sliding_menu_right_top_control_edit_txt_done:
        changeEditConversationStatus(false);
        if (mConversationList.size() == 0) {
          disableDelete();
        }
        break;
      case R.id.activity_main_menu_left_text_how_to_use:
        handleMenu(FUNCTION_HOW_TO_USE, true);
        break;
      case R.id.activity_main_menu_left_text_support:
        handleMenu(FUNCTION_SUPPORT, true);
        break;
      case R.id.activity_main_menu_left_contact_us:
        handleMenu(FUNCTION_CONTACT, true);
        break;
      case R.id.activity_main_menu_left_ll_free_points:
        Fragment fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_FREE_POINT);
        mNavigationManager.switchPage(fragment, true);
        showContent();
        break;

      default:
        Object object = v.getTag();
        if (object instanceof ExtraPage) {
          mExtraPageSelected = (ExtraPage) object;
          handleMenu(FUNCTION_EXTRA_PAGE, true);
        }
        break;
    }
  }

  private void requestDeleteConversation(List<String> userIdList) {
    String[] userIds = new String[userIdList.size()];
    for (int i = 0; i < userIdList.size(); i++) {
      userIds[i] = userIdList.get(i);
    }

    isRequestDeleteAll = true;
    String token = UserPreferences.getInstance().getToken();
    DeleteConversationRequest deleteConversation = new DeleteConversationRequest(
        token, userIds);
    restartRequestServer(LOADER_DELETE_CONVERSATION, deleteConversation);
  }

  private void registerReceiveMessage() {
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // assign type for message: pp or wink
        if (action.equals(ChatManager.ACTION_LOCAL_MESSAGE)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_WINK)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_FILE)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_GIFT)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_LOCATION)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_STICKER)
            || action.equals(ChatManager.ACTION_MESSAGE_WINK)
            || action.equals(ChatManager.ACTION_MESSAGE)
            || action.equals(ChatManager.ACTION_MESSAGE_FILE)
            || action.equals(ChatManager.ACTION_MESSAGE_GIFT)
            || action.equals(ChatManager.ACTION_MESSAGE_LOCATION)
            || action.equals(ChatManager.ACTION_MESSAGE_STICKER)
            || action.equals(ChatManager.ACTION_MESSAGE_CALL)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_CALL)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION)
            ) {
          enableDelete();
          MessageClient compat = (MessageClient) intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          mMessage = compat.getMessage();
          messageFromUserId = mMessage.from;
          // tungdx: if user in blocklist -> not add to list
          if (Utility.isBlockedWithUser(getApplicationContext(),
              messageFromUserId)
              || Utility.isBlockedWithUser(
              getApplicationContext(), mMessage.to)) {
            return;
          }
          if (mMessage.msgType == MessageType.WINK) {
            messageType = ChatMessage.WINK;
          } else if (mMessage.msgType == MessageType.PP) {
            messageType = ChatMessage.PP;
          } else if (mMessage.msgType == MessageType.FILE) {
            // If confirm message-> not show in list
            // conversation
//                        if (ChatUtils.isConfirmMessage(mMessage.value)) {
//                            return;
//                        }
            messageType = ChatMessage.FILE;
          } else if (mMessage.msgType == MessageType.GIFT) {
            messageType = ChatMessage.GIFT;
          } else if (mMessage.msgType == MessageType.LCT) {
            messageType = ChatMessage.LOCATION;
          } else if (mMessage.msgType == MessageType.STK) {
            messageType = ChatMessage.STICKER;
          } else if (mMessage.msgType == MessageType.SVIDEO) {
            messageType = ChatMessage.STARTVIDEO;
          } else if (mMessage.msgType == MessageType.EVIDEO) {
            messageType = ChatMessage.ENDVIDEO;
          } else if (mMessage.msgType == MessageType.SVOICE) {
            messageType = ChatMessage.STARTVOICE;
          } else if (mMessage.msgType == MessageType.EVOICE) {
            messageType = ChatMessage.ENDVOICE;
          } else if (mMessage.msgType == MessageType.CALLREQ) {
            messageType = ChatMessage.CALLREQUEST;
            restartRequestBasicUserInfo(mMessage.from);
          } else {
            // NOP
          }

          String userFrom = mMessage.from;
          String userTo = mMessage.to;
          UserPreferences userPreferences = UserPreferences.getInstance();
          String currentUserId = userPreferences.getUserId();
          String isChatUserId = userPreferences.getCurentFriendChat();

          LogUtils.d(TAG, "userFrom=" + userFrom + ", currentUserId=" + currentUserId);
          isHasOnConversationList = false;
          for (int i = 0; i < mConversationList.size(); i++) {
            if (mConversationList.get(i).getFriendId().equalsIgnoreCase(userFrom)
                || mConversationList.get(i).getFriendId().equalsIgnoreCase(userTo)) {
              if (userFrom.equalsIgnoreCase(isChatUserId)) {
                mConversationList.get(i).setLastMessage(mMessage.value);
                mConversationList.get(i).setSentTime(Utility.convertLocalDate(mMessage.originTime));
                mConversationList.get(i).setMessageType(messageType);
                if (userTo.equalsIgnoreCase(isChatUserId)) {
                  mConversationList.get(i).setOwn(true);
                } else {
                  mConversationList.get(i).setOwn(false);
                }

              } else {
                mConversationList.get(i).setMessageType(messageType);
                if (userFrom.equalsIgnoreCase(currentUserId)) {
                  mConversationList.get(i).setOwn(true);
                } else {
                  mConversationList.get(i)
                      .setUnreadNum(mConversationList.get(i).getUnreadNum() + 1);
                  UserPreferences.getInstance().increaseUnreadMessage(1);
                  mConversationList.get(i).setOwn(false);
                }

                // handle wink message
                if (mMessage.msgType == MessageType.WINK) {
                  if (null == mMessage.value || "".equals(mMessage.value)) {
                    if (mConversationList.get(i).isOwn()) {
                      String value = getString(R.string.message_wink_2,
                          mConversationList.get(i).getName());
                      mMessage.value = value;
                    } else {
                      String value = getString(R.string.message_wink,
                          mConversationList.get(i).getName());
                      mMessage.value = value;
                    }
                  }
                }
                mConversationList.get(i).setLastMessage(mMessage.value);
                mConversationList.get(i).setSentTime(Utility.convertLocalDate(mMessage.originTime));
              }
              Collections.sort(mConversationList, new ConversationComparator());
              mConversationAdapter.notifyDataSetChanged();
              isHasOnConversationList = true;
              break;
            }
          }
          if (isHasOnConversationList) {
            isHasOnConversationList = false;
            showUnreadMessage();
          } else {
            LogUtils.e("get info", "GetBasicInfoRequest");
            String token = UserPreferences.getInstance().getToken();
            GetBasicInfoRequest basicInfoRequest = null;
            if (userFrom.equalsIgnoreCase(currentUserId)) {
              basicInfoRequest = new GetBasicInfoRequest(token, userTo);
            } else {
              basicInfoRequest = new GetBasicInfoRequest(token, userFrom);
            }

            mNewMessage = mMessage.value;
            mNewDate = Utility.convertLocalDate(mMessage.originTime);
            restartRequestServer(LOADER_ID_BASIC_USER_INFO, basicInfoRequest);
            isHasOnConversationList = false;
          }
        } else if (action.equals(ChatManager.ACTION_MESSAGE_CMD)) {
          MessageClient compat = (MessageClient) intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          Message message = compat.getMessage();
          handleBlockMessage(message);
        } else if (action.equals(AccountStatus.ACTION_BLOCKED)) {
          String blockedUserId = intent.getStringExtra(AccountStatus.EXTRA_DATA);
          handleBlockUser(blockedUserId);
        } else if (action.equals(ACTION_STREAMING_ERROR)) {
          showDialogCanNotStreaming();
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(
        ChatManager.ACTION_AUTHENTICATION);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_WINK);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_FILE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_GIFT);
    intentFilter.addAction(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_LOCATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CALL);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_STICKER);
    intentFilter.addAction(AccountStatus.ACTION_BLOCKED);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_WINK);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CMD);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_FILE);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_GIFT);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_LOCATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_STICKER);
    intentFilter.addAction(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_CALL);
    intentFilter.addAction(ACTION_STREAMING_ERROR);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION);
    LocalBroadcastManager.getInstance(getApplicationContext())
        .registerReceiver(mBroadcastReceiver, intentFilter);
  }

  private void handleBlockMessage(Message message) {
    String patternBlock = "\\Ablock&";
    Pattern ppp = Pattern.compile(patternBlock, Pattern.DOTALL);
    Matcher m = ppp.matcher(message.value);
    boolean blocked = false;

    if (m.find()) {
      blocked = true;
    }

    if (blocked && mConversationList != null) {
      // Remove item from list of conversations and update number of
      // unread messages
      handleBlockUser(message.from);
    }
  }

  private void handleBlockUser(String userId) {
    int notification = 0;
    boolean found = false;

    for (int i = 0; i < mConversationList.size(); i++) {
      if (userId.equals(mConversationList.get(i).getFriendId())) {
        mConversationList.remove(i);
        found = true;
      } else {
        notification = notification
            + mConversationList.get(i).getUnreadNum();
      }
    }

    if (found) {
      mConversationAdapter.notifyDataSetChanged();
      showUnreadMessage(notification);
    }
  }

  public void clearUnreadMessage(String userId) {
    if (mConversationList != null && mConversationAdapter != null) {
      int unReadNumHaveToRemove = 0;
      for (ConversationItem item : mConversationList) {
        if (item.getFriendId().equalsIgnoreCase(userId)) {
          unReadNumHaveToRemove = item.getUnreadNum();
          item.setUnreadNum(0);
        }
      }

      Collections.sort(mConversationList, new ConversationComparator());
      mConversationAdapter.notifyDataSetChanged();

      showUnreadMessage(UserPreferences.getInstance()
          .getNumberUnreadMessage() - unReadNumHaveToRemove);
    }
  }

  public void showUnreadMessage() {
    int unreadMessag = UserPreferences.getInstance()
        .getNumberUnreadMessage();
    mTxtChatsInLeft.setTextNumber(unreadMessag);
    if (getActiveFragment() instanceof HomeFragment) {
      ((HomeFragment) getActiveFragment()).showUnreadMessage(unreadMessag);
    }
  }

  private void showUnreadMessage(int numUnreadMessage) {
    if (numUnreadMessage < 0) {
      numUnreadMessage = 0;
    }
    UserPreferences.getInstance().saveNumberUnreadMessage(numUnreadMessage);
    mTxtChatsInLeft.setTextNumber(numUnreadMessage);
    if (getActiveFragment() instanceof HomeFragment) {
      ((HomeFragment) getActiveFragment()).showUnreadMessage(numUnreadMessage);
    }
  }

  private void unregisterReceiveMessage() {
    LocalBroadcastManager.getInstance(getApplicationContext())
        .unregisterReceiver(mBroadcastReceiver);
  }

  private void changeEditConversationStatus(boolean isEdit) {
    if (isEdit) {
      mlnHeadNormal.setVisibility(View.GONE);
      mlnHeadEdit.setVisibility(View.VISIBLE);
      mConversationAdapter.setEdit(true);
    } else {
      mlnHeadNormal.setVisibility(View.VISIBLE);
      mlnHeadEdit.setVisibility(View.GONE);
      mConversationAdapter.setEdit(false);
    }
    mConversationAdapter.notifyDataSetChanged();
  }

  private void handleUnlock(UnlockResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      showProfile(mNotificationMessage);
    }
  }

//  private void handleGetBasicInfoResponse(GetBasicInfoResponse response) {
//    LogUtils.d(TAG, "handleGetBasicInfoResponse Started");
//    if (response.getCode() == Response.SERVER_SUCCESS) {
//      // Add to list conversation
//      ConversationItem item = new ConversationItem();
//      item.setAvaId(response.getAvataId());
//      item.setFriendId(response.getUserId());
//      item.setLastMessage(mNewMessage);
//      item.setSentTime(mNewDate);
//      item.setLattitude(response.getLatitude());
//      item.setLongtitude(response.getLongitude());
//      item.setOnline(response.isOnline());
//      item.setMessageType(messageType);
//      item.setDistance(response.getDistance());
//
//      UserPreferences userPreferences = UserPreferences.getInstance();
//      String currentUserId = userPreferences.getUserId();
//      if (mMessage.from.equalsIgnoreCase(currentUserId)) {
//        item.setUnreadNum(0);
//      } else {
//        String currentUserIdToSend = userPreferences
//            .getCurentFriendChat();
//        if (currentUserIdToSend != null
//            && currentUserIdToSend.equals(mMessage.from)) {
//          item.setUnreadNum(0);
//        } else {
//          item.setUnreadNum(1);
//          //update number new message of new converstaion
//          UserPreferences.getInstance().increaseUnreadMessage(1);
//        }
//      }
//
//      item.setName(response.getUserName());
//      item.setGender(response.getGender());
//      if (currentUserId.equals(messageFromUserId)) {
//        item.setOwn(true);
//      } else {
//        item.setOwn(false);
//      }
//
//      boolean hasList = false;
//      for (int i = 0; i < mConversationList.size(); i++) {
//        ConversationItem itemOld = mConversationList.get(i);
//        if (item.getFriendId().equalsIgnoreCase(itemOld.getFriendId())) {
//          mConversationList.set(i, item);
//          hasList = true;
//          break;
//        }
//      }
//      if (!hasList) {
//        mConversationList.add(item);
//      }
//      Collections.sort(mConversationList, new ConversationComparator());
//      mConversationAdapter.notifyDataSetChanged();
//      showUnreadMessage();
//    } else if (response.getCode() == Response.SERVER_BLOCKED_USER) {
//      // NOP
//    } else {
//      ErrorApiDialog.showAlert(MainActivity.this, R.string.common_error,
//          response.getCode());
//    }
//
//    LogUtils.d(TAG, "handleGetBasicInfoResponse Ended");
//  }

  private void handlerCheckRequestCall(GetBasicInfoResponse response) {
    String userId = response.getUserId();
    String userIdToSend = UserPreferences.getInstance().getCallingUserId();
    if (AndGApp.isApplicationVisibile() && !TextUtils.isEmpty(userIdToSend) && userId
        .equals(userIdToSend)) {
      return;
    }

    String userName = response.getUserName();
    if (TextUtils.isEmpty(userName)) {
      userName = getString(R.string.notification_username_default);
    }
    handleNotification(userName, userId, Constants.LOCK_KEY_CALLREQUEST,
        Constants.NOTI_REQUEST_CALL);
  }

  private void handleNotification(String userName, String userId, String lockey, int notiType) {
    UserPreferences preferences = UserPreferences.getInstance();
    // push notification here
    String message = "{\"alert\":{\"loc-args\":[\"" + userName + "\"],\"loc-key\":\"" + lockey
        + "\"},\"data\":{\"userid\":\"" + userId + "\",\"noti_type\":" + notiType + "}}";
    NotificationMessage notiMessage = new NotificationMessage(message);
    ApplicationNotificationManager notificationManager = new ApplicationNotificationManager(this);

    // if app isn't in forebackgroup
    if (!AndGApp.isApplicationVisibile()) {
      // if calling not show notification
      if (preferences.getInCallingProcess()) {
        return;
      }
      notificationManager.showNotification(notiMessage);
      // if app is in forebackgroup and not in Chat screen
    } else if (TextUtils.isEmpty(preferences.getCurentFriendChat())) {
      sendBroadcastReceiveMessage(notiMessage);
    } else if (!preferences.getCurentFriendChat().equals(userId)) {
      NotificationUtils.playNotificationSound(this);
      NotificationUtils.vibarateNotification(this);
    }
  }

  public void sendBroadcastReceiveMessage(NotificationMessage message) {
    Intent intent = new Intent(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intent.putExtra(WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE, message);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  public void showProfile(NotificationMessage notificationMessage) {
    replaceFragment(
        MyProfileFragment.newInstance(notificationMessage.getUserid()),
        MyProfileFragment.TAG_FRAGMENT_USER_PROFILE);
  }

  public void showProfile(String userId) {
    if (TextUtils.isEmpty(userId)) {
      return;
    }
    replaceFragment(
        MyProfileFragment.newInstance(userId),
        MyProfileFragment.TAG_FRAGMENT_USER_PROFILE);
  }

  public void showListBackstage(String userId) {
    if (TextUtils.isEmpty(userId)) {
      return;
    }
    ManageBackstageActivity.startManagerBackstage(this, userId);
  }

  public void showMyPost(NotificationMessage notificationMessage) {
    replaceFragment(BuzzFragment.newInstance(false, BuzzFragment.TAB_MINE),
        MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  public void showBuzzDetail(NotificationMessage notificationMessage, boolean showKeyboard) {
    BuzzDetail fragmentBuzzDetail = BuzzDetail.newInstance(
        notificationMessage.getBuzzid(), Constants.BUZZ_TYPE_NONE);
    fragmentBuzzDetail.showSoftkeyWhenStart(showKeyboard);
    mNavigationManager.addPage(fragmentBuzzDetail);
    // replaceFragment(fragmentBuzzDetail,
    // MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_DELETE_CONVERSATION
        || loaderId == LOADER_ID_MARK_AS_READ
        || loaderId == LOADER_ID_CHECK_UNLOCK) {
      if (progressDialog == null) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.waiting));
      }
      progressDialog.show();
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (pullToRefreshListView.isRefreshing()) {
      pullToRefreshListView.onRefreshComplete();
    }

    if (loader.getId() == LOADER_GET_UPDATE_INFO_FLAG) {
      if (mDialog != null && mDialog.isShowing()) {
        mDialog.dismiss();
      }
    }

    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(this, R.string.common_error,
          response.getCode());
      pullToRefreshListView.onRefreshComplete();
      getSupportLoaderManager().destroyLoader(loader.getId());
      return;
    } else if (response instanceof ConversationResponse) {
      disableDelete();
      ConversationResponse conversationResponse = (ConversationResponse) response;
      mtxtEmptyChatView.setText(R.string.no_more_items_to_show);
      if (conversationResponse.getCode() == Response.SERVER_SUCCESS) {
        if (conversationResponse.getCode() == Response.SERVER_SUCCESS) {
          // Filter results
          filterConnectionResponseResults(conversationResponse);
        } else {
          ErrorApiDialog.showAlert(MainActivity.this,
              R.string.common_error, response.getCode());
        }
        boolean hasList = false;
        if (conversationResponse.getConversationItem().size() > 0) {
          for (ConversationItem itemNew : conversationResponse
              .getConversationItem()) {
            for (int j = 0; j < mConversationList.size(); j++) {
              ConversationItem itemOld = mConversationList.get(j);
              if (itemNew.getFriendId().equalsIgnoreCase(
                  itemOld.getFriendId())) {
                mConversationList.set(j, itemNew);
                hasList = true;
                break;
              } else {
                hasList = false;
              }
            }
            if (!hasList) {
              mConversationList.add(itemNew);
            }
          }

          Collections.sort(mConversationList,
              new ConversationComparator());
          mTimeSpan = mConversationList.get(
              mConversationList.size() - 1).getSentTime();
          mConversationAdapter.notifyDataSetChanged();
        }
        showUnreadMessage();
      } else {
        pullToRefreshListView.setMode(Mode.PULL_FROM_START);
        ErrorApiDialog.showAlert(MainActivity.this,
            R.string.common_error, response.getCode());
        return;
      }
      if (mConversationList.size() > 0) {
        enableDelete();
      } else {
        disableDelete();
      }
    } else if (response instanceof DeleteConversationResponse) {
      handleDeleteAllComversation((DeleteConversationResponse) response);
    } else if (response instanceof MarkReadsResponse) {
      if (response.getCode() == Response.SERVER_SUCCESS
          && listMarkAsReadUser != null) {
        int numberOfMarkAsRead = listMarkAsReadUser.length;
        if (numberOfMarkAsRead > 0) {
          for (ConversationItem item : mConversationList) {
            for (String userId : listMarkAsReadUser) {
              if (item.getFriendId().equals(userId)) {
                item.setUnreadNum(0);
                break;
              }
            }
          }
        }
        listMarkAsReadUser = null;
        mConversationAdapter.notifyDataSetChanged();
        requestTotalUnreadMsg();
      }
    } else if (response instanceof GetTotalUnreadMesageResponse) {
      int numberOfMarkAsRead = ((GetTotalUnreadMesageResponse) response)
          .getNumber();
      UserPreferences userPreference = UserPreferences.getInstance();
      if (numberOfMarkAsRead > 0) {
        if (numberOfMarkAsRead > BadgeConfig.MAX_NUMBER_OF_UNREAD_MSG) {
          String format = getString(R.string.badge_max);
          String msg = String.format(format, String
              .valueOf(BadgeConfig.MAX_NUMBER_OF_UNREAD_MSG));
          getNavigationBar().setNotification(msg);
        } else {
          getNavigationBar().setNotification(
              String.valueOf(numberOfMarkAsRead));
        }
        userPreference.saveNumberUnreadMessage(numberOfMarkAsRead);
      } else {
        getNavigationBar().setNotification("");
        userPreference.saveNumberUnreadMessage(0);
      }
      showUnreadMessage(numberOfMarkAsRead);
    } else if (response instanceof UnlockResponse) {
      handleUnlock((UnlockResponse) response);
    } else if (response instanceof GetBasicInfoResponse) {
      int loaderID = loader.getId();
      if (loaderID == LOADER_ID_USER_INFO_CALL) {
        handlerCheckRequestCall((GetBasicInfoResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_USER_INFO_CALL);
      }
    } else if (response instanceof GetUpdateInfoFlagResponse) {
      handleGetUpdateInfoResponse((GetUpdateInfoFlagResponse) response);
    } else if (response instanceof UserInfoResponse) {
      handleMyUserInfo((UserInfoResponse) response);
    } else if (response instanceof InstallCountResponse) {
      int code = response.getCode();
      switch (code) {
        case com.application.connection.Response.SERVER_SUCCESS:
          Preferences preferences = Preferences.getInstance();
          preferences.saveIsInstalled();
          if (!preferences.isAttachCmcode()) {
            preferences.saveIsAttachCmcode();
            preferences.saveIsInstalled();
            String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            String url = String
                .format(Config.COUNTER_SERVER, androidId);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            finish();
          }
          break;
      }
    }
  }

  private void filterConnectionResponseResults(ConversationResponse response) {
    List<ConversationItem> listConversations = response
        .getConversationItem();

    if (listConversations != null) {
      ListIterator<ConversationItem> li = listConversations
          .listIterator();
      ConversationItem current;
      List<ConversationItem> distinctResult = new ArrayList<>();

      boolean contains;

      while (li.hasNext()) {
        current = li.next();

        contains = false;
        for (int i = 0; i < distinctResult.size(); i++) {
          if (distinctResult.get(i).getFriendId()
              .equals(current.getFriendId())) {
            contains = true;
            break;
          }
        }

        if (contains
            || Utility.isBlockedWithUser(this,
            current.getFriendId())
            || mConversationAdapter.contains(current.getFriendId())) {
          li.remove();
        } else {
          distinctResult.add(current);
        }
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
    LogUtils.d(TAG, "parseResponse Started");
    Response response = null;

    switch (loaderID) {
      case LOADER_CONVERSATION:
        response = new ConversationResponse(data);
        break;
      case LOADER_ID_USER_INFO_CALL:
        LogUtils.e(TAG, "parseResponse LOADER_ID_BASIC_USER_INFO");
        response = new GetBasicInfoResponse(getApplicationContext(), data);
        break;
      case LOADER_DELETE_CONVERSATION:
        response = new DeleteConversationResponse(data);
        break;
      case LOADER_ID_TOTAl_UNREAD_MSG:
        response = new GetTotalUnreadMesageResponse(data);
        break;
      case LOADER_ID_MARK_AS_READ:
        response = new MarkReadsResponse(data);
        break;
      case LOADER_ID_CHECK_UNLOCK:
        response = new CheckUnlockResponse(data);
        break;
      case LOADER_ID_UNLOCK_TYPE:
        response = new UnlockResponse(data);
        break;
      case LOADER_ID_EXTRA_PAGE:
        response = new GetExtraPageResponse(data);
        break;
      case LOADER_GET_UPDATE_INFO_FLAG:
        response = new GetUpdateInfoFlagResponse(data);
        break;
      case LOADER_GET_MY_INFO:
        response = new UserInfoResponse(data);
        break;
      case LOADER_INSTALL_COUNT:
        response = new InstallCountResponse(data);

      case LOADER_RETRY_LOGIN:
        response = new LoginResponse(data);

      case LOADER_GET_USER_STATUS:
        response = new GetUserStatusResponse(data);

      default:
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    mNavigationManager.terminate();
    unregisterReceiveMessage();
    mNetworkListener.unRegister();
    doUnbindUploadService();
    stopChatHiddenUser();
    mMainHandler.removeMessages(MainHandler.CHAT_HIDDEN);
    StatusController.getInstance(getApplicationContext())
        .removeStatusChangedListener(mConversationAdapter);

    //hiepuh
    mHiddenChatMessageList = null;
    mHiddenUserId = null;
    selectedIndex = 0;

    //end
    super.onDestroy();
  }

  /**
   * Update item on Main Menu.
   */
  public void onMainMenuUpdate(int type) {
    TextView textView = null;
    String newValue = "";

    UserPreferences userPreferences = UserPreferences.getInstance();
    switch (type) {
      case UPDATE_NAME:
        textView = (TextView) findViewById(R.id.activity_main_menu_left_username);
        newValue = userPreferences.getUserName();
        break;
      case UPDATE_POINTS:
        textView = (TextView) findViewById(R.id.activity_main_menu_left_text_points_number);
        int point = userPreferences.getNumberPoint();
        String format = getResources().getString(R.string.point_suffix);
        newValue = MessageFormat.format(format, point);
        // update new point top
        mTxtPointsInTop.setText(newValue);
        mTxtPointsInLeft.setText(newValue);
        break;
      case UPDATE_NOTIFICATIONS:
        mTxtNotisInLeft.setTextNumber(userPreferences
            .getNumberNotification());
        break;
      case UPDATE_AVATAR:
        loadAvatar();
        break;

      default:
        break;
    }

    if (textView != null) {
      textView.setText(newValue);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    if (parent.getItemAtPosition(position) instanceof ConversationItem) {
      ConversationItem item = (ConversationItem) parent
          .getItemAtPosition(position);
      if (!item.getFriendId().equalsIgnoreCase("null")
          && !item.getFriendId().equalsIgnoreCase(
          UserPreferences.getInstance().getUserId())
          && item.getFriendId().trim().length() > 0) {
        clearUnreadMessage(item.getFriendId());

        // ChatFragment chatFragment = ChatFragment.newInstance(
        // item.getFriendId(), item.getAvaId(), item.getName(),
        // item.getGender(), item.isVoiceCallWaiting(),
        // item.isVideoCallWaiting(), true);

        ChatFragment chatFragment = ChatFragment.newInstance(
            item.getFriendId(), true);
        replaceFragment(chatFragment, TAG_FRAGMENT_CHAT);
        getSlidingMenu().showContent();
      }
    }
  }

  public void replaceAllFragment(final int placeHolder,
      final BaseFragment baseFragment, final String tag) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        getSupportFragmentManager().popBackStackImmediate(
            BaseFragment.TAB_BACKSTACK,
            FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
            .beginTransaction();
        fragmentTransaction.replace(placeHolder, baseFragment, tag);
        fragmentTransaction.commit();
      }
    };
    Handler handler = new Handler();
    handler.post(runnable);
  }

  public void replaceFragment(BaseFragment baseFragment, final String tag) {
    mNavigationManager.addPage(baseFragment);
  }

  public void replaceAllFragment(BaseFragment baseFragment, String tag) {
    mNavigationManager.switchPage(baseFragment);
  }

  public void onEvent(final DetailPictureEvent event) {
    if (event == null) {
      return;
    }
    Handler handler = new Handler();
    handler.post(new Runnable() {
      @Override
      public void run() {
        navigate(event);
      }
    });
  }

  private void navigate(final DetailPictureEvent event) {
    if (!TextUtils.isEmpty(event.getBuzzId())) {
      final BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(
          event.getBuzzId(), Constants.BUZZ_TYPE_IMAGE);
      if (event.getOption() == Constants.BUZZ_DETAIL_OPTION_CMT) {
        buzzDetailFragment.showSoftkeyWhenStart(true);
      }
      mNavigationManager.addPageStateLoss(buzzDetailFragment);
    } else {
      if (event.getOption() == Constants.BUZZ_DETAIL_OPTION_BACK_PROFILE) {
        Fragment activePage = mNavigationManager.getActivePage();
        if ((!(activePage instanceof SliderProfileFragment) || activePage instanceof BuzzDetail)
            && !TextUtils.isEmpty(event.getUserId())) {
          mNavigationManager.addPageStateLoss(MyProfileFragment
              .newInstance(event.getUserId()));
        }
      }
    }
  }

  private void navigateToBuzzDetail(DetailPictureEvent event) {
    if (!TextUtils.isEmpty(event.getBuzzId())) {
      BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(
          event.getBuzzId(), Constants.BUZZ_TYPE_IMAGE);

      if (event.getOption() == Constants.BUZZ_DETAIL_OPTION_CMT) {
        buzzDetailFragment.showSoftkeyWhenStart(true);
      }

      mNavigationManager.addPageStateLoss(buzzDetailFragment);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == REQUEST_WEB_VIEW) {
      if (resultCode == RESULT_EXIT) {
        isBackPressed = true;
        onBackPressed();
      }
    }
    //hiepuh call onActivityResult of fragments active
    mNavigationManager.getActivePage().onActivityResult(requestCode, resultCode, intent);
    //end
  }

  @Override
  public void onRetryPurchaseStart() {
  }

  // Shake To Chat

  @Override
  public void onRetryPurchaseFailure(int code) {
  }

  @Override
  public void onRetryPurchaseSuccess(int totalPoint) {
    UserPreferences.getInstance().saveNumberPoint(totalPoint);
    onMainMenuUpdate(UPDATE_POINTS);
    Toast.makeText(getApplicationContext(), "Buy point success",
        Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  /**
   * Start timer when chat with hidden user. If timer is running, do nothing.
   */
  public void startChatWithHiddenUser(String userId) {
    if (mRemain <= 0) {
      mRemain = HIDE_USER_TIME_IN_SECONDS;
      // initializeTimerTask();
      mMainHandler.sendEmptyMessage(MainHandler.CHAT_HIDDEN);
      // mConversationAdapter.setMessage(mHiddenUserId, "...");
    }
  }

  public void addHiddenUserListeners(
      ChatFragment.ChatWithHiddenUserListener chatWithHiddenUserListener) {
    if (mHiddenUserListeners == null) {
      mHiddenUserListeners = new ArrayList<ChatFragment.ChatWithHiddenUserListener>();
    }
    mHiddenUserListeners.add(chatWithHiddenUserListener);
  }

  public void removeHiddenUserListener(
      ChatFragment.ChatWithHiddenUserListener hiddenUserListener) {
    if (mHiddenUserListeners != null) {
      mHiddenUserListeners.remove(hiddenUserListener);
    }
  }

  public void stopChatHiddenUser() {
    // TODO replace by eventbus
    EventBus.getDefault().post(
        new ConversationEvent(ConversationEvent.REMOVE, mHiddenUserId));
    mRemain = 0;
    mHiddenUserId = null;
    if (mHiddenChatMessageList != null) {
      mHiddenChatMessageList.clear();
    }
    if (mHiddenUserTimer != null) {
      mHiddenUserTimer.cancel();
      mHiddenUserTimer.purge();
      mHiddenUserTimer = null;
    }
  }

  private void showHiddenUserInListConversation() {
    // TODO replace by eventbus
    EventBus.getDefault().post(
        new ConversationEvent(ConversationEvent.UPDATE));
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mNavigationManager.serialize(outState);
    mNeedRefreshConversationList = true;
    outState.putInt(KEY_TIME_REMAIN_HIDDEN, mRemain);
    mStateSaved = true;
  }

  private void handleExtraPageResponse(GetExtraPageResponse pageResponse) {
    if (pageResponse.getCode() == Response.SERVER_SUCCESS) {
      for (final ExtraPage entry : pageResponse.getPageList()) {
        LinearLayout llExtraPage = (LinearLayout) getLayoutInflater()
            .inflate(R.layout.item_menu_left_extra, null);
        TextView txt = (TextView) llExtraPage
            .findViewById(R.id.activity_main_menu_left_txt_extra);
        txt.setText(entry.title);
        txt.setTag(entry);
        txt.setOnClickListener(this);
        mMenuLeftRoot.addView(llExtraPage);
      }
    }
  }

  @Override
  public void finish() {
    if (mNetworkListener != null) {
      mNetworkListener.unRegister();
    }
    super.finish();
  }

  @Override
  public void onNetworkConnected() {
    LogUtils.d(TAG, "Network connected");
    ChatService chatService = getChatService();
    if (chatService != null) {
      ChatManager chatManager = chatService.getChatManager();
      if (chatManager != null) {
        chatManager.sendAuthenticationMessage();
      }
    }

    Fragment fragment = mNavigationManager.getActivePage();
    if (fragment instanceof ChatFragment) {
      ((ChatFragment) fragment).requestMoreMessage(true);
    }
  }

  @Override
  public void onNetworkDisconnected() {
    LogUtils.d(TAG, "Network disconnect!");

    Fragment fragment = mNavigationManager.getActivePage();
    if (fragment instanceof ChatFragment) {
      ((ChatFragment) fragment).dismissRequestMoreMessage();
    }
  }

  public boolean isCurrentChatAnonymous() {
    return !TextUtils.isEmpty(mHiddenUserId);
  }

  public boolean isChatAnonymousEmpty() {
    return mHiddenChatMessageList == null
        || mHiddenChatMessageList.size() == 0;
  }

  @Override
  public void onBackPressed() {
    Fragment activePage = mNavigationManager.getActivePage();
    if (activePage instanceof MyProfileFragment) {
      ((MyProfileFragment) activePage).goBack();
      return;
    }

    if (activePage instanceof ChatFragment) {
      ((ChatFragment) activePage).goBack();
      return;
    }

    if (!mNavigationManager.goBack()) {
      AlertDialog mDialog = new CustomConfirmDialog(this, null,
          getString(R.string.message_end_app), true)
          .setPositiveButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              isBackPressed = true;
              MainActivity.super.onBackPressed();
            }
          })
          .setNegativeButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
          })
          .create();

      mDialog.show();

      int dividerId = mDialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = mDialog.findViewById(dividerId);
      if (divider != null) {
        divider
            .setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
      }
    }
  }

  public void doBindUploadService() {
    bindService(new Intent(this, CustomUploadService.class),
        mUploadConnection, Context.BIND_AUTO_CREATE);
    mIsBound = true;
  }

  public void doUnbindUploadService() {
    if (mIsBound) {
      unbindService(mUploadConnection);
      mIsBound = false;
    }
  }

  @Override
  public void executeLogoutTask() {
    stopCallService();
    // removeInfoWhenLogout();
    UserPreferences.getInstance().setIsLogout(true);

  }

  // Stop call service
  private void stopCallService() {
    // stop LinphoneService
    Intent linphone = new Intent(this, LinphoneService.class);
    stopService(linphone);
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    mStateSaved = false;
  }

  @Override
  public boolean isStateSaved() {
    return mStateSaved;
  }

  @Override
  public NavigationManager getNavigationManager() {
    return mNavigationManager;
  }

  @Override
  public CustomActionBar getCustomActionBar() {
    return mActionBar;
  }

  @Override
  protected boolean isNoTitle() {
    return false;
  }

  @Override
  public FragmentManager getCustomFragmentManager() {
    return super.getSupportFragmentManager();
  }

  @Override
  public void initNavigationManager(Bundle savedInstanceState) {
    mNavigationManager = new NavigationManager(this);
    if (savedInstanceState != null) {
      mNavigationManager.deserialize(savedInstanceState);
    }
  }

  @Override
  public void initCustomActionBar() {
    mActionBar = CustomActionBarFactory.getInstance(this);
    mActionBar.initialize(mNavigationManager, this);
  }

  @Override
  public int getPlaceHolder() {
    return R.id.activity_main_content;
  }

  private void loadAvatar() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String avaId = userPreferences.getAvaId();
    CircleImageRequest imageRequest = new CircleImageRequest(token, avaId);
    ImageUtil.loadCircleAvataImage(this, imageRequest.toURL(), mImgAvatarInLeft);
  }

  public void startForResult(Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode);
  }

  private void checkFinishRegister() {
    UserPreferences preferences = UserPreferences.getInstance();
    int finishRegStatus = preferences.getFinishRegister();
    if (finishRegStatus == Constants.FINISH_REGISTER_NO) {
      Intent intent = new Intent(this, ProfileRegisterActivity.class);
      startActivity(intent);
      finish();
    } else {
      AgeVerificationPrefers ageVerificationPrefers = new AgeVerificationPrefers();
      String userId = preferences.getUserId();
      int genderTemp = preferences.getGender();
      boolean ageVerificationShowed = ageVerificationPrefers
          .hasContainAgeVerification(userId);
      if (!ageVerificationShowed
          && genderTemp == UserSetting.GENDER_FEMALE) {
        showDialogHowToUseForFemale();
        ageVerificationPrefers.saveAgeVerification(userId);
      }
    }
  }

  private void showDialogAgeVerificationPending() {
    LogUtils.d("TamNV", "showDialogAgeVerificationPending");

    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
    builder.setCancelable(false);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.flow_reg_dialog_age_verifying_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.flow_reg_dialog_age_verifying_title);
    builder.setMessage(R.string.flow_reg_dialog_age_verifying_message);

    builder.setPositiveButton(R.string.common_ok,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (numberShowPending >= MAX_SHOW_PENDING) {
              finish();
            } else {
              callAPIGetUpdateInfoFlag();
              dialog.dismiss();
              mDialog.show();
            }
          }
        });

    numberShowPending++;
    builder.create();
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void enableDelete() {
    if (mtxtAddFriend != null) {
      mtxtAddFriend.setEnabled(true);
      mtxtAddFriend.setCompoundDrawablesWithIntrinsicBounds(0,
          R.drawable.ic_delete_selected, 0, 0);
    }
    if (mtxtEdit != null) {
      mtxtEdit.setEnabled(true);
    }
  }

  private void disableDelete() {
    if (mtxtAddFriend != null) {
      mtxtAddFriend.setEnabled(false);
      mtxtAddFriend.setCompoundDrawablesWithIntrinsicBounds(0,
          R.drawable.ic_delete_selected_disable, 0, 0);
    }
    if (mtxtEdit != null) {
      mtxtEdit.setEnabled(false);
    }
  }

  private void showDialogHowToUseForFemale() {
    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, true);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.flow_reg_dialog_how_use_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.flow_reg_dialog_how_use_title);
    builder.setMessage(R.string.flow_reg_dialog_how_use_message_female);
    builder.setPositiveButton(R.string.flow_reg_dialog_how_use_yes,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            handleMenu(MainActivity.FUNCTION_HOW_TO_USE, true);
          }
        });

    builder.setNegativeButton(R.string.flow_reg_dialog_how_use_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            ErrorApiDialog
                .showAlert(
                    MainActivity.this,
                    "",
                    getString(R.string.flow_reg_dialog_dimiss_how_use_message));
          }
        });
    builder.create();
    AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void callAPIGetUpdateInfoFlag() {
    UserPreferences preferences = UserPreferences.getInstance();
    GetUpdateInfoFlagRequest request = new GetUpdateInfoFlagRequest(
        preferences.getToken());
    restartRequestServer(LOADER_GET_UPDATE_INFO_FLAG, request);
  }

  public void handleGetUpdateInfoResponse(
      GetUpdateInfoFlagResponse getUpdateInfoFlagResponse) {
    UserPreferences userPreferences = UserPreferences.getInstance();

    int finishRegStatus = getUpdateInfoFlagResponse.getFinishRegisterFlag();
    userPreferences.saveFinishRegister(finishRegStatus);
    if (finishRegStatus == Constants.FINISH_REGISTER_NO) {
      Intent intent = new Intent(this, ProfileRegisterActivity.class);
      startActivity(intent);
      customeFinishActivity();
    } else {
      int verifyFlagStatus = getUpdateInfoFlagResponse
          .getVerificationFlag();
      userPreferences.saveAgeVerification(verifyFlagStatus);

      int gender = userPreferences.getGender();

      if (gender == UserSetting.GENDER_FEMALE
          && verifyFlagStatus == Constants.AGE_VERIFICATION_PENDING) {
        showDialogAgeVerificationPending();
      } else if (gender == UserSetting.GENDER_FEMALE
          && verifyFlagStatus == Constants.AGE_VERIFICATION_VERIFIED) {
        ErrorApiDialog
            .showAlert(
                this,
                getString(R.string.flow_reg_dialog_age_accepted_title),
                getString(R.string.flow_reg_dialog_age_accepted_message),
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog,
                      int which) {
                    showDialogHowToUseForFemale();
                  }
                });

        AgeVerificationPrefers ageVerificationPrefers = new AgeVerificationPrefers();
        ageVerificationPrefers.saveAgeVerification(userPreferences
            .getUserId());

        requestConversation(mTimeSpan);
        requestExtraPage();

//                FragmentManager manager = getSupportFragmentManager();
//                Fragment fragment = manager
//                        .findFragmentById(R.id.activity_main_content);
//
//                if (fragment instanceof MeetPeopleFragment) {
//                    ((MeetPeopleFragment) fragment).requestFirstListPeople();
//                    ((MeetPeopleFragment) fragment)
//                            .restartRequestAttendtionNum();
//                }
        Fragment fragment = getActiveFragment();
        if (fragment instanceof HomeFragment) {
          Fragment childFragment = ((HomeFragment) fragment).getChildFragment();
          if (childFragment instanceof HotPagePeopleFragment) {
            ((HotPagePeopleFragment) childFragment).requestFirstListPeople();
//                        ((MeetPeopleFragment) childFragment).restartRequestAttendtionNum();
          }
        }

      }
    }
  }

  private void handleMyUserInfo(UserInfoResponse response) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    //Updated by Hiepnk userId when get user info. restrictive lost userId.

    if (!TextUtils.isEmpty(response.getUserId())) {
      userPreferences.saveUserId(response.getUserId());
    }
    userPreferences.saveUserName(response.getUserName());
    userPreferences.saveNumberPoint(response.getPoint());
    int unreadMsg = response.getUnreadNum();
    showUnreadMessage(unreadMsg);
    Handler handler = new Handler();
    handler.post(new Runnable() {
      @Override
      public void run() {
        onMainMenuUpdate(UPDATE_POINTS);
        onMainMenuUpdate(UPDATE_NOTIFICATIONS);
        onMainMenuUpdate(UPDATE_AVATAR);
        onMainMenuUpdate(UPDATE_NAME);
      }
    });
  }

  private void handleDeleteAllComversation(DeleteConversationResponse response) {
    enableDelete();
    if (response.getCode() == Response.SERVER_SUCCESS) {
      StatusController statusController = StatusController
          .getInstance(getApplicationContext());
      if (isRequestDeleteAll) {
        statusController.clearAllMsg();
        ArrayList<ConversationItem> collection = new ArrayList<ConversationItem>();
        String[] listFriendIds = new String[collection.size()];
        int i = 0;
        for (ConversationItem item : collection) {
          listFriendIds[i] = item.getFriendId();
        }
        statusController.deleteListConversation(listFriendIds);
        mConversationList.retainAll(collection);
        mConversationAdapter.notifyDataSetChanged();
        isRequestDeleteAll = false;
      } else {
        int pos = mConversationAdapter.getDeletePosition();
        if (pos >= 0) {
          ConversationItem item = mConversationAdapter.getItem(pos);
          String userId = item.getFriendId();
          statusController
              .deleteListConversation(new String[]{userId});
          statusController.clearAllMsgFrom(userId);
          mConversationList.remove(mConversationAdapter
              .getDeletePosition());
          mConversationAdapter.notifyDataSetChanged();
          mConversationAdapter.setDeletePosition(-1);
        }
      }
      showUnreadMessage();
    }
    if (mConversationList.size() == 0) {
      disableDelete();
      changeEditConversationStatus(false);
    }
  }

  private void showDialogCanNotStreaming() {
    if (dialogCanNotStreaming != null && dialogCanNotStreaming.isShowing()) {
      return;
    }
    Builder builder = new CenterButtonDialogBuilder(this, false);
    builder.setTitle("");
    builder.setMessage(R.string.voip_stream_error);
    builder.setPositiveButton(R.string.common_ok, null);
    dialogCanNotStreaming = builder.create();
    dialogCanNotStreaming.setCancelable(false);
    dialogCanNotStreaming.show();
  }

  //hiepuh
  public void changeTabActive(int tab) {
    Fragment fragment = getActiveFragment();
    HomeFragment homeFragment;
    if (fragment instanceof HomeFragment) {
      homeFragment = (HomeFragment) fragment;
      switch (tab) {
        case HomeFragment.TAB_MEET_PEOPLE:
          homeFragment.goToMeetPeople();
          return;
        case HomeFragment.TAB_HOT_PAGE:
          homeFragment.goToHotPage();
          return;
        case HomeFragment.TAB_FOOTER_PRINT:
          homeFragment.goToWhoCheckMeOut();
          return;
        case HomeFragment.TAB_TIMELINE:
          homeFragment.goToTimeline();
          return;
        case HomeFragment.TAB_CHAT:
          homeFragment.goToConversation();
          return;
      }
    } else {
      homeFragment = HomeFragment.newInstance(tab, finishRegisterFlag);
      replaceAllFragment(homeFragment, TAG_HOME_FRAGMENT);
    }
  }

  private Fragment getActiveFragment() {
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager
        .findFragmentById(R.id.activity_main_content);
    return fragment;
  }

  private static class MainHandler extends Handler {

    public static final int CHAT_HIDDEN = 100;
    private WeakReference<MainActivity> mReference;

    public MainHandler(MainActivity activity) {
      mReference = new WeakReference<MainActivity>(activity);
    }

    @Override
    public void handleMessage(android.os.Message msg) {
      super.handleMessage(msg);
      MainActivity mainActivity = mReference.get();
      if (mainActivity == null) {
        return;
      }
      switch (msg.what) {
        case CHAT_HIDDEN:
          if (mainActivity.mHiddenUserListeners != null) {
            for (ChatFragment.ChatWithHiddenUserListener listener : mainActivity.mHiddenUserListeners) {
              if (listener != null) {
                listener.onUpdateTime(mainActivity.mRemain);
              }
            }
          }

          if (mainActivity.mRemain <= 0) {
            // Cancel Timer
            String userIdToSend = UserPreferences.getInstance()
                .getCurentFriendChat();
            // listener !=null & HiddenUser == UserIdToSend now
            if (mainActivity.mHiddenUserListeners != null
                && !TextUtils.isEmpty(userIdToSend)
                && mainActivity.mHiddenUserId.equals(userIdToSend)) {
              for (ChatFragment.ChatWithHiddenUserListener listener : mainActivity.mHiddenUserListeners) {
                if (listener != null) {
                  listener.onStopHiding();
                }
              }
            }
            mainActivity.showHiddenUserInListConversation();
            mainActivity.stopChatHiddenUser();
          } else {
            mainActivity.mRemain--;
            sendEmptyMessageDelayed(CHAT_HIDDEN, 1000);
          }
          break;

        default:
          break;
      }
    }
  }

  private class ConversationAdapter extends ArrayAdapter<ConversationItem>
      implements IStatusChatChanged {

    private int mAvatarSize;
    private boolean isEdit = false;
    private List<ConversationItem> objects;
    private int deletePosition = -1;
    private Context context;

    public ConversationAdapter(Context context, int textViewResourceId,
        List<ConversationItem> objects) {
      super(context, textViewResourceId, objects);
      this.context = context;
      this.objects = objects;
      mAvatarSize = getResources().getDimensionPixelSize(
          R.dimen.activity_setupprofile_img_avatar_height);
      EventBus.getDefault().post(new ConversationChangeEvent(objects));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ConversationItem item = objects.get(position);
      HolderView holderView = null;
      if (convertView == null) {
        holderView = new HolderView();
        LayoutInflater inflater = (LayoutInflater) getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(
            R.layout.item_list_sliding_menu_right_chat, parent,
            false);
        holderView.imgAvata = (ImageView) convertView
            .findViewById(R.id.sliding_menu_right_item_img_avata);
        holderView.tvNotification = (BadgeTextView) convertView
            .findViewById(R.id.text_type_sliding_menu_right_item_txt_notification);
        holderView.tvName = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_name);
        holderView.tvChatToMe = (EmojiTextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_chat_to_me_content);
        holderView.tvDistance = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_info_distance);
        holderView.tvDistanceTime = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_info_time_range);
        holderView.tvChatByMe = (EmojiTextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_chat_by_me_content);
        holderView.btnDelete = (Button) convertView
            .findViewById(R.id.sliding_menu_right_item_btn_delete);
        // holderView.imgOnline = (ImageView) convertView
        // .findViewById(R.id.sliding_menu_right_item_img_online);
        holderView.layoutTimeDistance = (LinearLayout) convertView
            .findViewById(R.id.layout_time_location);
        holderView.footerView = (View) convertView
            .findViewById(R.id.sliding_menu_right_item_footer_view);
        holderView.imgWarning = convertView.findViewById(R.id.warning);
        convertView.setTag(holderView);
      } else {
        holderView = (HolderView) convertView.getTag();
      }

      // remove footer line
      if (position == (getCount() - 1)) {
        holderView.footerView.setVisibility(View.GONE);
      } else {
        holderView.footerView.setVisibility(View.VISIBLE);
      }
      // else
      // holderView.footerView.setVisibility(View.VISIBLE);
      // String distance = Utility.getDistanceString(getContext(),
      // item.getDistance());
      // holderView.tvDistance.setText(distance);
      holderView.tvDistance.setVisibility(View.GONE);

      holderView.btnDelete.setTag(position);
      holderView.btnDelete.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          setDeletePosition(Integer.parseInt(v.getTag().toString()));
          String userId = objects.get(getDeletePosition())
              .getFriendId();
          requestDeleteConversation(new String[]{userId});
        }
      });

      if (isEdit) {
        holderView.btnDelete.setVisibility(View.VISIBLE);
      } else {
        holderView.btnDelete.setVisibility(View.GONE);
      }

      if (item.isOwn()) {
        // holderView.tvChatByMe.setVisibility(View.VISIBLE);
        // holderView.tvChatToMe.setVisibility(View.GONE);

        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_by_me_layout)
            .setVisibility(View.VISIBLE);
        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_to_me_layout)
            .setVisibility(View.GONE);

        if (item.getMessageType().equals(ChatMessage.WINK)) {
          String value = item.getLastMessage();
          if (null == value || "".equals(value)) {
            value = getString(R.string.message_wink_2,
                item.getName());
          }
          holderView.tvChatByMe.setText(value);
        } else if (item.getMessageType().equals(ChatMessage.FILE)) {
          String type = ChatUtils.getFileType(item.getLastMessage());
          holderView.tvChatByMe
              .setEmojiText(ChatUtils.getMessageByFileType(type,
                  getApplicationContext()));
        } else if (item.getMessageType().equals(ChatMessage.GIFT)) {

          String content = item.getLastMessage();
          String[] split = content.split("\\|");
          int point = 0;
          if (split != null && split.length >= 4) {
            point = Integer.valueOf(split[3]);
          }

          String format = getResources().getString(
              R.string.send_gift_price_not_free);
          String pointStr = MessageFormat.format(format, point);

          String display = String.format(
              getResources().getString(
                  R.string.gift_message_display_send),
              pointStr);

          holderView.tvChatByMe.setText(display);
        } else if (item.getMessageType().equals(ChatMessage.LOCATION)) {
          holderView.tvChatByMe
              .setText(R.string.chat_item_share_a_location);
        } else if (item.getMessageType().equals(ChatMessage.STICKER)) {
          holderView.tvChatByMe.setText(R.string.sticker);

        } else if (item.getMessageType().equals(ChatMessage.STARTVIDEO)
            || item.getMessageType().equals(ChatMessage.ENDVIDEO)
            || item.getMessageType().equals(ChatMessage.STARTVOICE)
            || item.getMessageType().equals(ChatMessage.ENDVOICE)) {
          showCallIconAndContent(holderView.tvChatByMe,
              item.getLastMessage(), true);
        } else if (item.getMessageType().equals(ChatMessage.CALLREQUEST)) {
          String msg;
          if (item.getLastMessage().equals(
              ChatMessage.CALLREQUEST_VIDEO)) {
            msg = getString(R.string.message_video_call_request,
                item.getName());
          } else {
            msg = getString(R.string.message_voice_call_request,
                item.getName());
          }
          holderView.tvChatByMe.setText(msg);
        } else {
          holderView.tvChatByMe.setEmojiText(item.getLastMessage());
        }
      } else {
        // holderView.tvChatByMe.setVisibility(View.GONE);
        // holderView.tvChatToMe.setVisibility(View.VISIBLE);

        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_by_me_layout)
            .setVisibility(View.GONE);
        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_to_me_layout)
            .setVisibility(View.VISIBLE);

        if (item.getMessageType().equals(ChatMessage.WINK)) {
          String value = item.getLastMessage();
          if (null == value || "".equals(value)) {
            value = getString(R.string.message_wink, item.getName());
          }
          holderView.tvChatToMe.setText(value);
        } else if (item.getMessageType().equals(ChatMessage.FILE)) {
          String type = ChatUtils.getFileType(item.getLastMessage());
          holderView.tvChatToMe
              .setEmojiText(ChatUtils.getMessageByFileType(type,
                  getApplicationContext()));
        } else if (item.getMessageType().equals(ChatMessage.GIFT)) {

          String content = item.getLastMessage();
          String[] split = content.split("\\|");
          int point = 0;
          if (split != null && split.length >= 4) {
            point = Integer.valueOf(split[3]);
          }

          String format = getResources().getString(
              R.string.send_gift_price_not_free);
          String pointStr = MessageFormat.format(format, point);

          String display = String.format(
              getResources().getString(
                  R.string.gift_message_display_recieve),
              pointStr);

          holderView.tvChatToMe.setEmojiText(display);
        } else if (item.getMessageType().equals(ChatMessage.LOCATION)) {
          holderView.tvChatToMe
              .setText(R.string.chat_item_share_a_location);
        } else if (item.getMessageType().equals(ChatMessage.STICKER)) {
          holderView.tvChatToMe.setText(R.string.sticker);
        } else if (item.getMessageType().equals(ChatMessage.STARTVIDEO)
            || item.getMessageType().equals(ChatMessage.ENDVIDEO)
            || item.getMessageType().equals(ChatMessage.STARTVOICE)
            || item.getMessageType().equals(ChatMessage.ENDVOICE)) {
          showCallIconAndContent(holderView.tvChatToMe,
              item.getLastMessage(), false);
        } else if (item.getMessageType().equals(ChatMessage.CALLREQUEST)) {
          String msg;
          if (item.getLastMessage().equals(
              ChatMessage.CALLREQUEST_VIDEO)) {
            msg = getString(R.string.message_video_call_request,
                UserPreferences.getInstance().getUserName());
          } else {
            msg = getString(R.string.message_voice_call_request,
                UserPreferences.getInstance().getUserName());
          }
          holderView.tvChatToMe.setText(msg);
        } else {
          holderView.tvChatToMe.setEmojiText((item.getLastMessage()));
        }
      }
      holderView.tvNotification.setTextNumber(item.getUnreadNum());

      if (item.getSentTime().length() > 0) {
        try {
          Calendar calendarNow = Calendar.getInstance();

          Utility.YYYYMMDDHHMMSSSSS.setTimeZone(TimeZone
              .getTimeZone("GMT"));
          Date dateSend = Utility.YYYYMMDDHHMMSSSSS.parse(item
              .getSentTime());
          Calendar calendarSend = Calendar.getInstance(TimeZone
              .getDefault());
          calendarSend.setTime(dateSend);

          holderView.tvDistanceTime.setText(Utility.getDifference(
              context, calendarSend, calendarNow));
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }

      if (TextUtils.isEmpty(item.getName())) {
        holderView.tvName
            .setText(R.string.notification_username_default);
      } else {
        holderView.tvName.setText(item.getName());
      }
      holderView.tvDistance.setVisibility(View.VISIBLE);
      holderView.tvDistanceTime.setVisibility(View.VISIBLE);
      holderView.layoutTimeDistance.setVisibility(View.VISIBLE);

      // load avatar
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token,
          item.getAvaId());
      getImageFetcher().loadImageByGender(imageRequest,
          holderView.imgAvata, mAvatarSize, item.getGender());

      boolean isVisible = !item.getIsAnonymous() && item.isMsgError();
      holderView.imgWarning.setVisibility(isVisible ? View.VISIBLE
          : View.GONE);
      return convertView;
    }

    private void showCallIconAndContent(TextView textView, String content, boolean isSend) {
      CallInfo callInfo = ChatUtils.getCallInfo(content);
      // textView.setText(ChatUtils.getCallDuration(getApplicationContext(),
      // callInfo));
      String msg = "";
      if (callInfo.voipType == ChatMessage.VoIPActionVideoEnd
          || callInfo.voipType == ChatMessage.VoIPActionVoiceEnd) {
        msg = getString(R.string.voip_action_video_voice_end_new,
            ChatUtils.getCallDuration(getApplicationContext(),
                callInfo));
      } else {
        if (isSend) {
          msg = ChatUtils.getCallDuration(getApplicationContext(), callInfo);
        } else {
          msg = ChatUtils.getCallDuration1(getApplicationContext(), callInfo);
        }
      }
      textView.setText(msg);
    }

    public void setEdit(boolean isEdit) {
      this.isEdit = isEdit;
    }

    private void requestDeleteConversation(String[] userId) {
      String token = UserPreferences.getInstance().getToken();
      DeleteConversationRequest deleteConversation = new DeleteConversationRequest(
          token, userId);
      restartRequestServer(LOADER_DELETE_CONVERSATION, deleteConversation);
    }

    public int getDeletePosition() {
      return deletePosition;
    }

    public void setDeletePosition(int deletePosition) {
      this.deletePosition = deletePosition;
    }

    public boolean contains(String friendId) {
      boolean found = false;

      for (int i = 0; i < objects.size(); i++) {
        if (objects.get(i).getFriendId().equals(friendId)) {
          found = true;
          break;
        }
      }

      return found;
    }

    @Override
    public void notifyDataSetChanged() {
      super.notifyDataSetChanged();
      EventBus.getDefault().post(new ConversationChangeEvent(objects));
      for (ConversationItem item : mConversationList) {
        boolean hasMsgError = StatusController.getInstance(
            getApplicationContext()).hasMsgErrorWith(
            item.getFriendId());
        item.setMsgError(hasMsgError);
      }
    }

    /**********************
     * Update status sent message error
     ******************************/
    @Override
    public void create(final MessageInDB msgInDB) {
      String userId = UserPreferences.getInstance().getUserId();

      if (msgInDB.getFrom().equals(userId)) {
        MainActivity.this.runOnUiThread(new Runnable() {

          @Override
          public void run() {
            checkHasMsgError(msgInDB);
          }
        });
      }
    }

    @Override
    public void update(final MessageInDB msgInDB) {
      String userId = UserPreferences.getInstance().getUserId();

      if (msgInDB.getFrom().equals(userId)) {
        MainActivity.this.runOnUiThread(new Runnable() {

          @Override
          public void run() {
            checkHasMsgError(msgInDB);
          }
        });
      }
    }

    @Override
    public void resendFile(MessageInDB msgInDB) {
      // Do nothing
    }

    private void checkHasMsgError(MessageInDB msgInDB) {
      String sendId = msgInDB.getTo();
      for (ConversationItem item : mConversationList) {
        if (item.getFriendId().equals(sendId)) {
          item.setMsgError(StatusController.getInstance(
              getApplicationContext()).hasMsgErrorWith(sendId));
          notifyDataSetChanged();
          break;
        }
      }
    }

    private class HolderView {

      public ImageView imgAvata;
      // public View imgOnline;
      public BadgeTextView tvNotification;
      public TextView tvName;
      public EmojiTextView tvChatToMe;
      public EmojiTextView tvChatByMe;
      public TextView tvDistance;
      public TextView tvDistanceTime;
      public Button btnDelete;
      public LinearLayout layoutTimeDistance;
      public View footerView;
      public View imgWarning;
    }

  }
  //end

}
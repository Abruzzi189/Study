package com.application.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.MyPageInfoRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.response.AddBuzzResponse;
import com.application.connection.response.MyPageInfoResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.ui.buzz.BaseBuzzListFragment;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.chat.CallLogFragment;
import com.application.ui.connection.ConnectionFragment;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.LockableScrollView;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.settings.OnlineAlertFragment;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MyPageFragment extends BaseBuzzListFragment implements View.OnClickListener,
    ResponseReceiver {

  private static final int LOADER_ID_USER_INFO = 100;
  private static final int LOADER_ID_MY_PAGE_INFO = 101;
  private static final int HANDLER_ADD_BUZZ = 100;

  private UserInfoResponse mUserInfo;
  private MyPageInfoResponse mMyPageInfo;
  private BadgeTextView mTxtFootprintNoti;
  private BadgeTextView mTxtFavoritesNoti;
  private BadgeTextView mTxtLikeNoti;
  private BadgeTextView mTxtChatsNoti;
  private TextView mTxtBuzz;
  private TextView mTxtBackstage;
  private View mRootView;
  private final DialogInterface.OnClickListener onErrorOKClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      // mScrollRootView.setVisibility(View.GONE);
      if (mRootView != null) {
        View loading = mRootView.findViewById(R.id.view_loading);
        if (loading != null) {
          loading.setVisibility(View.INVISIBLE);
        }
      }
    }
  };
  private LockableScrollView mScrollRootView;
  private MainActivity mActivity;
  private OnSharedPreferenceChangeListener mOnUserPreferenceChange = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(
        SharedPreferences sharedPreferences, String key) {
      if (key.equals(UserPreferences.KEY_USER_NAME)) {
        initNameView();
      } else if (key.equals(UserPreferences.KEY_NUMBER_UNREAD_MESSAGE)) {
        showUnreadChatMessage();
      } else if (key.equals(UserPreferences.KEY_AVA_ID)) {
        initAvatarView();
      } else if (key.equals(UserPreferences.KEY_NUMBER_POINT)) {
        initPointView();
      }
    }
  };
  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      int message = (Integer) msg.obj;
      switch (message) {
        case HANDLER_ADD_BUZZ:
          goToTimeLine();
          break;
        default:
          break;
      }

    }

    ;
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreateOrigin(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_mypage, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mRootView = view;
    initView();
    registerListener();

    // Register user preference
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.registerOnChange(mOnUserPreferenceChange);
  }

  @Override
  public void onDestroyView() {
    // Register user preference
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.unregisterOnChange(mOnUserPreferenceChange);
    super.onDestroyView();
  }

  @Override
  public void onStart() {
    super.onStartOrigin();
  }

  @Override
  public void onResume() {
    super.onResumeOrigin();
    requestUserinfo();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof MainActivity) {
      mActivity = (MainActivity) activity;
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreatedOrigin(savedInstanceState);
  }

  private void initView() {
    if (mRootView == null) {
      return;
    }
    // Scroll root view
    mScrollRootView = (LockableScrollView) mRootView
        .findViewById(R.id.scroll_root_view);

    // Add photo to status
    mImageViewTakeAPhoto = (ImageView) mRootView
        .findViewById(R.id.iv_buzz_take_photo);
    mImageViewTakeAPhoto.setOnClickListener(this);

    // Buzz status
    mEditTextStatus = (EditText) mRootView
        .findViewById(R.id.ed_input_my_status);
    mEditTextStatus.setOnFocusChangeListener(this);
    mEditTextStatus.addTextChangedListener(this);

    // Send status
    mButtonSend = (Button) mRootView.findViewById(R.id.bt_buzz_send);
    mButtonSend.setOnClickListener(this);

    TextView tvInputAStatus = (TextView) mRootView
        .findViewById(R.id.tv_buzz_input_a_status);
    ImageButton ibTakeAPhoto = (ImageButton) mRootView
        .findViewById(R.id.ib_buzz_take_a_photo);
    tvInputAStatus.setLongClickable(false);
    tvInputAStatus.setOnClickListener(this);
    ibTakeAPhoto.setOnClickListener(this);

    // Share status frame
    mShareMyStatusView = mRootView.findViewById(R.id.ll_share_my_status);

    // Freeze layer view
    mFreezedLayer = mRootView.findViewById(R.id.ib_buzz_freezed_layer);
    mFreezedLayer.setOnClickListener(this);
    mFreezedLayer.setOnTouchListener(this);
    RelativeLayout.LayoutParams freezedParams = (LayoutParams) mFreezedLayer
        .getLayoutParams();
    freezedParams.height = com.application.util.Utility
        .getScreenSize(mAppContext)[1];
    freezedParams.addRule(RelativeLayout.BELOW, R.id.ll_share_my_status);
    mFreezedLayer.setLayoutParams(freezedParams);

    // Initial notification view
    mTxtFootprintNoti = (BadgeTextView) mRootView
        .findViewById(R.id.footprint_noti);
    mTxtFavoritesNoti = (BadgeTextView) mRootView
        .findViewById(R.id.favorites_noti);
    mTxtChatsNoti = (BadgeTextView) mRootView.findViewById(R.id.chats_noti);
//		mTxtLikeNoti = (BadgeTextView) mRootView.findViewById(R.id.likes_noti);

    // Initial tab view
    mTxtBuzz = (TextView) mRootView.findViewById(R.id.buzz_num_txt);
    mTxtBackstage = (TextView) mRootView
        .findViewById(R.id.backstage_num_txt);

    initNameView();
    initPointView();
    initAvatarView();
  }

  private void showUnreadChatMessage() {
    int numChat = UserPreferences.getInstance().getNumberUnreadMessage();
    mTxtChatsNoti.setTextNumber(numChat);
  }

  private void initNameView() {
    if (mRootView == null) {
      return;
    }

    String username = UserPreferences.getInstance().getUserName();
    ((TextView) mRootView.findViewById(R.id.username_txt))
        .setText(username);
  }

  private void initPointView() {
    if (mRootView == null) {
      return;
    }

    int points = UserPreferences.getInstance().getNumberPoint();
    String format = getResources().getString(R.string.point_suffix);
    TextView txtPoint = ((TextView) mRootView.findViewById(R.id.point_txt));
    txtPoint.setText(MessageFormat.format(format, points));
    txtPoint.setOnClickListener(this);
  }

  private void initAvatarView() {
    if (mRootView == null) {
      return;
    }

    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String avaId = userPreferences.getAvaId();
    int gender = userPreferences.getGender();
    CircleImageRequest imageRequest = new CircleImageRequest(token, avaId);
    ImageView avatar = (ImageView) mRootView.findViewById(R.id.avatar_img);
    getImageFetcher().loadImageByGender(imageRequest, avatar,
        avatar.getWidth(), gender);
  }

  private void registerListener() {
    mRootView.findViewById(R.id.footprint_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.favourite_btn).setOnClickListener(this);
//		mRootView.findViewById(R.id.like_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.template_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.point_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.edit_profile_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.call_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.setting_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.online_alert_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.user_layout).setOnClickListener(this);
    mRootView.findViewById(R.id.mybuzz_layout).setOnClickListener(this);
    mRootView.findViewById(R.id.backstage_layout).setOnClickListener(this);
    mRootView.findViewById(R.id.user_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.myprofile_txt).setOnClickListener(this);

    //hiepuh
    mRootView.findViewById(R.id.message_btn).setOnClickListener(this);
    mRootView.findViewById(R.id.timeline_btn).setOnClickListener(this);
    //end
  }

  @Override
  public void onClick(View v) {
    super.onClick(v);
    switch (v.getId()) {
      //hiepuh
      case R.id.message_btn:
        mActivity.changeTabActive(HomeFragment.TAB_CHAT);
        break;
      case R.id.timeline_btn:
        mActivity.changeTabActive(HomeFragment.TAB_TIMELINE);
        break;
      //end

      case R.id.user_btn:
        int gender = UserPreferences.getInstance().getGender();
        navigateFragmentPoint(gender);
        break;
      case R.id.point_txt:
        int userGender = UserPreferences.getInstance().getGender();
        navigateFragmentPoint(userGender);
        break;
      case R.id.user_layout:
      case R.id.edit_profile_btn:
        if (mUserInfo != null) {
          mNavigationManager.addPage(EditProfileFragment
              .newInstance(mUserInfo, true));
        }
        break;

      case R.id.footprint_btn:
        mActivity.changeTabActive(HomeFragment.TAB_FOOTER_PRINT);
        break;

      case R.id.favourite_btn:
        ConnectionFragment fragment = ConnectionFragment.newInstance(
            false, ConnectionFragment.TAB_WHO_INDEX);
        mNavigationManager.addPage(fragment);
        break;

//			case R.id.like_btn:
//				NotificationFragment notificationFragment = new NotificationFragment();
//				notificationFragment.setOnlyLike(true);
//				mNavigationManager.addPage(notificationFragment);
//				break;

      case R.id.template_btn:
        mNavigationManager
            .addPage(TemplateFragment.newInstance(TemplateFragment.STYLE_MY_PAGE), false);
        break;

      case R.id.point_btn:
        int genderUser = UserPreferences.getInstance().getGender();
        navigateFragmentPoint(genderUser);
        break;

      case R.id.call_btn:
        CallLogFragment callLogFragment = CallLogFragment
            .getInstance(CallLogFragment.INDEX_RECEIVER);
        mNavigationManager.addPage(callLogFragment);
        break;

      case R.id.setting_btn:
        SettingsFragment settingsFragment = new SettingsFragment();
        mNavigationManager.addPage(settingsFragment);
        break;

      case R.id.online_alert_btn:
        OnlineAlertFragment alertFragment = new OnlineAlertFragment();
        mNavigationManager.addPage(alertFragment);
        break;

      case R.id.mybuzz_layout:
        Preferences.getInstance().saveBuzzTab(BuzzFragment.TAB_MINE);
        mNavigationManager.addPage(new BuzzFragment());
        break;

      case R.id.backstage_layout:
        if (mUserInfo != null) {
          ManageBackstageActivity.startManagerBackstage(
              getActivity(), mUserInfo.getUserId(),
              mUserInfo.getUserName(), mUserInfo.getAvataId(),
              mUserInfo.getBackstageNumber());
        }
        break;
      case R.id.myprofile_txt:
        if (mUserInfo != null) {
          mNavigationManager.addPage(MyProfileFragment.newInstance(
              mUserInfo.getUserId(), false));
        }
        break;
      default:
        break;
    }
  }

  private void navigateFragmentPoint(int gender) {
    Fragment fragment = WebViewFragment
        .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
    mNavigationManager.addPage(fragment);
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

    restartRequestServer(LOADER_ID_USER_INFO, infoRequest);
  }

  private void requestMyPageInfo() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();

    MyPageInfoRequest myPageInfoRequest = new MyPageInfoRequest(token);
    restartRequestServer(LOADER_ID_MY_PAGE_INFO, myPageInfoRequest);
  }

  @Override
  public void startRequest(int loaderId) {
    super.startRequest(loaderId);
    showLoadingLayout();
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    getLoaderManager().destroyLoader(loader.getId());
    if (response.getCode() != Response.SERVER_SUCCESS) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode(), onErrorOKClickListener, false);
      return;
    }
    if (response instanceof UserInfoResponse) {
      UserInfoResponse info = (UserInfoResponse) response;
      if (info.getCode() == Response.SERVER_SUCCESS) {
        mUserInfo = info;
      }
      requestMyPageInfo();
    } else if (response instanceof MyPageInfoResponse) {
      MyPageInfoResponse myPageInfo = (MyPageInfoResponse) response;
      if (myPageInfo.getCode() == Response.SERVER_SUCCESS) {
        mMyPageInfo = myPageInfo;
        updateUI();

        showDialogSetupProfile(!TextUtils.isEmpty(mUserInfo
            .getReviewingAvatar()));
      }
    } else if (response instanceof AddBuzzResponse) {
      handleAddBuzz((AddBuzzResponse) response);
    }
    hideLoadingLayout();
  }

  private void handleAddBuzz(AddBuzzResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      // Close Share My Status view
      if (mOpenShareMyStatus) {
        closeShareMyStatusView();
      }
      Message msg = handler.obtainMessage();
      msg.obj = HANDLER_ADD_BUZZ;
      handler.sendMessage(msg);

    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  private void updateUI() {
    if (mMyPageInfo != null) {

      // update notifications
      int numCheckout = mMyPageInfo.getCheckoutNum();
      mTxtFootprintNoti.setTextNumber(numCheckout);
      UserPreferences.getInstance().saveUnlockWhoCheckMeOut(numCheckout);

      int numFavouriteMe = mMyPageInfo.getFavoritesNum();
      UserPreferences.getInstance().saveNumberFavoritedMe(numFavouriteMe);
      mTxtFavoritesNoti.setTextNumber(numFavouriteMe);

      int numNotifiLike = mMyPageInfo.getNotiLikeNum();
      // show unread message
//			mTxtLikeNoti.setTextNumber(numNotifiLike);
      showUnreadChatMessage();

      // update number backstage
      mTxtBackstage.setText("+ "
          + String.valueOf(mMyPageInfo.getBackstageNum()));

      // update number buzz
      mTxtBuzz.setText(String.valueOf(mMyPageInfo.getBuzzNum()));
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = super.parseResponse(loaderID, data, requestType);
    if (loaderID == LOADER_ID_USER_INFO) {
      response = new UserInfoResponse(data);
    } else if (loaderID == LOADER_ID_MY_PAGE_INFO) {
      response = new MyPageInfoResponse(data);
    }

    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    return null;
  }

  @Override
  protected void onRefreshCompleted() {
  }

  private void goToTimeLine() {
    mActivity.handleMenu(MainActivity.FUNCTION_BUZZ, true);
  }

  @Override
  protected void openShareMyStatusView(int type) {
    super.openShareMyStatusView(type);
    mScrollRootView.setScrollingEnabled(false);
  }

  @Override
  protected void closeShareMyStatusView() {
    super.closeShareMyStatusView();
    mScrollRootView.setScrollingEnabled(true);
  }

  private void showLoadingLayout() {
    mRootView.findViewById(R.id.view_loading).setVisibility(View.VISIBLE);
    mRootView.findViewById(R.id.real_layout).setVisibility(View.INVISIBLE);
  }

  private void hideLoadingLayout() {
    if (mUserInfo != null && mMyPageInfo != null) {
      mRootView.findViewById(R.id.view_loading).setVisibility(
          View.INVISIBLE);
      mRootView.findViewById(R.id.real_layout)
          .setVisibility(View.VISIBLE);
    }
  }

  private void showDialogSetupProfile(boolean isAvatarReviewing) {
    if (isAvatarReviewing) {
      return;
    }

    UserPreferences preferences = UserPreferences.getInstance();
    String avatar = preferences.getAvaId();
    String lastTimeShowed = preferences.getTimeShowSetupProfile();

    SimpleDateFormat format = new SimpleDateFormat(
        Constants.FORMAT_TIME_SHOW_SETUP_PROFILE, Locale.getDefault());
    Date currentTime = new Date();
    Date lastTime = null;
    if (!TextUtils.isEmpty(lastTimeShowed)) {
      try {
        lastTime = format.parse(lastTimeShowed);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    // Calculate time
    boolean over24Hours = false;
    if (lastTime != null) {
      long time = currentTime.getTime() - lastTime.getTime();
      over24Hours = time > 24 * 60 * 60 * 1000;
    } else {
      over24Hours = true;
    }

    // Show dialog
    if (TextUtils.isEmpty(avatar) && over24Hours) {
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), true);

      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.dialog_setup_profile_title);
      builder.setCustomTitle(customTitle);
      //builder.setTitle(R.string.dialog_setup_profile_title);
      builder.setMessage(R.string.dialog_setup_profile_message);
      builder.setNegativeButton(R.string.dialog_setup_profile_later,
          new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });
      builder.setPositiveButton(R.string.dialog_setup_profile_setnow,
          new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              if (mUserInfo != null) {
                mNavigationManager.addPage(EditProfileFragment
                    .newInstance(mUserInfo));
              }
            }
          });
      builder.create();
      AlertDialog element = builder.show();
      int dividerId = element.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = element.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }

      preferences.saveTimeShowSetupProfile(format.format(currentTime));
    }
  }
}
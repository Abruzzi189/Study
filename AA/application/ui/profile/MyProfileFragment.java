package com.application.ui.profile;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.application.call.LinphoneVideoCall;
import com.application.call.LinphoneVoiceCall;
import com.application.chat.ChatManager;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddBlockUserRequest;
import com.application.connection.request.AddCommentRequest;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.BuzzDetailRequest;
import com.application.connection.request.BuzzListProfileRequest;
import com.application.connection.request.CheckCallRequest;
import com.application.connection.request.DeleteBuzzRequest;
import com.application.connection.request.DeleteCommentRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.LikeBuzzRequest;
import com.application.connection.request.ListPublicImageRequest;
import com.application.connection.request.RemoveBlockUserRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.request.ReportRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.response.AddBlockUserResponse;
import com.application.connection.response.AddCommentResponse;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.BuzzDetailResponse;
import com.application.connection.response.BuzzListResponse;
import com.application.connection.response.CheckCallResponse;
import com.application.connection.response.DeleteBuzzResponse;
import com.application.connection.response.DeleteCommentResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.LikeBuzzResponse;
import com.application.connection.response.ListPublicImageResponse;
import com.application.connection.response.RemoveBlockUserResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.connection.response.RemoveFriendResponse;
import com.application.connection.response.ReportResponse;
import com.application.connection.response.UploadImageResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.entity.CallUserInfo;
import com.application.entity.GiftCategories;
import com.application.entity.UserInfo;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.ChatFragment;
import com.application.ui.HomeFragment;
import com.application.ui.MainActivity;
import com.application.ui.MeetPeopleFragment;
import com.application.ui.MyPageFragment;
import com.application.ui.SettingsFragment;
import com.application.ui.TrackingBlockFragment;
import com.application.ui.buzz.BuzzDetail;
import com.application.ui.buzz.BuzzItemListView.OnActionBuzzListener;
import com.application.ui.buzz.CommentItemBuzz.OnActionCommentListener;
import com.application.ui.buzz.DetailPictureBuzzActivity;
import com.application.ui.buzz.FavoriteBuzzHandler;
import com.application.ui.buzz.FavoriteBuzzHandler.OnAccessListBuzz;
import com.application.ui.buzz.FavoriteBuzzHandler.OnHandleFavoriteResult;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import com.application.ui.chat.ChatMoreLayout;
import com.application.ui.chat.OnChatMoreListener;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.Panel;
import com.application.ui.customeview.Panel.OnPanelListener;
import com.application.ui.customeview.ProfileApdaterCommon;
import com.application.ui.customeview.ProfileApdaterCommon.OnPanelClickListener;
import com.application.ui.customeview.pulltorefresh.GridViewWithHeaderAndFooter;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridViewAuto;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.notification.ManageOnlineAlertFragment;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import glas.bbsystem.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class MyProfileFragment extends TrackingBlockFragment implements
    ResponseReceiver, OnPanelListener, OnNavigationClickListener,
    OnPanelClickListener, OnActionBuzzListener, OnActionCommentListener, OnDeleteSubCommentListener,
    View.OnTouchListener {

  //hiepuh

  public static final int NUMBER_OF_IMG_THUMB = 4;
  public static final int REQUEST_CODE_CAMERA_IMAGE_CAPTURE = 100;
  public static final int REQUEST_CODE_GET_AVATAR = 101;
  public static final int REQUEST_CODE_GET_IMAGE_THEATER = 102;
  public static final int REQUEST_CODE_BACKSTAGE = 103;
  public static final int REQUEST_CODE_MANAGER_ONLINE_ALERT = 104;
  public static final String TAG_FRAGMENT_USER_PROFILE = "user_profile";
  public static final String TAG_FRAGMENT_BUZZ_DETAIL = "buzz_detail";
  //end
  public static final String FROM_EDIT_PROFILE = "FROM_EDIT_PROFILE";
  public static final String FROM_EDIT_PROFILE_MY_PAGE = "FROM_EDIT_PROFILE_PARENT_IS_MY_PAGE";
  // private ProgressDialog progressDialog;
  private static final int LOADER_USER_INFO = 0;
  private static final int LOADER_LASTEST_BUZZ = 1;
  private static final int LOADER_LASTEST_GIFT = 2;
  private static final int LOADER_ADD_FAVORITE = 3;
  private static final int LOADER_REMOVE_FAVORITE = 4;
  // private static final int LOADER_CHECK_OUT = 5;
  private static final int LOADER_REMOVE_FRIEND = 6;
  private static final int LOADER_DELETE_BUZZ = 7;
  private static final int LOADER_DELETE_COMMENT = 8;
  private static final int LOADER_LIKE_BUZZ = 9;
  private static final int LOADER_ADD_COMMENT = 10;
  private static final int LOADER_PROFILE_IMAGE = 11;
  private static final int LOADER_ID_ADD_BLOCK_USER = 12;
  private static final int LOADER_ID_REMOVE_BLOCK_USER = 13;
  private static final int LOADER_ID_REPORT_USER = 14;
  private static final int LOADER_ID_ADD_TO_FAVORITES = 15;
  private static final int LOADER_ID_REMOVE_FROM_FAVORITES = 16;
  private static final int LOADER_ID_LOAD_BUZZ_DETAIL = 17;
  private static final int LOADER_ID_CHECK_CALL_VOICE = 18;
  private static final int LOADER_ID_CHECK_CALL_VIDEO = 19;
  private static final int LOADER_ID_BASIC_USER_INFO = 20;
  private static final int NUMBER_BUZZ_REQUEST = 11;
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_LIST_USER_ID = "list_user_id";
  private static final String KEY_USER_NAME = "user_name";
  private static final String KEY_IS_MORE_AVAILABLE = "is_more_available";
  private static final String KEY_IS_FROM_MEET_PEOPLE = "is_from_meet_people";
  private static final String EXTRA_HAS_NAVIGATION = "HAS_NAVIGATION";
  private static final String EXTRA_TAB_PROFILE_SELECTED = "PROFILE_SELECTED";
  private static final String EXTRA_IS_FROM_EDIT_PROFILE = "FROM_EDIT_PROFILE";
  private static final String KEY_LIST_MEET_PEOPLE = "list_meet_people";
  private static final String KEY_MEET_PEOPLE_SETTING = "meet_people_setting";
  private static final int REQUEST_GIFT = 5001;
  public static int Alert;
  boolean isMyProfile = false;
  private ChatMoreLayout mChatMoreLayout;
  private PopupWindow mPopupChatMoreOptions;
  private boolean isVoiceCallWaiting = false;
  private boolean isVideoCallWaiting = false;
  private View mViewFreezedLayer;
  private PopupWindow mPopupChatMoreOptions1;
  private AlertDialog mConfirmDialog;
  private String userId;
  private PullToRefreshGridViewAuto pullToRefreshView;
  private GridViewWithHeaderAndFooter gridView;
  private View mEmptyBuzz;
  private ArrayList<Object> objectList;
  private ProfileApdaterCommon adapterCommon;
  private AlertDialog confirmDialog;
  private Dialog dialogMore;
  private android.app.AlertDialog mAlertDialog;
  private boolean isFavorite = false;
  private UserInfoResponse mUserInfoResponse;
  private View mView;
  private RelativeLayout rlParentComment;
  private ImageView imgCommentLike;
  private EditText edtComment;
  private BuzzListItem itemToLike;
  private BuzzListItem itemToDelete;
  private BuzzListItem itemToDeleteComment;
  private BuzzListCommentItem itemComment;
  private String mAvatarId;
  private int gender = Constants.GENDER_TYPE_MAN;
  private int isAlert = 0;
  private boolean isRequesAddFavorite = false;
  private boolean isButtonEnable = true;
  private ProgressDialog progressDialog;
  private boolean isFirstProfile;
  private boolean isLastProfile;
  private int mPixelPos;
  private boolean mBlockedUser = false;
  private boolean mHasBackNavigation = false;
  private String isFromEditProfile;
  private boolean mNeedRefresh = false;
  private MainActivity mMainActivity;
  private FavoriteBuzzHandler mFavoriteHandler;
  private CallUserInfo callUserInfo;
  private int mTabProfileSelected = ProfileApdaterCommon.PROFILE_TAB;
  private Bundle mBundleSaveStated;
  private String mBuzzDetailId = "";
  private String resumeBuzzID;
  private ArrayList<String> mListUserIds;
  private String mUserName;
  private boolean isMoreAvailable = true;
  private boolean isFromMeetPeople = false;
  private OnCheckStateNavigation onSlideClickListener;
  private int mHeightScreen;
  private int mCurrentCallType = Constants.CALL_TYPE_VOICE;
  private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
  private boolean mUserScrolled = false;
  private OnScrollListener onScrollListener = new OnScrollListener() {

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
      if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        mUserScrolled = false;
      } else {
        mUserScrolled = true;
      }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
        int visibleItemCount, int totalItemCount) {
      if (mUserScrolled) {
        initSlideState();
      }
    }
  };
  private OnRefreshListener2<GridViewWithHeaderAndFooter> onRefreshListener = new OnRefreshListener2<GridViewWithHeaderAndFooter>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      refreshData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      requestAddBuzz();
    }
  };
  private UploadImageProgress uploadImageProgress = new UploadImageProgress() {

    @Override
    public void uploadImageSuccess(UploadImageResponse response) {
      if (getActivity() == null) {
        return;
      }
      Toast.makeText(getActivity(), R.string.upload_success,
          Toast.LENGTH_LONG).show();
      if (response.getIsApproved() == Constants.IS_APPROVED) {
        UserPreferences.getInstance().saveAvaId(response.getImgId());
        LogUtils.d("LOAD", "uploadImageSuccess  -refreshData");
        refreshData();
      } else {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);

        Builder builder = new Builder(getActivity());
        String title = getResources().getString(
            R.string.unapproved_image_dialog_title);
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);

        //builder.setTitle(title);
        String message = getResources().getString(
            R.string.unapproved_image_dialog_content);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ok, null);
        AlertDialog element = builder.show();
        int dividerId = element.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = element.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

        UserPreferences.getInstance().savePendingAva(
            response.getImgId());
      }
    }

    @Override
    public void uploadImageStart() {
    }

    @Override
    public void uploadImageFail(int code) {
      // progressDialog.dismiss();
      if (code == Response.SERVER_UPLOAD_IMAGE_ERROR) {
        if (getActivity() == null) {
          return;
        }
        com.application.ui.customeview.AlertDialog
            .showUploadImageErrorAlert(getActivity());
      } else {
        String message = getString(R.string.upload_fail);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
            .show();
      }
    }
  };
  private OnHandleFavoriteResult handleFavoriteResult = new OnHandleFavoriteResult() {

    @Override
    public void startRemoveFavorite(
        RemoveFavoriteRequest removeFavoriteRequest) {
      restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES,
          removeFavoriteRequest);
    }

    @Override
    public void startAddFavorite(AddFavoriteRequest addFavoriteRequest) {
      restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
    }

    @Override
    public void removeFavoriteSuccess(BuzzListItem item) {
      adapterCommon.notifyDataSetChanged();
    }

    @Override
    public void addFavoriteSuccess(BuzzListItem item) {
      adapterCommon.notifyDataSetChanged();
    }

    @Override
    public void sendGift(BuzzListItem item) {
      showGiveGiftFragment(item);
    }
  };
  private OnAccessListBuzz accessListBuzz = new OnAccessListBuzz() {

    @Override
    public int getNumberBuzz() {
      if (adapterCommon == null) {
        return 0;
      }
      return adapterCommon.getNumberBuzzList();
    }

    @Override
    public BuzzListItem getBuzzAtPosition(int position) {
      if (adapterCommon == null) {
        return null;
      }
      return adapterCommon.getBuzzListItem(position);
    }
  };
  private OnChatMoreListener onChatMoreListener = new OnChatMoreListener() {
    @Override
    public void onVoiceCall() {
    }

    @Override
    public void onVideoCall() {
    }

    @Override
    public void onSendGift() {
      hideChatMoreOptions();
      mMainActivity.setUnbindChatOnStop(true);

      GiftCategories categories = new GiftCategories("get_all_gift", 0,
          getResources().getString(R.string.give_gift_all_title), 1);
      ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
          mUserInfoResponse.getUserId(),
          mUserInfoResponse.getUserName(), categories, false, false);
      chooseGiftToSend
          .setTargetFragment(getRootParentFragment(MyProfileFragment.this), REQUEST_GIFT);
      replaceFragment(chooseGiftToSend, "ChooseGiftToSend");
    }

    @Override
    public void onReport() {
      hideChatMoreOptions();
      executeReportUser();
    }

    @Override
    public void onFavorite() {
      hideChatMoreOptions();
      favorite();
    }

    @Override
    public void onBlock() {
      hideChatMoreOptions();
      executeBlockUser();
    }

    @Override
    public void onAlertOnline() {
      hideChatMoreOptions();
      if (userId != null && userId.length() > 0) {
        ManageOnlineAlertFragment fragment = ManageOnlineAlertFragment
            .newInstance(userId, mAvatarId, mUserName, isAlert);
        fragment.setTargetFragment(getRootParentFragment(MyProfileFragment.this),
            REQUEST_CODE_MANAGER_ONLINE_ALERT);
        replaceFragment(fragment, "manage_online");
      }
    }

    @Override
    public void onShown() {
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          adapterCommon.setButtonEnable(true);
          getSlidingMenu().setSlidingEnabled(true);
        }
      }, 200);
    }
  };

  public static MyProfileFragment newInstance(String userId,
      boolean isBackNavigation) {
    MyProfileFragment fragment = new MyProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putBoolean(EXTRA_HAS_NAVIGATION, isBackNavigation);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static MyProfileFragment newInstance(String userId) {
    MyProfileFragment fragment = new MyProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putBoolean(EXTRA_HAS_NAVIGATION, true);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static MyProfileFragment newInstance(String userId,
      String isFromEditProfile) {
    MyProfileFragment fragment = new MyProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putString(EXTRA_IS_FROM_EDIT_PROFILE, isFromEditProfile);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static MyProfileFragment newInstance(String userId, String userName,
      ArrayList<String> listUserIds, boolean isMoreAvailable,
      boolean isFromMeetPeople) {
    MyProfileFragment fragment = new MyProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putString(KEY_USER_NAME, userName);
    bundle.putSerializable(KEY_LIST_USER_ID, listUserIds);
    bundle.putBoolean(KEY_IS_MORE_AVAILABLE, isMoreAvailable);
    bundle.putBoolean(KEY_IS_FROM_MEET_PEOPLE, isFromMeetPeople);
    fragment.setArguments(bundle);
    return fragment;
  }

  public void setOnSlideClickListener(OnCheckStateNavigation listener) {
    this.onSlideClickListener = listener;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mUserInfoResponse != null) {
      Alert = mUserInfoResponse.getIsAlt();
    }
    isButtonEnable = true;
    //setTitleName();
    adapterCommon.setButtonEnable(isButtonEnable);
    BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
    baseFragmentActivity.setOnNavigationClickListener(this);

    setupActionBar();

    // If need to update one Item of List
    if (resumeBuzzID != null) {
      if (mBundleSaveStated != null
          && mBundleSaveStated
          .containsKey(EXTRA_TAB_PROFILE_SELECTED)) {
        mTabProfileSelected = mBundleSaveStated
            .getInt(EXTRA_TAB_PROFILE_SELECTED);
      }
      LogUtils.e("resumeBuzzID", resumeBuzzID);
      String token = UserPreferences.getInstance().getToken();
      RequestParams request = new BuzzDetailRequest(token, resumeBuzzID,
          Constants.BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_COMMENTS);
      restartRequestServer(LOADER_ID_LOAD_BUZZ_DETAIL, request);
    } else if (mNeedRefresh) {
      LogUtils.d("LOAD", "onResume -refreshData");
      refreshData();
      mNeedRefresh = false;
    }

    // Update favorite
    checkFavorite(FavouritedPrefers.getInstance().hasContainFav(userId));
    Fragment activePage = mNavigationManager.getActivePage();
    if (activePage instanceof MyProfileFragment) {
      getSlidingMenu().setSlidingEnabled(true);
    }
  }

  private void setupActionBar() {
    if (isMyProfile) {
      mActionBar.setProfileVisibility(View.INVISIBLE);
    } else {
      mActionBar.setProfileVisibility(View.VISIBLE);
    }
  }

  private void setTitleName() {
    if (mUserName != null && mUserName.length() > 0) {
      if (getNavigationBar() != null) {
        getNavigationBar().setCenterTitle(mUserName);
      }
      if (mActionBar != null) {
        mActionBar.setTextCenterTitle(mUserName);
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(EXTRA_HAS_NAVIGATION, mHasBackNavigation);
    outState.putString(KEY_USER_ID, userId);
    outState.putSerializable(KEY_LIST_USER_ID, mListUserIds);
    outState.putString(KEY_USER_NAME, mUserName);
    outState.putBoolean(KEY_IS_MORE_AVAILABLE, isMoreAvailable);
    outState.putBoolean(KEY_IS_FROM_MEET_PEOPLE, isFromMeetPeople);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      userId = savedInstanceState.getString(KEY_USER_ID);
      mListUserIds = (ArrayList<String>) savedInstanceState
          .getSerializable(KEY_LIST_USER_ID);
      mUserName = savedInstanceState.getString(KEY_USER_NAME);
      isMoreAvailable = savedInstanceState
          .getBoolean(KEY_IS_MORE_AVAILABLE);
      isFromMeetPeople = savedInstanceState
          .getBoolean(KEY_IS_FROM_MEET_PEOPLE);
    } else {
      userId = getArguments().getString(KEY_USER_ID);
      mListUserIds = (ArrayList<String>) getArguments().getSerializable(
          KEY_LIST_USER_ID);
      mUserName = getArguments().getString(KEY_USER_NAME);
      isMoreAvailable = getArguments().getBoolean(KEY_IS_MORE_AVAILABLE);
      isFromMeetPeople = getArguments().getBoolean(
          KEY_IS_FROM_MEET_PEOPLE);
    }

    Bundle bundle = getArguments();
    if (bundle != null && bundle.containsKey(EXTRA_HAS_NAVIGATION)) {
      mHasBackNavigation = bundle.getBoolean(EXTRA_HAS_NAVIGATION);
    } else if (savedInstanceState != null
        && savedInstanceState.containsKey(EXTRA_HAS_NAVIGATION)) {
      mHasBackNavigation = savedInstanceState
          .getBoolean(EXTRA_HAS_NAVIGATION);
    }
    if (bundle != null && bundle.containsKey(EXTRA_TAB_PROFILE_SELECTED)) {
      mTabProfileSelected = bundle.getInt(EXTRA_TAB_PROFILE_SELECTED);
    }
    mFavoriteHandler = new FavoriteBuzzHandler(this, handleFavoriteResult,
        accessListBuzz);

    if (bundle != null && bundle.containsKey(EXTRA_IS_FROM_EDIT_PROFILE)) {
      setIsFromEditProfile(bundle.getString(EXTRA_IS_FROM_EDIT_PROFILE));

    } else if (savedInstanceState != null
        && savedInstanceState.containsKey(EXTRA_IS_FROM_EDIT_PROFILE)) {

      setIsFromEditProfile(savedInstanceState
          .getString(EXTRA_IS_FROM_EDIT_PROFILE));
    }

    DisplayMetrics displaymetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay()
        .getMetrics(displaymetrics);
    mHeightScreen = displaymetrics.heightPixels;
  }

  private boolean checkIsMyProfile() {
    UserPreferences preferences = UserPreferences.getInstance();
    if (userId.equalsIgnoreCase(preferences.getUserId())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    requestDirtyWord();
    rlParentComment = (RelativeLayout) mView
        .findViewById(R.id.fragment_my_profile_rl_comment);
    imgCommentLike = (ImageView) mView
        .findViewById(R.id.fragment_my_profile_like_button_list_buzz);
    edtComment = (EditText) mView
        .findViewById(R.id.fragment_my_profile_ed_buzz_item_input_comment);
    mView.findViewById(R.id.fragment_my_profile_tv_out).setOnClickListener(
        new OnClickListener() {

          @Override
          public void onClick(View v) {
            rlParentComment.setVisibility(View.GONE);
            Utility.hideSoftKeyboard(getActivity());
            edtComment.setText("");
          }
        });
    initialListview(mView);
    initialDialoOptionMore();
    if (!isMoreAvailable()) {
      setTitleName();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater
        .inflate(R.layout.fragment_my_profile, container, false);
    return mView;
  }

  private boolean listIsAtTop() {
    if (gridView == null) {
      return true;
    }
    if (gridView.getChildCount() == 0) {
      return true;
    }
    return gridView.getChildAt(0).getTop() == 0;
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mNeedRefresh = true;
    mView = null;
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
    if (confirmDialog != null && confirmDialog.isShowing()) {
      confirmDialog.dismiss();
    }
    if (dialogMore != null && dialogMore.isShowing()) {
      dialogMore.dismiss();
    }
    if (mAlertDialog != null && mAlertDialog.isShowing()) {
      mAlertDialog.dismiss();
    }
    if (mBundleSaveStated == null) {
      mBundleSaveStated = new Bundle();
      mBundleSaveStated.putInt(EXTRA_TAB_PROFILE_SELECTED,
          adapterCommon.getTabSelected());
    }
    Utility.hideSoftKeyboard(getActivity());
    hideChatMoreOptions();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
    mMainActivity.setOnNavigationClickListener(this);
  }

  private int getScroll() {
    View c = gridView.getChildAt(0); // this is the first visible row
    if (c == null) {
      return 0;
    }
    int scrollY = -c.getTop();
    listViewItemHeights
        .put(gridView.getFirstVisiblePosition(), c.getHeight());
    for (int i = 0; i < gridView.getFirstVisiblePosition(); ++i) {
      if (listViewItemHeights.get(i) != null) // (this is a sanity check)
      {
        scrollY += listViewItemHeights.get(i); // add all heights of the
      }
      // views that are gone
    }
    return scrollY;
  }

  private void initialListview(View view) {
    mViewFreezedLayer = (View) view
        .findViewById(R.id.ib_chat_freezed_layer);
    mViewFreezedLayer.setOnTouchListener(this);
    pullToRefreshView = (PullToRefreshGridViewAuto) view
        .findViewById(R.id.fragment_my_profile_lv_info);
    pullToRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
    pullToRefreshView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    pullToRefreshView.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
    pullToRefreshView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
          mUserScrolled = false;
        } else {
          mUserScrolled = true;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (mUserScrolled) {
          initSlideState();
        }

        if (pullToRefreshView.isRefreshing() || adapterCommon == null || adapterCommon.isEmpty()) {
          return;
        }

        if (firstVisibleItem + visibleItemCount == totalItemCount) {
          PullToRefreshBase.Mode mode = pullToRefreshView.getMode();
          if (pullToRefreshView.isRefreshing()) {
            LogUtils.i(TAG, "PullView.isRefreshing: true");
          } else {
            LogUtils.e(TAG, "PullView.isRefreshing: false");
          }
          if (mode == PullToRefreshBase.Mode.PULL_FROM_END
              || mode == PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY
              || mode == PullToRefreshBase.Mode.BOTH) {
            pullToRefreshView.performPullUp();
          }
        }
      }
    });
    gridView = pullToRefreshView.getRefreshableView();
    gridView.setVerticalSpacing(0);
    pullToRefreshView.setOnRefreshListener(onRefreshListener);
    pullToRefreshView.getLoadingLayoutProxy(true, false);
    objectList = new ArrayList<Object>();

    UserPreferences userPreferences = UserPreferences.getInstance();
    String myId = userPreferences.getUserId();
    if (userId == null || userId.length() == 0 || myId.equals(userId)) {
      isMyProfile = true;
    }
    if (adapterCommon == null) {
      adapterCommon = new ProfileApdaterCommon(getActivity(), this,
          objectList, isMyProfile);
      adapterCommon.setOnActionBuzzListener(this);
      adapterCommon.setOnDeleteBuzzCommentListener(this);
      adapterCommon.setOnDeleteSubCommentListener(this);

      if (mBundleSaveStated != null
          && mBundleSaveStated
          .containsKey(EXTRA_TAB_PROFILE_SELECTED)) {
        mTabProfileSelected = mBundleSaveStated
            .getInt(EXTRA_TAB_PROFILE_SELECTED);
      }
      adapterCommon.setTabSelected(mTabProfileSelected);
      adapterCommon.setOnPanelClickListener(this);
      gridView.setAdapter(adapterCommon);
      gridView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
      gridView.setCacheColorHint(Color.TRANSPARENT);
      LogUtils.e("initialListview", "refeshData");
      isButtonEnable = true;
      adapterCommon.setButtonEnable(isButtonEnable);
      if (isMyProfile || !isFromMeetPeople) {
        refreshData();
      }
    } else {
      if (mBundleSaveStated != null
          && mBundleSaveStated
          .containsKey(EXTRA_TAB_PROFILE_SELECTED)) {
        mTabProfileSelected = mBundleSaveStated
            .getInt(EXTRA_TAB_PROFILE_SELECTED);
      }
      adapterCommon.setOnActionBuzzListener(this);
      adapterCommon.setOnDeleteBuzzCommentListener(this);
      adapterCommon.setTabSelected(mTabProfileSelected);
      adapterCommon.setOnPanelClickListener(this);
      gridView.setAdapter(adapterCommon);
      gridView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
      gridView.setCacheColorHint(Color.TRANSPARENT);
    }
  }

  /**
   * using pull data
   */
  private void refreshData() {
    showWaitingDialog();
    if (adapterCommon != null) {
      isButtonEnable = true;
      adapterCommon.setButtonEnable(isButtonEnable);
    }
    String token = UserPreferences.getInstance().getToken();
    // requestLastestBuzz(token);
    requestUserinfo(token);
  }

  /**
   * call api add favorite user
   */
  private void requestAddFavorite() {
    String token = UserPreferences.getInstance().getToken();
    isRequesAddFavorite = true;
    AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
        userId);
    restartRequestServer(LOADER_ADD_FAVORITE, addFavoriteRequest);
  }
  ;

  /**
   * call api remove favorite user
   */
  private void requestRemoveFavorite() {
    String token = UserPreferences.getInstance().getToken();
    RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
        token, userId);
    restartRequestServer(LOADER_REMOVE_FAVORITE, removeFavoriteRequest);
  }

  /**
   * get user info
   *
   * @param token using call api
   */
  private void requestUserinfo(String token) {
    // showWaitingDialog();
    UserInfoRequest infoRequest = null;
    if (userId == null || userId.length() == 0) {
      infoRequest = new UserInfoRequest(token);
    } else {
      infoRequest = new UserInfoRequest(token, userId);
    }
    restartRequestServer(LOADER_USER_INFO, infoRequest);
  }

  /**
   * get latest buzz list
   */
  private void requestLastestBuzz(int skip) {
    String token = UserPreferences.getInstance().getToken();
    BuzzListProfileRequest buzzListRequest = null;
    if (userId == null || userId.length() == 0) {
      buzzListRequest = new BuzzListProfileRequest(token, null, skip,
          NUMBER_BUZZ_REQUEST);
    } else {
      buzzListRequest = new BuzzListProfileRequest(token, userId, skip,
          NUMBER_BUZZ_REQUEST);
    }
    restartRequestServer(LOADER_LASTEST_BUZZ, buzzListRequest);
  }

  private void restartLastestBuzz() {
    requestLastestBuzz(0);
  }

  private void requestAddBuzz() {
    int skip = adapterCommon.getNumberBuzzList();
    requestLastestBuzz(skip);
  }

  private void initSlideState() {
    mPixelPos = getScroll();
    int state = changeSlideState();
    if (SliderProfileFragment.getStateList() != null) {
      SliderProfileFragment.getStateList().put(userId, state);
    }
    if (onSlideClickListener != null) {
      onSlideClickListener.onCheckState(state);
    }
  }

  @Override
  public void onPanelClosed(Panel panel) {
  }

  @Override
  public void onPanelOpened(Panel panel) {
  }

  private void showGiveGiftFragment() {
    // GiveGiftFragment giveGiftfragment = null;
    // if (userId != null && userId.length() > 0) {
    // giveGiftfragment = GiveGiftFragment.newInstance(userId, userName);
    // } else {
    // giveGiftfragment = GiveGiftFragment.newInstance(Preferences
    // .getInstance(getActivity()).getUserId(), Preferences
    // .getInstance(getActivity()).getUserName());
    // }
    // replaceFragment(giveGiftfragment,
    // GiveGiftFragment.TAG_FRAGMENT_GIVE_GIFT);

    GiftCategories categories = new GiftCategories("get_all_gift", 0,
        getResources().getString(R.string.give_gift_all_title), 1);
    ChooseGiftToSend chooseGiftToSend = null;

    if (userId != null && userId.length() > 0) {
      chooseGiftToSend = ChooseGiftToSend.newInstance(userId, mUserName,
          categories, true);

    } else {
      String userName = UserPreferences.getInstance().getUserName();
      chooseGiftToSend = ChooseGiftToSend.newInstance(UserPreferences
          .getInstance().getUserId(), userName, categories, true);
    }

    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  private void favorite() {
    if (isFavorite) {
      if (confirmDialog != null && confirmDialog.isShowing()) {
        confirmDialog.dismiss();
      }
      String title = getString(R.string.profile_remove_from_favorites_title);
      String msg = String.format(
          getString(R.string.profile_remove_from_favorites_message),
          mUserName);
      confirmDialog = new CustomConfirmDialog(getActivity(), title, msg,
          false)
          .setPositiveButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              requestRemoveFavorite();
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
      requestAddFavorite();
    }
  }

  private void initialDialoOptionMore() {
    if (dialogMore == null) {
      AlertDialog.Builder dialogMoreBuilder = new AlertDialog.Builder(
          getActivity());
      dialogMoreBuilder.setItems(R.array.more_array,
          new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              switch (which) {
                case 0:
                  // manager online alert
                  if (userId != null && userId.length() > 0) {
                    replaceFragment(ManageOnlineAlertFragment
                            .newInstance(userId, mAvatarId,
                                mUserName, isAlert),
                        "manage_online");
                  }
                  break;
                case 1:
                  // give gif
                  showGiveGiftFragment();
                  break;
                case 2:
                  // block user
                  requestBlockUser();
                  break;
                default:
                  break;
              }
            }
          });
      dialogMore = dialogMoreBuilder.create();
      dialogMore.setCancelable(true);
    }
  }

  private void requestBlockUser() {
    LogUtils.d(TAG, "requestBlockUser Started");

    String title = "";
    String message = "";

    if (mBlockedUser) {
      title = getString(R.string.chat_screen_unblock_dialog_title);
      message = String.format(
          getString(R.string.chat_screen_unblock_dialog_message),
          mUserName);
    } else {
      title = getString(R.string.chat_screen_block_dialog_title);
      message = String.format(
          getString(R.string.chat_screen_block_dialog_message),
          mUserName);
    }
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder builder = new CenterButtonDialogBuilder(getActivity(), true);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.common_cancel, null);
    builder.setPositiveButton(R.string.common_yes,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            String token = UserPreferences.getInstance().getToken();
            if (mBlockedUser) {
              RemoveBlockUserRequest rbur = new RemoveBlockUserRequest(
                  token, userId);
              restartRequestServer(LOADER_ID_REMOVE_BLOCK_USER,
                  rbur);
            } else {
              AddBlockUserRequest abur = new AddBlockUserRequest(
                  token, userId);
              restartRequestServer(LOADER_ID_ADD_BLOCK_USER, abur);
            }
          }
        });
    builder.create();
    mAlertDialog = builder.show();
    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "requestBlockUser Ended");
  }

  private void requestReportUser() {
    LogUtils.d(TAG, "requestReportUser Started");
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = getResources();
    Builder builder = new Builder(getActivity());
    String title = "";
    String[] items = null;

    title = resource.getString(R.string.dialog_confirm_report_user_title);
    items = resource.getStringArray(R.array.report_user_type);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.select_dialog_item, items);

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        LogUtils.d(TAG, "onClick Started");

        LogUtils.d(TAG,
            String.format("requestReportUser: which = %d", which));

        if (which > 0) {
          int reportType = 0;
          Resources resource = getResources();
          String[] reportTypes = resource
              .getStringArray(R.array.report_type);
          String[] reportUsers = resource
              .getStringArray(R.array.report_user_type);
          String reportString = reportUsers[which];
          int length = reportTypes.length;
          for (int i = 0; i < length; i++) {
            if (reportString.equals(reportTypes[i])) {
              reportType = i;
            }
          }
          String token = UserPreferences.getInstance().getToken();
          ReportRequest reportRequest = new ReportRequest(token,
              userId, reportType, Constants.REPORT_TYPE_USER);
          restartRequestServer(LOADER_ID_REPORT_USER, reportRequest);
        }

        LogUtils.d(TAG, "onClick Ended");
      }
    });
    builder.create();
    mAlertDialog = builder.show();
    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "requestReportUser Ended");
  }

  /**
   * Setup Navigation bar for application
   */
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    if (mHasBackNavigation) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    } else {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    }
    getNavigationBar().setShowUnreadMessage(true);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    getSlidingMenu().showSecondaryMenu(true);
  }

  @Override
  public void startRequest(int loaderId) {
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage(getString(R.string.waiting));
    if (loaderId == LOADER_ID_CHECK_CALL_VIDEO
        || loaderId == LOADER_ID_CHECK_CALL_VOICE) {
      progressDialog.show();
    }
  }

  private void checkFavorite(boolean favorite) {
    if (userId == null || userId.length() == 0) {
      return;
    }
    isFavorite = favorite;
    adapterCommon.setFavorite(isFavorite);
  }

  private void filterUserInfo() {
    if (userId != null && userId.length() > 0 && mUserInfoResponse != null) {
      checkFavorite(mUserInfoResponse.isFavorite());
    }

    checkStateProfile();

    UserInfo userInfo = new UserInfo();
    Alert = mUserInfoResponse.getIsAlt();
    mAvatarId = mUserInfoResponse.getAvataId();
    gender = mUserInfoResponse.getGender();
    isAlert = mUserInfoResponse.getIsAlt();
    mUserName = mUserInfoResponse.getUserName() == null ? ""
        : mUserInfoResponse.getUserName();
    setTitleName();
    userInfo.setUser(mUserInfoResponse);
    userInfo.setFirstProfile(isFirstProfile);
    userInfo.setLastProfile(isLastProfile);
    adapterCommon.updateUser(userInfo);
    restartLastestBuzz();
  }

  private void filterLastestBuzz(BuzzListResponse listResponse) {
    ArrayList<BuzzListItem> list = listResponse.getBuzzListItem();
    if (list == null || list.size() == 0) {
      pullToRefreshView.setMode(Mode.PULL_FROM_START);
    }
    adapterCommon.updateBuzz(list);

  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (progressDialog != null) {
      progressDialog.dismiss();
    }

    hideWaitingDialog();

    int responseCode = response.getCode();
    if (responseCode == Response.SERVER_BLOCKED_USER) {
      handleBlockedUser();
      return;
    }

    boolean updateConnections = false;
    // boolean showGiveGiftDialog = false;

    if (response instanceof ListPublicImageResponse) {
      ListPublicImageResponse listPublicImageResponse = (ListPublicImageResponse) response;
      if (listPublicImageResponse.getCode() == Response.SERVER_SUCCESS) {
        if (mUserInfoResponse == null) {
          return;
        }
        ProfilePictureData profilePictureData = new ProfilePictureData();
        profilePictureData.addListImg(listPublicImageResponse
            .getListImage());
        profilePictureData.setUserId(userId);
        profilePictureData.setGender(gender);
        profilePictureData.setNumberOfImage(mUserInfoResponse
            .getPublicImageNumber());
        mUserInfoResponse.setProfilePicData(profilePictureData);
        adapterCommon.setThumbImage(profilePictureData);
      }
      getLoaderManager().destroyLoader(LOADER_PROFILE_IMAGE);
    }

    if (response instanceof UserInfoResponse) {
      UserInfoResponse info = (UserInfoResponse) response;
      if (info.getCode() == Response.SERVER_SUCCESS) {
        // Check user gender
        UserPreferences userPreferences = UserPreferences.getInstance();
        if ((userPreferences.getGender() == info.getGender() && !checkIsMyProfile())) {
          new Handler().post(new Runnable() {
            @Override
            public void run() {
              MeetPeopleFragment fragment = new MeetPeopleFragment();
              mNavigationManager.switchPage(fragment);
            }
          });
          return;
        }

        mUserInfoResponse = info;
        filterUserInfo();
        // must update point to references
        if (mUserInfoResponse.getUserId().equals(
            userPreferences.getUserId())) {
          userPreferences.saveNumberPoint(mUserInfoResponse
              .getPoint());
          userPreferences.saveGender(info.getGender());
          // save avatar again and change in MainActivity to sync
          userPreferences.saveAvaId(mUserInfoResponse.getAvataId());
          ((MainActivity) getActivity())
              .onMainMenuUpdate(MainActivity.UPDATE_AVATAR);
        } else {
          FavouritedPrefers favouritedPrefers = FavouritedPrefers
              .getInstance();
          if (info.isFavorite()) {
            favouritedPrefers
                .saveFav(mUserInfoResponse.getUserId());
          } else {
            favouritedPrefers.removeFav(mUserInfoResponse
                .getUserId());
          }
        }
      } else {
        com.application.ui.customeview.ErrorApiDialog.showAlert(
            getActivity(), R.string.common_error,
            response.getCode());
      }
      pullToRefreshView.onRefreshComplete();
      loadListThumbImage();
      getLoaderManager().destroyLoader(LOADER_USER_INFO);
    }
    if (response instanceof BuzzListResponse) {
      BuzzListResponse listResponse = (BuzzListResponse) response;
      pullToRefreshView.onRefreshComplete();
      if (listResponse.getCode() == Response.SERVER_SUCCESS) {
        filterLastestBuzz(listResponse);
      }
      getLoaderManager().destroyLoader(LOADER_LASTEST_BUZZ);
    }
    if (response instanceof AddFavoriteResponse) {
      AddFavoriteResponse favorite = (AddFavoriteResponse) response;
      if (favorite.getCode() == Response.SERVER_SUCCESS) {
        mUserInfoResponse.setFavorite(true);
        checkFavorite(true);
        // Increase number of favorites
        UserPreferences.getInstance().increaseFavorite();
        updateConnections = true;

        // save fav to list
        FavouritedPrefers.getInstance().saveFav(userId);
        // isFavorite = true;
        if (confirmDialog != null && confirmDialog.isShowing()) {
          confirmDialog.dismiss();
        }
        String title = getString(R.string.profile_add_to_favorites_title);
        String msg = String.format(
            getString(R.string.profile_add_to_favorites_message),
            mUserName);
        confirmDialog = new CustomConfirmDialog(getActivity(), title,
            msg, true)
            .setPositiveButton(0, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                confirmDialog.dismiss();
                showGiveGiftFragment();
                isFavorite = true;
              }
            })
            .setNegativeButton(0, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                confirmDialog.dismiss();
                isFavorite = false;
              }
            })
            .create();
        if (isRequesAddFavorite) {
          confirmDialog.show();

          int dividerId = confirmDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = confirmDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(
                confirmDialog.getContext().getResources().getColor(R.color.transparent));
          }
          isRequesAddFavorite = false;
        }
      }
      getLoaderManager().destroyLoader(LOADER_ADD_FAVORITE);
    }
    if (response instanceof RemoveFavoriteResponse) {
      RemoveFavoriteResponse removeFavoriteResponse = (RemoveFavoriteResponse) response;
      if (removeFavoriteResponse.getCode() == Response.SERVER_SUCCESS) {
        mUserInfoResponse.setFavorite(false);
        checkFavorite(false);
        // Decrease number of favorites
        UserPreferences.getInstance().decreaseFavorite();
        updateConnections = true;
        isFavorite = false;

        // remove fav from list
        FavouritedPrefers.getInstance().removeFav(userId);

      }
      getLoaderManager().destroyLoader(LOADER_REMOVE_FAVORITE);
    }
    switch (loader.getId()) {
      case LOADER_DELETE_BUZZ:
      case LOADER_DELETE_COMMENT:
      case LOADER_LIKE_BUZZ:
      case LOADER_ADD_COMMENT:
        handleBuzz(response);
        getLoaderManager().destroyLoader(LOADER_DELETE_BUZZ);
        getLoaderManager().destroyLoader(LOADER_DELETE_COMMENT);
        getLoaderManager().destroyLoader(LOADER_LIKE_BUZZ);
        getLoaderManager().destroyLoader(LOADER_ADD_COMMENT);
        break;
      case LOADER_ID_ADD_BLOCK_USER:
        handleBlockUserResponse((AddBlockUserResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_ADD_BLOCK_USER);
        break;
      case LOADER_ID_REMOVE_BLOCK_USER:
        handleUnblockUserResponse((RemoveBlockUserResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_REMOVE_BLOCK_USER);
        break;
      case LOADER_ID_REPORT_USER:
        handleReportUserResponse(response);
        getLoaderManager().destroyLoader(LOADER_ID_REPORT_USER);
        break;
      case LOADER_ID_ADD_TO_FAVORITES:
        mFavoriteHandler
            .handleAddFavoriteResponse((AddFavoriteResponse) response);
        getLoaderManager().destroyLoader(LOADER_ADD_FAVORITE);
        break;
      case LOADER_ID_REMOVE_FROM_FAVORITES:
        mFavoriteHandler
            .handleRemoveFavoriteResponse((RemoveFavoriteResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
        break;
      case LOADER_ID_LOAD_BUZZ_DETAIL:
        handleLoadBuzzDetail((BuzzDetailResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_LOAD_BUZZ_DETAIL);
        break;
      case LOADER_ID_CHECK_CALL_VOICE:
        handleCheckCall(false, (CheckCallResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VOICE);
        break;
      case LOADER_ID_CHECK_CALL_VIDEO:
        handleCheckCall(true, (CheckCallResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VIDEO);
        break;
      case LOADER_ID_BASIC_USER_INFO:
        handlerCheckRequestCall((GetBasicInfoResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_BASIC_USER_INFO);
        break;
      default:
        break;
    }
    if (updateConnections) {
      FragmentActivity parent = getActivity();
      if (parent != null && parent instanceof MainActivity) {
        ((MainActivity) parent)
            .onMainMenuUpdate(MainActivity.UPDATE_CONNECTIONS);
      }
    }
  }

  private void handleCheckCall(boolean isVideo, CheckCallResponse response) {
    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      if (isVideo) {
        if (LinphoneVideoCall.instance == null) {
          LinphoneVideoCall.startOutGoingCall(getActivity(),
              callUserInfo);
        }
      } else {
        if (LinphoneVoiceCall.instance == null) {
          LinphoneVoiceCall.startOutGoingCall(getActivity(),
              callUserInfo);
        }
      }
    } else if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
      if (isVideo) {
        NotEnoughPointDialog.showForVideoCall(getActivity(),
            response.getPoint());

        // CUONGNV01032016 : Remove show dialog het point
//				Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//				intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_VIDEO_CALL);
//				startActivity(intent);
      } else {
        NotEnoughPointDialog.showForVoiceCall(getActivity(),
            response.getPoint());

        // CUONGNV01032016 : Remove show dialog het point
//				Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//				intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_VOICE_CALL);
//				startActivity(intent);
      }
    } else if (code == Response.SERVER_RECEIVER_NOT_ENOUGH_MONEY) {
      NotEnoughPointDialog.showForCallRecever(getActivity());
    }
  }

  private void handleLoadBuzzDetail(BuzzDetailResponse response) {
    BuzzListItem item = response.getBuzzDetail();
    if (item != null) {
      adapterCommon.replaceBuzzItem(resumeBuzzID, item);
      resumeBuzzID = "";
    } else {
      adapterCommon.deleteBuzz(resumeBuzzID);
      resumeBuzzID = "";
    }
  }

  private void handleBlockedUser() {
    LogUtils.d(TAG, "handleBlockedUser Started");

    Utility.showToastMessage(getActivity(),
        getString(R.string.action_is_not_performed));

    View navigationBar = getNavigationBar();
    if (navigationBar != null) {
      new Handler().post(new Runnable() {
        public void run() {
          LogUtils.d(TAG, "handleBlockedUser.Runnable.run Started");

          onNavigationLeftClick(getNavigationBar());

          LogUtils.d(TAG, "handleBlockedUser.Runnable.run Ended");
        }
      });
    }

    LogUtils.d(TAG, "handleBlockedUser Ended");
  }

  private void handleBuzz(Response response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      if (response instanceof LikeBuzzResponse) {
        adapterCommon.updateLikeBuzz(itemToLike);
      } else if (response instanceof AddCommentResponse) {
        UserPreferences.getInstance().saveNumberPoint(
            ((AddCommentResponse) response).getPoint());
        restartLastestBuzz();
      } else if (response instanceof DeleteBuzzResponse) {
        adapterCommon.deleteBuzz(itemToDelete);
        getLoaderManager().destroyLoader(LOADER_DELETE_BUZZ);
      } else if (response instanceof DeleteCommentResponse) {
        adapterCommon.deleteComment(itemToDeleteComment, itemComment);
        getLoaderManager().destroyLoader(LOADER_DELETE_COMMENT);
      }
    } else if (response.getCode() == Response.SERVER_BUZZ_NOT_FOUND
        || response.getCode() == Response.SERVER_ACCESS_DENIED) {
      // Show Toast Message for User
      Utility.showToastMessage(mAppContext,
          mAppContext.getString(R.string.buzz_item_not_found));
      if (response instanceof LikeBuzzResponse) {
        adapterCommon.deleteBuzz(itemToLike);
      } else if (response instanceof AddCommentResponse) {
        restartLastestBuzz();
      } else if (response instanceof DeleteBuzzResponse) {
        adapterCommon.deleteBuzz(itemToDelete);
        getLoaderManager().destroyLoader(LOADER_DELETE_BUZZ);
      } else if (response instanceof DeleteCommentResponse) {
        adapterCommon.deleteBuzz(itemToDeleteComment);
        getLoaderManager().destroyLoader(LOADER_DELETE_COMMENT);
      }
    } else if (response.getCode() == Response.SERVER_COMMENT_NOT_FOUND) {
      Utility.showToastMessage(mAppContext,
          mAppContext.getString(R.string.comment_item_not_found));
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      if (response instanceof AddCommentResponse) {
        int point = ((AddCommentResponse) response).getCommentPoint();
        NotEnoughPointDialog.showForCommentBuzz(getActivity(), point);

        // CUONGNV01032016 : Remove show dialog het point
//				Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//				intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_COMMENT);
//				startActivity(intent);
      }
    } else {
      com.application.ui.customeview.ErrorApiDialog.showAlert(
          getActivity(), R.string.common_error, response.getCode());
    }
  }

  private void handleBlockUserResponse(AddBlockUserResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      BlockUserPreferences blockUserPreferences = BlockUserPreferences
          .getInstance();
      mBlockedUser = !mBlockedUser;
      String from = userPreferences.getUserId();
      String to = userId;
      blockUserPreferences.insertBlockedUser(userId);

      // update connection started
      int numOfFriend = response.getFriendsNum();
      int numOfFavorite = response.getFavouriteFriendsNum();
      userPreferences.saveNumberConnection(numOfFriend, numOfFavorite);

      // update connection ended
      sendBlockMessage(from, to);

      // Send LocalBroadcast
      Intent intent = new Intent(AccountStatus.ACTION_BLOCKED);
      intent.putExtra(AccountStatus.EXTRA_DATA, userId);
      Utility.sendLocalBroadcast(getActivity(), intent);

      // Navigate to Meet People screen
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
//          mNavigationManager.goBack();
          if (mAppContext != null) {
            ((MainActivity) baseFragmentActivity).replaceAllFragment(
                new HomeFragment(),
                MainActivity.TAG_FRAGMENT_MEETPEOPLE);
          }
        }
      };
      Handler handler = new Handler();
      handler.post(runnable);
    }
  }

  private void handleUnblockUserResponse(RemoveBlockUserResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      mBlockedUser = !mBlockedUser;
      UserPreferences userPreferences = UserPreferences.getInstance();
      BlockUserPreferences blockUserPreferences = BlockUserPreferences
          .getInstance();
      String from = userPreferences.getUserId();
      String to = userId;
      blockUserPreferences.removeBlockedUser(to);
      int numOfFriend = response.getFriendsNum();
      int numOfFavorite = response.getFavouriteFriendsNum();
      userPreferences.saveNumberConnection(numOfFriend, numOfFavorite);
      sendUnblockMessage(from, to);
    }

  }

  private void handleReportUserResponse(Response response) {
    LogUtils.d(TAG, "handleReportUserResponse Started");

    if (response.getCode() == Response.SERVER_SUCCESS) {
      // Show confirm dialog
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      Resources resource = getResources();
      Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
      String title = "";
      String message = "";
      title = resource
          .getString(R.string.dialog_confirm_report_user_title);
      message = resource
          .getString(R.string.dialog_confirm_report_user_content);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
      builder.setCustomTitle(customTitle);

      //builder.setTitle(title);
      builder.setMessage(message);
      DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
      };
      builder.setPositiveButton(R.string.common_ok, clickListener);
      builder.create();
      mAlertDialog = builder.show();
      int dividerId = mAlertDialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = mAlertDialog.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    }

    LogUtils.d(TAG, "handleReportUserResponse Ended");
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response respone = null;
    if (loaderID == LOADER_USER_INFO) {
      LogUtils.e("LOADER_USER_INFO", "LOADER_USER_INFO");
      respone = new UserInfoResponse(data);
    } else if (loaderID == LOADER_LASTEST_BUZZ) {
      respone = new BuzzListResponse(data);
    } else if (loaderID == LOADER_LASTEST_GIFT) {
    } else if (loaderID == LOADER_ADD_FAVORITE) {
      LogUtils.e("LOADER_ADD_FAVORITE", "LOADER_ADD_FAVORITE");
      respone = new AddFavoriteResponse(data);
    } else if (loaderID == LOADER_REMOVE_FAVORITE) {
      respone = new RemoveFavoriteResponse(data);
    } else if (loaderID == LOADER_REMOVE_FRIEND) {
      respone = new RemoveFriendResponse(data);
    } else if (loaderID == LOADER_DELETE_BUZZ) {
      respone = new DeleteBuzzResponse(data);
    } else if (loaderID == LOADER_DELETE_COMMENT) {
      respone = new DeleteCommentResponse(data);
    } else if (loaderID == LOADER_LIKE_BUZZ) {
      respone = new LikeBuzzResponse(data);
    } else if (loaderID == LOADER_ADD_COMMENT) {
      respone = new AddCommentResponse(data);
    } else if (loaderID == LOADER_PROFILE_IMAGE) {
      respone = new ListPublicImageResponse(data);
    } else if (loaderID == LOADER_ID_ADD_BLOCK_USER) {
      respone = new AddBlockUserResponse(data);
    } else if (loaderID == LOADER_ID_REMOVE_BLOCK_USER) {
      respone = new RemoveBlockUserResponse(data);
    } else if (loaderID == LOADER_ID_REPORT_USER) {
      respone = new ReportResponse(data);
    } else if (loaderID == LOADER_ID_ADD_TO_FAVORITES) {
      respone = new AddFavoriteResponse(data);
    } else if (loaderID == LOADER_ID_REMOVE_FROM_FAVORITES) {
      respone = new RemoveFavoriteResponse(data);
    } else if (loaderID == LOADER_ID_LOAD_BUZZ_DETAIL) {
      respone = new BuzzDetailResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_CALL_VIDEO
        || loaderID == LOADER_ID_CHECK_CALL_VOICE) {
      respone = new CheckCallResponse(data);
    } else if (loaderID == LOADER_ID_BASIC_USER_INFO) {
      respone = new GetBasicInfoResponse(mAppContext, data);
    }
    return respone;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case Activity.RESULT_OK:
        onActivityResultOk(requestCode, data);
        break;
      default:
        break;
    }
  }

  public void onActivityResultOk(int requestCode, Intent data) {
    // if have activity result -> not need refresh beacause refresh below.

    mNeedRefresh = false;
    switch (requestCode) {
      case Camera.REQUEST_CODE_CAMERA:
      case Gallery.REQUEST_CODE_GALLERY:
        Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
        for (Parcelable parcelable : files) {
          MediaFile file = (MediaFile) parcelable;
          uploadAvatarToServer(file.getPath());
        }
        break;
      case REQUEST_CODE_GET_AVATAR:
        if (data != null) {
          if (data.hasExtra(Constants.INTENT_BUZZ_ID)) {
            mBuzzDetailId = data.getStringExtra(Constants.INTENT_BUZZ_ID);

            // Go to buzzDetail
            if (!TextUtils.isEmpty(mBuzzDetailId)) {
              BuzzDetail buzzDetailFragment = BuzzDetail
                  .newInstance(mBuzzDetailId, Constants.BUZZ_TYPE_IMAGE);
              mBuzzDetailId = "";
              mNavigationManager.addPage(buzzDetailFragment);
            }
          } else if (data.hasExtra(DetailPictureBaseActivity.KEY_UPDATE_AVATAR)) {
            ((MainActivity) getActivity()).onMainMenuUpdate(MainActivity.UPDATE_AVATAR);
            refreshData();
          }
        } else {
          LogUtils.d("LOAD",
              "REQUEST_CODE_GET_IMAGE_THEATER -- data null -refreshData");
          refreshData();
        }
        break;
      case REQUEST_CODE_CAMERA_IMAGE_CAPTURE:
//                ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//                if (mMediaSelectedList != null) {
//                    for (final MediaItem mediaItem : mMediaSelectedList) {
//                        String croppedPath = mediaItem.getCroppedPath();
//                        replaceFragment(ShareMyBuzzFragment.newInstance(croppedPath, true), "share_picture");
//                    }
//                } else {
//                    LogUtils.e(TAG, "Error to get media, NULL");
//                }
        break;
      case REQUEST_CODE_BACKSTAGE:
        LogUtils.d("LOAD", "REQUEST_CODE_BACKSTAGE  -refreshData");
        refreshData();
        break;
      case REQUEST_CODE_MANAGER_ONLINE_ALERT:
        LogUtils.d("LOAD",
            "REQUEST_CODE_MANAGER_ONLINE_ALERT  -refreshData");
        refreshData();
      default:
        LogUtils.d("LOAD", "default  -refreshData");
        refreshData();
        break;
    }
  }

  private void uploadAvatarToServer(String imagePath) {
    ImageUploader imageUploader = new ImageUploader(uploadImageProgress);
    if (null != imagePath) {
      File file = new File(imagePath);
      String token = UserPreferences.getInstance().getToken();
      String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
      UploadImageRequest imageRequest = new UploadImageRequest(token, UploadImageRequest.AVATAR,
          file, md5Encrypted);
      imageUploader.execute(imageRequest);
    }
  }

  public void loadListThumbImage() {
    String token = UserPreferences.getInstance().getToken();
    ListPublicImageRequest listPublicImageRequest;
    listPublicImageRequest = new ListPublicImageRequest(token, userId, 0,
        DetailPictureBaseActivity.LIST_DEFAULT_SIZE);
    restartRequestServer(LOADER_PROFILE_IMAGE, listPublicImageRequest);
  }

  private void sendBlockMessage(final String from, final String to) {
    ChatManager chatManager = getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendBlockMessage(from, to);
  }

  private void sendUnblockMessage(final String from, final String to) {
    ChatManager chatManager = getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendUnblockMessage(from, to);
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected String getUserIdTracking() {
    if (mUserInfoResponse != null) {
      return mUserInfoResponse.getUserId();
    }
    return "";
  }

  public String getUserId() {
    return userId;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (resumeBuzzID == null) {
      if (mNeedRefresh) {
        // refreshData();
        mNeedRefresh = false;
      }
    }
  }

  public void setNeedRefreshData(boolean needRefresh) {
    mNeedRefresh = needRefresh;
  }

  private ChatManager getChatManager() {
    if (mMainActivity.getChatService() == null) {
      return null;
    }
    return mMainActivity.getChatService().getChatManager();

  }

  @Override
  public void onFavourite() {
    favorite();
  }

  @Override
  public void chat() {
    if (mUserInfoResponse != null) {
      ChatFragment chatFragment = ChatFragment.newInstance(
          mUserInfoResponse.getUserId(),
          mUserInfoResponse.getAvataId(),
          mUserInfoResponse.getUserName(),
          mUserInfoResponse.getGender(),
          mUserInfoResponse.isVoiceCallWaiting(),
          mUserInfoResponse.isVideoCallWaiting(), true);
      replaceFragment(chatFragment, MainActivity.TAG_FRAGMENT_CHAT);
    }
  }

  private void checkCall(boolean isVideoCall) {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    int type = isVideoCall ? Constants.CALL_TYPE_VIDEO
        : Constants.CALL_TYPE_VOICE;
    CheckCallRequest request = new CheckCallRequest(token,
        callUserInfo.getUserId(), type);
    if (isVideoCall) {
      restartRequestServer(LOADER_ID_CHECK_CALL_VIDEO, request);
    } else {
      restartRequestServer(LOADER_ID_CHECK_CALL_VOICE, request);
    }
  }

  private boolean handlerCheckRequestCall(GetBasicInfoResponse response) {
    String currentUserId = UserPreferences.getInstance().getUserId();
    if (!response.isOnline()) {
      if (mCurrentCallType == Constants.CALL_TYPE_VOICE) {
        if (!response.isVoiceWaiting()) {
          if (!response.isVideoWaiting()) {
            Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
                Utility.REQUEST_VOICE_VIDEO_OFF, currentUserId, response.getUserName(),
                response.getUserId(), null);
            return false;
          }
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VOICE_CALL_OFF, currentUserId, response.getUserName(),
              response.getUserId(), null);
          return false;
        } else {
          checkCall(false);
          return true;
        }
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VIDEO) {
        if (!response.isVideoWaiting()) {
          if (!response.isVoiceWaiting()) {
            Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
                Utility.REQUEST_VOICE_VIDEO_OFF, currentUserId, response.getUserName(),
                response.getUserId(), null);
            return false;
          }
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VIDEO_CALL_OFF, currentUserId, response.getUserName(),
              response.getUserId(), null);
          return false;
        } else {
          checkCall(true);
          return true;
        }
      }
      return false;
    } else {
      if (!response.isVideoWaiting() && !response.isVoiceWaiting()) {
        Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
            Utility.REQUEST_VOICE_VIDEO_OFF, currentUserId, response.getUserName(),
            response.getUserId(), null);
        return false;
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VOICE) {
        if (response.isVoiceWaiting()) {
          checkCall(false);
          return true;
        } else {
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VOICE_CALL_OFF, currentUserId, response.getUserName(),
              response.getUserId(), null);
          return false;
        }
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VIDEO) {
        if (response.isVideoWaiting()) {
          checkCall(true);
          return true;
        } else {
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VIDEO_CALL_OFF, currentUserId, response.getUserName(),
              response.getUserId(), null);
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public void phone(CallUserInfo userInfo) {
    callUserInfo = userInfo;
    mCurrentCallType = Constants.CALL_TYPE_VOICE;
    Utility.showDialogAskingVoiceCall(getActivity(), userInfo,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            restartRequestBasicUserInfo();
          }
        });
  }

  private void restartRequestBasicUserInfo() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, callUserInfo.getUserId());
    restartRequestServer(LOADER_ID_BASIC_USER_INFO, request);
  }

  @Override
  public void video(CallUserInfo userInfo) {
    callUserInfo = userInfo;
    mCurrentCallType = Constants.CALL_TYPE_VIDEO;
    Utility.showDialogAskingVideoCall(getActivity(), userInfo,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            restartRequestBasicUserInfo();
          }
        });
  }

  @Override
  public void deleteBuzzAt(int position) {
    itemToDelete = adapterCommon.getBuzzListItem(position);
    showDialogConfirmDeleteBuzz(itemToDelete);
  }

  @Override
  public void chatWithUserAt(int position) {
    BuzzListItem item = adapterCommon.getBuzzListItem(position);
    ChatFragment chatFragment = ChatFragment.newInstance(item.getUserId(),
        item.getAvatarId(), item.getUserName(), item.getGender(),
        mUserInfoResponse.isVoiceCallWaiting(),
        mUserInfoResponse.isVideoCallWaiting(), true);
    replaceFragment(chatFragment, MainActivity.TAG_FRAGMENT_CHAT);
  }

  @Override
  public void viewDetailPictureAt(int position) {
    BuzzListItem item = adapterCommon.getBuzzListItem(position);
    if (isButtonEnable) {
      isButtonEnable = false;
      adapterCommon.setButtonEnable(isButtonEnable);
      Intent intent = new Intent(getActivity(),
          DetailPictureBuzzActivity.class);
      Bundle userData = null;

      userData = ProfilePictureData.parseDataToBundle(item);
      intent.putExtras(userData);
      resumeBuzzID = item.getBuzzId();
      startActivity(intent);
      mNeedRefresh = true;

    }
  }

  @Override
  public void viewBuzzDetailAt(int position) {
    BuzzListItem item = adapterCommon.getBuzzListItem(position);
    BuzzDetail buzzDetail = BuzzDetail.newInstance(item.getBuzzId(),
        Constants.BUZZ_TYPE_NONE);
    resumeBuzzID = item.getBuzzId();
    replaceFragment(buzzDetail, MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  @Override
  public void likeBuzzAt(int position) {
    itemToLike = adapterCommon.getBuzzListItem(position);
    int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
    if (itemToLike.getIsLike() == Constants.BUZZ_LIKE_TYPE_LIKE) {
      newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
    }
    String token = UserPreferences.getInstance().getToken();
    LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(token,
        itemToLike.getBuzzId(), newLikeType);
    restartRequestServer(LOADER_LIKE_BUZZ, likeBuzzRequest);
  }

  @Override
  public void openLikeAndPostCommentAt(int position) {
    final BuzzListItem item = adapterCommon.getBuzzListItem(position);
    if (rlParentComment.getVisibility() == View.GONE
        || rlParentComment.getVisibility() == View.INVISIBLE) {

      rlParentComment.setVisibility(View.VISIBLE);
      if (item.getIsLike() == Constants.BUZZ_LIKE_TYPE_LIKE) {
        imgCommentLike.setImageResource(R.drawable.btn_like);
        imgCommentLike
            .setBackgroundResource(R.drawable.bg_btn_like_buzz_active);
      } else {
        imgCommentLike.setImageResource(R.drawable.btn_unlike);
        imgCommentLike
            .setBackgroundResource(R.drawable.bg_btn_like_buzz_none_active);
      }

      imgCommentLike.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          if (item.getIsLike() == Constants.BUZZ_LIKE_TYPE_UNLIKE) {
            imgCommentLike.setImageResource(R.drawable.btn_like);
            imgCommentLike
                .setBackgroundResource(R.drawable.bg_btn_like_buzz_active);
          } else {
            imgCommentLike.setImageResource(R.drawable.btn_unlike);
            imgCommentLike
                .setBackgroundResource(R.drawable.bg_btn_like_buzz_none_active);
          }
          // like buzz
          itemToLike = item;
          int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
          if (item.getIsLike() == Constants.BUZZ_LIKE_TYPE_LIKE) {
            newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
          }
          String token = UserPreferences.getInstance().getToken();
          LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(
              token, item.getBuzzId(), newLikeType);
          adapterCommon.updateLikeBuzz(itemToLike);
          restartRequestServer(LOADER_LIKE_BUZZ, likeBuzzRequest);
        }
      });

      edtComment.requestFocus();
      edtComment.setOnEditorActionListener(new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if ((actionId == EditorInfo.IME_ACTION_SEND)
              || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (!Utility.isContainDirtyWord(getActivity(),
                edtComment)) {
              String commentValue = v.getText().toString()
                  .replace("\u3000", " ").trim();
              if (commentValue.length() != 0) {

                String token = UserPreferences.getInstance()
                    .getToken();
                // add comment
                AddCommentRequest addCommentRequest = new AddCommentRequest(
                    token, item.getBuzzId(), commentValue);
                restartRequestServer(LOADER_ADD_COMMENT,
                    addCommentRequest);

                // hide comment view
                InputMethodManager in = (InputMethodManager) getActivity()
                    .getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(
                    v.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
                v.setText("");
                rlParentComment.setVisibility(View.GONE);
              }
            }
          }
          return true;
        }
      });
      InputMethodManager imm = (InputMethodManager) getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);

    }
  }

  @Override
  public void showUserInfoAt(int position) {

  }

  @Override
  public void reportBuzz() {

  }

  ;

  @Override
  public void handleFavorite(int position) {
    BuzzListItem current = adapterCommon.getBuzzListItem(position);
    String userId = current.getUserId();
    mFavoriteHandler.handleFavoriteAtPosition(position, userId);
  }

  @Override
  public void onReplyComment(String commentId, int commentPosition, int buzzPosition) {
    navigateToBuzzDetail(true, commentId, buzzPosition, commentPosition);
  }

  @Override
  public void onShowMoreComment(int commentPosition, String commentId, int skip, int buzzPosition) {
    navigateToBuzzDetail(false, commentId, buzzPosition, commentPosition);
  }

  private void navigateToBuzzDetail(boolean isReply, String commentId, int buzzPosition,
      int commentPosition) {
    BuzzListItem item = adapterCommon.getBuzzListItem(buzzPosition);
    if (item.getIsApproved() == Constants.IS_NOT_APPROVED) {
      return;
    }
    BuzzDetail buzzDetail;
    if (isReply) {
      buzzDetail = BuzzDetail.newInstance(item.getBuzzId(), Constants.BUZZ_TYPE_NONE,
          BuzzDetail.INPUT_TYPE_SUB_COMMENT, commentId, commentPosition);
    } else {
      buzzDetail = BuzzDetail.newInstance(item.getBuzzId(),
          Constants.BUZZ_TYPE_NONE);
    }
    resumeBuzzID = item.getBuzzId();
    replaceFragment(buzzDetail, MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  @Override
  public void onDeleteComment(int buzzPosition, int commentPosition) {
    BuzzListItem item = adapterCommon.getBuzzListItem(buzzPosition);
    itemComment = item.getCommentList().get(commentPosition);
    itemToDeleteComment = item;

    // show dialog to delete comment
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.timeline_delete_comment),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String toekn = UserPreferences.getInstance().getToken();
            DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(
                toekn, itemToDeleteComment.getBuzzId(),
                itemComment.cmt_id);
            restartRequestServer(LOADER_DELETE_COMMENT,
                deleteCommentRequest);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showGiveGiftFragment(BuzzListItem addingItem) {
    GiftCategories categories = new GiftCategories("get_all_gift", 0,
        getResources().getString(R.string.give_gift_all_title), 1);
    ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
        addingItem.getUserId(), addingItem.getUserName(), categories,
        true);
    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  @Override
  public void onGiveGif() {
    showGiveGiftFragment();
  }

  @Override
  public void onSetOnlineAlert() {
    if (userId != null && userId.length() > 0) {
      ManageOnlineAlertFragment fragment = ManageOnlineAlertFragment
          .newInstance(userId, mAvatarId, mUserName, isAlert);
      fragment.setTargetFragment(getRootParentFragment(this), REQUEST_CODE_MANAGER_ONLINE_ALERT);
      replaceFragment(fragment);
    }
  }

  @Override
  public void onReport() {
    requestReportUser();
  }

  @Override
  public void onBlock() {
    requestBlockUser();
  }

  public String getIsFromEditProfile() {
    return isFromEditProfile;
  }

  public void setIsFromEditProfile(String isFromEditProfile) {
    this.isFromEditProfile = isFromEditProfile;
  }

  public void goBack() {
    if (TextUtils.isEmpty(getIsFromEditProfile())) {
      mNavigationManager.goBack();
    } else if (getIsFromEditProfile().equals(FROM_EDIT_PROFILE_MY_PAGE)) {
      MyPageFragment fragment = new MyPageFragment();
      mNavigationManager.switchPage(fragment);

    } else {
      SettingsFragment fragment = new SettingsFragment();
      mNavigationManager.switchPage(fragment);

    }
  }


  private void showDialogConfirmDeleteBuzz(final BuzzListItem current) {
    confirmDialog = new CustomConfirmDialog(getActivity(), "",
        getString(R.string.timeline_delete_buzz), true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String token = UserPreferences.getInstance().getToken();
            DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(
                token, current.getBuzzId());
            restartRequestServer(LOADER_DELETE_BUZZ, deleteBuzzRequest);
          }
        })
        .setNegativeButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            confirmDialog.dismiss();
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

  public void disablePullDownToRefresh() {
    pullToRefreshView.setMode(Mode.PULL_FROM_START);
  }

  public void enablePullDownToRefresh() {
    pullToRefreshView.setMode(Mode.BOTH);
  }

  @SuppressLint("InflateParams")
  public void addBuzzEmpty() {
    if (mEmptyBuzz == null) {
      mEmptyBuzz = LayoutInflater.from(mAppContext).inflate(
          R.layout.item_profile_buzz, null);
      gridView.addFooterView(mEmptyBuzz);
    }
  }

  public void removeBuzzEmpty() {
    if (mEmptyBuzz != null) {
      gridView.removeFooterView(mEmptyBuzz);
      mEmptyBuzz = null;
    }
  }

  public void setResumeBuzzID(String resumeBuzzID) {
    this.resumeBuzzID = resumeBuzzID;
  }

  private void checkStateProfile() {
    LogUtils.d("SCROLL", "checkStateProfile : isMoreAvailable : "
        + isMoreAvailable);
    if (mListUserIds != null && userId != null) {
      int currentLocation = mListUserIds.indexOf(userId);

      if (currentLocation == -1) {
        isFirstProfile = true;
        isLastProfile = true;
      } else if (currentLocation == 0 && mListUserIds.size() == 1) {
        isFirstProfile = true;
        isLastProfile = true;
      } else if (currentLocation == 0 && mListUserIds.size() > 1) {
        isFirstProfile = true;
        isLastProfile = false;
      } else if (currentLocation == mListUserIds.size() - 1
          && isMoreAvailable()) {
        isFirstProfile = false;
        isLastProfile = false;
      } else if (currentLocation == mListUserIds.size() - 1
          && !isMoreAvailable()) {
        isFirstProfile = false;
        isLastProfile = true;
      } else {
        isFirstProfile = false;
        isLastProfile = false;
      }
    } else {
      isFirstProfile = true;
      isLastProfile = true;
    }
    LogUtils.d("STATE", "MyProfileFragment - checkStateProfile - initState");
    initSlideState();
  }

  public int changeSlideState() {

    if (mPixelPos > (mHeightScreen / 2)) {
      return SliderProfileFragment.STATE_HIDE_NAVIGATION;
    }
    if (!isFirstProfile && !isLastProfile && listIsAtTop()) {
      return SliderProfileFragment.STATE_SHOW_NAVIGATION;
    } else if (isFirstProfile && !isLastProfile && listIsAtTop()) {
      return SliderProfileFragment.STATE_SHOW_ONLY_RIGHT;
    } else if (isLastProfile && !isFirstProfile && listIsAtTop()) {
      return SliderProfileFragment.STATE_SHOW_ONLY_LEFT;
    } else {
      return SliderProfileFragment.STATE_NOT_CHANGE;
    }
  }

  private boolean isMoreAvailable() {
    return isMoreAvailable;
  }

  public void setMoreAvailable(boolean isMoreAvailable) {
    this.isMoreAvailable = isMoreAvailable;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (getUserVisibleHint() && !isUserInfoReady() && getView() == null) {
      refreshData();
      setTitleName();
    } else if (getUserVisibleHint() && !isUserInfoReady()
        && getView() != null) {
      refreshData();
      setTitleName();
    } else if (getUserVisibleHint()) {
      setTitleName();
    }
  }


  private boolean isUserInfoReady() {
    return mUserInfoResponse != null;
  }

  @Override
  public void onDeleteSubComment(String commentId, String subCommentId, int commentPosition,
      int subCommentPosition) {
  }

  public void clickMoreOptions() {
    if (mUserInfoResponse == null) {
      return;
    }
    if (mPopupChatMoreOptions != null && mPopupChatMoreOptions.isShowing()) {
      hideChatMoreOptions();
    } else {
      showChatMoreOptions();
    }
  }

  private void showChatMoreOptions() {
    getSlidingMenu().setSlidingEnabled(false);
    adapterCommon.setButtonEnable(false);
    mChatMoreLayout = new ChatMoreLayout(mAppContext, onChatMoreListener,
        userId, isVoiceCallWaiting, isVideoCallWaiting, false, Alert);
    mPopupChatMoreOptions = new PopupWindow(mChatMoreLayout,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);
    mPopupChatMoreOptions.showAsDropDown(getNavigationBar());

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        mViewFreezedLayer.setVisibility(View.VISIBLE);
      }
    };

    Handler handler = new Handler();
    handler.post(runnable);

  }

  public void hideChatMoreOptions() {
    if (mPopupChatMoreOptions != null && mPopupChatMoreOptions.isShowing()) {
      mPopupChatMoreOptions1 = mPopupChatMoreOptions;
      mPopupChatMoreOptions1.dismiss();
      mViewFreezedLayer.setVisibility(View.GONE);
    }
    getSlidingMenu().setSlidingEnabled(false);
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (v.getId() == R.id.ib_chat_freezed_layer) {
      hideChatMoreOptions();
    }
    return true;
  }

  private void executeBlockUser() {
    LogUtils.d(TAG, "executeBlockUser Started");
    if (mUserInfoResponse == null) {
      return;
    }
    UserPreferences userPreferences = UserPreferences.getInstance();
    if (userPreferences.getInRecordingProcess()) {
      LogUtils.d(TAG, "executeBlockUser Ended (1)");
      return;
    }

    String title = "";
    String message = "";

    title = getString(R.string.chat_screen_block_dialog_title);
    if (isChatWithHiddenUser()) {
      message = String.format(
          getString(R.string.chat_screen_block_dialog_message),
          getString(R.string.chat_screen_hidden_user_name));
    } else {
      message = String.format(
          getString(R.string.chat_screen_block_dialog_message),
          mUserInfoResponse.getUserName());
    }

    mConfirmDialog = new CustomConfirmDialog(getActivity(), title, message, true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            UserPreferences userPreferences = UserPreferences.getInstance();
            String token = userPreferences.getToken();
            AddBlockUserRequest abur = new AddBlockUserRequest(token,
                userId);
            restartRequestServer(LOADER_ID_ADD_BLOCK_USER, abur);
          }
        })
        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    mConfirmDialog.show();
    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "executeBlockUser Ended (2)");
  }

  private void executeReportUser() {
    LogUtils.d(TAG, "executeReportUser Started");
    if (UserPreferences.getInstance().getInRecordingProcess()) {
      LogUtils.d(TAG, "executeReportUser Ended (1)");
      return;
    }
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    Resources resource = getResources();
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    String title = "";
    String[] items = null;

    title = resource.getString(R.string.dialog_confirm_report_user_title);
    items = resource.getStringArray(R.array.report_user_type);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.select_dialog_item, items);

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        LogUtils.d(TAG, "onClick Started");

        LogUtils.d(TAG,
            String.format("executeReportUser: which = %d", which));

        if (which > 0) {
          int reportType = 0;
          Resources resource = getResources();
          String[] reportTypes = resource
              .getStringArray(R.array.report_type);
          String[] reportUsers = resource
              .getStringArray(R.array.report_user_type);
          String reportString = reportUsers[which];
          int length = reportTypes.length;
          for (int i = 0; i < length; i++) {
            if (reportString.equals(reportTypes[i])) {
              reportType = i;
            }
          }
          String token = UserPreferences.getInstance().getToken();
          String subject_id = UserPreferences.getInstance()
              .getCurentFriendChat();
          ReportRequest reportRequest = new ReportRequest(token,
              subject_id, reportType, Constants.REPORT_TYPE_USER);
          restartRequestServer(LOADER_ID_REPORT_USER, reportRequest);
        }

        LogUtils.d(TAG, "onClick Ended");
      }
    });
    mAlertDialog = builder.create();
    mAlertDialog.show();
    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  /**
   * Check chat with hidden user
   */
  public boolean isChatWithHiddenUser() {
    Activity activity = getActivity();
    if (activity != null && activity instanceof MainActivity) {
      if (mUserInfoResponse != null
          && !TextUtils.isEmpty(mUserInfoResponse.getUserId())
          && mUserInfoResponse.getUserId().equals(
          ((MainActivity) activity).mHiddenUserId)) {
        return true;
      }
    }
    return false;
  }

  public void disablePullUpToRefresh() {
    pullToRefreshView.setMode(Mode.PULL_FROM_START);
  }

  public void enablePullUpToRefresh() {
    pullToRefreshView.setMode(Mode.BOTH);
  }

  public interface OnCheckStateNavigation {

    public void onCheckState(int state);
  }

}
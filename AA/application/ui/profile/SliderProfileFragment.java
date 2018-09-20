package com.application.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ListUserRequest;
import com.application.connection.response.MeetPeopleResponse;
import com.application.connection.response.MeetPeopleSettingResponse;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.entity.MeetPeople;
import com.application.model.ChatUser;
import com.application.ui.ChatFragment;
import com.application.ui.MainActivity;
import com.application.ui.TrackingBlockFragment;
import com.application.ui.profile.MyProfileFragment.OnCheckStateNavigation;
import com.application.util.LogUtils;
import com.application.util.preferece.LocationPreferences;
import com.application.util.preferece.UserPreferences;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import glas.bbsystem.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SliderProfileFragment extends TrackingBlockFragment implements
    ResponseReceiver, OnCloseListener, OnClickListener {

  public static final String ACTION_STOP_RECORD = "stop_record";
  public static final int STATE_HIDE_NAVIGATION = 0;
  public static final int STATE_SHOW_NAVIGATION = 1;
  public static final int STATE_SHOW_ONLY_LEFT = 2;
  public static final int STATE_SHOW_ONLY_RIGHT = 3;
  public static final int STATE_NOT_CHANGE = 4;
  /*namit*/
  // Intent key
  private static final String KEY_PARTNER_ID = "partner_id";
  private static final String KEY_PARTNER_NAME = "partner_name";
  private static final String KEY_PARTNER_AVATA = "partner_avata";
  private static final String KEY_PARTNER_GENDER = "partner_gender";
  private static final String KEY_RECEIVED_MESSAGE_NUM = "received_message_num";
  private static final String KEY_DOWNLOAD_ID = "download_id";
  private static final String KEY_IS_WAITING_DOWNLOAD = "is_download_waiting";
  private static final String KEY_IS_NAVIGATION_BACK = "is_navi_back";
  private static final String KEY_SEND_GIFT_FROM_PROFILE = "is_send_gift_from_profile";
  //===================================================================
  private static final String KEY_TAKE = "take";
  private static final String KEY_IS_VOICE_CALL_WAITING = "is_video_call_waiting";
  private static final String KEY_IS_VIDEO_CALL_WAITING = "is_voice_call_waiting";
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_LIST_MEET_PEOPLE = "list_meet_people";
  private static final String KEY_MEET_PEOPLE_SETTING = "meet_people_setting";
  private static final String KEY_IS_MORE_AVAILABLE = "is_more_available";
  private static final int LOADER_LIST_USER_PROFILE = 0;
  private static final int NO_NEED_LOAD_MORE_NUMBER = 15;
  public static HashMap<String, Integer> mStateList;
  FrameLayout flslider;
  private ChatUser mFriend = new ChatUser();
  private String mUserId;
  private List<MeetPeople> mListPeoples;
  private MeetPeopleSettingResponse mSettingMeetPeople;
  private View mPrevious, mNext;
  private ViewPager mViewPager;
  private ProfilePagerAdapter mAdapter;
  private ArrayList<String> mListUserIds;
  private boolean isMoreAvailable = true;
  private boolean isStillLoadingListUser = false;
  private int mTake;
  private int mState;
  /* namit */
  private MainActivity mMainActivity;
  private Context mContext;
  private CallUserInfo callUserInfo;
  private int mCurrentCallType = Constants.CALL_TYPE_VOICE;
  private android.app.AlertDialog mAlertDialog;
  private boolean mNewAddFavoriteRequest = false;
  /*-------end-------*/
  private boolean isNavigationBack = true;

  public static HashMap<String, Integer> getStateList() {
    return mStateList;
  }

  public static SliderProfileFragment newInstance(String userId,
      ArrayList<MeetPeople> peopleList,
      MeetPeopleSettingResponse settingMeetPeople, boolean isMoreAvailable) {
    SliderProfileFragment fragment = new SliderProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putSerializable(KEY_LIST_MEET_PEOPLE, peopleList);
    bundle.putSerializable(KEY_MEET_PEOPLE_SETTING, settingMeetPeople);
    bundle.putBoolean(KEY_IS_MORE_AVAILABLE, isMoreAvailable);
    fragment.setArguments(bundle);
    return fragment;
  }

  /*namit*/
  public static SliderProfileFragment newInstance(String friendId, String friendAva,
      String friendName, int friendGender, boolean isVoiceCallWaiting,
      boolean isVideoCallWaiting, boolean isNavigationBack,
      ArrayList<MeetPeople> peopleList,
      MeetPeopleSettingResponse settingMeetPeople, boolean isMoreAvailable
  ) {

    SliderProfileFragment fragment = new SliderProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_PARTNER_ID, friendId);
    bundle.putString(KEY_PARTNER_AVATA, friendAva);
    bundle.putString(KEY_PARTNER_NAME, friendName);
    bundle.putInt(KEY_PARTNER_GENDER, friendGender);
    bundle.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    bundle.putBoolean(KEY_IS_VOICE_CALL_WAITING, isVoiceCallWaiting);
    bundle.putBoolean(KEY_IS_VIDEO_CALL_WAITING, isVideoCallWaiting);
    //==============
    bundle.putString(KEY_USER_ID, friendId);
    bundle.putSerializable(KEY_LIST_MEET_PEOPLE, peopleList);
    bundle.putSerializable(KEY_MEET_PEOPLE_SETTING, settingMeetPeople);
    bundle.putBoolean(KEY_IS_MORE_AVAILABLE, isMoreAvailable);
    fragment.setArguments(bundle);
    return fragment;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (bundle != null) {
      //=============================
      // Get friend information
      mFriend.setId(bundle.getString(KEY_PARTNER_ID));
      mFriend.setAvatar(bundle.getString(KEY_PARTNER_AVATA));
      mFriend.setName(bundle.getString(KEY_PARTNER_NAME));
      mFriend.setGender(bundle.getInt(KEY_PARTNER_GENDER));

//            // Get call information
//            isVoiceCallWaiting = bundle.getBoolean(KEY_IS_VOICE_CALL_WAITING);
//            isVideoCallWaiting = bundle.getBoolean(KEY_IS_VIDEO_CALL_WAITING);

      // Get navigation data
      isNavigationBack = bundle.getBoolean(KEY_IS_NAVIGATION_BACK);

      //-----------------------
      mUserId = bundle.getString(KEY_USER_ID);
      mListPeoples = (ArrayList<MeetPeople>) bundle
          .getSerializable(KEY_LIST_MEET_PEOPLE);
      mSettingMeetPeople = (MeetPeopleSettingResponse) bundle
          .getSerializable(KEY_MEET_PEOPLE_SETTING);
      isMoreAvailable = bundle
          .getBoolean(KEY_IS_MORE_AVAILABLE);
    } else {
      Bundle arguments = getArguments();
      if (arguments != null) {
        // Get friend information
        mFriend.setId(arguments.getString(KEY_PARTNER_ID));
        mFriend.setName(arguments.getString(KEY_PARTNER_NAME));
        mFriend.setAvatar(arguments.getString(KEY_PARTNER_AVATA));
        mFriend.setGender(arguments.getInt(KEY_PARTNER_GENDER));

//                // Get call information
//                isVoiceCallWaiting = arguments
//                        .getBoolean(KEY_IS_VOICE_CALL_WAITING);
//                isVideoCallWaiting = arguments
//                        .getBoolean(KEY_IS_VIDEO_CALL_WAITING);

        // Get navigation data
        isNavigationBack = arguments.getBoolean(KEY_IS_NAVIGATION_BACK);
      }
      mUserId = getArguments().getString(KEY_USER_ID);
      mListPeoples = (ArrayList<MeetPeople>) getArguments()
          .getSerializable(KEY_LIST_MEET_PEOPLE);
      mSettingMeetPeople = (MeetPeopleSettingResponse) getArguments()
          .getSerializable(KEY_MEET_PEOPLE_SETTING);
      isMoreAvailable = getArguments().getBoolean(KEY_IS_MORE_AVAILABLE);
    }
    mStateList = new HashMap<String, Integer>();
    mListUserIds = initialListUserId();
    mTake = getResources().getInteger(R.integer.take_meetpeople);
//        if (mListPeoples.size() < NO_NEED_LOAD_MORE_NUMBER)
//            isMoreAvailable = false;
//        mFavoriteHandler = new FavoriteBuzzHandler(this, handleFavoriteResult,
//                accessListBuzz);
  }

  @Override
  public void onResume() {
    super.onResume();
    changeSlideState(mState);
//        isFavorited = FavouritedPrefers.getInstance().hasContainFav(
//                mFriend.getId());
//        Log.e("hihihihi", "" + isFavorited);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    if (getActivity() instanceof MainActivity) {
      ((MainActivity) getActivity()).clearUnreadMessage(mFriend.getId());
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_slider_profile,
        container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
//        mViewFreezedLayer = (View) view
//                .findViewById(R.id.ib_chat_freezed_layer);
    mViewPager = (ViewPager) view.findViewById(R.id.pager);
    mPrevious = view.findViewById(R.id.ivBackProfile);
    mNext = view.findViewById(R.id.ivNextProfile);
    mPrevious.setOnClickListener(this);
    mNext.setOnClickListener(this);
    mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int postition) {
        if (mStateList != null && mListUserIds != null && mListUserIds.get(postition) != null
            && mStateList.get(mListUserIds.get(postition)) != null) {
          LogUtils.d("STATE", "onPageSelected - changeSlideState");
          mState = mStateList.get(mListUserIds.get(postition));
          changeSlideState(mState);
        }
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }

      @Override
      public void onPageScrollStateChanged(int arg0) {
      }
    });

    /**
     * trigger load data on first time #12322
     */
    if (mViewPager!=null)
      mViewPager.post(() -> {
        MyProfileFragment fragment = mAdapter.getItemAt(mViewPager.getCurrentItem());
        if (fragment != null) {
          fragment.setUserVisibleHint(true);
        }
      });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    rebindData();
    getSlidingMenu().setSlidingEnabled(false);
    getSlidingMenu().setOnCloseListener(this);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
    mContext = mMainActivity.getApplicationContext();
  }


  private void requestListUser() {
    if (mListPeoples == null) {
      return;
    }
    isStillLoadingListUser = true;
    int skip = mListPeoples.size();
    LogUtils.d("LOAD", "requestListUser - skip : " + skip);
    ListUserRequest listUserRequest = buildMeetPeopleRequest(
        mSettingMeetPeople, skip);
    requestServer(LOADER_LIST_USER_PROFILE, listUserRequest);
  }

  private ListUserRequest buildMeetPeopleRequest(
      MeetPeopleSettingResponse settingResponse, int skip) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    LocationPreferences locationPreferences = LocationPreferences
        .getInstance();
    double lon = locationPreferences.getLongtitude();
    double lat = locationPreferences.getLatitude();
    ListUserRequest listUserRequest = null;
    if (settingResponse != null && token != null) {
      listUserRequest = new ListUserRequest(token,
          settingResponse.getSortType(),
          settingResponse.getFilter(),
          settingResponse.isNewLogin(),
          settingResponse.getLowerAge(),
          settingResponse.getUpperAge(), lon, lat,
          settingResponse.getDistance(), settingResponse.getRegion(),
          skip, mTake);
    }
    return listUserRequest;
  }

  private void rebindData() {

    if (mAdapter == null) {
      mAdapter = new ProfilePagerAdapter(getChildFragmentManager());
    }
    mViewPager.setAdapter(mAdapter);
    mViewPager.setCurrentItem(mListUserIds.indexOf(mUserId));
  }

  private ArrayList<String> initialListUserId() {
    ArrayList<String> listUserIds = new ArrayList<String>();
    if (mListPeoples != null) {
      for (MeetPeople people : mListPeoples) {
        listUserIds.add(people.getUserId());
      }
    }
    return listUserIds;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_USER_ID, mUserId);
    outState.putSerializable(KEY_LIST_MEET_PEOPLE,
        (Serializable) mListPeoples);
    outState.putSerializable(KEY_MEET_PEOPLE_SETTING, mSettingMeetPeople);
    outState.putBoolean(KEY_IS_MORE_AVAILABLE, isMoreAvailable);
  }

  @Override
  public void onDestroyView() {
    mAdapter = null;
    mViewPager = null;
    getSlidingMenu().setSlidingEnabled(true);
    super.onDestroyView();
    if (mAlertDialog != null && mAlertDialog.isShowing()) {
      mAlertDialog.dismiss();
    }
//        if (confirmDialog != null && confirmDialog.isShowing())
//            confirmDialog.dismiss();
//        hideChatMoreOptions();
  }

  @Override
  public void onDestroy() {
//        hideChatMoreOptions();
    super.onDestroy();
    mStateList = null;
  }

  @Override
  protected String getUserIdTracking() {
    return null;
  }

  @Override
  public void startRequest(int loaderId) {

  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }

    boolean updateConnections = false;
    if (response instanceof MeetPeopleResponse) {
      MeetPeopleResponse meetPeopleResponse = (MeetPeopleResponse) response;
      if (meetPeopleResponse == null
          || meetPeopleResponse.getPeoples() == null
          || meetPeopleResponse.getPeoples().size() == 0
          || meetPeopleResponse.getPeoples().size() < mTake) {
        LogUtils.d("LOAD", "no more available");
        isMoreAvailable = false;
      }
      mListPeoples = meetPeopleResponse.filter(mListPeoples,
          meetPeopleResponse.getPeoples());
      LogUtils.d("LOAD", "List people size : " + mListPeoples.size());
      mListUserIds = initialListUserId();
      mAdapter.notifyDataSetChanged();
      isStillLoadingListUser = false;
      getLoaderManager().destroyLoader(LOADER_LIST_USER_PROFILE);
    }
//        if (response instanceof AddFavoriteResponse) {
//            AddFavoriteResponse favorite = (AddFavoriteResponse) response;
//            if (favorite.getCode() == Response.SERVER_SUCCESS) {
////                mUserInfoResponse.setFavorite(true);
////                checkFavorite(true);
//                // Increase number of favorites
//                UserPreferences.getInstance().increaseFavorite();
//                updateConnections = true;
//
//                // save fav to list
//                FavouritedPrefers.getInstance().saveFav(mFriend.getId());
//                // isFavorite = true;
////                if (confirmDialog != null && confirmDialog.isShowing()) {
////                    confirmDialog.dismiss();
////                }
//                String title = getString(R.string.profile_add_to_favorites_title);
//                String msg = String.format(
//                        getString(R.string.profile_add_to_favorites_message),
//                        mFriend.getName());
//                confirmDialog = new CustomConfirmDialog(getActivity(), title,
//                        msg, true);
//                confirmDialog.setOnButtonClick(new CustomConfirmDialog.OnButtonClickListener() {
//
//                    @Override
//                    public void onYesClick() {
//                        confirmDialog.dismiss();
//                        showGiveGiftFragment();
//                        isFavorited = true;
//                    }
//                });
//                confirmDialog
//                        .setCancelClickListener(new CustomConfirmDialog.OnCancelClickListener() {
//
//                            @Override
//                            public void OnCancelClick() {
//                                confirmDialog.dismiss();
//                                isFavorited = false;
//                            }
//                        });
//                if (isRequesAddFavorite) {
//                    confirmDialog.show();
//
//                    int dividerId = confirmDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//                    View divider = confirmDialog.findViewById(dividerId);
//                    if (divider != null) {
//                        divider.setBackgroundColor(confirmDialog.getContext().getResources().getColor(R.color.transparent));
//                    }
//                    isRequesAddFavorite = false;
//                }
//            }
//            getLoaderManager().destroyLoader(LOADER_ADD_FAVORITE);
//        }
//        if (response instanceof RemoveFavoriteResponse) {
//            RemoveFavoriteResponse removeFavoriteResponse = (RemoveFavoriteResponse) response;
//            if (removeFavoriteResponse.getCode() == Response.SERVER_SUCCESS) {
////                mUserInfoResponse.setFavorite(false);
////                checkFavorite(false);
//                // Decrease number of favorites
//                UserPreferences.getInstance().decreaseFavorite();
//                updateConnections = true;
//                isFavorited = false;
//
//                // remove fav from list
//                FavouritedPrefers.getInstance().removeFav(mFriend.getId());
//
//            }
//            getLoaderManager().destroyLoader(LOADER_REMOVE_FAVORITE);
//        }
//        switch (loader.getId()) {
//            case LOADER_ID_REPORT_USER:
//                handleReportUserResponse(response);
//                getLoaderManager().destroyLoader(LOADER_ID_REPORT_USER);
//                break;
//            case LOADER_ID_REMOVE_BLOCK_USER:
//                handleUnblockUserResponse((RemoveBlockUserResponse) response);
//                getLoaderManager().destroyLoader(LOADER_ID_REMOVE_BLOCK_USER);
//                break;
//            case LOADER_ID_ADD_BLOCK_USER:
//                handleBlockUserResponse((AddBlockUserResponse) response);
//                getLoaderManager().destroyLoader(LOADER_ID_ADD_BLOCK_USER);
//                break;
//            case LOADER_ID_ADD_TO_FAVORITES:
//                mFavoriteHandler.handleAddFavoriteResponse((AddFavoriteResponse) response);
//                getLoaderManager().destroyLoader(LOADER_ADD_FAVORITE);
//                break;
//            case LOADER_ID_REMOVE_FROM_FAVORITES:
//                mFavoriteHandler.handleRemoveFavoriteResponse((RemoveFavoriteResponse) response);
//                getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
//                break;
//        }
//        if (updateConnections) {
//            FragmentActivity parent = getActivity();
//            if (parent != null && parent instanceof MainActivity) {
//                ((MainActivity) parent)
//                        .onMainMenuUpdate(MainActivity.UPDATE_CONNECTIONS);
//            }
//        }
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
    int count = getFragmentManager().getBackStackEntryCount();
//        hideChatMoreOptions();
    if (count > 1) {
      super.onNavigationLeftClick(view);
    } else {
      super.onNavigationLeftClick(view);
    }
  }

  @Override
  public void onNavigationRightClick(View view) {
    super.onNavigationRightClick(view);
    getSlidingMenu().showSecondaryMenu();
    getMyProfileFragment().hideChatMoreOptions();
//        hideChatMoreOptions();
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response respone = null;
    if (loaderID == LOADER_LIST_USER_PROFILE) {
      respone = new MeetPeopleResponse(data);
    }
//        } else if (loaderID == LOADER_ID_REPORT_USER) {
//            respone = new ReportResponse(data);
//        } else if (loaderID == LOADER_ID_ADD_BLOCK_USER) {
//            respone = new AddBlockUserResponse(data);
//        } else if (loaderID == LOADER_ID_REMOVE_BLOCK_USER) {
//            respone = new RemoveBlockUserResponse(data);
//        } else if (loaderID == LOADER_ADD_FAVORITE) {
//            respone = new AddFavoriteResponse(data);
//        } else if (loaderID == LOADER_REMOVE_FAVORITE) {
//            respone = new RemoveFavoriteResponse(data);
//        }
    return respone;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onClose() {
    if (mNavigationManager == null) {
      return;
    }
    Fragment fragment = mNavigationManager.getActivePage();
    if (fragment instanceof SliderProfileFragment) {
      getSlidingMenu().setSlidingEnabled(false);
    } else if (!(fragment instanceof ChatFragment)) {
      getSlidingMenu().setSlidingEnabled(true);
    }
  }

  public void changeSlideState(int state) {
    if (mPrevious == null || mNext == null) {
      return;
    }

    LogUtils.d("STATE", " SliderProfileFragment - changeSlideState : "
        + state);

    switch (state) {
      case STATE_HIDE_NAVIGATION:
        mPrevious.setVisibility(View.GONE);
        mNext.setVisibility(View.GONE);
        break;
      case STATE_SHOW_NAVIGATION:
        mPrevious.setVisibility(View.VISIBLE);
        mNext.setVisibility(View.VISIBLE);
        break;
      case STATE_SHOW_ONLY_LEFT:
        mPrevious.setVisibility(View.VISIBLE);
        mNext.setVisibility(View.GONE);
        break;
      case STATE_SHOW_ONLY_RIGHT:
        mPrevious.setVisibility(View.GONE);
        mNext.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ivBackProfile:
        hideChatMoreOptions();
        if (mViewPager == null) {
          return;
        }
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);

        break;
      case R.id.ivNextProfile:
        hideChatMoreOptions();
        if (mViewPager == null) {
          return;
        }
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        break;
      default:
        break;
    }
  }

  /* Namit PopupWindow*/
  private MyProfileFragment getMyProfileFragment() {
    try {
      return ((MyProfileFragment) mViewPager.getAdapter()
          .instantiateItem(mViewPager, mViewPager.getCurrentItem()));
    } catch (Exception e) {
      try {
        Log.d("namit", "getMyProfileFragment Exception");
        return MyProfileFragment.newInstance(
            mListUserIds.get(0), mListPeoples.get(0)
                .getUser_name(), mListUserIds, isMoreAvailable,
            true);
      } catch (Exception ex) {
        return null;
      }
    }
  }

  public void clickMoreOptions() {
    if (null == getMyProfileFragment()) {
      return;
    }
    getMyProfileFragment().clickMoreOptions();
    getSlidingMenu().setSlidingEnabled(false);
  }

  public void hideChatMoreOptions() {
    if (null == getMyProfileFragment()) {
      return;
    }
    getMyProfileFragment().hideChatMoreOptions();
    getSlidingMenu().setSlidingEnabled(false);
  }

  private class ProfilePagerAdapter extends FragmentPagerAdapter implements
      OnCheckStateNavigation {

    private MyProfileFragment profileFragment;

    /**
     * store instance of added fragment #12322
     */
    private SparseArray<MyProfileFragment> registeredFragment = new SparseArray<>();

    public ProfilePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      //            if(position>0){
//            mFriend.setId(mListUserIds.get((position)-1));
//            mFriend.setName(mListPeoples.get((position)-1).getUser_name());
//            }
      profileFragment = MyProfileFragment.newInstance(
          mListUserIds.get(position), mListPeoples.get(position)
              .getUser_name(), mListUserIds, isMoreAvailable,
          true);
      profileFragment.setOnSlideClickListener(this);
      if (isMoreAvailable && position >= (mListUserIds.size() - 5)
          && !isStillLoadingListUser) {
        requestListUser();
      }
//            hideChatMoreOptions();

      registeredFragment.put(position, profileFragment);
      return profileFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      super.destroyItem(container, position, object);
      registeredFragment.remove(position);
    }

    @Override
    public int getCount() {
      return mListUserIds != null ? mListUserIds.size() : 0;
    }

    @Override
    public void onCheckState(int state) {
      mState = state;
      changeSlideState(mState);
    }

    /**
     * @param position to get fragment
     * @see #registeredFragment
     */
    public MyProfileFragment getItemAt(int position) {
      return registeredFragment.get(position);
    }
  }
}

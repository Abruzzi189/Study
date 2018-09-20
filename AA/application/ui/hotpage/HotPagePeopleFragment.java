package com.application.ui.hotpage;

import static com.application.actionbar.popup.controllers.IPopupRequest.REQUEST_GIFT;
import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.GetApplicationInfoRequest;
import com.application.connection.request.GetAttendtionNumberRequest;
import com.application.connection.request.ListNewPostRequest;
import com.application.connection.request.ListNewsRequest;
import com.application.connection.request.MeetPeopleRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.GetApplicationInfoResponse;
import com.application.connection.response.GetAttendtionNumberResponse;
import com.application.connection.response.ListNewPostResponse;
import com.application.connection.response.ListNewsResponse;
import com.application.connection.response.MeetPeopleResponse;
import com.application.connection.response.MeetPeopleSettingResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.entity.GiftCategories;
import com.application.entity.MeetPeople;
import com.application.entity.NewPostItem;
import com.application.entity.News;
import com.application.ui.BaseFragment;
import com.application.ui.ChatFragment;
import com.application.ui.CustomActionBarActivity;
import com.application.ui.HomeFragment;
import com.application.ui.MainActivity;
import com.application.ui.buzz.BuzzDetail;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.NewsDialog;
import com.application.ui.customeview.pulltorefresh.GridViewWithHeaderAndFooter;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridViewAuto;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.profile.SliderProfileFragment;
import com.application.ui.settings.MeetPeopleSetting;
import com.application.util.LogUtils;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.LocationPreferences;
import com.application.util.preferece.NewsPreference;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThoNh on 12/7/2017.
 */

public class HotPagePeopleFragment extends BaseFragment implements ResponseReceiver,
    HotPageUserOnlineAdapter.IUserOnlineEventListener {

  private static final String TAG = HotPagePeopleFragment.class.getSimpleName();
  private static final int TAKE = 24;

  private static final int LOADER_MEET_PEOPLE = 0x00;
  private final static int LOADER_GET_ATTENDTION_NUMBER = 0x01;
  private final static int LOADER_GET_NEW_POST = 0x02;
  private static final int LOADER_ID_REMOVE_FROM_FAVORITES = 0x03;
  private static final int LOADER_ID_ADD_TO_FAVORITES = 0x04;
  private static final int LOADER_GET_NEWS = 0x05;
  private static final int LOADER_APPLICATION_INFO = 0x06;

  // View vertical
  private PullToRefreshGridViewAuto mPullView;
  private GridViewWithHeaderAndFooter mListView; // GridView set Column = 1
  private TextView mTextViewEmpty;

  /*List new post -> scroll horizontal*/
  private RecyclerView mListNewPost;
  private LinearHorizontalLayoutManager mLayoutManager;
  private HotPagerNewPostAdapter mNewPostAdapter;
  private ProgressBar mRefreshView;
  private LinearLayout mHorizontalLayout;

  private ImageView mImagePostTimeline;


  // Data
  private ArrayList<MeetPeople> mPeopleList;
  private List<NewPostItem> mNewPosts;
  private HotPageUserOnlineAdapter mUserOnlineAdapter;
  private MeetPeople mPeopleSelected;

  private boolean isMoreAvailable = true;
  private boolean isRestartMeetPeople = false;

  // if isNewPostRefresh = true --> refresh , else --> isLoadMore
  private boolean isNewPostRefresh;
  /*************************************** Listener *********************************************/

  private PullToRefreshBase.OnRefreshListener2<GridViewWithHeaderAndFooter> onRefreshListener =
      new PullToRefreshBase.OnRefreshListener2<GridViewWithHeaderAndFooter>() {
        @Override
        public void onPullDownToRefresh(
            PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
          LogUtils.e(TAG, "onRefreshListener ---> onPullDownToRefresh");
          restartRequestListPeople();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
          LogUtils.e(TAG, "onRefreshListener ---> onPullUpToRefresh");
          requestLoadMoreListPeople();
        }
      };
  private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
        int totalItemCount) {
      if (mPullView.isRefreshing() || mUserOnlineAdapter == null || mUserOnlineAdapter.isEmpty()) {
        return;
      }

      if (firstVisibleItem + visibleItemCount >= totalItemCount - visibleItemCount) {
        PullToRefreshBase.Mode mode = mPullView.getMode();
        if (mode == PullToRefreshBase.Mode.PULL_FROM_END
            || mode == PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY
            || mode == PullToRefreshBase.Mode.BOTH) {
          mPullView.performPullUp();
        }
      }
    }
  };
  private HotPagerNewPostAdapter.IOnNewPostListener onNewPostEventListener
      = new HotPagerNewPostAdapter.IOnNewPostListener() {
    @Override
    public void startRefresh() {
      restartRequestNewPost();
    }

    @Override
    public void startLoadMore() {
      loadMoreRequestNewPost();
    }

    @Override
    public void onNewPostClick(NewPostItem item, int position) {
      BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(item.buzzId, item.buzzType);
      buzzDetailFragment.showSoftkeyWhenStart(true);
      ((CustomActionBarActivity) getContext()).getNavigationManager().addPage(buzzDetailFragment);
    }
  };

  public static HotPagePeopleFragment newInstance() {
    Bundle args = new Bundle();
    HotPagePeopleFragment fragment = new HotPagePeopleFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    LogUtils.e(TAG, "onAttach");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtils.e(TAG, "onCreate");
    LogUtils.e(TAG, "ScreenSize:" + getResources().getDisplayMetrics().density);
//         return 0.75 if it 's LDPI
//        return 1.0 if it 's MDPI
//        return 1.5 if it 's HDPI
//        return 2.0 if it 's XHDPI
//        return 3.0 if it 's XXHDPI
//        return 4.0 if it 's XXXHDPI
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    LogUtils.e(TAG, "onCreateView");
    restartRequestAttendtionNum();
    View view = inflater.inflate(R.layout.fragment_hot_page, container, false);

    // init pull to refresh view (Vertical list)
    mPullView = (PullToRefreshGridViewAuto) view.findViewById(R.id.frg_hot_page_people);
    mPullView.setOnRefreshListener(onRefreshListener);
    mPullView.setOnScrollListener(mOnScrollListener);
    mTextViewEmpty = createTextViewEmpty();
    mPullView.setEmptyView(mTextViewEmpty);

    // setup ListView for pull to refresh
    mListView = mPullView.getRefreshableView();
    mListView.setNumColumns(1);
    mListView.setVerticalSpacing(0);

    // init list new post (horizontal list)
    mHorizontalLayout = (LinearLayout) view.findViewById(R.id.cc);
    mRefreshView = (ProgressBar) view.findViewById(R.id.refresh_view);
    mListNewPost = (RecyclerView) view.findViewById(R.id.list_new_post);
    mLayoutManager = new LinearHorizontalLayoutManager(getContext(), false);
    mListNewPost.setLayoutManager(mLayoutManager);
    mNewPostAdapter = new HotPagerNewPostAdapter(mHorizontalLayout, mListNewPost, mLayoutManager,
        mRefreshView, onNewPostEventListener);
    mListNewPost.setAdapter(mNewPostAdapter);

    mImagePostTimeline = (ImageView) view.findViewById(R.id.imv_post_timeline);
    mImagePostTimeline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((HomeFragment) getParentFragment()).goToTimeline();

        new Handler().post(new Runnable() {
          @Override
          public void run() {
            Fragment homeActivePage = ((HomeFragment) getParentFragment()).getHomeActivePage();
            if (homeActivePage instanceof BuzzFragment) {
              ((BuzzFragment) homeActivePage).setFocusEditStatus();
            }
          }
        });
      }
    });

    // prevent open menu right when touch on area horizontal scroll
    view.findViewById(R.id.cc).setTag("cc");
    getSlidingMenu().addIgnoredView(view.findViewById(R.id.cc));

    showNews();

    return view;
  }

  /**
   * <p>1. request api get_app_info to check BE allow show news or not?</p> <p>2. if allow ->
   * request api get list news</p> <p>3. show news dialog</p>
   */
  private void showNews() {
    // - check tick don\'t show this day?
    // - backend setting
    Context appContext = getContext().getApplicationContext();
    String userId = UserPreferences.getInstance().getUserId();
    boolean shouldShowNews = NewsPreference
        .isShowNews(appContext, userId, NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE);
    if (shouldShowNews) {
      GetApplicationInfoRequest applicationInfoRequest = new GetApplicationInfoRequest();
      restartRequestServer(LOADER_APPLICATION_INFO, applicationInfoRequest);
    }
  }

  /**
   * request api to get list news from server
   */
  public void requestGetListNews() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    int gender = userPreferences.getGender();
    String userid = userPreferences.getUserId();
    ListNewsRequest listNewsRequest = new ListNewsRequest(token, gender, userid);
    restartRequestServer(LOADER_GET_NEWS, listNewsRequest);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LogUtils.e(TAG, "onActivityCreated");

    mPeopleList = new ArrayList<>();
    mUserOnlineAdapter = new HotPageUserOnlineAdapter(getContext(), mPeopleList);
    mUserOnlineAdapter.setListener(this);
    mListView.setAdapter(mUserOnlineAdapter);
    mNewPosts = new ArrayList<>();
  }

  @Override
  public void onStart() {
    super.onStart();
    LogUtils.e(TAG, "onStart");
  }

  @Override
  public void onResume() {
    super.onResume();
    mActionBar.syncActionBar(this);        // reset nav like primary page in app
    requestFirstListPeople();
    mNewPostAdapter.startRefresh();
    LogUtils.e(TAG, "onResume");

    if (mPeopleSelected != null) {
      LogUtils.e("ThoNH", TAG + " -> onResume -> " + mPeopleSelected.getUser_name() + " --> isFav:"
          + mPeopleSelected.isFav);
      boolean b = FavouritedPrefers.getInstance().hasContainFav(mPeopleSelected.getUserId());
      mPeopleSelected.isFav = b ? 1 : 0;
      mUserOnlineAdapter.updateFavorite(mPeopleSelected);
    }

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LogUtils.e(TAG, "onDestroy");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    LogUtils.e(TAG, "onDestroyView");
  }

  /*************************************Adapter event********************************************/

  @Override
  public void onChat(MeetPeople meetPeople, int position) {
    if (meetPeople != null) {
      ChatFragment chatFragment = ChatFragment.newInstance(
          meetPeople.getUserId(),
          meetPeople.getAva_id(),
          meetPeople.getUser_name(),
          meetPeople.getGender(),
          meetPeople.isVoiceCallWaiting(),
          meetPeople.isVideoCallWaiting(), true);
      replaceFragment(chatFragment, MainActivity.TAG_FRAGMENT_CHAT);
    }
  }

  @Override
  public void onFavorite(final MeetPeople meetPeople, int position) {
    mPeopleSelected = meetPeople;
    switch (meetPeople.isFav) {
      case 0:
        addUserFavorite(meetPeople.getUserId());
        break;
      case 1:
//                unFavoriteUser(meetPeople.getUserId());
        String title = getString(R.string.profile_remove_from_favorites_title);
        String msg = String.format(getString(R.string.profile_remove_from_favorites_message),
            meetPeople.getUser_name());
        AlertDialog confirmDialog =
            new CustomConfirmDialog(getActivity(), title, msg, false)
                .setPositiveButton(0, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    unFavoriteUser(meetPeople.getUserId());
                  }
                }).create();
        confirmDialog.show();
        int dividerId = confirmDialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = confirmDialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(
              confirmDialog.getContext().getResources().getColor(R.color.transparent));
        }
        break;
    }
  }

  @Override
  public void onItemClick(MeetPeople meetPeople, int position) {
    replaceFragment(SliderProfileFragment.newInstance(
        meetPeople.getUserId(),
        meetPeople.getAva_id(),
        meetPeople.getUser_name(),
        meetPeople.getGender(),
        false, false, true,
        (ArrayList<MeetPeople>) mUserOnlineAdapter.getData(),
        MeetPeopleSetting.getInstance(mAppContext).getResponse(),
        isMoreAvailable));
  }

  /*************************************** Private func *****************************************/
  private TextView createTextViewEmpty() {
    TextView textView = new TextView(getContext());
    textView.setGravity(Gravity.CENTER);
    textView.setText(R.string.common_loading);
    textView.setTextColor(Color.BLACK);
    return textView;
  }

  private void showCheckOutAndFavor(GetAttendtionNumberResponse mResponse) {
    int new_checkout_num = mResponse.getNew_checkout_num();
    int new_fvt_num = mResponse.getNew_fvt_num();
    int fvt_num = mResponse.getFvt_num();
    int checkout_num = mResponse.getCheckout_num();

    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.saveUnlockWhoCheckMeOut(checkout_num);
    userPreferences.saveNumberFavoritedMe(fvt_num);

  }

  /************************************* Request Server *****************************************/
  public void requestFirstListPeople() {
    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting.getInstance(mAppContext)
        .getResponse();
    MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(settingResponse, 0);
    if (meetPeopleRequest != null) {
      requestServer(LOADER_MEET_PEOPLE, meetPeopleRequest);
      mPullView.setEmptyView(createTextViewEmpty());
    }
  }

  private void restartRequestListPeople() {
    LogUtils.e(TAG, "restartRequestListPeople()");

    isRestartMeetPeople = true;
    mUserOnlineAdapter.clearData();

    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
        .getInstance(mAppContext).getResponse();
    MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(
        settingResponse, 0);
    if (meetPeopleRequest != null) {
      //Disable load more when request load more people on meet people
      restartRequestServer(LOADER_MEET_PEOPLE, meetPeopleRequest);
      mPullView.setEmptyView(createTextViewEmpty());
    }

  }

  private void requestLoadMoreListPeople() {
    LogUtils.e(TAG, "requestLoadMoreListPeople()");
    if (mListView != null) {
      mListView.post(new Runnable() {
        @Override
        public void run() {
          mListView.smoothScrollToPosition(mUserOnlineAdapter
              .getCount() - 1);
        }
      });
    }

    int count = mUserOnlineAdapter.getCount();
    int d = count % TAKE;
    int v = TAKE - d;
    int next = count;
    if (d != 0) {
      next += v;
    }

    LogUtils.e(TAG, "skip:" + next);

    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
        .getInstance(mAppContext).getResponse();
    MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(
        settingResponse, next);
    requestServerGetMoreData(LOADER_MEET_PEOPLE, meetPeopleRequest, this);
  }

  public void restartRequestAttendtionNum() {
    GetAttendtionNumberRequest getAttendRequest = new GetAttendtionNumberRequest(
        UserPreferences.getInstance().getToken());
    if (getAttendRequest != null) {
      restartRequestServer(LOADER_GET_ATTENDTION_NUMBER, getAttendRequest);
    }
  }

  public void restartRequestNewPost() {
    isNewPostRefresh = true;        // --> refre
    ListNewPostRequest request = new ListNewPostRequest(UserPreferences.getInstance().getToken(),
        0);
    restartRequestServer(LOADER_GET_NEW_POST, request);
  }

  public void loadMoreRequestNewPost() {
    isNewPostRefresh = false;  // --> loadmore
    ListNewPostRequest request = new ListNewPostRequest(UserPreferences.getInstance().getToken(),
        mNewPostAdapter.getItemCount());
    restartRequestServer(LOADER_GET_NEW_POST, request);
  }

  public void addUserFavorite(String userId) {
    String token = UserPreferences.getInstance().getToken();
    AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token, userId);
    restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
  }

  public void unFavoriteUser(String userId) {
    String token = UserPreferences.getInstance().getToken();
    RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(token, userId);
    restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES, removeFavoriteRequest);
  }


  /**********************************************************************************************/

  private MeetPeopleRequest buildMeetPeopleRequest(MeetPeopleSettingResponse setting, int skip) {
    MeetPeopleRequest meetPeopleRequest = null;
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();

    LocationPreferences location = LocationPreferences.getInstance();
    double lon = location.getLongtitude();
    double lat = location.getLatitude();

    int sort = setting.getSortType();
    int filter = setting.getFilter();
    boolean isNew = setting.isNewLogin();
    int lowerAge = setting.getLowerAge();
    int upperAge = setting.getUpperAge();
    int distance = setting.getDistance();
    int[] region = setting.getRegion();
    int take = TAKE;

    if (skip < 1) {
      take *= 2;
    }
//        int lostMember = (take + skip) % numOfColumn;
//        if (lostMember != 0) {
//            take += numOfColumn - lostMember;
//        }

    if (setting != null && token != null) {
      meetPeopleRequest = new MeetPeopleRequest(token, sort, filter,
          isNew, lowerAge, upperAge, lon, lat, distance, region,
          skip, take);
    }
    return meetPeopleRequest;
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

    if (mPeopleSelected.getUserId() != null && mPeopleSelected.getUserId().length() > 0) {
      chooseGiftToSend = ChooseGiftToSend
          .newInstance(mPeopleSelected.getUserId(), mPeopleSelected.getUser_name(),
              categories, true);

    } else {
      String userName = UserPreferences.getInstance().getUserName();
      chooseGiftToSend = ChooseGiftToSend.newInstance(UserPreferences
          .getInstance().getUserId(), userName, categories, true);
    }

    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  /********************************** Server Response *******************************************/

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    mPullView.onRefreshComplete();

    if (getActivity() == null) {
      LogUtils.e(TAG, "receiveResponse:getActivity() == null");
      return;
    }

    if (response.getCode() != Response.SERVER_SUCCESS) {
      getLoaderManager().destroyLoader(loader.getId());
      if (mPullView.getMode() == PullToRefreshBase.Mode.DISABLED) {
        mPullView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
      }
      return;
    }

    if (response instanceof MeetPeopleResponse) {
      MeetPeopleResponse meetPeopleResponse = (MeetPeopleResponse) response;
      if (meetPeopleResponse.getPeoples().size() == 0) {
        LogUtils.e(TAG, "response ---> MeetPeopleResponse ---> size=0");
      }

      isMoreAvailable = meetPeopleResponse.isMoreAvaiable();
      if (isMoreAvailable) {
        mPullView.setMode(PullToRefreshBase.Mode.BOTH);
        Resources resource = getResources();
        mPullView.setPullLabelFooter(resource.getString(R.string.pull_to_load_more_pull_label));
        mPullView
            .setReleaseLabelFooter(resource.getString(R.string.pull_to_load_more_release_label));
      } else {
        mPullView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
      }

      mUserOnlineAdapter.appendData(meetPeopleResponse.getPeoples());

      if (mUserOnlineAdapter.getCount() == 0) {
        mTextViewEmpty.setText(R.string.no_more_items_to_show);
        getLoaderManager().destroyLoader(loader.getId());
      }

      if (isRestartMeetPeople) {
        if (mUserOnlineAdapter.getCount() > 0) {
          mListView.smoothScrollToPosition(0);
        }
        isRestartMeetPeople = false;
      }
    } else if (response instanceof GetAttendtionNumberResponse) {
      showCheckOutAndFavor((GetAttendtionNumberResponse) response);
      Fragment fragment = getParentFragment();
      if (fragment instanceof HomeFragment) {
        ((HomeFragment) fragment)
            .showChatAndFootPrintNotification((GetAttendtionNumberResponse) response);
      }
      getLoaderManager().destroyLoader(LOADER_GET_ATTENDTION_NUMBER);

    } else if (response instanceof ListNewPostResponse) {
      ListNewPostResponse data = (ListNewPostResponse) response;
      if (isNewPostRefresh) {      // --> refresh
        mNewPostAdapter.clearData();
        mNewPostAdapter.refreshComplete();
        mNewPostAdapter.setData(data.mData);
      } else {                     // --> load more
        mNewPostAdapter.loadMoreComplete(data.mData);
      }

      getLoaderManager().destroyLoader(LOADER_GET_NEW_POST);
    } else if (response instanceof AddFavoriteResponse) {

      AddFavoriteResponse data = (AddFavoriteResponse) response;
      if (data.getCode() == Response.SERVER_SUCCESS) {
        mPeopleSelected.isFav = 1;
        FavouritedPrefers.getInstance().saveFav(mPeopleSelected.getUserId());
        mUserOnlineAdapter.updateFavorite(mPeopleSelected);

        String title = getString(R.string.profile_add_to_favorites_title);
        String msg = String.format(getString(R.string.profile_add_to_favorites_message),
            mPeopleSelected.getUser_name());

        new CustomConfirmDialog(getActivity(), title,
            msg, true)
            .setPositiveButton(0, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showGiveGiftFragment();
              }
            })
            .setNegativeButton(0, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
            .create().show();
      }

      getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);

    } else if (response instanceof RemoveFavoriteResponse) {

      RemoveFavoriteResponse data = (RemoveFavoriteResponse) response;
      if (data.getCode() == 0) {
        mPeopleSelected.isFav = 0;
        FavouritedPrefers.getInstance().removeFav(mPeopleSelected.getUserId());
        mUserOnlineAdapter.updateFavorite(mPeopleSelected);
      }

      getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
    } else if (response instanceof ListNewsResponse) {
      handleListNewResponse((ListNewsResponse) response);
      getLoaderManager().destroyLoader(LOADER_GET_NEWS);
    } else if (response instanceof GetApplicationInfoResponse) {
      onResponseApplicationInfo((GetApplicationInfoResponse) response);
      // don't call again on resume
      getLoaderManager().destroyLoader(LOADER_APPLICATION_INFO);
    }
  }

  private void onResponseApplicationInfo(GetApplicationInfoResponse response) {
    // only request list news if BE setting allow show news
    if (response.getCode() == Response.SERVER_SUCCESS && response.isShowNews()) {
      requestGetListNews();
    }
  }

  private void handleListNewResponse(ListNewsResponse response) {
    if (response == null || response.getCode() != Response.SERVER_SUCCESS) {
      return;
    }
    final List<News> mNewsList = response.getNewsList();
    if (mNewsList != null && !mNewsList.isEmpty()) {

      final String userId = UserPreferences.getInstance().getUserId();
      new NewsDialog(getContext(), mNewsList) {
        @Override
        protected void onSaveDontShowNewsToday() {
          NewsPreference.saveTimeSettingNews(getContext(), userId,
              NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE, System.currentTimeMillis());
        }
      }.show();

      NewsPreference.setShowNews(getContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE, false);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
    Response response = null;
    if (loaderID == LOADER_MEET_PEOPLE) {
      response = new MeetPeopleResponse(data);
    } else if (loaderID == LOADER_GET_ATTENDTION_NUMBER) {
      response = new GetAttendtionNumberResponse(data);
    } else if (loaderID == LOADER_GET_NEW_POST) {
      response = new ListNewPostResponse(data);
    } else if (loaderID == LOADER_ID_ADD_TO_FAVORITES) {
      response = new AddFavoriteResponse(data);
    } else if (loaderID == LOADER_ID_REMOVE_FROM_FAVORITES) {
      response = new RemoveFavoriteResponse(data);
    } else if (loaderID == LOADER_GET_NEWS) {
      response = new ListNewsResponse(data);
    } else if (loaderID == LOADER_APPLICATION_INFO) {
      response = new GetApplicationInfoResponse(data);
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }
}

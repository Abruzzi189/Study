package com.application.ui;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.GetApplicationInfoRequest;
import com.application.connection.request.GetAttendtionNumberRequest;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.ListNewsRequest;
import com.application.connection.request.MeetPeopleRequest;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.GetApplicationInfoResponse;
import com.application.connection.response.GetAttendtionNumberResponse;
import com.application.connection.response.ListNewsResponse;
import com.application.connection.response.MeetPeopleResponse;
import com.application.connection.response.MeetPeopleSettingResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.ConversationItem;
import com.application.entity.MeetPeople;
import com.application.entity.News;
import com.application.event.ConversationChangeEvent;
import com.application.ui.connection.ConnectionFragment;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NewNotificationsDialog;
import com.application.ui.customeview.NewsDialog;
import com.application.ui.customeview.TrimmedTextView;
import com.application.ui.customeview.UnlockDialog;
import com.application.ui.customeview.pulltorefresh.GridViewWithHeaderAndFooter;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridViewAuto;
import com.application.ui.notification.NotificationFragment;
import com.application.ui.profile.SliderProfileFragment;
import com.application.ui.settings.MeetPeopleSetting;
import com.application.util.ErrorString;
import com.application.util.LogUtils;
import com.application.util.RegionUtils;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.LocationPreferences;
import com.application.util.preferece.NewsPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.angmarch.views.NiceSpinner;


/**
 * Display list all people in MeetPeople
 *
 * @author tungdx
 */
public class MeetPeopleFragment extends LocationFragment implements ResponseReceiver,
    OnClickListener, OnItemClickListener {

  private static final String TAG = "MeetPeopleFragment";
  // id for loader
  private static final int LOADER_APPLICATION_INFO = 0x06;
  private final static int LOADER_MEETPEOPLE = 0;
  private final static int LOADER_GET_MEET_PEOPLE_SETTING = 1;
  private final static int LOADER_ID_CHECK_UNLOCK = 2;
  private final static int LOADER_GET_ATTENDTION_NUMBER = 4;
  private final static int LOADER_GET_NEWS = 5;
  private final static int REQUEST_SETTING_CODE = 100;
  //hiepuh
  public static boolean isload = false;
  // Params for View in Screen
  private PullToRefreshGridViewAuto mpPullView;
  private GridViewWithHeaderAndFooter mGridPeople;
  //end
  private android.app.AlertDialog mAlertDialog;

  // show number redtime
  private ImageView mBtnGridList;
  private NiceSpinner spnFilter;
  private RelativeLayout btnSearch;
  private LinearLayout llListSwitcher, llGridSwitcher;
  private LinearLayout btngrid_list;
  private android.os.Handler mHandler = new android.os.Handler();
  //ThoNH
  private ImageView mButtonGird, mButtonList;
  // Amount item is get when load data
  private int mTake;

  // Initial position get data
  private MeetPeopleAdapter mMeetPeopleAdapter;
  private TextView txtEmpty;
  private boolean isList = false;

  private UnlockDialog mUnlockDialog;
  private NewNotificationsDialog mNewNotificationsDialog;
  private Context mAppContext;

  private RegionUtils mRegionUtils;
  private boolean isRestartMeetPeople = false;
  private boolean isMoreAvailable = true;

  // get finish register flag
  private int finishRegisterFlag = Constants.FINISH_REGISTER_NO;
  private OnRefreshListener2<GridViewWithHeaderAndFooter> onRefreshListener = new OnRefreshListener2<GridViewWithHeaderAndFooter>() {
    @Override
    public void onPullDownToRefresh(
        PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      restartRequestListPeople();
    }

    @Override
    public void onPullUpToRefresh(
        PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      if (mGridPeople != null) {
        mGridPeople.post(new Runnable() {
          @Override
          public void run() {
            mGridPeople.smoothScrollToPosition(mMeetPeopleAdapter
                .getCount() - 1);
          }
        });
      }
      int count = mMeetPeopleAdapter.getCount();
      int d = count % mTake;
      int v = mTake - d;
      int next = count;
      if (d != 0) {
        next += v;
      }

      MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
          .getInstance(mAppContext).getResponse();
      MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(
          settingResponse, next);
      requestServerGetMoreData(LOADER_MEETPEOPLE, meetPeopleRequest,
          MeetPeopleFragment.this);
    }
  };

  public static MeetPeopleFragment newInstance() {
    MeetPeopleFragment fragment = new MeetPeopleFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTake = getResources().getInteger(R.integer.take_meetpeople);
    EventBus.getDefault().register(this);
    shouldShowDialogGetFreePoints();
  }

  @Override
  public void onResume() {
    super.onResume();
    mActionBar.syncActionBar(this);
    //hiepuh
    if (MainActivity.checkactionbar) {
      mActionBar.syncActionBar(this);
    }
    restartRequestListPeople();
    int selectedIndex = MeetPeopleSetting.getInstance(mAppContext)
        .getFilter();
    spnFilter.setSelectedIndex(selectedIndex);
    initListGridSwitcher();
    //end
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplication();

    GoogleReviewPreference preference = new GoogleReviewPreference();

    String appVerNameS = Utility.getAppVersionName(getActivity());
    String appVerNameFromBackend = preference.getAppVersion();
    try {
      float appVerNameF = Float.valueOf(appVerNameS);
      float appVerNameFBackend = Float.valueOf(appVerNameFromBackend);

      boolean isFirstShow = preference.isFirstShow();
      if (appVerNameFBackend > appVerNameF && isFirstShow) {
        boolean isForce = preference.isForceUpdate();
        showDialogUpdate(isForce, preference.getWebUrl());

        // nếu không phải là bắt buộc thì sau khi show dialog sẽ k hiển thị lại dialog đó nữa
        // nếu là bắt buộc thì cứ trở lại màn hình meetpeople sẽ hiển thị lại popup
        if (!isForce) {
          preference.setFirstShowUpdate(false);
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void showDialogUpdate(final boolean isForce, final String url) {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    final AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), true);
    String title = getResources().getString(R.string.dialog_confirm_update_app);
    String message = "";

    if (isForce) {
      message = getResources().getString(R.string.dialog_confirm_force_update_app_content);
    } else {
      message = getResources().getString(R.string.dialog_confirm_update_app_content);
    }

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    builder.setMessage(message);
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getActivity(), "Ok", Toast.LENGTH_SHORT).show();

        BaseFragment fragment = null;
        fragment = WebViewFragment
            .newInstance(url,
                getActivity().getResources()
                    .getString(R.string.dialog_confirm_force_update_app_content));

        if (fragment != null) {
          mNavigationManager.addPage(fragment);
          mAlertDialog.dismiss();
        }
      }
    };

    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

        if (isForce) {
          System.exit(0);
        } else {
          mAlertDialog.dismiss();
        }

      }
    });

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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_meetpeople, container,
        false);
    initView(view);
    mRegionUtils = new RegionUtils(mAppContext);

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences != null) {
      int gender = preferences.getGender();
      int ageVerification = preferences.getAgeVerification();
      if (gender == UserSetting.GENDER_MALE
          || (gender == UserSetting.GENDER_FEMALE)) {
        restartRequestAttendtionNum();
      }
    }

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
        .isShowNews(appContext, userId, NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE);
    if (shouldShowNews) {
      GetApplicationInfoRequest applicationInfoRequest = new GetApplicationInfoRequest();
      restartRequestServer(LOADER_APPLICATION_INFO, applicationInfoRequest);
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    List<MeetPeople> listPeoples = new ArrayList<MeetPeople>();
    mMeetPeopleAdapter = new MeetPeopleAdapter(mAppContext, listPeoples);
    mGridPeople.setAdapter(mMeetPeopleAdapter);
    //hiepuh
    int selectedIndex = MeetPeopleSetting.getInstance(mAppContext)
        .getFilter();
    spnFilter.setSelectedIndex(selectedIndex);
    //end
  }

  /**
   * Initial data
   */
  private void shouldShowDialogGetFreePoints() {
    Bundle bundle = getArguments();
    if (bundle != null) {
      finishRegisterFlag = bundle
          .getInt(Constants.ARGUMENT_FINISH_REGISTER_FLAG);
      // remove argument key after get data
      bundle.remove(Constants.ARGUMENT_FINISH_REGISTER_FLAG);
    }
  }

  private void controlShowAlertDialog(int gender) {
    switch (gender) {
      case UserSetting.GENDER_MALE:
        showDialogHowToUseForMale();
        break;
      default:
        break;
    }
  }

  private void initView(View view) {
    mpPullView = (PullToRefreshGridViewAuto) view
        .findViewById(R.id.fragment_meetpeople_grid_people);
    mpPullView.setMode(Mode.DISABLED);
//        mpPullView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        mpPullView.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
    mpPullView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (mpPullView.isRefreshing() || mMeetPeopleAdapter == null || mMeetPeopleAdapter
            .isEmpty()) {
          return;
        }

        if (firstVisibleItem + visibleItemCount >= totalItemCount - visibleItemCount) {
          Mode mode = mpPullView.getMode();
          if (mode == Mode.PULL_FROM_END || mode == Mode.MANUAL_REFRESH_ONLY || mode == Mode.BOTH) {
            mpPullView.performPullUp();
          }
        }
      }
    });

    createViewGroupFilter(view);
    int filter = MeetPeopleSetting.getInstance(mAppContext).getFilter();
    updateViewGroupFilter(filter);

    //hiepuh

    spnFilter = (NiceSpinner) view.findViewById(R.id.spnFilter);
    btnSearch = (RelativeLayout) view.findViewById(R.id.btnSearch);
    llListSwitcher = (LinearLayout) view
        .findViewById(R.id.ll_switcher_list);
    llGridSwitcher = (LinearLayout) view
        .findViewById(R.id.ll_switcher_grid);
    btngrid_list = (LinearLayout) view
        .findViewById(R.id.grid_list);

    mButtonGird = (ImageView) btngrid_list.findViewById(R.id.iv_switcher_grid);
    mButtonList = (ImageView) btngrid_list.findViewById(R.id.iv_switcher_list);

    btnSearch.setOnClickListener(this);
    llListSwitcher.setOnClickListener(this);
    llGridSwitcher.setOnClickListener(this);

    initFilterSpinner();
    initListGridSwitcher();

    //end

    mGridPeople = mpPullView.getRefreshableView();

    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    Preferences preferences = Preferences.getInstance();
    isList = preferences.getMeetPeopleListType();
    if (googleReviewPreference.isTurnOffUserInfo()) {
      btngrid_list.setVisibility(View.VISIBLE);
    } else {
      isList = false;
      preferences.saveMeetPeopleListType(isList);
      btngrid_list.setVisibility(View.GONE);
    }

    mpPullView.setOnRefreshListener(onRefreshListener);
    txtEmpty = new TextView(mAppContext);
    txtEmpty.setGravity(Gravity.CENTER);
    txtEmpty.setText(R.string.loading);
    txtEmpty.setTextColor(Color.BLACK);
    mpPullView.setEmptyView(txtEmpty);
    final int mImageThumbSize = getResources().getDimensionPixelSize(
        R.dimen.image_thumbnail_size);
    final int mImageThumbSpacing = getResources().getDimensionPixelSize(
        R.dimen.image_thumbnail_spacing);
    mGridPeople.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (mMeetPeopleAdapter.getNumColumns() == 0) {
              final int numColumns = (int) Math.floor(mGridPeople
                  .getWidth()
                  / (mImageThumbSize + mImageThumbSpacing));
              if (numColumns > 0) {
                final int columnWidth = (mGridPeople.getWidth() / numColumns)
                    - mImageThumbSpacing;
                mMeetPeopleAdapter.setNumColumns(numColumns);
                mMeetPeopleAdapter.setItemHeight(columnWidth);
                LogUtils.e(TAG, "Gridview has " + numColumns
                    + " columns");
                isList = Preferences.getInstance()
                    .getMeetPeopleListType();
                if (isList) {
                  mMeetPeopleAdapter.displayInList(true);
                  mGridPeople.setNumColumns(1);
                }
              }
            }
          }
        });
    mGridPeople.setOnItemClickListener(this);

  }

  public void requestFirstListPeople() {
    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
        .getInstance(mAppContext).getResponse();
    MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(
        settingResponse, 0);
    if (meetPeopleRequest != null) {
      requestServer(LOADER_MEETPEOPLE, meetPeopleRequest);
    }
  }

  public void requestGetListNews() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    int gender = userPreferences.getGender();
    String userid = userPreferences.getUserId();
    ListNewsRequest listNewsRequest = new ListNewsRequest(token, gender, userid);
    restartRequestServer(LOADER_GET_NEWS, listNewsRequest);
  }

  /**
   * Restart load MeetPeople
   */
  public void restartRequestListPeople() {
    isRestartMeetPeople = true;
    mMeetPeopleAdapter.clearAllData();
    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
        .getInstance(mAppContext).getResponse();
    MeetPeopleRequest meetPeopleRequest = buildMeetPeopleRequest(
        settingResponse, 0);
    if (meetPeopleRequest != null) {
      //Disable load more when request load more people on meet people
      mpPullView.setMode(Mode.DISABLED);
      restartRequestServer(LOADER_MEETPEOPLE, meetPeopleRequest);
    }
  }

  public void restartRequestAttendtionNum() {
    GetAttendtionNumberRequest getAttendRequest = new GetAttendtionNumberRequest(
        UserPreferences.getInstance().getToken());
    if (getAttendRequest != null) {
      restartRequestServer(LOADER_GET_ATTENDTION_NUMBER, getAttendRequest);
    }
  }

  private MeetPeopleRequest buildMeetPeopleRequest(
      MeetPeopleSettingResponse setting, int skip) {
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
    int take = mTake;
//        int numOfColumn = mGridPeople.getNumColumns();
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

  @Override
  public void startRequest(int loaderId) {
    mpPullView.setMode(Mode.DISABLED);
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    mpPullView.onRefreshComplete();
    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      getLoaderManager().destroyLoader(loader.getId());
      if (mpPullView.getMode() == Mode.DISABLED) {
        mpPullView.setMode(Mode.PULL_FROM_START);
      }
      return;
    }
    if (response instanceof MeetPeopleResponse) {
      MeetPeopleResponse meetPeopleResponse = (MeetPeopleResponse) response;
      if (meetPeopleResponse.getPeoples().size() == 0) {
        // if adapter not empty -> show end of list
        if (mMeetPeopleAdapter.getCount() > 0) {
          Toast.makeText(mAppContext, R.string.end_of_list,
              Toast.LENGTH_LONG).show();
        } else {
          //hiepuh
          if (isload) {
            // if adapter empty -> show alert search setting
            String title = getString(R.string.meet_people);
            String message = getResources().getString(
                R.string.meetpeople_not_found_by_search);
            AlertDialog confirmDialog = new CustomConfirmDialog(
                getActivity(), title, message, true)
                .setPositiveButton(0, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
                        .getInstance(mAppContext).getResponse();
                    gotoSearchSetting(settingResponse);
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
        }
      }
      isMoreAvailable = meetPeopleResponse.isMoreAvaiable();
      if (isMoreAvailable) {
        mpPullView.setMode(Mode.BOTH);
        Resources resource = getResources();
        mpPullView.setPullLabelFooter(resource
            .getString(R.string.pull_to_load_more_pull_label));
        mpPullView.setReleaseLabelFooter(resource
            .getString(R.string.pull_to_load_more_release_label));
      } else {
        mpPullView.setMode(Mode.PULL_FROM_START);
      }

      mMeetPeopleAdapter.appendList(meetPeopleResponse.getPeoples());

//            List<MeetPeople> listMeetPeople = meetPeopleResponse.getPeoples();
//            int delta = listMeetPeople.size() % mGridPeople.getNumColumns();
//            for (int i = 0; i < delta; i++) {
//                int size = listMeetPeople.size();
//                if (size > 0) {
//                    listMeetPeople.remove(size - 1);
//                }
//            }
//            mMeetPeopleAdapter.appendList(listMeetPeople);

      if (mMeetPeopleAdapter.getCount() == 0) {
        txtEmpty.setText(R.string.no_more_items_to_show);
        getLoaderManager().destroyLoader(loader.getId());
      }
      if (isRestartMeetPeople) {
        if (mMeetPeopleAdapter.getCount() > 0) {
          mGridPeople.smoothScrollToPosition(0);
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
    } else if (response instanceof ListNewsResponse) {
      handleListNewResponse((ListNewsResponse) response);
      getLoaderManager().destroyLoader(LOADER_GET_NEWS);
    } else if (response instanceof GetApplicationInfoResponse) {
      onResponseApplicationInfo((GetApplicationInfoResponse) response);
      // don't call again on resume
      getLoaderManager().destroyLoader(LOADER_APPLICATION_INFO);
    }
    LogUtils.d(TAG, "receiveResponse Ended (3)");
  }

  private void onResponseApplicationInfo(GetApplicationInfoResponse response) {
    // only request list news if BE setting allow show news
    if (response.getCode() == Response.SERVER_SUCCESS && response.isShowNews()) {

      requestGetListNews();
      // copy current follow
//            if (finishRegisterFlag == Constants.FINISH_REGISTER_YES) {
//                UserPreferences preferences = UserPreferences.getInstance();
//                controlShowAlertDialog(preferences.getGender());
//                requestGetListNews();
//                finishRegisterFlag = Constants.FINISH_REGISTER_NO;
//            } else {
//                if (!UserPreferences.getInstance().isNewlyAccount()) {
//                    requestGetListNews();
//                }
//            }
    }
  }

  private void handleListNewResponse(ListNewsResponse listNewsResponse) {
    int code = listNewsResponse.getCode();

    switch (code) {
      case Response.SERVER_SUCCESS:
        //hiepuh
        News news = listNewsResponse.getCmcode();
        int checkcm = news.getHaveCMcode();
        // 0: user dont have cmcode
        // 1: user has cmcode
        if (checkcm == 0) {

          final List<News> mNewsList = listNewsResponse.getNewsList();
          if (mNewsList != null && !mNewsList.isEmpty()) {
            showPopupNews(mNewsList);
          } else {
            FragmentActivity fa = getActivity();
            if (fa != null && fa instanceof MainActivity) {
              UserPreferences userPreferences = UserPreferences.getInstance();
              boolean showNewNotifications = userPreferences
                  .getShowNewNotifications();

              if (showNewNotifications) {
//                             Reset flag to show only once in one login session
                userPreferences.setShowNewNotifications(false);

                mNewNotificationsDialog = new NewNotificationsDialog(fa);
                mNewNotificationsDialog.mTotalNewNotifications = userPreferences
                    .getNumberNotification();
                LogUtils.e(TAG, "Number of notification: "
                    + mNewNotificationsDialog.mTotalNewNotifications);

                boolean isSignUp = userPreferences.isNewlyAccount();
                if (mNewNotificationsDialog.mTotalNewNotifications > 0
                    && !isSignUp) {
                  mNewNotificationsDialog.show();

                  // Load Avatar
                  ImageView ivAvatar = (ImageView) mNewNotificationsDialog
                      .findViewById(R.id.iv_fragment_new_notifications_avatar);
                  CircleImageRequest imageRequest = new CircleImageRequest(
                      UserPreferences.getInstance().getToken(),
                      userPreferences.getAvaId());
                  getImageFetcher().loadImageByGender(imageRequest, ivAvatar,
                      ivAvatar.getWidth(), userPreferences.getGender());

                  Button btnOk = (Button) mNewNotificationsDialog
                      .findViewById(R.id.bt_fragment_new_notifications_done);
                  btnOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (mNewNotificationsDialog.isShowing()) {
                        mNewNotificationsDialog.dismiss();
                      }
                    }
                  });

                  // Set OnClickListener event
                  RelativeLayout rlNewNotifications = (RelativeLayout) mNewNotificationsDialog
                      .findViewById(R.id.rl_fragment_new_notifications_total);
                  rlNewNotifications.setTag(fa);
                  rlNewNotifications
                      .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          // Close NewNotificationsDialog
                          mNewNotificationsDialog.dismiss();

                          Object tag = v.getTag();
                          // Navigate to Notification screen
                          if (tag != null
                              && tag instanceof MainActivity) {
                            NotificationFragment nf = new NotificationFragment();
                            ((MainActivity) tag)
                                .replaceAllFragment(
                                    nf,
                                    MainActivity.TAG_FRAGMENT_NOTIFICATION);
                          }
                        }
                      });
                }
                UserPreferences.getInstance().setIsNewlyAccount(false);
              }
            }
          }
        } else {
          if (Constants.checkopenURLcmcode) {
            final String url = news.getUrl();
            if (url.equals("") || url == null) {
            } else {
              if (url.contains("http") || url.contains("https")) {
                if (getActivity() instanceof MainActivity) {
                  mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                      ((MainActivity) getActivity()).handleMenuAddUrl(
                          MainActivity.FUNCTION_URL_CMCODE, true, url);
                    }
                  });
                }
              }
            }
          }
          Constants.checkopenURLcmcode = false;
        }
        break;
      default:
        int error = ErrorString.getDescriptionOfErrorCode(code);
        LogUtils.d("HungHN", "Load News error: " + code);
        break;
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    LogUtils.d(TAG, "parseResponse Started");
    Response response;
    if (loaderID == LOADER_GET_MEET_PEOPLE_SETTING) {
      LogUtils.i(TAG, "parseResponse MP Setting is called");
      response = new MeetPeopleSettingResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_UNLOCK) {
      response = new CheckUnlockResponse(data);
    } else if (loaderID == LOADER_GET_ATTENDTION_NUMBER) {
      response = new GetAttendtionNumberResponse(data);
    } else if (loaderID == LOADER_GET_NEWS) {
      response = new ListNewsResponse(data);
    } else if (loaderID == LOADER_APPLICATION_INFO) {
      response = new GetApplicationInfoResponse(data);
    } else {
      response = new MeetPeopleResponse(data);
    }
    LogUtils.d(TAG, "parseResponse Ended");
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  public void onClick(View v) {
    switch (v.getId()) {
//            case R.id.collection_button_meetpeople_grid_list:
//                if (mMeetPeopleAdapter.getCount() != 0) {
//                    showGridOrList();
//                }
//                break;
//            case R.id.all_layout:
//                MeetPeopleSetting.getInstance(mAppContext).saveFilter(
//                        MeetPeopleSetting.FILTER_ALL);
//                updateViewGroupFilter(MeetPeopleSetting.FILTER_ALL);
//                restartRequestListPeople();
//                break;
//            case R.id.call_layout:
//                MeetPeopleSetting.getInstance(mAppContext).saveFilter(
//                        MeetPeopleSetting.FILTER_CALL_WAITING);
//                updateViewGroupFilter(MeetPeopleSetting.FILTER_CALL_WAITING);
//                restartRequestListPeople();
//                break;
//            case R.id.register_layout:
//                MeetPeopleSetting.getInstance(mAppContext).saveFilter(MeetPeopleSetting.FILTER_NEW_REGISTER);
//                updateViewGroupFilter(MeetPeopleSetting.FILTER_NEW_REGISTER);
//                restartRequestListPeople();
//                break;
      case R.id.ll_switcher_list:
      case R.id.ll_switcher_grid:
        if (mMeetPeopleAdapter.getCount() != 0) {
          showGridOrList();
        }
        break;
      case R.id.btnSearch:
        MeetPeopleSettingResponse settingResponse = MeetPeopleSetting
            .getInstance(mAppContext).getResponse();
        gotoSearchSetting(settingResponse);
        break;
      default:
        break;
    }
  }

  private void gotoSearchSetting(MeetPeopleSettingResponse settingResponse) {
    if (settingResponse == null) {
      return;
    }
    SearchSettingFragment settingFragment = SearchSettingFragment
        .newInstance(settingResponse);
    settingFragment.setTargetFragment(getRootParentFragment(this), REQUEST_SETTING_CODE);
    mNavigationManager.addPage(settingFragment);
  }

  private void gotoFavorites() {
    ConnectionFragment fragment = ConnectionFragment.newInstance(false,
        ConnectionFragment.TAB_WHO_INDEX);
    replaceFragment(fragment, MainActivity.TAG_FRAGMENT_CONNECTION);
  }

  private void showGridOrList() {
    //hiepuh
    isList = Preferences.getInstance().getMeetPeopleListType();
    if (!isList) {
      isList = true;
      mGridPeople.setNumColumns(1);
      mGridPeople.setVerticalSpacing(0);
      mMeetPeopleAdapter.displayInList(true);
    } else {
      isList = false;
      mGridPeople.setNumColumns(mMeetPeopleAdapter.getNumColumns());
      mGridPeople.setVerticalSpacing((int) getResources().getDimension(
          R.dimen.meetpeople_columm_space));
      mGridPeople.setHorizontalSpacing((int) getResources().getDimension(
          R.dimen.meetpeople_columm_space));
      mMeetPeopleAdapter.displayInList(false);
    }
    initListGridSwitcher();
    Preferences.getInstance().saveMeetPeopleListType(isList);
    //end
  }

  @Override
  public void onItemClick(AdapterView<?> parents, View view, int position,
      long id) {
    Object object = parents.getItemAtPosition(position);
    if (object instanceof MeetPeople) {
      MeetPeople meetPeople = (MeetPeople) object;
//            replaceFragment(SliderProfileFragment
//                    .newInstance(meetPeople.getUserId(),
//                            (ArrayList<MeetPeople>) mMeetPeopleAdapter
//                                    .getListPeoples(), MeetPeopleSetting
//                                    .getInstance(mAppContext).getResponse(),
//                            isMoreAvailable));
      //hiepuh
      replaceFragment(SliderProfileFragment.newInstance(
          meetPeople.getUserId(),
          meetPeople.getAva_id(),
          meetPeople.getUser_name(),
          meetPeople.getGender(),
          false, false, true,
          (ArrayList<MeetPeople>) mMeetPeopleAdapter.getListPeoples(),
          MeetPeopleSetting.getInstance(mAppContext).getResponse(),
          isMoreAvailable));
      //end
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    if (mMeetPeopleAdapter != null) {
      mMeetPeopleAdapter.clearAllData();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (mUnlockDialog != null && mUnlockDialog.isShowing()) {
      mUnlockDialog.dismiss();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    super.onLocationChanged(location);
    requestFirstListPeople();
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == REQUEST_SETTING_CODE
        && resultCode == Activity.RESULT_OK) {
      // requestSettingRequest();
      mMeetPeopleAdapter.clearAllData();
      getLoaderManager().destroyLoader(LOADER_GET_MEET_PEOPLE_SETTING);
      getLoaderManager().destroyLoader(LOADER_ID_CHECK_UNLOCK);
      getLoaderManager().destroyLoader(LOADER_MEETPEOPLE);
      restartRequestListPeople();
    }
  }

  private void createViewGroupFilter(View rootView) {
//        mFilterAll = rootView.findViewById(R.id.all_layout);
//        mFilterAll.setOnClickListener(this);
//        mFilterRegister = rootView.findViewById(R.id.register_layout);
//        mFilterRegister.setOnClickListener(this);
//        mFilterCallWaiting = rootView.findViewById(R.id.call_layout);
//        mFilterCallWaiting.setOnClickListener(this);
//        mAllCheckedImgView = (ImageView) rootView
//                .findViewById(R.id.all_checked_img);
//        mRegisterImgView = (ImageView) rootView
//                .findViewById(R.id.register_checked_img);
//        mCallImgView = (ImageView) rootView.findViewById(R.id.call_checked_img);
  }

  private void updateViewGroupFilter(int filter) {
//        int transparent = getResources().getColor(android.R.color.transparent);
//        if (filter == MeetPeopleSetting.FILTER_ALL) {
//            mFilterAll.setBackgroundResource(R.drawable.radio_three_checked);
//            mAllCheckedImgView.setVisibility(View.VISIBLE);
//        } else {
//            mFilterAll.setBackgroundColor(transparent);
//            mAllCheckedImgView.setVisibility(View.GONE);
//        }
//
//        if (filter == MeetPeopleSetting.FILTER_NEW_REGISTER) {
//            mFilterRegister.setBackgroundResource(R.drawable.radio_two_checked);
//            mRegisterImgView.setVisibility(View.VISIBLE);
//        } else {
//            mFilterRegister.setBackgroundColor(transparent);
//            mRegisterImgView.setVisibility(View.GONE);
//        }
//
//        if (filter == MeetPeopleSetting.FILTER_CALL_WAITING) {
//            mFilterCallWaiting.setBackgroundResource(R.drawable.radio_one_checked);
//            mCallImgView.setVisibility(View.VISIBLE);
//        } else {
//            mFilterCallWaiting.setBackgroundColor(transparent);
//            mCallImgView.setVisibility(View.GONE);
//        }

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
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

  public void onEvent(ConversationChangeEvent event) {
    if (event == null) {
      return;
    }
    List<ConversationItem> items = event.getConversationList();
    if (items == null) {
      return;
    }
    for (ConversationItem item : items) {
      mMeetPeopleAdapter.updateUnreadMessage(
          item.getFriendId(), item.getUnreadNum());
    }
  }

  private void showDialogHowToUseForMale() {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), true);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.flow_reg_dialog_how_use_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.flow_reg_dialog_how_use_title);
    builder.setMessage(R.string.flow_reg_dialog_how_use_message_male);
    builder.setPositiveButton(R.string.common_yes,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            if (getActivity() instanceof MainActivity) {
              ((MainActivity) getActivity()).handleMenu(
                  MainActivity.FUNCTION_HOW_TO_USE, true);
            }
          }
        });

    builder.setNegativeButton(R.string.common_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            showDialogConfirmHowToUse();
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
  }

  private void showDialogConfirmHowToUse() {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), false);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.flow_reg_dialog_how_use_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.flow_reg_dialog_how_use_title);
    builder.setMessage(R.string.flow_reg_dialog_confirm_how_use_message);
    builder.setPositiveButton(R.string.common_ok,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
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
  }

  //hiepuh
  private void initFilterSpinner() {
    List<String> dataset = new LinkedList<>(Arrays.asList(
        getString(R.string.sort_by_all),
        getString(R.string.sort_by_new_reg),
        getString(R.string.sort_by_call_waiting)));
    spnFilter.attachDataSource(dataset);
    spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        if (getActivity() instanceof MainActivity) {
          ((MainActivity) getActivity()).selectedIndex = position;
        }
        onFilterSelected(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void initListGridSwitcher() {
    if (isList) {
      llGridSwitcher.setSelected(false);
      llListSwitcher.setSelected(true);

      if (llListSwitcher.isSelected()) {
        mButtonList.setColorFilter(ContextCompat.getColor(mAppContext, R.color.white));
        mButtonGird.setColorFilter(ContextCompat.getColor(mAppContext, R.color.white));
      }

    } else {
      llListSwitcher.setSelected(false);
      llGridSwitcher.setSelected(true);

      if (llGridSwitcher.isSelected()) {
        mButtonGird.setColorFilter(ContextCompat.getColor(mAppContext, R.color.white));
        mButtonList.setColorFilter(ContextCompat.getColor(mAppContext, R.color.white));
      }
    }
  }

  private void onFilterSelected(int sid) {
    switch (sid) {
      case 0:
        MeetPeopleSetting.getInstance(mAppContext).saveFilter(
            MeetPeopleSetting.FILTER_ALL);
        restartRequestListPeople();
        break;
      case 1:
        MeetPeopleSetting.getInstance(mAppContext).saveFilter(
            MeetPeopleSetting.FILTER_NEW_REGISTER);
        restartRequestListPeople();
        break;
      case 2:
        MeetPeopleSetting.getInstance(mAppContext).saveFilter(
            MeetPeopleSetting.FILTER_CALL_WAITING);
        restartRequestListPeople();
        break;
      default:
        break;
    }
  }

  //end
  private void showPopupNews(List<News> newsList) {
    // Check whether or not show New Notifications
    FragmentActivity fa = getActivity();
    if (fa != null && fa instanceof MainActivity) {
      final String userId = UserPreferences.getInstance().getUserId();
      new NewsDialog(getContext(), newsList) {
        @Override
        protected void onSaveDontShowNewsToday() {
          NewsPreference.saveTimeSettingNews(getContext(), userId,
              NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE, System.currentTimeMillis());
        }
      }.show();

      NewsPreference
          .setShowNews(getContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE, false);
    }
  }

  public interface UpdateLocation {

    public void updateLocation(Location location);
  }

  private class MeetPeopleAdapter extends BaseAdapter {

    private static final int NUM_TYPE = 2;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_GRID = 1;
    private List<MeetPeople> mlistPeople;
    private Context mContext;
    private int mAvatarSize;
    private int mNumColumns = 0;
    private int mItemHeight = 0;
    private GridView.LayoutParams layoutParams;
    /**
     * Determine type of gridview: 1 column or n column
     */
    private boolean isList = false;

    public MeetPeopleAdapter(Context context, List<MeetPeople> listPeople) {
      mlistPeople = listPeople;
      mContext = context;
      layoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT);
      mAvatarSize = getResources().getDimensionPixelSize(
          R.dimen.activity_setupprofile_img_avatar_width);
    }

    @Override
    public int getCount() {
      return mlistPeople.size();
    }

    @Override
    public Object getItem(int position) {
      return mlistPeople.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public void displayInList(boolean value) {
      LogUtils.i(TAG, "Display in list");
      isList = value;
      if (mlistPeople != null && mContext != null) {
        notifyDataSetChanged();
      }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (getItemViewType(position) == TYPE_LIST) {
        return getViewForList(position, convertView, parent);
      } else {
        return getViewForGrid(position, convertView, parent);
      }
    }

    public View getViewForGrid(int position, View convertView, ViewGroup parent) {
      ViewHolderForGrid holder = null;
      MeetPeople meetPeople = mlistPeople.get(position);
      if (convertView == null
          || !(convertView.getTag() instanceof ViewHolderForGrid)) {
        convertView = View.inflate(mContext,
            R.layout.item_grid_meetpeople, null);
        holder = new ViewHolderForGrid();
        holder.imgAvatar = (ImageView) convertView
            .findViewById(R.id.item_grid_meetpeople_img_avatar);
        holder.imgStatus = (ImageView) convertView
            .findViewById(R.id.item_grid_meetpeople_img_status);
        holder.txtName = (TextView) convertView
            .findViewById(R.id.item_grid_meetpeople_txt_name);
        holder.frameLayout = (FrameLayout) convertView
            .findViewById(R.id.item_grid_meetpeople_layout_frm);
        holder.frameLayout.setLayoutParams(layoutParams);
        holder.tvNotification = (BadgeTextView) convertView
            .findViewById(R.id.item_grid_meetpeople_txt_notification);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolderForGrid) convertView.getTag();
      }

      if (meetPeople.isVideoCallWaiting()) {
        holder.imgStatus.setImageResource(R.drawable.ic_online_video);
      } else if (meetPeople.isVoiceCallWaiting()) {
        holder.imgStatus.setImageResource(R.drawable.ic_online_voice);
      } else {
        holder.imgStatus.setImageResource(R.drawable.ic_online_chat);
      }

      holder.txtName.setText(mlistPeople.get(position).getUser_name());
      String token = UserPreferences.getInstance().getToken();
      // load avatar that has cover by gender
      ImageRequest imageRequest = new ImageRequest(token,
          meetPeople.getAva_id(), ImageRequest.THUMBNAIL);
      int res;
      if (meetPeople.getGender() == Constants.GENDER_TYPE_MAN) {
        res = R.drawable.dummy_avatar_male;
      } else {
        res = R.drawable.dummy_avatar_female;
      }
      Picasso.with(mAppContext).load(imageRequest.toURL())
          .placeholder(res).into(holder.imgAvatar);

      if (holder.frameLayout.getLayoutParams().height != mItemHeight) {
        holder.frameLayout.setLayoutParams(layoutParams);
      }
      holder.tvNotification.setTextNumber(meetPeople.getUnreadNum());
      return convertView;
    }

    public View getViewForList(int position, View convertView,
        ViewGroup parent) {
      ViewHolderForList holder = null;
      if (convertView == null
          || !(convertView.getTag() instanceof ViewHolderForList)) {
        holder = new ViewHolderForList();
        convertView = View.inflate(mContext,
            R.layout.item_list_meetpeople, null);
        holder.imgAvatar = (ImageView) convertView
            .findViewById(R.id.item_list_connection_common_img_avatar);
        holder.txtName = (TrimmedTextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_name);
        // holder.txtAge = (TextView) convertView
        // .findViewById(R.id.item_list_connection_common_txt_age);
        holder.txtLocation = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_location);
        holder.txtTime = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_time);
        holder.txtStatus = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_status);
        holder.tvNotification = (BadgeTextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_notification);
        holder.imgState = (ImageView) convertView
            .findViewById(R.id.item_list_connection_common_img_status);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolderForList) convertView.getTag();
      }
      MeetPeople meetPeople = mlistPeople.get(position);

      try {
        Calendar calendarNow = Calendar.getInstance();
        Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateSend = Utility.YYYYMMDDHHMMSS.parse(meetPeople
            .getLastLogin());
        Calendar calendarSend = Calendar.getInstance(TimeZone
            .getDefault());
        calendarSend.setTime(dateSend);

        holder.txtTime.setText(Utility.getDifference(mContext,
            calendarSend, calendarNow));
      } catch (ParseException e) {
        e.printStackTrace();
        holder.txtTime.setText(R.string.common_now);
      }

      // load avatar that has cover by gender
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token,
          meetPeople.getAva_id());
      getImageFetcher().loadImageByGender(imageRequest, holder.imgAvatar,
          mAvatarSize, meetPeople.getGender());

      // set about
      String about = meetPeople.getAbout();
      if (!TextUtils.isEmpty(about)) {
        holder.txtStatus.setText(about);
        holder.txtStatus.setVisibility(View.VISIBLE);
      } else {
        holder.txtStatus.setVisibility(View.GONE);
      }

      // set location
      holder.txtLocation.setText(mRegionUtils.getRegionName(meetPeople
          .getRegion()));

      // set state
      if (meetPeople.isVideoCallWaiting()) {
        holder.imgState.setImageResource(R.drawable.ic_online_video);
      } else if (meetPeople.isVoiceCallWaiting()) {
        holder.imgState.setImageResource(R.drawable.ic_online_voice);
      } else {
        holder.imgState.setImageResource(R.drawable.ic_online_chat);
      }

      // set name and age
      String age = meetPeople.getAge() + getString(R.string.yo);
      String name = mlistPeople.get(position).getUser_name();
      SpannableStringBuilder text = new SpannableStringBuilder();
      text.append(TrimmedTextView.ellipsizeText(name));
      text.append(" " + age);
      holder.txtName.setTextSpanned(text);
      holder.tvNotification.setTextNumber(meetPeople.getUnreadNum());
      return convertView;
    }

    public void setItemHeight(int height) {
      if (height == mItemHeight) {
        return;
      }
      mItemHeight = height;
      layoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT,
          mItemHeight);
      notifyDataSetChanged();
    }

    public int getNumColumns() {
      return mNumColumns;
    }

    public void setNumColumns(int numColumns) {
      mNumColumns = numColumns;
    }

    @Override
    public int getViewTypeCount() {
      return NUM_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
      if (isList) {
        return TYPE_LIST;
      } else {
        return TYPE_GRID;
      }
    }

    /**
     * Remove all elements from this adapter, leaving it empty
     */
    public void clearAllData() {
      if (mlistPeople != null) {
        mlistPeople.clear();
        this.notifyDataSetChanged();
      }
    }

    public void appendList(List<MeetPeople> peoples) {
      mlistPeople = peoples;
      this.notifyDataSetChanged();
    }

    public List<MeetPeople> getListPeoples() {
      return mlistPeople;
    }

    public void updateUnreadMessage(String userId, int unreadNum) {
      for (MeetPeople meetPeople : mlistPeople) {
        if (meetPeople.getUserId().equals(userId)) {
          meetPeople.setUnreadNum(unreadNum);
          notifyDataSetChanged();
          break;
        }
      }
    }

    private class ViewHolderForGrid {

      public TextView txtName;
      public ImageView imgAvatar;
      public ImageView imgStatus;
      public FrameLayout frameLayout;
      public BadgeTextView tvNotification;
    }

    private class ViewHolderForList {

      public ImageView imgAvatar;
      public TrimmedTextView txtName;
      public TextView txtStatus;
      public TextView txtLocation;
      public TextView txtTime;
      public BadgeTextView tvNotification;
      public ImageView imgState;
    }
  }
}
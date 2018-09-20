package com.application.ui.connection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.application.call.LinphoneVideoCall;
import com.application.call.LinphoneVoiceCall;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CheckCallRequest;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.CheckCallResponse;
import com.application.connection.response.ConnectionResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.entity.PeopleConnection;
import com.application.ui.BaseFragment;
import com.application.ui.connection.ConnectionFragment.OnChangeItem;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @author tungdx
 */
public abstract class BasePeopleListFragment extends BaseFragment implements
    ResponseReceiver {

  protected static final String TAG = "CommonFragment";
  private static final int LOADER_ID_LOAD_PEOPLE_LIST = 100;
  private static final int LOADER_ID_LOAD_MORE = 101;
  private static final int LOADER_ID_CHECK_CALL_VIDEO = 102;
  private static final int LOADER_ID_CHECK_CALL_VOICE = 103;
  private static final int LOADER_ID_BASIC_USER_INFO_CALL = 104;
  public ListType mListType = ListType.NONE;
  protected PeopleListAdapter mConnectionCommonAdapter;
  protected Context mAppContext;
  private int mCurrentCallType = Constants.CALL_TYPE_VOICE;
  private int mAvatarSize;
  // Amount item is get when load data
  private int mTake;
  // Initital position get data
  private int mFirstSkip = 0;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mlistPeople;
  private TextView mTextViewEmpty;
  private View mEmptyDataView;
  private int titleNumber = 0;
  private CallUserInfo callUserInfo;
  private ProgressDialog progressDialog;
  private OnChangeItem mOnChangeItem;
  private OnUserClickListener mOnUserClickListener = new OnUserClickListener() {

    @Override
    public void onUserClick(String userId) {
      replaceFragment(MyProfileFragment.newInstance(userId),
          MyProfileFragment.TAG_FRAGMENT_USER_PROFILE);
    }
  };
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      onRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      startRequestServer(LOADER_ID_LOAD_PEOPLE_LIST,
          mConnectionCommonAdapter.getCount(), mTake);
      if (mTextViewEmpty != null) {
        mTextViewEmpty.setVisibility(View.INVISIBLE);
      }

    }
  };

  public OnChangeItem getOnChangeItem() {
    return mOnChangeItem;
  }

  public void setOnChangeItem(OnChangeItem changeItem) {
    mOnChangeItem = changeItem;
  }

  protected abstract RequestParams getRequestParams(int take, int skip);

  @Override
  public void onAttach(android.app.Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplicationContext();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTake = getResources().getInteger(R.integer.take_people_list);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_connection_common,
        container, false);

    mEmptyDataView = inflater.inflate(
        R.layout.item_list_connection_common_empty, null);

    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initImageFetcher();
    mConnectionCommonAdapter = new PeopleListAdapter(getActivity());
    mlistPeople.setAdapter(mConnectionCommonAdapter);
    mlistPeople.setDividerHeight(0);
    mlistPeople.setCacheColorHint(Color.TRANSPARENT);
    startRequestServer(LOADER_ID_LOAD_PEOPLE_LIST, mFirstSkip, mTake);
  }

  protected ListView getListView() {
    return this.mlistPeople;
  }

  /**
   * Request server by tab -> request type
   */
  private void startRequestServer(int idloader, int skip, int take) {
    restartRequestServer(idloader, getRequestParams(take, skip));
  }

  private void initView(View view) {
    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.fragment_connection_common_grid);

    mlistPeople = mPullToRefreshListView.getRefreshableView();
    // Set Header View for listPeople
    if (getHeaderListView() != null) {
      View headerView = LayoutInflater.from(mAppContext).inflate(
          R.layout.header_list_view, null);
      TextView txt = (TextView) headerView.findViewById(R.id.txt_header);
      txt.setText(getHeaderListView());
      mlistPeople.addHeaderView(headerView);
    }

    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);
    mlistPeople.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object instanceof PeopleConnection) {
          PeopleConnection peopleConnection = (PeopleConnection) object;
          if (Utility.isBlockedWithUser(mAppContext,
              peopleConnection.getUserId())) {
            // Show toast message
            Utility.showToastMessage(mAppContext,
                getString(R.string.action_is_not_performed));

            // Remove this item from list
            mConnectionCommonAdapter.removeItem(peopleConnection
                .getUserId());

            // Update number of connections
            onRemoveItem();
          } else {
            mOnUserClickListener.onUserClick(peopleConnection
                .getUserId());
          }
        }
      }
    });
    mlistPeople.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Pause fetcher to ensure smoother scrolling when flinging
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
          getImageFetcher().setPauseWork(true);
        } else {
          getImageFetcher().setPauseWork(false);
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    });
  }

  /**
   * SetUp ImageFetcher to load avatar
   */
  private void initImageFetcher() {
    mAvatarSize = getResources().getDimensionPixelSize(
        R.dimen.activity_setupprofile_img_avatar_height);
  }

  /**
   * Get empty view when loading data
   *
   * @return view
   */
  protected View getEmptyViewForLoading() {
    if (mTextViewEmpty == null) {
      mTextViewEmpty = new TextView(mAppContext);
      mTextViewEmpty.setText(R.string.common_loading);
      mTextViewEmpty.setTextColor(Color.BLACK);
      mTextViewEmpty.setGravity(Gravity.CENTER);
    }
    mTextViewEmpty.setVisibility(View.VISIBLE);
    return mTextViewEmpty;
  }

  protected View getEmptyViewWhenEmptyData() {
    if (mTextViewEmpty != null) {
      mTextViewEmpty.setVisibility(View.GONE);
    }

    return mEmptyDataView;
  }

  @Override
  public void onStop() {
    super.onStop();
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  protected PullToRefreshListView getPullToRefreshListView() {
    return mPullToRefreshListView;
  }

  protected abstract RequestParams getRequestParamsLoadMore();

  private void restartRequestBasicUserInfo() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, callUserInfo.getUserId());
    restartRequestServer(LOADER_ID_BASIC_USER_INFO_CALL, request);
  }

  private boolean handlerCheckRequestCall(GetBasicInfoResponse response) {
    UserPreferences preferences = UserPreferences.getInstance();
    String currentUserId = preferences.getUserId();
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

  private void appendListPeople(ConnectionResponse connectionResponse) {
    if (connectionResponse.getListPeople() != null) {
      int preCount = mConnectionCommonAdapter.getCount();
      mConnectionCommonAdapter.appendList(connectionResponse
          .getListPeople());
      // if no any people -> set empty when no data
      if (connectionResponse.getListPeople().size() == 0
          && mConnectionCommonAdapter.getCount() == 0) {
        mPullToRefreshListView
            .setEmptyView(getEmptyViewWhenEmptyData());
      } else {
        getEmptyViewWhenEmptyData().setVisibility(View.GONE);
        mPullToRefreshListView.removeView(getEmptyViewWhenEmptyData());
      }
      mConnectionCommonAdapter.notifyDataSetChanged();

      int count = mConnectionCommonAdapter.getCount();
      if (count - preCount <= 0) {
        updateTitle(count);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        onRefreshCompleted(mConnectionCommonAdapter.getListPeople(),
            mConnectionCommonAdapter.getCount());
      } else {
        if (count > titleNumber) {
          updateTitle(count);
          mPullToRefreshListView.setMode(Mode.BOTH);
        }
        Resources resource = getResources();
        mPullToRefreshListView.setPullLabelFooter(resource
            .getString(R.string.pull_to_load_more_pull_label));
        mPullToRefreshListView.setReleaseLabelFooter(resource
            .getString(R.string.pull_to_load_more_release_label));
      }
    } else {
      if (mConnectionCommonAdapter.getCount() == 0) {
        mPullToRefreshListView
            .setEmptyView(getEmptyViewWhenEmptyData());
      } else {
        getEmptyViewWhenEmptyData().setVisibility(View.GONE);
        mPullToRefreshListView.removeView(getEmptyViewWhenEmptyData());
      }

      mPullToRefreshListView.setMode(Mode.BOTH);
      Resources resource = getResources();
      mPullToRefreshListView.setPullLabelFooter(resource
          .getString(R.string.pull_to_load_more_pull_label));
      mPullToRefreshListView.setReleaseLabelFooter(resource
          .getString(R.string.pull_to_load_more_release_label));
    }
  }

  /**
   * handle list people when receive data from server
   *
   * @param connectionResponse response from server
   */
  private void handleListPeople(ConnectionResponse connectionResponse) {
    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.onRefreshComplete();
    }
    if (connectionResponse.getCode() == Response.SERVER_SUCCESS) {
      appendListPeople(connectionResponse);
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

  public int getNumItemInAdapter() {
    if (mConnectionCommonAdapter != null) {
      return mConnectionCommonAdapter.getCount();
    }
    return 0;
  }

  @Override
  public void startRequest(int loaderId) {
    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.removeView(getEmptyViewWhenEmptyData());
      mPullToRefreshListView.setEmptyView(getEmptyViewForLoading());
    }
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage(getString(R.string.waiting));
    if (loaderId == LOADER_ID_CHECK_CALL_VIDEO
        || loaderId == LOADER_ID_CHECK_CALL_VOICE) {
      progressDialog.show();
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    LogUtils.e(TAG, "receiveResponse");
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    if (getActivity() == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    if (response == null) {
      return;
    }
    int loaderId = loader.getId();
    switch (loaderId) {
      case LOADER_ID_LOAD_PEOPLE_LIST:
        handleListPeople((ConnectionResponse) response);
        break;
      case LOADER_ID_CHECK_CALL_VOICE:
        handleCheckCall(false, (CheckCallResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VOICE);
        break;
      case LOADER_ID_CHECK_CALL_VIDEO:
        handleCheckCall(true, (CheckCallResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VIDEO);
        break;
      case LOADER_ID_BASIC_USER_INFO_CALL:
        handlerCheckRequestCall((GetBasicInfoResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_BASIC_USER_INFO_CALL);
        break;
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_LOAD_PEOPLE_LIST) {
      LogUtils.e(TAG, "parseResponse");
      return new ConnectionResponse(data);
    } else if (loaderID == LOADER_ID_LOAD_MORE) {
      return loadMoreResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_CALL_VIDEO
        || loaderID == LOADER_ID_CHECK_CALL_VOICE) {
      return new CheckCallResponse(data);
    } else if (loaderID == LOADER_ID_BASIC_USER_INFO_CALL) {
      return new GetBasicInfoResponse(mAppContext, data);
    } else {
      return null;
    }
  }

  protected abstract Response loadMoreResponse(ResponseData data);

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
    // Do nothing
  }

  protected abstract void onRefreshCompleted(
      List<PeopleConnection> peopleConnections, int newItems);

  protected abstract void onRemoveItem();

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  protected void updateTitle(int count) {
    titleNumber = count;
  }

  protected abstract String getHeaderListView();

  protected void onRefresh() {
    mPullToRefreshListView.setMode(Mode.BOTH);
    if (mConnectionCommonAdapter != null) {
      mConnectionCommonAdapter.clearAllData();
    }
    startRequestServer(LOADER_ID_LOAD_PEOPLE_LIST, mFirstSkip, mTake);
    if (mTextViewEmpty != null) {
      mTextViewEmpty.setVisibility(View.VISIBLE);
      getEmptyViewWhenEmptyData().setVisibility(View.GONE);
    }
    if (getRequestParamsLoadMore() != null) {
      restartRequestServer(LOADER_ID_LOAD_MORE,
          getRequestParamsLoadMore());
    }
  }

  public enum ListType {
    NONE, WHO_CHECKS_OUT_ME, WHO_FAVORITES_ME, MY_FAVORITE_LIST, MY_FRIENDS_LIST, FRIENDS_OF_MY_FRIEND
  }

  public interface OnUserClickListener {

    public void onUserClick(String userId);
  }

  static class ViewHolder {

    public View viewLineTop;
    public ImageView imgAvatar;
    // public ImageView imgStatus;
    public TextView txtName;
    public TextView txtTime;
    public TextView txtCheckOutTime;
    public ImageView imgVoiceCall;
    public ImageView imgVideoCall;
  }

  protected class PeopleListAdapter extends BaseAdapter {

    private List<PeopleConnection> mlistPeople = new ArrayList<PeopleConnection>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat checkOutTimeFormat;

    public PeopleListAdapter(Context context) {
      mContext = context;
      mLayoutInflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      checkOutTimeFormat = new SimpleDateFormat(mContext.getResources()
          .getString(R.string.format_checkout_time), Locale.US);
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

    public List<PeopleConnection> getListPeople() {
      return mlistPeople;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(final int position, View convertView,
        ViewGroup parent) {
      ViewHolder holder = null;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mLayoutInflater.inflate(
            R.layout.item_list_connection_common, parent, false);
        holder.viewLineTop = convertView.findViewById(R.id.line_top);
        holder.imgAvatar = (ImageView) convertView
            .findViewById(R.id.avatar);
        // holder.imgStatus = (ImageView) convertView
        // .findViewById(R.id.status);
        holder.txtName = (TextView) convertView
            .findViewById(R.id.user_name);
        holder.txtTime = (TextView) convertView.findViewById(R.id.time);
        holder.imgVoiceCall = (ImageView) convertView
            .findViewById(R.id.voice_call);
        holder.imgVideoCall = (ImageView) convertView
            .findViewById(R.id.video_call);
        holder.txtCheckOutTime = (TextView) convertView
            .findViewById(R.id.checkout_time);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }
      // Show top line
      if (position == 0) {
        holder.viewLineTop.setVisibility(View.VISIBLE);
      }
      final PeopleConnection peopleConnection = mlistPeople.get(position);
      // Set user name
      holder.txtName.setText(peopleConnection.getUserName());
      // Set user avata
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token,
          peopleConnection.getAvaId());
      getImageFetcher().loadImageByGender(imageRequest, holder.imgAvatar,
          mAvatarSize, peopleConnection.getGender());
      holder.imgAvatar.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mOnUserClickListener.onUserClick(peopleConnection
              .getUserId());
        }
      });

      try {
        Calendar calendarNow = Calendar.getInstance();
        Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateSend = Utility.YYYYMMDDHHMMSS.parse(peopleConnection
            .getLastLogin());
        Calendar calendarSend = Calendar.getInstance(TimeZone
            .getDefault());
        calendarSend.setTime(dateSend);

        if (mListType == ListType.WHO_CHECKS_OUT_ME) {
          long timeDiff = calendarNow.getTimeInMillis()
              - calendarSend.getTimeInMillis();

          if (timeDiff <= 3600000
              && (peopleConnection.isVideoWaiting() || peopleConnection
              .isVoiceWaiting())) {

            holder.txtTime.setText(R.string.time_less_than_hour);
          } else {
            holder.txtTime.setText(mContext.getResources()
                .getString(
                    R.string.foot_print_time_login_prefix)
                + Utility.getDifference(mContext, calendarSend,
                calendarNow));
          }

          Date checkOutTime = Utility.YYYYMMDDHHMMSS
              .parse(peopleConnection.getCheckOutTime());
          holder.txtCheckOutTime.setVisibility(View.VISIBLE);
          holder.txtCheckOutTime.setText(checkOutTimeFormat
              .format(checkOutTime));

        } else {
          holder.txtTime.setText(Utility.getDifference(mContext,
              calendarSend, calendarNow));
        }
      } catch (ParseException e) {
        e.printStackTrace();
        holder.txtTime.setText(R.string.common_now);
      }

      // Set image call icon
      boolean isVoiceWaiting = peopleConnection.isVoiceWaiting();
      if (isVoiceWaiting) {
        holder.imgVoiceCall.setImageResource(R.drawable.ic_action_communication_call_active);
        holder.imgVoiceCall.setBackgroundResource(R.drawable.bg_circle_active);
        holder.imgVoiceCall.setColorFilter(ContextCompat.getColor(mAppContext, R.color.primary));
      } else {
        holder.imgVoiceCall.setImageResource(R.drawable.ic_action_communication_call);
        holder.imgVoiceCall.setBackgroundResource(R.drawable.bg_circle_deactive);
        holder.imgVoiceCall.clearColorFilter();
      }
      holder.imgVoiceCall.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
//					if (peopleConnection.isVoiceWaiting()) {
          CallUserInfo userInfo = new CallUserInfo(
              peopleConnection.getUserName(),
              peopleConnection.getUserId(), peopleConnection
              .getAvaId(), peopleConnection
              .getGender());
          callUserInfo = userInfo;
          mCurrentCallType = Constants.CALL_TYPE_VOICE;
          Utility.showDialogAskingVoiceCall(getActivity(),
              userInfo,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  dialog.dismiss();
                  restartRequestBasicUserInfo();
                }
              });
        }
//				}
      });
      boolean isVideoWaiting = peopleConnection.isVideoWaiting();
      if (isVideoWaiting) {
        holder.imgVideoCall.setImageResource(R.drawable.ic_action_av_videocam_active);
        holder.imgVideoCall.setBackgroundResource(R.drawable.bg_circle_active);
        holder.imgVideoCall.setColorFilter(ContextCompat.getColor(mAppContext, R.color.primary));
      } else {
        holder.imgVideoCall.setImageResource(R.drawable.ic_action_av_videocam);
        holder.imgVideoCall.setBackgroundResource(R.drawable.bg_circle_deactive);
        holder.imgVideoCall.clearColorFilter();
      }
      holder.imgVideoCall.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
//					if (peopleConnection.isVideoWaiting()) {
          CallUserInfo userInfo = new CallUserInfo(
              peopleConnection.getUserName(),
              peopleConnection.getUserId(), peopleConnection
              .getAvaId(), peopleConnection
              .getGender());
          callUserInfo = userInfo;
          mCurrentCallType = Constants.CALL_TYPE_VIDEO;
          Utility.showDialogAskingVideoCall(getActivity(),
              userInfo,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                    int which) {
                  dialog.dismiss();
                  restartRequestBasicUserInfo();
                }
              });
//					}
        }
      });
      return convertView;
    }

    public void appendList(List<PeopleConnection> peoples) {
      filter(mlistPeople, peoples);
      this.notifyDataSetChanged();
    }

    /**
     * filter to not duplicate
     */
    private List<PeopleConnection> filter(List<PeopleConnection> old,
        List<PeopleConnection> listNew) {
      listNew = filterInSeft(listNew);
      if (old.size() == 0) {
        old.addAll(listNew);
        return old;
      }

      /* ADD Started */
      /* ADDD Ended */

      for (PeopleConnection meetPeople : listNew) {
        String id = meetPeople.getUserId();
        boolean duplicate = false;
        for (PeopleConnection meetPeople2 : old) {
          if (id.equals(meetPeople2.getUserId())) {
            duplicate = true;
            break;
          }
        }
        /* MOD Started */
        /* DEL Started */
        // if (!duplicate) {
        /* DEL Ended */
        /* ADD Started */
        if (!duplicate && !Utility.isBlockedWithUser(mAppContext, id)) {
          /* ADDD Ended */
          /* MOD Ended */
          old.add(meetPeople);
        }
      }
      return old;
    }

    private List<PeopleConnection> filterInSeft(
        List<PeopleConnection> peoples) {
      List<PeopleConnection> list = new ArrayList<PeopleConnection>();
      for (PeopleConnection meetPeople : peoples) {
        if (!list.contains(meetPeople)) {
          list.add(meetPeople);
        }
      }
      peoples = null;
      return list;
    }

    public void removeItem(String userId) {
      boolean found = false;

      for (int i = 0; i < mlistPeople.size(); i++) {
        if (mlistPeople.get(i).getUserId().equals(userId)) {
          mlistPeople.remove(i);
          found = true;
          break;
        }
      }

      if (found) {
        this.notifyDataSetChanged();
      }
    }

    /**
     * Remove all elements from this adapter, leaving it empty
     */
    public void clearAllData() {
      mlistPeople.clear();
      this.notifyDataSetChanged();
    }
  }
}
package com.application.ui.chat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.application.call.LinphoneVideoCall;
import com.application.call.LinphoneVoiceCall;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CheckCallRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.GetCallLogRequest;
import com.application.connection.response.CheckCallResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.GetCallLogResponse;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.ui.BaseFragment;
import com.application.ui.chat.CallLogListAdapter.ICheckCall;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;

public class BaseCallLogListFragment extends BaseFragment implements
    ResponseReceiver {

  public static final int TYPE_SENDER = 1;
  public static final int TYPE_RECEIVER = 2;
  private static final String KEY_TYPE = "type";
  private final int REQUEST_CALL_LOG = 0;
  private final int REQUEST_REFRESH = 1;
  private final int LOADER_ID_CHECK_CALL_VIDEO = 2;
  private final int LOADER_ID_CHECK_CALL_VOICE = 3;
  private final int LOADER_ID_BASIC_USER_INFO = 4;
  private final int TAKE = 24;

  private int type;

  private PullToRefreshListView mPullListView;
  private CallLogListAdapter adapter;
  private ProgressDialog progressDialog;
  private CallUserInfo callUserInfo = new CallUserInfo();
  private int mCurrentCallType = Constants.CALL_TYPE_VOICE;
  private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
        long arg3) {
      String userId = adapter.getItem(position - 1).getUserId();
      replaceFragment(MyProfileFragment.newInstance(userId));
    }
  };

  public static BaseCallLogListFragment getInstance(int type) {
    BaseCallLogListFragment fragment = new BaseCallLogListFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE, type);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      this.type = getArguments().getInt(KEY_TYPE);
    } else {
      this.type = savedInstanceState.getInt(KEY_TYPE);
    }
    if (adapter != null && adapter.getCount() <= 0) {
      refresh();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(KEY_TYPE, type);
    super.onSaveInstanceState(outState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list_call_log,
        container, false);
    initView(view);
    if (adapter.getCount() <= 0) {
      refresh();
    }
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void initView(View view) {
    mPullListView = (PullToRefreshListView) view.findViewById(R.id.list);
    mPullListView.setMode(Mode.BOTH);
    mPullListView
        .setPullLabelFooter(getString(R.string.pull_to_load_more_pull_label));
    mPullListView
        .setReleaseLabelFooter(getString(R.string.pull_to_load_more_release_label));
    mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
      @Override
      public void onPullDownToRefresh(
          PullToRefreshBase<ListView> refreshView) {
        refresh();
      }

      @Override
      public void onPullUpToRefresh(
          PullToRefreshBase<ListView> refreshView) {
        requestListLog(adapter.getCount());
      }
    });
    ListView listView = mPullListView.getRefreshableView();
    listView.setDividerHeight(0);
    listView.setCacheColorHint(Color.TRANSPARENT);
    if (adapter == null) {
      adapter = new CallLogListAdapter(getActivity(), getImageFetcher());
      adapter.setCheckCall(new ICheckCall() {
        @Override
        public void checkCall(boolean isVideoCall) {
          if (isVideoCall) {
            mCurrentCallType = Constants.CALL_TYPE_VIDEO;
          } else {
            mCurrentCallType = Constants.CALL_TYPE_VOICE;
          }
          restartRequestBasicUserInfo();
        }

        @Override
        public void setUserInfo(CallUserInfo userInfo) {
          callUserInfo = userInfo;
        }
      });
    }

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(mOnItemClickListener);
  }

  private void restartRequestBasicUserInfo() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, callUserInfo.getUserId());
    restartRequestServer(LOADER_ID_BASIC_USER_INFO, request);
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

  private void refresh() {
    String token = UserPreferences.getInstance().getToken();
    GetCallLogRequest request = new GetCallLogRequest(token, type, 0, TAKE);
    restartRequestServer(REQUEST_REFRESH, request);
  }

  private void requestListLog(int skip) {
    String token = UserPreferences.getInstance().getToken();
    GetCallLogRequest request = new GetCallLogRequest(token, type, skip,
        TAKE);
    restartRequestServer(REQUEST_CALL_LOG, request);
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

  public void responseCallLog(GetCallLogResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        List<CallLog> listCallLog = response.getListCallLog();
        adapter.addAll(listCallLog);
        mPullListView.onRefreshComplete();
        break;
      default:
        mPullListView.onRefreshComplete();
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    int loaderID = loader.getId();
    switch (loaderID) {
      case REQUEST_REFRESH:
        adapter.clear();
      case REQUEST_CALL_LOG:
        responseCallLog((GetCallLogResponse) response);
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

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case REQUEST_CALL_LOG:
      case REQUEST_REFRESH:
        response = new GetCallLogResponse(data);
        break;
      case LOADER_ID_CHECK_CALL_VIDEO:
      case LOADER_ID_CHECK_CALL_VOICE:
        response = new CheckCallResponse(data);
        break;
      case LOADER_ID_BASIC_USER_INFO:
        response = new GetBasicInfoResponse(mAppContext, data);
        break;
      default:
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }
}
package com.application.ui.chat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.SetCallSettingRequest;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.SetCallSettingResponse;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


/**
 * Flow requestGetSettingCall(); requestGetSettingCall() receiveResponse() onGetCallReceiver()
 *
 *
 * button right onClick requestSettingCall() receiveResponse() onSetCallReceiver() --> goBack
 */

public class IncomingSettingFragment extends BaseFragment implements ResponseReceiver {

  private static final String KEY_IS_VOICE = "is_voice";
  private static final String KEY_IS_VIDEO = "is_video";

  private final int REQUEST_GET_CALL = 0;
  private final int REQUEST_SET_CALL = 1;
  boolean isVoiceEnable;
  boolean isVideoEnable;
  private View mLayoutVoice;
  private View mLayoutVideo;
  private CheckBox mCkbVoice;
  private CheckBox mCkbVideo;
  private ProgressDialog progressDialog;

  public static IncomingSettingFragment getInstance(boolean isVoice, boolean isVideo) {
    IncomingSettingFragment fragment = new IncomingSettingFragment();
    Bundle bundle = new Bundle();
    bundle.putBoolean(KEY_IS_VOICE, isVoice);
    bundle.putBoolean(KEY_IS_VIDEO, isVideo);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      Bundle bundle = getArguments();
      isVoiceEnable = bundle.getBoolean(KEY_IS_VOICE);
      isVideoEnable = bundle.getBoolean(KEY_IS_VIDEO);
    } else {
      isVoiceEnable = savedInstanceState.getBoolean(KEY_IS_VOICE);
      isVideoEnable = savedInstanceState.getBoolean(KEY_IS_VIDEO);
    }

    if (mLayoutVoice != null) {
      mCkbVoice.setChecked(isVoiceEnable);
      mLayoutVoice.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mCkbVoice.setChecked(!mCkbVoice.isChecked());
        }
      });
    }

    if (mLayoutVideo != null) {
      mCkbVideo.setChecked(isVideoEnable);
      mLayoutVideo.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mCkbVideo.setChecked(!mCkbVideo.isChecked());
        }
      });
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(KEY_IS_VOICE, mCkbVoice.isChecked());
    outState.putBoolean(KEY_IS_VIDEO, mCkbVideo.isChecked());
    super.onSaveInstanceState(outState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_incoming_setting, container, false);
    initView(view);
    setUpNavigationBar();
    requestGetSettingCall();
    return view;
  }

  private void initView(View view) {
    mLayoutVoice = view.findViewById(R.id.cbx_is_voice_layout);
    mCkbVoice = (CheckBox) view.findViewById(R.id.cbx_is_voice);
    mLayoutVideo = view.findViewById(R.id.cbx_is_video_layout);
    mCkbVideo = (CheckBox) view.findViewById(R.id.cbx_is_video);
  }

  private void setUpNavigationBar() {
    NavigationBar navigationBar = getNavigationBar();
    navigationBar.setNavigationLeftVisibility(View.VISIBLE);
    navigationBar.setCenterTitle(R.string.incoming_setting_title);
    navigationBar.setNavigationRightTitle(R.string.common_done);
  }


  private void requestGetSettingCall() {
    String token = UserPreferences.getInstance().getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token);
    restartRequestServer(REQUEST_GET_CALL, request);
  }


  @Override
  public void startRequest(int loaderId) {
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage(getString(R.string.waiting));
    progressDialog.show();
  }

  public void onRightNaviButtonClicked() {
    requestSettingCall();
  }

  private void onGetCallReceiver(GetBasicInfoResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        UserPreferences preferences = UserPreferences.getInstance();
        boolean isVoiceWaiting = response.isVoiceWaiting();
        if (isVoiceWaiting != mCkbVoice.isChecked()) {
          mCkbVoice.setChecked(isVoiceWaiting);
          preferences.saveEnableVoiceCall(isVoiceWaiting);
        }
        boolean isVideoWaiting = response.isVideoWaiting();
        if (isVideoWaiting != mCkbVideo.isChecked()) {
          mCkbVideo.setChecked(isVideoWaiting);
          preferences.saveEnableVideoCall(isVideoWaiting);
        }
        break;
      default:
    }
  }

  private void onSetCallReceiver(SetCallSettingResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        UserPreferences preferences = UserPreferences.getInstance();
        preferences.saveEnableVoiceCall(mCkbVoice.isChecked());
        preferences.saveEnableVideoCall(mCkbVideo.isChecked());
        Handler handler = new Handler();
        handler.post(new Runnable() {
          @Override
          public void run() {
            mNavigationManager.goBack();
          }
        });
        break;
      default:
    }
  }

  /**
   * Call when Button right of navigation Click
   */
  public void onDone() {
    requestSettingCall();
  }

  private void requestSettingCall() {
    String token = UserPreferences.getInstance().getToken();
    SetCallSettingRequest request = new SetCallSettingRequest(token, mCkbVoice.isChecked(),
        mCkbVideo.isChecked());
    restartRequestServer(REQUEST_SET_CALL, request);
  }


  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case REQUEST_GET_CALL:
        response = new GetBasicInfoResponse(mAppContext, data);
        break;
      case REQUEST_SET_CALL:
        response = new SetCallSettingResponse(data);
        break;
      default:
        break;
    }
    return response;
  }


  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    getLoaderManager().destroyLoader(loader.getId());
    if (response.getCode() != Response.SERVER_SUCCESS) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error, response.getCode());
      return;
    }
    int loaderID = loader.getId();
    switch (loaderID) {
      case REQUEST_GET_CALL:
        onGetCallReceiver((GetBasicInfoResponse) response);
        break;
      case REQUEST_SET_CALL:
        onSetCallReceiver((SetCallSettingResponse) response);
        break;
      default:
        break;
    }
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }


  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }


}
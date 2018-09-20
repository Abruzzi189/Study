package com.application.ui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.DeactivateAccountRequest;
import com.application.connection.response.DeactivateAccountResponse;
import com.application.service.DataFetcherService;
import com.application.status.StatusController;
import com.application.ui.BaseFragment;
import com.application.ui.account.SignUpActivity;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

/**
 * Button action onClick restartRequestServer(LOADER_ID_DEACTIVATE_ACCOUNT, dar);   	(152)
 */

public class DeactivateAccountFragment extends BaseFragment implements
    ResponseReceiver, OnNavigationClickListener, OnGlobalLayoutListener {

  private static final String TAG = DeactivateAccountFragment.class.getSimpleName();
  private static final int LOADER_ID_DEACTIVATE_ACCOUNT = 5700;
  private EditText mComment;
  private ProgressDialog mProgressDialog;
  private ScrollView mScrollView;
  private Handler mHandlerKeyboard;
  private OnLogoutTask mLogoutTask;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mLogoutTask = (OnLogoutTask) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    requestDirtyWord();
    View view = inflater.inflate(R.layout.fragment_deactivate_account, container, false);
    initView(view);
    return view;
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
  public void onPause() {
    // Hide soft-key
    if (mHandlerKeyboard != null) {
      mHandlerKeyboard.removeCallbacksAndMessages(null);
    }
    hideKeyboard(mComment);
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mComment.isFocused()) {
      mComment.clearFocus();
    }
    mComment.requestFocus();
    InputMethodManager keyboard = (InputMethodManager) mAppContext
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    keyboard.showSoftInput(mComment, InputMethodManager.SHOW_FORCED);
  }

  private void hideKeyboard(View v) {
    InputMethodManager imm = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.settings_account_deactivate);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setShowUnreadMessage(false);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  private void initView(View view) {
    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setCancelable(false);
    mProgressDialog.setMessage(getString(R.string.waiting));

    Button action = (Button) view.findViewById(R.id.bt_fragment_deactivate_account_action);
    mComment = (EditText) view.findViewById(R.id.ed_fragment_deactivate_account_comment);

    action.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mProgressDialog.isShowing()) {
          return;
        }
        if (!Utility.isContainDirtyWord(getActivity(), mComment)) {
          mProgressDialog.show();

          String token = UserPreferences.getInstance().getToken();
          String comment = mComment.getText().toString();

          comment = comment.replace("\u3000", " ").trim();

          if (comment.equals("")) {
            // Show dialog and exit
            String title = getString(R.string.alert);
            String message = getString(R.string.settings_account_deactivate_alert);
            com.application.ui.customeview.ErrorApiDialog
                .showAlert(getActivity(), title, message);
            mProgressDialog.dismiss();
            return;
          }

          hideKeyboard(mComment);

          DeactivateAccountRequest dar = new DeactivateAccountRequest(
              token, comment);
          restartRequestServer(LOADER_ID_DEACTIVATE_ACCOUNT, dar);
        }
      }
    });
    mScrollView = (ScrollView) view.findViewById(R.id.scroll_view_deactive_account);
    mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    // showKeyboard();
    mHandlerKeyboard = Utility.showDelayKeyboard(mComment, 100);
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    } else {
      mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
  }

  @Override
  public void startRequest(int loaderId) {
    LogUtils.d(TAG, "startRequest Started");
    LogUtils.d(TAG, "startRequest Ended");
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    LogUtils.d(TAG, "receiveResponse Started");

    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }

    if (getActivity() == null || loader == null || response == null) {
      LogUtils.d(TAG, String.format(
          "Parent Activity: %s, Loader: %s, Response: %s",
          getActivity(), loader, response));

      return;
    }

    switch (loader.getId()) {
      case LOADER_ID_DEACTIVATE_ACCOUNT:
        handleDeactivateAccountResponse((DeactivateAccountResponse) response);
        break;
      default:
        break;
    }
  }

  private void handleDeactivateAccountResponse(DeactivateAccountResponse response) {
    LogUtils.d(TAG, "handleDeactivateAccountResponse Started");

    if (response.getCode() == Response.SERVER_SUCCESS) {
      StatusController.getInstance(getActivity()).clearAllMsg();
      mLogoutTask.executeLogoutTask();
      // Navigate to Sign-Up screen
      Intent intent = new Intent(getActivity(), SignUpActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      getActivity().finish();
    } else {
      com.application.ui.customeview.ErrorApiDialog.showAlert(
          getActivity(), R.string.common_error, response.getCode());
    }

  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_ID_DEACTIVATE_ACCOUNT:
        response = new DeactivateAccountResponse(data);
        break;
      default:
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
    LogUtils.d(TAG, "onBaseLoaderReset Started");
    LogUtils.d(TAG, "onBaseLoaderReset Ended");
  }

  @Override
  public void onGlobalLayout() {
    mScrollView.fullScroll(View.FOCUS_DOWN);
  }

}

package com.application.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.application.AndGApp;
import com.application.actionbar.CustomActionBar;
import com.application.chat.ChatManager;
import com.application.connection.DataLoader;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseReceiver;
import com.application.connection.ServerRequest;
import com.application.connection.request.RequestParams;
import com.application.facebook.FacebookController;
import com.application.imageloader.ImageFetcher;
import com.application.navigationmanager.NavigationManager;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.NavigationBar;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import glas.bbsystem.R;

/**
 * Base Fragment for all Fragment use in application
 *
 * @author tungdx
 */
public abstract class BaseFragment extends Fragment implements
    OnNavigationClickListener {

  protected static final String TAG = "BaseFragment";
  protected static final String TAB_BACKSTACK = "backstack_frg";
  public static boolean isAnimation = true;
  protected BaseFragmentActivity baseFragmentActivity;
  // saveInstance is called
  protected boolean isSaveInstanceCalled = false;
  protected Context mAppContext;
  protected NavigationManager mNavigationManager;
  protected CustomActionBar mActionBar;
  private ResponseReceiver responseReceiver;
  private ServerRequest serverRequest;
  private LocationManager mLocationManager;
  private FacebookController mFacebookController;
  private AlertDialog mTerminateDialog;
  private ProgressDialog mWaitingDialog;
  private boolean hasTerminateCall;
  private BroadcastReceiver mTerminateCallReceiver;

  protected void setResponseReceiver(ResponseReceiver responseReceiver) {
    this.responseReceiver = responseReceiver;
  }

  @Override
  public void onStop() {
    super.onStop();
    dismissTerminateCallDialog();
  }

  /**
   * Define fragment may control navigation or not, default {@code true}
   *
   * @return true - control navigation , false - not control
   */
  protected boolean isControlNavigation() {
    return true;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    baseFragmentActivity = (BaseFragmentActivity) activity;
    mAppContext = baseFragmentActivity.getApplicationContext();
    baseFragmentActivity.resetHandleShowNotification();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (this instanceof ResponseReceiver) {
      setResponseReceiver((ResponseReceiver) this);
    }
    serverRequest = new ServerRequest(getLoaderManager(), getActivity(),
        responseReceiver);
    if (hasFacebookController()) {
      mFacebookController = new FacebookController(this);
      mFacebookController.onCreate(savedInstanceState);
    }
  }

  @Override
  public void onResume() {
    setUpNavigationBar(baseFragmentActivity, this);
    if (hasFacebookController()) {
      mFacebookController.onResume();
    }
    isSaveInstanceCalled = false;
    // ads are removed
    if (hasTerminateCall) {
      hasTerminateCall = false;
      showTerminateCallMessage();
    }
    super.onResume();
  }

  protected View onBaseCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    return null;
  }

  protected void resetNavigationBar() {
    if (getNavigationBar() != null) {
      getNavigationBar().reset();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // if (baseFragmentActivity.hasImageFetcher() && hasImageFetcher()) {
    // mImageFetcher = baseFragmentActivity.getImageFetcher();
    // }
    View view = onBaseCreateView(inflater, container, savedInstanceState);
    if (isControlNavigation()) {
      resetNavigationBar();
    }

    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    if (hasFacebookController()) {
      mFacebookController.onPause();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unRegisterTerminateCallReceiver();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    isSaveInstanceCalled = false;
    Activity activity = getActivity();
    if (activity instanceof CustomActionBarActivity) {
      CustomActionBarActivity customActionBarActivity = ((CustomActionBarActivity) activity);
      mActionBar = customActionBarActivity.getCustomActionBar();
      mNavigationManager = customActionBarActivity.getNavigationManager();
    } else if (activity instanceof CustomActionBar) {
      mActionBar = (CustomActionBar) activity;
    }
    registerTerminateCallReceiver();

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (hasFacebookController()) {
      mFacebookController.onActivityResult(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    isSaveInstanceCalled = true;
  }

  /**
   * Setup navigation bar to {@link BaseFragment} , make BaseFragment can control NavigationBar
   */
  private void setUpNavigationBar(BaseFragmentActivity baseFragmentActivity,
      OnNavigationClickListener onNavigationClickListener) {
    if (isControlNavigation()) {
      resetNavigationBar();
      baseFragmentActivity
          .setOnNavigationClickListener(onNavigationClickListener);
    }
  }

  /**
   * unregister location update
   */
  public void unregisterLocationUpdate(LocationListener locationListener) {
    if (mLocationManager != null && locationListener != null) {
      mLocationManager.removeUpdates(locationListener);
    }
  }

  public SlidingMenu getSlidingMenu() {
    MainActivity mainActivity = (MainActivity) baseFragmentActivity;
    return mainActivity.getSlidingMenu();
  }

  /**
   * Use for <b>first time request</b> server to get data
   *
   * @param loaderID id of loader that implement request
   * @param requestType type of data that get
   * @param requestParams params of request
   */
  protected void requestServer(int loaderID, int requestType,
      RequestParams requestParams) {
    serverRequest.initLoader(loaderID, requestType, requestParams);
  }

  /**
   * Use for <b>first time request</b> server to get data
   */
  protected void requestServer(int loaderId, RequestParams data) {
    serverRequest.initLoader(loaderId, RequestType.JSON, data);
  }

  /**
   * Use for <b>restart request</b> server to get data
   */
  protected void restartRequestServer(int loaderID, int requestType,
      RequestParams data) {
    serverRequest.restartLoader(loaderID, requestType, data);
  }

  /**
   * Use for <b>restart request</b> server to get data
   */
  public void restartRequestServer(int loaderID, RequestParams data) {
    if (isAdded()) {
      serverRequest.restartLoader(loaderID, RequestType.JSON, data);
    }
  }

  /**
   * Use for <b>restart request</b> server to get data
   */
  protected void restartRequestServer(int loaderID, RequestParams data,
      int timeoutConnect, int timeoutRead) {
    serverRequest.restartLoader(loaderID, RequestType.JSON, data,
        timeoutConnect, timeoutRead);
  }

  /**
   * Use for get more data (only called after requestServer called}
   */
  protected void requestServerGetMoreData(int loaderId,
      RequestParams requestParams, ResponseReceiver responseReceiver) {
    Loader<Response> loader = getLoaderManager().getLoader(loaderId);
    DataLoader dataLoader = (DataLoader) loader;
    if (dataLoader == null) {
      return;
    }

    Request requestBuilder = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, requestParams, responseReceiver, loaderId);
    dataLoader.setRequest(requestBuilder);
    dataLoader.onContentChanged();
  }

  /**
   * Get Navigationbar, please check NULL may be return NULL if fragment not place in {@link
   * BaseFragmentActivity}
   *
   * @return {@link NavigationBar}
   */
  protected NavigationBar getNavigationBar() {
    if (baseFragmentActivity == null) {
      return null;
    }
    return baseFragmentActivity.getNavigationBar();
  }

  @Override
  public void onNavigationLeftClick(View view) {
    int count = getFragmentManager().getBackStackEntryCount();
    String name = null;
    if (count > 0) {
      name = getFragmentManager().getBackStackEntryAt(count - 1)
          .getName();
      if (TAB_BACKSTACK.equals(name)) {
        getFragmentManager().popBackStack();
      } else {
        getSlidingMenu().showMenu(true);
      }
    } else {
      getSlidingMenu().showMenu(true);
    }
  }

  @Override
  public void onNavigationRightClick(View view) {

  }

  /**
   * Replace Fragment with option placeHolder
   */
  private void replaceFragment(int placeHolder, Fragment fragment, String tag) {
    String backstack = TAB_BACKSTACK;
    FragmentTransaction fragmentTransaction = getFragmentManager()
        .beginTransaction();
    fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,
        R.anim.fragment_exit, R.anim.fragment_pop_enter,
        R.anim.fragment_pop_exit);
    fragmentTransaction.replace(placeHolder, fragment, tag);
    fragmentTransaction.addToBackStack(backstack);
    fragmentTransaction.commitAllowingStateLoss();
  }

  /**
   * Replace Fragment with option placeHolder
   *
   * @param placeHolder
   * @param fragment
   * @param tag
   */
  // protected void replaceFragmentUser(int placeHolder, BaseFragment
  // fragment,
  // String tag) {
  // FragmentTransaction fragmentTransaction = getFragmentManager()
  // .beginTransaction();
  // LogUtils.e("tag", "tag=" + tag);
  // fragmentTransaction.replace(placeHolder, fragment, tag);
  // fragmentTransaction.addToBackStack(tag);
  // fragmentTransaction.commitAllowingStateLoss();
  // }

  // protected void replaceFragmentUser(BaseFragment fragment, String tag) {
  // replaceFragmentUser(R.id.activity_main_content, fragment, tag);
  // }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
    if (!isAnimation) {
      Animation anim = new Animation() {
      };
      anim.setDuration(0);
      return anim;
    }
    return super.onCreateAnimation(transit, enter, nextAnim);
  }

  /**
   * Repalce Fragment in main content
   */
  protected void replaceFragment(BaseFragment fragment, String tag) {
    // replaceFragment(R.id.activity_main_content, fragment, TAB_BACKSTACK);
    mNavigationManager.addPage(fragment);
  }

  public void replaceFragment(Fragment baseFragment) {
    // replaceFragment(R.id.activity_main_content, baseFragment,
    // TAB_BACKSTACK);
    mNavigationManager.addPage(baseFragment);
  }

  protected void clearAllFocusableFields() {
    // NOP
  }

  public ImageFetcher getImageFetcher() {
    return baseFragmentActivity.getImageFetcher();
  }

  /**
   * Indicate that use ImageFetcher in Fragment, default: false
   */
  protected boolean hasImageFetcher() {
    return false;
  }

  protected boolean hasFacebookController() {
    return false;
  }

  protected FacebookController getFacebookController() {
    return mFacebookController;
  }

  @Override
  public void onLowMemory() {
    if (hasImageFetcher()) {
      getImageFetcher().flushCache();
      getImageFetcher().clearCache();
    }
    super.onLowMemory();
  }

  public void showLeftSlidingMenu() {
    getSlidingMenu().showMenu(true);
  }

  public void showRightSlidingMenu() {
    getSlidingMenu().showSecondaryMenu();
  }

  private void registerTerminateCallReceiver() {
    mTerminateCallReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        handleTerminateCallMessage();
      }
    };
    IntentFilter intentFilter = new IntentFilter(
        ChatManager.ACTION_LOCAL_MESSAGE_TERMINATE_CALL);
    LocalBroadcastManager.getInstance(mAppContext).registerReceiver(
        mTerminateCallReceiver, intentFilter);
  }

  private void unRegisterTerminateCallReceiver() {
    LocalBroadcastManager.getInstance(mAppContext).unregisterReceiver(
        mTerminateCallReceiver);
  }

  private void handleTerminateCallMessage() {
    if (AndGApp.isApplicationVisibile()) {
      showTerminateCallMessage();
    } else {
      hasTerminateCall = true;
    }
  }

  public void showWaitingDialog() {
    if (!isVisible()) {
      return;
    }
    if (mWaitingDialog == null) {
      mWaitingDialog = new ProgressDialog(getActivity());
      mWaitingDialog.setMessage(getString(R.string.waiting));
    }

    if (mWaitingDialog.isShowing()) {
      mWaitingDialog.dismiss();
    }
    mWaitingDialog.show();
  }

  public void hideWaitingDialog() {
    if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
      mWaitingDialog.dismiss();
    }
  }

  private void showTerminateCallMessage() {
    if (!isVisible()) {
      return;
    }
    if (mTerminateDialog == null) {
      mTerminateDialog = new CustomConfirmDialog(getActivity(), "",
          getString(R.string.message_terminate_call_by_real_call),
          false)
          .create();
    }
    if (mTerminateDialog.isShowing()) {
      mTerminateDialog.dismiss();
    }
    mTerminateDialog.show();

    int dividerId = mTerminateDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mTerminateDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mTerminateDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void dismissTerminateCallDialog() {
    if (mTerminateDialog != null && mTerminateDialog.isShowing()) {
      mTerminateDialog.dismiss();
    }
  }
}
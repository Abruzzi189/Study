package com.application.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import com.application.AndGApp;
import com.application.Config;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetApplicationInfoRequest;
import com.application.connection.request.GetUpdateInfoFlagRequest;
import com.application.connection.request.GetUserStatusRequest;
import com.application.connection.request.InstallCountRequest;
import com.application.connection.response.GetApplicationInfoResponse;
import com.application.connection.response.GetUpdateInfoFlagResponse;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.InstallCountResponse;
import com.application.connection.response.LoginResponse;
import com.application.service.DataFetcherService;
import com.application.ui.account.AuthenticationData;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.uploadmanager.CustomUploadService;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.ntq.G2metric;
import com.ntq.adjust.AdjustSdk;
import glas.bbsystem.BuildConfig;
import glas.bbsystem.R;
import net.nex8.tracking.android.Nex8Tracker;
import net.nex8.tracking.android.TrackingMode;
import org.linphone.LinphoneService;


public class SplashActivity extends BaseFragmentActivity implements
    ResponseReceiver {

  private static final int LOADER_LOGIN = 1;
  private static final int LOADER_GET_UPDATE_INFO_FLAG = 3;
  private static final int LOADER_INSTALL_COUNT = 4;
  private static final int LOADER_APPLICATION_INFO = 5;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    G2metric.register(getApplication(), false);

    // Initial view
    setContentView(R.layout.activity_splash);

    // Register project service
    registerService();

    GetApplicationInfoRequest applicationInfoRequest = new GetApplicationInfoRequest();
    restartRequestServer(LOADER_APPLICATION_INFO, applicationInfoRequest);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    G2metric.onFinishRegister();
  }

  private void registerService() {
    // Register Dirty word
    DataFetcherService.startLoadDirtyWord(this);

    // Register Linphone service
    Intent intentLinphone = new Intent(this, LinphoneService.class);
    startService(intentLinphone);

    // Register Upload service
    Intent intentUpload = new Intent(this, CustomUploadService.class);
    startService(intentUpload);
  }

  private void checkInfoUpdated() {
    UserPreferences prefers = UserPreferences.getInstance();
    String token = prefers.getToken();

    int type = Utility.isNeededGetUserStatus();

    if (type != GetUserStatusRequest.TYPE_NONE) {
      requestGetUserStatus(type);
    } else if (TextUtils.isEmpty(token)) {
      autoLogin();
    } else {
      // Register Linphone service
      LinphoneService.startLogin(getApplicationContext());

      // Request user information updated
      GetUpdateInfoFlagRequest request = new GetUpdateInfoFlagRequest(
          token);
      restartRequestServer(LOADER_GET_UPDATE_INFO_FLAG, request);
    }
  }

  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    switch (loaderID) {
      case LOADER_RETRY_LOGIN:
        return new LoginResponse(data);
      case LOADER_GET_UPDATE_INFO_FLAG:
        return new GetUpdateInfoFlagResponse(data);
      case LOADER_INSTALL_COUNT:
        return new InstallCountResponse(data);
      case LOADER_APPLICATION_INFO:
        return new GetApplicationInfoResponse(data);
      case LOADER_GET_USER_STATUS:
        return new GetUserStatusResponse(data);
      default:
        return null;
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    int loaderId = loader.getId();
    // Destroy previous loader
    getSupportLoaderManager().destroyLoader(loaderId);

    switch (loaderId) {
      case LOADER_GET_UPDATE_INFO_FLAG:
        onResponseUpdateInfoFlag((GetUpdateInfoFlagResponse) response);
        break;
      case LOADER_LOGIN:
        onResponseLogin((LoginResponse) response);
        break;
      case LOADER_INSTALL_COUNT:
        onResponseInstallCount((InstallCountResponse) response);
        break;
      case LOADER_APPLICATION_INFO:
        onResponseApplicationInfo((GetApplicationInfoResponse) response);
        break;
      default:
        break;
    }

  }

  private void onResponseApplicationInfo(GetApplicationInfoResponse response) {
    int code = response.getCode();
    GoogleReviewPreference preference = new GoogleReviewPreference();
    if (code == Response.SERVER_SUCCESS) {
      preference.saveTurnOffVersion(response.getSwitchBrowserVersion());
      preference.saveEnableGetFreePoint(response.isGetFreePoint());
      preference.saveEnableLoginByAnotherSystem(response
          .isLoginByAnotherSystem());
      preference.saveEnableBrowser(response.isSwitchBrowser());
      preference.saveIsTurnOffUserInfo(response.isTurnOffUserInfo());

      UserPreferences userPreferences = UserPreferences.getInstance();
      if (userPreferences.getShowNewsPopup()) {
        userPreferences.setShowNewsPopup(response.isShowNews());
      }

      preference.saveForceUpdate(response.isForce_updating());
      preference.saveWebUrl(response.getUrl_web());
      preference.saveAppVersion(response.getApp_version());
      preference.setFirstShowUpdate(true);

    } else {
      preference.saveTurnOffVersion("");
      preference.saveEnableGetFreePoint(false);
      preference.saveEnableLoginByAnotherSystem(false);
      preference.saveEnableBrowser(false);
      preference.saveIsTurnOffUserInfo(false);

      preference.saveForceUpdate(false);
      preference.saveWebUrl("");
      preference.saveAppVersion("");
      preference.setFirstShowUpdate(true);

    }

    // Check application first install
    Preferences preferences = Preferences.getInstance();
    if (!preferences.isTrackedApptizer()) {
      if (BuildConfig.IS_PRODUCT) {
        Nex8Tracker tracker = ((AndGApp) getApplication()).getTracker();
        tracker.openedApp();
        tracker.setTrackingMode(TrackingMode.Debug);
        G2metric.trackStartApp();
      }

      //-------Track install app
      AdjustSdk.trackInstallApp();

      preferences.saveIsTrackedApptizer();
    }

    if (preferences.isInstall())

    {
      if (preferences.isAttachCmcode()) {
        checkInfoUpdated();
      } else {
        preferences.saveIsAttachCmcode();
        String androidId = Secure.getString(getContentResolver(),
            Secure.ANDROID_ID);
        String url = String
            .format(Config.COUNTER_SERVER, androidId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
      }
    } else

    {
      // Request to count and check user info after count
      String android_id = Secure.getString(getContentResolver(),
          Secure.ANDROID_ID);
      InstallCountRequest data = new InstallCountRequest(android_id);
      restartRequestServer(LOADER_INSTALL_COUNT, data);
    }

  }


  @Override
  protected void onStart() {
    super.onStart();
    Nex8Tracker tracker = ((AndGApp) getApplication()).getTracker();
    tracker.startSession(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Nex8Tracker tracker = ((AndGApp) getApplication()).getTracker();
    tracker.endSession(this);
  }

  private void onResponseUpdateInfoFlag(GetUpdateInfoFlagResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        UserPreferences pre = UserPreferences.getInstance();
        pre.saveAgeVerification(response.getVerificationFlag());
        pre.saveFinishRegister(response.getFinishRegisterFlag());
      case Response.SERVER_ACCESS_DENIED:
      case Response.SERVER_INVALID_TOKEN:
        UserPreferences prefers = UserPreferences.getInstance();
        prefers.setShowNewNotifications(true);
        if (!TextUtils.isEmpty(prefers.getToken())) {
          startMainScreen();
        } else {
          autoLogin();
        }

        break;
      default:
        // Show error dialog and close application
        ErrorApiDialog.showAlert(this, R.string.common_error, code,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                finish();
              }
            }, false);
    }
  }

  private void onResponseLogin(LoginResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        // Save info when login success
        AuthenticationData authenData = response.getAuthenticationData();

        UserPreferences userPreferences = UserPreferences.getInstance();
        userPreferences.saveSuccessLoginData(authenData, true);

        Preferences preferences = Preferences.getInstance();
        preferences.saveTimeSetting(authenData);
        preferences.savePointSetting(authenData);

        // Login time
        preferences.saveTimeRelogin(System.currentTimeMillis());
        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
        googleReviewPreference.saveTurnOffVersion(response
            .getSwitchBrowserVersion());
        googleReviewPreference.saveEnableGetFreePoint(response
            .isEnableGetFreePoint());
        googleReviewPreference.saveIsTurnOffUserInfo(response.isTurnOffUserInfo());
        startMainScreen();
        break;
      default:
        startAuthenScreen();
    }
  }

  private void onResponseInstallCount(InstallCountResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        Preferences preferences = Preferences.getInstance();
        preferences.saveIsInstalled();
        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
        if (googleReviewPreference.isEnableBrowser()) {
          if (isNavigable()) {
            preferences.saveIsAttachCmcode();
            String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            String url = String.format(Config.COUNTER_SERVER, androidId);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            finish();
          }
          break;
        }
      default:
        // Always call this function
        checkInfoUpdated();
        break;
    }
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }
}
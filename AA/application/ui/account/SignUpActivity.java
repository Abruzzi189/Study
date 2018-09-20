package com.application.ui.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.AndGApp;
import com.application.Config;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.InstallCountRequest;
import com.application.connection.request.LoginByFacebookRequest;
import com.application.connection.request.LoginRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.InstallCountResponse;
import com.application.connection.response.LoginResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.User;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.MainActivity;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.util.FreePageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.NewsPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.viewpagerindicator.BigCirclePageIndicator;
import glas.bbsystem.BuildConfig;
import glas.bbsystem.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends BaseFragmentActivity implements
    OnClickListener, ResponseReceiver {

  private static final String TAG = "SignUpActivity";
  private static final int LOADER_LOGIN_FACEBOOK = 0;
  private static final int LOADER_LOGIN_MOCOM = 1;
  private static final int LOADER_LOGIN_FAMU = 2;
  private static final int LOADER_INSTALL_COUNT = 3;
  private static final List<String> PERMISSIONS_READ = Arrays.asList("email",
      "user_about_me", "user_activities", "user_birthday", "user_groups",
      "user_interests", "user_likes", "user_events", "user_photos");
  private final int KEY_LOGIN_BY_ANOTHER_SYS = 10;
  private ViewPager mViewPager;
  private MyPagerAdapter pagerAdapter;
  private BigCirclePageIndicator mCirclePageIndicator;
  private Button mbtnSignup;
  private Button mbtnLogin;
  private RelativeLayout mbtnFacebookLogin;
  private Button mbtnLoginOtherSystem;
  private TextView mTxtAppVersion;
  private User user;
  private ProgressDialog progressDialog;
  private String mPackageName = BuildConfig.APPLICATION_ID;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_signup);
    initView();
    initData();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Preferences preferences = Preferences.getInstance();
    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    if (preferences.isInstall()) {
      if (!preferences.isAttachCmcode() && googleReviewPreference.isEnableBrowser()) {
        preferences.saveIsAttachCmcode();
        String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        String url = String.format(Config.COUNTER_SERVER, androidId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
      }
    } else {
      // Request to count and check user info after count
      String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
      InstallCountRequest data = new InstallCountRequest(android_id);
      restartRequestServer(LOADER_INSTALL_COUNT, data);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null && intent.hasExtra(FreePageUtil.ACT_INTENT)) {
      String act = intent.getStringExtra(FreePageUtil.ACT_INTENT);
      if (act.equals(FreePageUtil.ACT_CLOSE_APP)) {
        // close app
        SignUpActivity.super.onBackPressed();
      }
    }
  }

  public void clearPreviousData() {
    AuthenticationUtil.clearAuthenticationData();

    // Remove Homepage
    Preferences pre = Preferences.getInstance();
    String url = pre.getHomePageUrl();
    if (!TextUtils.isEmpty(url)) {
      pre.removeHomePageUrl();
    }
  }

  private void initView() {
    mbtnLogin = (Button) findViewById(R.id.activity_signup_btn_login);
    mbtnSignup = (Button) findViewById(R.id.activity_signup_btn_signup);
    mbtnFacebookLogin = (RelativeLayout) findViewById(R.id.activity_signup_connect_fb);
    mTxtAppVersion = (TextView) findViewById(R.id.tv_settings_terms_of_service_version);

    // TODO : 14/6/2017 update by ThoNH about ticket http://10.64.100.201/issues/8930
    PackageInfo packageInfo = null;
    try {
      packageInfo = getPackageManager().getPackageInfo(mPackageName, 0);
      if (packageInfo != null) {
        if (Config.IS_PRODUCT_SERVER) {
          mTxtAppVersion.setVisibility(View.GONE);
        } else {
          Date buildDate = new Date(BuildConfig.EXPORT_TIMESTAMP);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
          String timeBuild = sdf.format(buildDate);

          StringBuilder builder = new StringBuilder();
          builder.append(packageInfo.versionCode);
          builder.append("(");
          builder.append(packageInfo.versionName);
          builder.append(")");
          builder.append(" - STG - ");
          builder.append(timeBuild);
          mTxtAppVersion.setText(builder.toString());
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    //hiepuh remove login other system
//        mbtnLoginOtherSystem = (Button) findViewById(R.id.activity_login_other_system);
    //end
    mViewPager = (ViewPager) findViewById(R.id.activity_signup_viewpager);
    mCirclePageIndicator = (BigCirclePageIndicator) findViewById(R.id.activity_signup_indicator);
    mCirclePageIndicator.setFillColor(getResources().getColor(R.color.primary));
    mCirclePageIndicator.setPageColor(getResources().getColor(R.color.gray_light));

    mbtnSignup.setOnClickListener(this);
    mbtnLogin.setOnClickListener(this);
    mbtnFacebookLogin.setOnClickListener(this);

    //hiepuh
//        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
//        if (googleReviewPreference.isEnableLoginByAnotherSystem()) {
//            mbtnLoginOtherSystem.setVisibility(View.VISIBLE);
//            mbtnLoginOtherSystem.setOnClickListener(this);
//        } else {
//            mbtnLoginOtherSystem.setVisibility(View.GONE);
//        }
    //end
  }

  public void initData() {
    pagerAdapter = new MyPagerAdapter();
    mViewPager.setAdapter(pagerAdapter);
    mCirclePageIndicator.setViewPager(mViewPager);
  }

  @Override
  public void onClick(View v) {
    clearPreviousData();
    switch (v.getId()) {
      case R.id.activity_signup_btn_signup:
        startRegisterActivity();
        break;
      case R.id.activity_signup_btn_login:
        startCustomeActivityForResult(new Intent(this, LoginActivity.class));
        break;
      case R.id.activity_signup_connect_fb:
        facebookLogin();
        break;
      //hiepuh
//            case R.id.activity_login_other_system:
//                loginOtherSystem();
//                break;
      //end
      default:
        break;
    }
  }

  private void loginOtherSystem() {
    Intent intent = new Intent(this, WebViewActivity.class);
    intent.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
        WebViewFragment.PAGE_TYPE_LOGIN_OTHER_SYS);
    startActivityForResult(intent, KEY_LOGIN_BY_ANOTHER_SYS);
  }

  /**
   * Start RegisterActivity
   */
  private void startRegisterActivity() {
    Intent intent = new Intent(this, RegisterActivity.class);
    startCustomeActivityForResult(intent);
  }

  private void startRegisterActivity(User user) {
    Intent intent = new Intent(this, RegisterActivity.class);
    if (user != null) {
      intent.putExtra(RegisterActivity.EXTRA_USER_DATA, user);
    }
    startCustomeActivityForResult(intent);
  }

  private void facebookLogin() {
    Session session = Session.getActiveSession();
    if (session != null) {
      // Clear session before login
      session.closeAndClearTokenInformation();
    }

    Resources resources = getResources();
    session = new Session.Builder(this).setApplicationId(
        resources.getString(R.string.app_facebook_id)).build();
    Session.setActiveSession(session);
    if (!session.isOpened()) {
      OpenRequest request = new Session.OpenRequest(this).setCallback(
          new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
              LogUtils.i(TAG, "FB Token: " + session.getAccessToken());
              LogUtils.i(TAG, "FB App Id: " + session.getApplicationId());
              if (state.isOpened()) {
                LogUtils.e(TAG, "FB State: OPENED");
                getFacebookUserInfo(session);
              } else if (state == SessionState.OPENING) {
                LogUtils.e(TAG, "FB State: OPENING");
              } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
                LogUtils.e(TAG, "FB State: CLOSED_LOGIN_FAILED");
                session.closeAndClearTokenInformation();
              } else if (state == SessionState.CLOSED) {
                LogUtils.e(TAG, "FB State: CLOSED");
                session.close();
              } else {
                LogUtils.e(TAG, "FB State: " + state);
              }
            }
          }).setPermissions(PERMISSIONS_READ);
      session.openForRead(request);
    } else {
      getFacebookUserInfo(Session.getActiveSession());
    }
  }

  private void getFacebookUserInfo(Session session) {
    String listPermision = session.getPermissions().toString();
    LogUtils.d(TAG, "getFacebookUserInfo(" + listPermision + ")");

    if (progressDialog == null) {
      progressDialog = new ProgressDialog(this);
      progressDialog.setMessage(getString(R.string.waiting));
      progressDialog.setCancelable(false);
    }
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }

    Request requestLogin = Request.newMeRequest(session,
        new Request.GraphUserCallback() {
          @Override
          public void onCompleted(GraphUser me, Response response) {
            if (me != null) {
              int gender = UserSetting.GENDER_FEMALE;
              if (me.getProperty("gender") != null) {
                if (me.getProperty("gender").toString()
                    .equalsIgnoreCase("male")) {
                  gender = UserSetting.GENDER_MALE;
                }
              } else {
                gender = UserSetting.GENDER_BOTH;
              }

              LogUtils.i(TAG, "FB birth: " + me.getBirthday()
                  + "\nGender" + me.getProperty("gender"));

              SimpleDateFormat dateFormat = new SimpleDateFormat(
                  "MM/dd/yyyy", Locale.getDefault());
              Date date = null;

              if (me.getBirthday() != null) {
                try {
                  date = dateFormat.parse(me.getBirthday());
                } catch (ParseException e) {
                  e.printStackTrace();
                }
              }

              // Interested in
              int interested_in = UserSetting.INTERESTED_IN_MEN;
              if (me.getProperty("interested_in") != null) {
                try {
                  JSONArray jsonArray = new JSONArray(me
                      .getProperty("interested_in")
                      .toString());
                  int length = jsonArray.length();
                  if (length > 1) {
                    interested_in = UserSetting.INTERESTED_IN_MEN_AND_WOMEN;
                  } else {
                    if (jsonArray.get(0).toString()
                        .equals("female")) {
                      interested_in = UserSetting.INTERESTED_IN_WOMEN;
                    }
                  }
                } catch (JSONException je) {
                  je.printStackTrace();
                }
              }

              UserPreferences.getInstance().saveUserName(
                  me.getName());

              if (user == null) {
                user = new User(me.getName(), date, gender,
                    interested_in, me.getLink(), me.getId());
              } else {
                user.setName(me.getName());
                user.setBirthday(date);
                user.setGender(gender);
                user.setInterested(interested_in);
                user.setAnotherSystemId(me.getId());

                String deviceId = Utility
                    .getDeviceId(SignUpActivity.this);
//                                String notify_token = Preferences.getInstance()
//                                        .getGCMResitrationId();
                String notify_token = FirebaseInstanceId.getInstance().getToken();
                String loginTime = Utility.getLoginTime();
                String appVersion = Utility
                    .getAppVersionName(SignUpActivity.this);
                String adjustAdid = "";
                String application_name = Utility.getApplicationName(SignUpActivity.this);
                adjustAdid = Preferences.getInstance().getAdjustAdid();
                LoginRequest loginRequest = new LoginByFacebookRequest(
                    user.getAnotherSystemId(), deviceId,
                    notify_token, loginTime, appVersion, AndGApp.advertId, AndGApp.device_name,
                    AndGApp.os_version, adjustAdid, application_name);

                restartRequestServer(LOADER_LOGIN_FACEBOOK,
                    loginRequest);
              }
            } else {
              if (progressDialog != null
                  && progressDialog.isShowing()) {
                progressDialog.dismiss();
              }
            }
          }
        });
    Bundle params = new Bundle();
    params.putString("fields", "id, name, picture, birthday");
    requestLogin.setParameters(params);
    Request.executeBatchAsync(requestLogin);
    params = new Bundle();
    params.putBoolean("redirect", false);
    params.putString("height", "360");
    params.putString("type", "normal");
    params.putString("width", "360");
    /* make the API call */
    new Request(session, "/me/picture", params, HttpMethod.GET,
        new Request.Callback() {
          public void onCompleted(Response response) {
            if (response != null) {
              try {
                GraphObject graphObject = response
                    .getGraphObject();
                JSONObject jsonObject = graphObject
                    .getInnerJSONObject();
                if (jsonObject.has("data")) {
                  JSONObject dataObject = jsonObject
                      .getJSONObject("data");
                  if (dataObject.has("url")) {
                    String link = dataObject
                        .getString("url");
                    UserPreferences.getInstance()
                        .saveFacebookAvatar(link);
                    if (user == null) {
                      user = new User("", null,
                          Constants.GENDER_TYPE_MAN,
                          0, link, "");
                    } else {
                      user.setAvatar(link);
                      String deviceId = Utility
                          .getDeviceId(SignUpActivity.this);
//                                            String notify_token = Preferences
//                                                    .getInstance()
//                                                    .getGCMResitrationId();
                      String notify_token = FirebaseInstanceId.getInstance().getToken();
                      String loginTime = Utility
                          .getLoginTime();
                      String appVersion = Utility
                          .getAppVersionName(SignUpActivity.this);
                      String adjustAdid = "";
                      String application_name = Utility.getApplicationName(SignUpActivity.this);
                      adjustAdid = Preferences.getInstance().getAdjustAdid();
                      LoginRequest loginRequest = new LoginByFacebookRequest(
                          user.getAnotherSystemId(),
                          deviceId, notify_token,
                          loginTime, appVersion, AndGApp.advertId, AndGApp.device_name,
                          AndGApp.os_version, adjustAdid, application_name);

                      restartRequestServer(
                          LOADER_LOGIN_FACEBOOK,
                          loginRequest);
                    }
                  }
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            } else {
              if (progressDialog != null
                  && progressDialog.isShowing()) {
                progressDialog.dismiss();
              }
            }
          }
        }).executeAsync();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == KEY_LOGIN_BY_ANOTHER_SYS) {
      if (resultCode == RESULT_OK) {
        String mocomId = data
            .getStringExtra(WebViewFragment.INTENT_LOGIN_MOCOM_ID);
        String famuId = data
            .getStringExtra(WebViewFragment.INTENT_LOGIN_FAMU_ID);

        // Get basic login data
        String device_id = Utility.getDeviceId(this);
//                String notify_token = Preferences.getInstance()
//                        .getGCMResitrationId();
        String notify_token = FirebaseInstanceId.getInstance().getToken();
        String login_time = Utility.getLoginTime();
        String appVersion = Utility.getAppVersionName(this);

        // Request
        LoginRequest loginRequest = null;
        user = new User();
        if (!TextUtils.isEmpty(mocomId)) {
//                    loginRequest = new LoginByMocomRequest(mocomId, device_id,
//                            notify_token, login_time, appVersion);
//                    restartRequestServer(LOADER_LOGIN_MOCOM, loginRequest);
//
//                    user.setAnotherSystemId(mocomId);
        } else if (!TextUtils.isEmpty(famuId)) {
//                    loginRequest = new LoginByFamuRequest(famuId, device_id,
//                            notify_token, login_time, appVersion);
//                    restartRequestServer(LOADER_LOGIN_FAMU, loginRequest);
//
//                    user.setAnotherSystemId(famuId);
        }
      }
    } else {
      if (Session.getActiveSession() != null) {
        Session.getActiveSession().onActivityResult(this, requestCode,
            resultCode, data);
      }
    }
  }

  @Override
  public void receiveResponse(
      Loader<com.application.connection.Response> loader,
      com.application.connection.Response response) {
    super.receiveResponse(loader, response);
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }

    int loadderId = loader.getId();

    if (loadderId == LOADER_LOGIN_FACEBOOK) {
      LoginResponse loginResponse = ((LoginResponse) response);
      if (loginResponse != null) {
        if (loginResponse.getCode() == com.application.connection.Response.SERVER_EMAIL_NOT_FOUND) {
          // Save info to create new facebook id
          UserPreferences userPreferences = UserPreferences
              .getInstance();
          // Save facebook id
          userPreferences.saveFacebookId(user.getAnotherSystemId());

          // show news if backend setting = true
          NewsPreference
              .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE,
                  true);
          NewsPreference
              .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE,
                  true);

          startRegisterActivity(user);
        } else if (loginResponse.getCode() == com.application.connection.Response.SERVER_SUCCESS) {
          // Save login data
          AuthenticationData authenData = loginResponse
              .getAuthenticationData();
          AuthenticationUtil.saveAuthenticationSuccessData(
              authenData, true);

          // Save info when login success
          UserPreferences userPreferences = UserPreferences
              .getInstance();

          // Save facebook id
          userPreferences.saveFacebookId(user.getAnotherSystemId());

          // show news if backend setting = true
          NewsPreference
              .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE,
                  true);
          NewsPreference
              .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE,
                  true);

          // TODO:
          GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
          googleReviewPreference.saveTurnOffVersion(loginResponse
              .getSwitchBrowserVersion());
          googleReviewPreference.saveEnableGetFreePoint(loginResponse
              .isEnableGetFreePoint());
          googleReviewPreference.saveIsTurnOffUserInfo(loginResponse.isTurnOffUserInfo());

          int finishRegister = userPreferences.getFinishRegister();

          if (finishRegister == Constants.FINISH_REGISTER_NO) {
            startCustomeActivityForResult(new Intent(this,
                ProfileRegisterActivity.class));
          } else {
            startCustomeActivityForResult(new Intent(this,
                MainActivity.class));
          }

          customeFinishActivity();
        } else {
          com.application.ui.customeview.ErrorApiDialog.showAlert(
              this, R.string.common_error, response.getCode());
        }
      }
    } else if (loadderId == LOADER_LOGIN_MOCOM
        || loadderId == LOADER_LOGIN_FAMU) {
      LoginResponse loginResponse = ((LoginResponse) response);
      if (loginResponse != null) {
        if (response.getCode() == com.application.connection.Response.SERVER_EMAIL_NOT_FOUND) {
          // Save info to create new facebook id
          UserPreferences userPreferences = UserPreferences
              .getInstance();
          // Save mocom id
          if (loadderId == LOADER_LOGIN_MOCOM) {
            userPreferences.saveMocomId(user.getAnotherSystemId());
          } else {
            userPreferences.saveFamuId(user.getAnotherSystemId());
          }
          startRegisterActivity(user);
        } else if (response.getCode() == com.application.connection.Response.SERVER_SUCCESS) {
          AuthenticationData authenData = loginResponse
              .getAuthenticationData();
          // Save info when login success
          UserPreferences userPreferences = UserPreferences
              .getInstance();
          userPreferences.saveSuccessLoginData(authenData, true);

          Preferences preferences = Preferences.getInstance();
          preferences.saveTimeSetting(authenData);
          preferences.savePointSetting(authenData);

          // Login time
          preferences.saveTimeRelogin(System.currentTimeMillis());

          // Save mocom id
          if (loadderId == LOADER_LOGIN_MOCOM) {
            userPreferences.saveMocomId(user.getAnotherSystemId());
          } else {
            userPreferences.saveFamuId(user.getAnotherSystemId());
          }

          int finishRegister = userPreferences.getFinishRegister();

          if (finishRegister == Constants.FINISH_REGISTER_NO) {
            startCustomeActivityForResult(new Intent(this,
                ProfileRegisterActivity.class));
          } else {
            startCustomeActivityForResult(new Intent(this,
                MainActivity.class));
          }
          customeFinishActivity();
        } else {
          com.application.ui.customeview.ErrorApiDialog.showAlert(
              this, R.string.common_error, response.getCode());
        }
      }
    } else if (loadderId == LOADER_INSTALL_COUNT) {
      int code = response.getCode();
      switch (code) {
        case com.application.connection.Response.SERVER_SUCCESS:
          Preferences preferences = Preferences.getInstance();
          preferences.saveIsInstalled();
          if (!preferences.isAttachCmcode()) {
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
    }
  }

  @Override
  public com.application.connection.Response parseResponse(int loaderID,
      ResponseData data, int requestType) {
    switch (loaderID) {
      case LOADER_LOGIN_FACEBOOK:
      case LOADER_LOGIN_MOCOM:
      case LOADER_LOGIN_FAMU:
        return new LoginResponse(data);
      case LOADER_INSTALL_COUNT:
        return new InstallCountResponse(data);
      case LOADER_RETRY_LOGIN:
        return new LoginResponse(data);
      case LOADER_GET_USER_STATUS:
        return new GetUserStatusResponse(data);
      default:
        return null;
    }
  }

  @Override
  public void onBaseLoaderReset(
      Loader<com.application.connection.Response> loader) {
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_LOGIN_FACEBOOK || loaderId == LOADER_LOGIN_MOCOM
        || loaderId == LOADER_LOGIN_FAMU
        || loaderId == LOADER_INSTALL_COUNT) {
      if (progressDialog == null) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.waiting));
      }
      if (!progressDialog.isShowing()) {
        progressDialog.show();
      }
    }
  }

  @Override
  public void onBackPressed() {

    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }

    AlertDialog mDialog = new CustomConfirmDialog(this, null, getString(R.string.message_end_app),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            SignUpActivity.super.onBackPressed();
          }
        })
        .setNegativeButton(0, null)
        .create();
    mDialog.show();

    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }

  private class MyPagerAdapter extends PagerAdapter {

    int[] images = new int[]{R.drawable.guide_1, R.drawable.guide_2,
        R.drawable.guide_3, R.drawable.guide_4};

    @Override
    public int getCount() {
      return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      View view = View.inflate(getApplicationContext(),
          R.layout.item_pager_introduction, null);
      ImageView img = (ImageView) view
          .findViewById(R.id.item_pager_introduction_img);
      img.setImageResource(images[position]);
      ((ViewPager) container).addView(view);
      return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      ((ViewPager) container).removeView((View) object);
    }

  }
}
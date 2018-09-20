package com.application.ui.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.application.AndGApp;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.LoginByEmailRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.MainActivity;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.NewsPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import glas.bbsystem.R;
import org.linphone.LinphoneService;


public class LoginActivity extends BaseFragmentActivity implements
    ResponseReceiver, OnNavigationClickListener {

  private static final int LOADER_LOGIN = 0;
  private Button mbtnLogin;
  private EditText medtEmail;
  private EditText medtPassword;
  private TextView mTxtForgotPass;
  private ProgressDialog mProgressDialog;
  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_login);
    initView();
  }

  /**
   * Check whether or not register GCM ID
   */

  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationLeftLogo(R.drawable.navigation_back);
    getNavigationBar().setCenterTitle(R.string.login_login_button);
  }

  private void initView() {
    mbtnLogin = (Button) findViewById(R.id.button_login);
    medtEmail = (EditText) findViewById(R.id.email);
    mTxtForgotPass = (TextView) findViewById(R.id.textview_forgot_pass);
    final EditText edtEmailFake = (EditText) findViewById(R.id.email_fake);
    medtEmail.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before,
          int count) {
        if (medtEmail.getText().length() == 0) {
          edtEmailFake.setText("");
        } else {
          edtEmailFake.setText(" ");
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    medtPassword = (EditText) findViewById(R.id.password);
    medtEmail.setNextFocusDownId(medtPassword.getId());
    final EditText edtPassFake = (EditText) findViewById(R.id.password_fake);
    medtPassword.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before,
          int count) {
        if (medtPassword.getText().length() == 0) {
          edtPassFake.setText("");
        } else {
          edtPassFake.setText(" ");
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setMessage(getString(R.string.waiting));
    mbtnLogin.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isValidParams()) {
          login();
          Utility.hideSoftKeyboard(LoginActivity.this);
        }
      }
    });

    mTxtForgotPass.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startCustomeActivityForResult(new Intent(LoginActivity.this,
            ForgotPasswordSendEmail.class));
      }
    });
  }

  private void login() {
    String email = medtEmail.getText().toString();
    String password = medtPassword.getText().toString();
    password = Utility.encryptPassword(password);
//        String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    String appVersion = Utility.getAppVersionName(this);
    String adjustAdid = "";
    String applicationName = Utility.getApplicationName(this);
    adjustAdid = Preferences.getInstance().getAdjustAdid();
    LoginByEmailRequest loginRequest = new LoginByEmailRequest(email,
        password, Utility.getDeviceId(this), notify_token,
        Utility.getLoginTime(), appVersion, AndGApp.advertId, AndGApp.device_name,
        AndGApp.os_version, adjustAdid, applicationName);
    restartRequestServer(LOADER_LOGIN, loginRequest);
    mProgressDialog.show();
  }

  private boolean isValidParams() {
    String email = medtEmail.getText().toString();
    if (email != null && email.replace("\u3000", " ").trim().length() <= 0) {
      ErrorApiDialog.showAlert(this, getString(R.string.login),
          getString(R.string.email_is_empty));
      return false;
    }
    if (!Utility.isValidEmail(email)) {
      ErrorApiDialog.showAlert(this, getString(R.string.login),
          getString(R.string.email_invalid_format));

      return false;
    }
    if (medtEmail.getText().length() > UserSetting.MAX_EMAIL_lENGTH) {
      String msg = getString(R.string.email_length_must_than,
          UserSetting.MAX_EMAIL_lENGTH);
      String title = getString(R.string.login);
      ErrorApiDialog.showAlert(this, title, msg);
      return false;
    }
    // check empty verify code
    int length = medtPassword.getText().toString().length();
    if (medtPassword.getText().toString().equals("")) {
      String title = getString(R.string.login);
      String msg = getString(R.string.empty_pass);
      ErrorApiDialog.showAlert(this, title, msg);
      return false;
    } else if (length < UserSetting.MIN_PASSWORD_LENGTH && length > 0
        || length > UserSetting.MAX_PASSWORD_LENGTH) {
      String msg = String.format(
          getString(R.string.password_length_must_than),
          UserSetting.MIN_PASSWORD_LENGTH,
          UserSetting.MAX_PASSWORD_LENGTH);
      String title = getString(R.string.login);
      ErrorApiDialog.showAlert(this, title, msg);
      return false;
    }
    return true;
  }

  private void loginSuccess(LoginResponse loginResponse) {
    // Save token
    UserPreferences userPreferences = UserPreferences.getInstance();
    String email = medtEmail.getText().toString();
    String pass = medtPassword.getText().toString();
    pass = Utility.encryptPassword(pass);
    userPreferences.saveAuthentication(email, pass);
    // Save info when login success
    AuthenticationData authenData = loginResponse.getAuthenticationData();
    userPreferences.saveSuccessLoginData(authenData, true);

    // show news if backend setting = true
    NewsPreference
        .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE, true);
    NewsPreference
        .setShowNews(getApplicationContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE, true);

    Preferences preferences = Preferences.getInstance();
    preferences.saveTimeSetting(authenData);
    preferences.savePointSetting(authenData);

    // Login time
    preferences.saveTimeRelogin(System.currentTimeMillis());

    // Get banned word
    DataFetcherService.startLoadDirtyWord(this);

    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    googleReviewPreference.saveTurnOffVersion(loginResponse
        .getSwitchBrowserVersion());
    googleReviewPreference.saveEnableGetFreePoint(loginResponse
        .isEnableGetFreePoint());
    googleReviewPreference.saveIsTurnOffUserInfo(loginResponse.isTurnOffUserInfo());

    startCustomeActivityForResult(new Intent(this, MainActivity.class));
    customeFinishActivity();
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    if (response == null) {
      return;
    }

    if (loader.getId() == LOADER_LOGIN) {
      if (response.getCode() == Response.SERVER_SUCCESS) {
        LoginResponse loginResponse = (LoginResponse) response;
        loginSuccess(loginResponse);

        LinphoneService.startLogin(getApplicationContext());
        Constants.checkopenURLcmcode = true;
      } else {
        ErrorApiDialog.showAlert(this, R.string.login_failed_dialog_title,
            response.getCode());
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, final ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_LOGIN:
      case LOADER_RETRY_LOGIN:
        response = new LoginResponse(data);
        break;
      case LOADER_GET_USER_STATUS:
        response = new GetUserStatusResponse(data);
        break;
      default:
        break;
    }
    return response;

  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onNavigationLeftClick(View view) {
    finish();
  }

  @Override
  public void onNavigationRightClick(View view) {
    startCustomeActivityForResult(new Intent(this,
        ForgotPasswordSendEmail.class));
  }

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  public boolean isNoTitle() {
    return false;
  }

  private void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
  }
}

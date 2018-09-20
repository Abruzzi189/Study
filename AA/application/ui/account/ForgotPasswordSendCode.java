package com.application.ui.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.SendCodeRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.SendCodeResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.MainActivity;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import glas.bbsystem.R;


public class ForgotPasswordSendCode extends BaseFragmentActivity implements
    ResponseReceiver {

  private static final int LOADER_SEND_EMAIL = 0;

  private ProgressDialog mProgressDialog;
  private String mReceiverEmail = "";

  private EditText medtVerifyCode;
  private EditText medtPassword;
  private Button mbtnSend;
  private String mNewPassword = "";

  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_forgotpassword_sendcode);
    initView();

    Intent startedIntent = getIntent();
    if (startedIntent != null) {
      mReceiverEmail = startedIntent
          .getStringExtra(Constants.INTENT_RECEIVER_EMAIL);
    }
  }

  private void initView() {

    medtVerifyCode = (EditText) findViewById(R.id.activity_forgotpassword_sendcode_edt_verifycode);
    medtPassword = (EditText) findViewById(R.id.activity_forgotpassword_sendcode_edt_newpassword);
    mbtnSend = (Button) findViewById(R.id.activity_forgotpassword_sendcode_btn_send);

    mbtnSend.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (isValidParams()) {
          requestChangePass();
        }
      }
    });

  }

  private void requestChangePass() {
    String deviceId = Utility.getDeviceId(getApplicationContext());
    String email = mReceiverEmail;
    String vft_code = medtVerifyCode.getText().toString();
    String original = medtPassword.getText().toString();
    mNewPassword = Utility.encryptPassword(original);
//        String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    String appVersion = Utility.getAppVersionName(this);
    SendCodeRequest codeRequest = new SendCodeRequest(deviceId, email,
        vft_code, original, mNewPassword, Utility.getLoginTime(),
        notify_token, appVersion);
    restartRequestServer(LOADER_SEND_EMAIL, codeRequest);
  }

  private boolean isValidParams() {
    // check empty verify code
    if (medtVerifyCode.getText().toString().replace("\u3000", " ").trim()
        .length() == 0) {
      ErrorApiDialog.showAlert(this, getString(R.string.forgot_password),
          getString(R.string.verify_code_empty));
      return false;
    }

    // check empty verify code
    int length = medtPassword.getText().toString().length();
    if (length < UserSetting.MIN_PASSWORD_LENGTH
        || length > UserSetting.MAX_PASSWORD_LENGTH) {

      String msg = String.format(
          getString(R.string.password_length_must_than),
          UserSetting.MIN_PASSWORD_LENGTH,
          UserSetting.MAX_PASSWORD_LENGTH);
      String title = getString(R.string.forgot_password);
      ErrorApiDialog.showAlert(this, title, msg);
      return false;
    }

    return true;
  }

  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(
        R.string.login_forgot_verify_dialog_title);
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_SEND_EMAIL) {
      mProgressDialog = ProgressDialog.show(this, "", getString(R.string.send_code), false);
    }
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
    if (loader.getId() == LOADER_SEND_EMAIL) {
      if (response.getCode() == Response.SERVER_SUCCESS) {
        SendCodeResponse sendCodeResponse = (SendCodeResponse) response;
        handleForgotPasswordSuccess(sendCodeResponse);
      } else {
        ErrorApiDialog.showAlert(this, R.string.common_error,
            response.getCode());
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_SEND_EMAIL) {
      return new SendCodeResponse(data);
    } else if (loaderID == LOADER_RETRY_LOGIN) {
      return new LoginResponse(data);
    } else if (loaderID == LOADER_GET_USER_STATUS) {
      return new GetUserStatusResponse(data);
    } else {
      return null;
    }

  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  private void handleForgotPasswordSuccess(SendCodeResponse codeResponse) {
    Toast.makeText(getApplicationContext(),
        R.string.change_password_success, Toast.LENGTH_LONG).show();

    // Save login data
    AuthenticationData authenData = codeResponse.getAuthenticationData();
    AuthenticationUtil.saveAuthenticationSuccessData(authenData, false);

    // Save blocked users list
    String blockUser = codeResponse.getBlockedUserList();
    BlockUserPreferences.getInstance().saveBlockedUsersList(blockUser);

    // Save email
    UserPreferences.getInstance().saveEmail(mReceiverEmail);

    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_NEW_TASK);
    startCustomeActivityForResult(intent);
    customeFinishActivity();
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

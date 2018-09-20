package com.application.ui.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.SendEmailRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.SendEmailResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.Utility;
import glas.bbsystem.R;


public class ForgotPasswordSendEmail extends BaseFragmentActivity implements
    ResponseReceiver {

  private static final String TAG = "ForgotPasswordSendEmail";

  private static final int LOADER_SEND_EMAIL = 0;

  private ProgressDialog mProgressDialog;
  private EditText medtEmail;
  private Button mbtnSend;

  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_forgotpassword_sendemail);
    initView();
  }

  private void initView() {
    medtEmail = (EditText) findViewById(R.id.activity_forgotpassword_sendemail_edt_email);
    mbtnSend = (Button) findViewById(R.id.activity_forgotpassword_sendemail_btn);

    mbtnSend.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (isValidParams()) {
          SendEmailRequest emailRequest = new SendEmailRequest(
              medtEmail.getText().toString());
          restartRequestServer(LOADER_SEND_EMAIL, emailRequest);
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.forgot_password);
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
        String message = getString(R.string.message_code_sent);
        AlertDialog mDialog = new CustomConfirmDialog(this, null, message, false)
            .setPositiveButton(0, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Intent intentForgotPasswordSendCode = new Intent(
                    ForgotPasswordSendEmail.this,
                    ForgotPasswordSendCode.class);
                intentForgotPasswordSendCode.putExtra(
                    Constants.INTENT_RECEIVER_EMAIL, medtEmail
                        .getText().toString());
                startCustomeActivityForResult(intentForgotPasswordSendCode);
              }
            })
            .create();
        mDialog.show();

        int dividerId = mDialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = mDialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(
              mDialog.getContext().getResources().getColor(R.color.transparent));
        }
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
      return new SendEmailResponse(data);
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

  /*
   * @Override public void onNavigationLeftClick(View view) { finish(); }
   *
   * @Override public void onNavigationRightClick(View view) { // Do nothing
   *
   * }
   */

  private boolean isValidParams() {
    String email = medtEmail.getText().toString();

    if (email != null && email.replace("\u3000", " ").trim().length() <= 0) {
      ErrorApiDialog.showAlert(this, getString(R.string.forgot_password),
          getString(R.string.email_is_empty));
      return false;
    }
    if (!Utility.isValidEmail(email)) {
      ErrorApiDialog.showAlert(this, getString(R.string.forgot_password),
          getString(R.string.email_invalid_format));

      return false;
    }
    if (medtEmail.getText().length() > UserSetting.MAX_EMAIL_lENGTH) {
      String msg = getString(R.string.email_length_must_than,
          UserSetting.MAX_EMAIL_lENGTH);
      String title = getString(R.string.forgot_password);
      ErrorApiDialog.showAlert(this, title, msg);
      return false;
    }
    return true;
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

package com.application.ui.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.ChangeEmailRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UpdateInfoFlagRequest;
import com.application.connection.response.ChangeEmailResponse;
import com.application.connection.response.ChangePasswordResponse;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.UpdateInfoFlagResponse;
import com.application.constant.UserSetting;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ChangePasswordActivity extends BaseFragmentActivity {

  private static final int LOADER_INFO_FLAG_ID = 0;
  private static final int LOADER_CHANGE_EMAIL_ID = 1;
  private static final int LOADER_CHANGE_PASSWORD_ID = 2;

  private ViewGroup mRootView;
  private TextView mEdtOldEmail;
  private EditText mEdtNewEmail;
  private EditText mEdtOldPassword;
  private EditText mEdtNewPassword;
  private EditText mEdtConfirmPassword;
  private ProgressDialog mProgressDialog;
  private AlertDialog mAlertDialog;
  private NoFragmentActionBar mActionBar;
  private AlertDialog mChangeEmailPassDialog;
  private UpdateInfoFlagResponse mUpdateInfoFlagResponse;

  private ChangePasswordHandler mChangePasswordHandler;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_changepassword);
    findViews();
    requestUserInfoFlag();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mChangeEmailPassDialog != null && mChangeEmailPassDialog.isShowing()) {
      mChangeEmailPassDialog.dismiss();
    }
  }

  private void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
  }

  private void findViews() {
    mRootView = (ViewGroup) findViewById(R.id.root_view);
    mEdtOldEmail = (TextView) findViewById(R.id.activity_changepassword_edt_oldemail);
    mEdtNewEmail = (EditText) findViewById(R.id.activity_changepassword_edt_newemail);
    mEdtConfirmPassword = (EditText) findViewById(R.id.activity_changepassword_edt_confirmpassword);
    mEdtNewPassword = (EditText) findViewById(R.id.activity_changepassword_edt_newpassword);
    mEdtOldPassword = (EditText) findViewById(R.id.activity_changepassword_edt_oldpassword);
  }

  private void requestUserInfoFlag() {
    String token = UserPreferences.getInstance().getToken();
    UpdateInfoFlagRequest infoFlagRequest = new UpdateInfoFlagRequest(token);
    requestServer(LOADER_INFO_FLAG_ID, infoFlagRequest);
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_INFO_FLAG_ID
        || loaderId == LOADER_CHANGE_EMAIL_ID
        || loaderId == LOADER_CHANGE_PASSWORD_ID) {
      mProgressDialog = ProgressDialog.show(this, "",
          getString(R.string.waiting), false);
    }
  }


  private void handleUploadInfoResponse(UpdateInfoFlagResponse response) {
    mUpdateInfoFlagResponse = response;
    if (response.getCode() == Response.SERVER_SUCCESS) {
      setChangePasswordHandler((UpdateInfoFlagResponse) response);
    } else {
      showErrorDialog(response);
    }
  }

  private void setChangePasswordHandler(UpdateInfoFlagResponse response) {
    boolean isUpdateEmail = response.isUpdateEmail();
    if (!isUpdateEmail) {
      mChangePasswordHandler = new ChangePasswordFirstTime(getApplicationContext());
    } else {
      mChangePasswordHandler = new ChangePasswordSecondTime(getApplicationContext());
    }
    mChangePasswordHandler.rootView = mRootView;
    mChangePasswordHandler.edtOldEmail = mEdtOldEmail;
    mChangePasswordHandler.edtNewEmail = mEdtNewEmail;
    mChangePasswordHandler.edtOldPassword = mEdtOldPassword;
    mChangePasswordHandler.edtNewPassword = mEdtNewPassword;
    mChangePasswordHandler.edtConfirmPassword = mEdtConfirmPassword;
    mChangePasswordHandler.showViews();
  }

  private void showErrorDialog(UpdateInfoFlagResponse response) {
    ErrorApiDialog.showAlert(this, R.string.change_email_password_title,
        response.getCode(), new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        }, false);
  }

  private void handleChangeEmailResponse(Response response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      mChangePasswordHandler.handleResponseSuccess();
      if (mChangeEmailPassDialog == null) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);
        AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
            .setText(R.string.change_email_password_title);
        builder.setCustomTitle(customTitle);
        //builder.setTitle(R.string.change_email_password_title);
        builder.setMessage(R.string.change_email_password_success);
        builder.setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                finish();
              }
            });
        builder.setCancelable(false);
        mChangeEmailPassDialog = builder.create();
      }
      mChangeEmailPassDialog.show();
      int dividerId = this.getResources().getIdentifier("android:id/titleDivider", null, null);
      View divider = this.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    } else {
      ErrorApiDialog.showAlert(this,
          R.string.change_email_password_title, response.getCode());
    }

  }

  public void onChangeEmailPassword() {
    if (mChangePasswordHandler == null) {
      showErrorDialog(mUpdateInfoFlagResponse);
    }
    String error = mChangePasswordHandler.validParams();
    if (!TextUtils.isEmpty(error)) {
      handleInvalid(error);
    } else {
      requestChangeEmailPassword();
    }
  }

  private void handleInvalid(String error) {
    if (mAlertDialog != null && mAlertDialog.isShowing()) {
      mAlertDialog.dismiss();
    }
    mAlertDialog = new CustomConfirmDialog(this, "", error, false)
        .create();
    mAlertDialog.show();
    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mAlertDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void requestChangeEmailPassword() {
    RequestParams request = mChangePasswordHandler.buildRequest();
    if (request != null) {
      if (request instanceof ChangeEmailRequest) {
        restartRequestServer(LOADER_CHANGE_EMAIL_ID, request);
      } else {
        restartRequestServer(LOADER_CHANGE_PASSWORD_ID, request);
      }
    }
  }

  @Override
  protected boolean isNoTitle() {
    return false;
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (data == null) {
      return null;
    }
    if (loaderID == LOADER_INFO_FLAG_ID) {
      return new UpdateInfoFlagResponse(data);
    }

    if (loaderID == LOADER_CHANGE_EMAIL_ID) {
      return new ChangeEmailResponse(data);
    }

    if (loaderID == LOADER_CHANGE_PASSWORD_ID) {
      return new ChangePasswordResponse(data);
    }

    if (loaderID == LOADER_RETRY_LOGIN) {
      return new LoginResponse(data);
    }

    if (loaderID == LOADER_GET_USER_STATUS) {
      return new GetUserStatusResponse(data);
    }

    return null;
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    if (response instanceof UpdateInfoFlagResponse) {
      handleUploadInfoResponse((UpdateInfoFlagResponse) response);
    }
    if (response instanceof ChangeEmailResponse
        || response instanceof ChangePasswordResponse) {
      handleChangeEmailResponse(response);
    }
  }


  /**
   * ************************************** Inner class*******************************************
   */

  public static abstract class ChangePasswordHandler {

    public ViewGroup rootView;
    public TextView edtOldEmail;
    public EditText edtNewEmail;
    public EditText edtOldPassword;
    public EditText edtNewPassword;
    public EditText edtConfirmPassword;
    public Context context;

    public ChangePasswordHandler(Context context) {
      this.context = context;
    }

    public abstract void showViews();

    public abstract String validParams();

    public abstract RequestParams buildRequest();

    public abstract void handleResponseSuccess();

    protected String validNewEmail(String email) {
      if (TextUtils.isEmpty(email)) {
        return context.getString(R.string.email_is_empty_message);
      }

      if (!Utility.isValidEmail(email)) {
        return context.getString(R.string.email_invalid_format);
      }
      return null;
    }

    protected String validNewPass(String pass) {
      if (TextUtils.isEmpty(pass)) {
        return context
            .getString(R.string.new_password_is_empty_message);
      }
      if (pass.length() < UserSetting.MIN_PASSWORD_LENGTH
          || pass.length() > UserSetting.MAX_PASSWORD_LENGTH) {
        return context.getString(R.string.password_out_of_range);
      }
      return null;
    }
  }

}

package com.application.ui.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ChangeEmailRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UpdateInfoFlagRequest;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.response.ChangeEmailResponse;
import com.application.connection.response.ChangePasswordResponse;
import com.application.connection.response.UpdateInfoFlagResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.ui.BaseFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


/**
 * Flow : - requestUserInfo    (82) - Server reponse     (273) - handleUploadInfoResponse -
 * setChangePasswordHandler - NativeActionBar wating click ===> onChangeEmailPassword
 */

public class ChangePasswordFragment extends BaseFragment implements ResponseReceiver {

  private static final int LOADER_INFO_FLAG_ID = 0;
  private static final int LOADER_CHANGE_EMAIL_ID = 1;
  private static final int LOADER_CHANGE_PASSWORD_ID = 2;
  private static final int LOADER_GET_USER_INFO = 3;

  private ViewGroup mRootView;
  private TextView mEdtOldEmail;
  private EditText mEdtNewEmail, mEdtOldPassword, mEdtNewPassword, mEdtConfirmPassword;
  private ProgressDialog mProgressDialog;
  private AlertDialog mAlertDialog;
  private AlertDialog mChangeEmailPassDialog;
  private UpdateInfoFlagResponse mUpdateInfoFlagResponse;

  /**
   * ChangePasswordHandler is abstract class using handler check valid email or password
   */
  private ChangePasswordActivity.ChangePasswordHandler mChangePasswordHandler;


  public static ChangePasswordFragment newInstance() {
    return new ChangePasswordFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_changepassword, container, false);
    initViews(view);
    return view;
  }

  private void initViews(View view) {
    mRootView = (ViewGroup) view.findViewById(R.id.root_view);
    mEdtOldEmail = (TextView) view.findViewById(R.id.activity_changepassword_edt_oldemail);
    mEdtNewEmail = (EditText) view.findViewById(R.id.activity_changepassword_edt_newemail);

    mEdtConfirmPassword = (EditText) view
        .findViewById(R.id.activity_changepassword_edt_confirmpassword);
    mEdtNewPassword = (EditText) view.findViewById(R.id.activity_changepassword_edt_newpassword);
    mEdtOldPassword = (EditText) view.findViewById(R.id.activity_changepassword_edt_oldpassword);
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    requestUserInfo();
    requestUserInfoFlag();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mChangeEmailPassDialog != null && mChangeEmailPassDialog.isShowing()) {
      mChangeEmailPassDialog.dismiss();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Utility.hideSoftKeyboard(getActivity());
  }


  private void requestUserInfo() {
    String token = UserPreferences.getInstance().getToken();
    String userId = UserPreferences.getInstance().getUserId();
    UserInfoRequest userInfoRequest = new UserInfoRequest(token, userId);
    requestServer(LOADER_GET_USER_INFO, userInfoRequest);
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
      mProgressDialog = ProgressDialog.show(getActivity(), "",
          getString(R.string.waiting), false);
    }
  }

  //--------------------------------------------------------------------------------------------

  private void handleUploadInfoResponse(UpdateInfoFlagResponse response) {
    mUpdateInfoFlagResponse = response;
    if (response.getCode() == Response.SERVER_SUCCESS) {
      setChangePasswordHandler((UpdateInfoFlagResponse) response);
    } else {
      showErrorDialog(response);
    }
  }


  /**
   * ChangePasswordFirstTime(getActivity()) is first login ---> register new email for user
   * ChangePasswordSecondTime(getContext())  is change password
   */

  private void setChangePasswordHandler(UpdateInfoFlagResponse response) {
    boolean isUpdateEmail = response.isUpdateEmail();
    if (!isUpdateEmail) {
      mChangePasswordHandler = new ChangePasswordFirstTime(getActivity());
    } else {
      mChangePasswordHandler = new ChangePasswordSecondTime(getContext());
    }
    mChangePasswordHandler.rootView = mRootView;
    mChangePasswordHandler.edtOldEmail = mEdtOldEmail;
    mChangePasswordHandler.edtNewEmail = mEdtNewEmail;
    mChangePasswordHandler.edtOldPassword = mEdtOldPassword;
    mChangePasswordHandler.edtNewPassword = mEdtNewPassword;
    mChangePasswordHandler.edtConfirmPassword = mEdtConfirmPassword;
    mChangePasswordHandler.showViews();
  }

  //--------------------------------------------------------------------------------------------


  /**
   * Call when click rightButton on Navigation (NativeActionBar _ line 432,433)
   */
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


  private void handleChangeEmailResponse(Response response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      mChangePasswordHandler.handleResponseSuccess();

      if (mChangeEmailPassDialog == null) {
        View customTitle = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_customize, null);
        AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
        ((TextView) customTitle
            .findViewById(R.id.tv_title_dialog_customize))
            .setText(R.string.change_email_password_title);
        builder.setCustomTitle(customTitle);
        builder.setMessage(R.string.change_email_password_success);

        builder.setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                dialog.dismiss();
                mNavigationManager.goBack();
              }
            });
        builder.setCancelable(false);
        mChangeEmailPassDialog = builder.create();
      }

      mChangeEmailPassDialog.show();
      int dividerId = this.getResources().getIdentifier("android:id/titleDivider", null, null);
      View divider = getActivity().findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    } else {
      ErrorApiDialog.showAlert(getActivity(),
          R.string.change_email_password_title, response.getCode());
    }
  }

  private void handleInvalid(String error) {
    if (mAlertDialog != null && mAlertDialog.isShowing()) {
      mAlertDialog.dismiss();
    }
    mAlertDialog = new CustomConfirmDialog(getActivity(), null, error, false)
        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
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


  private void showErrorDialog(UpdateInfoFlagResponse response) {
    ErrorApiDialog.showAlert(getActivity(), R.string.change_email_password_title,
        response.getCode(), new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            mNavigationManager.goBack();
          }
        }, false);
  }

  private void handleGetUserInfo(UserInfoResponse response) {
    UserPreferences.getInstance().saveEmail(response.getEmail());
    mEdtOldEmail.setText(response.getEmail());
  }


  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }


  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
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

    if (loaderID == LOADER_GET_USER_INFO) {
      return new UserInfoResponse(data);
    }

    return null;
  }


  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }

    if (response instanceof UpdateInfoFlagResponse) {
      handleUploadInfoResponse((UpdateInfoFlagResponse) response);
    }

    if (response instanceof ChangeEmailResponse || response instanceof ChangePasswordResponse) {
      handleChangeEmailResponse(response);
    }

    if (response instanceof UserInfoResponse) {
      handleGetUserInfo((UserInfoResponse) response);
    }

  }
}

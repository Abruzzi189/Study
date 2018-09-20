package com.application.ui.account;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.application.connection.request.ChangeEmailRequest;
import com.application.connection.request.ChangePasswordRequest;
import com.application.connection.request.RequestParams;
import com.application.constant.UserSetting;
import com.application.ui.account.ChangePasswordActivity.ChangePasswordHandler;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

/**
 * This class handle change password
 */

public class ChangePasswordSecondTime extends ChangePasswordHandler {

  private ChangeType typeChange;
  private String newEmail;
  private String newPass;
  private String oldPass;
  private String confirmPass;
  public ChangePasswordSecondTime(Context context) {
    super(context);
  }

  @Override
  public void showViews() {
    rootView.findViewById(R.id.current_email_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.new_email_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.old_password_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.new_password_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.confirm_password_layout).setVisibility(View.VISIBLE);
    edtOldEmail.setText(UserPreferences.getInstance().getEmail());
  }

  @Override
  public String validParams() {
    newEmail = edtNewEmail.getText().toString();
    newEmail = newEmail.replace("\u3000", " ").trim();
    edtNewEmail.setText(newEmail);
    oldPass = edtOldPassword.getText().toString();
    newPass = edtNewPassword.getText().toString();
    confirmPass = edtConfirmPassword.getText().toString();

    boolean changePass = !TextUtils.isEmpty(newPass);
    boolean changeEmail = !TextUtils.isEmpty(newEmail);
    if (changePass && !changeEmail) {
      typeChange = ChangeType.PASSWORD;
      return validChangePass();
    } else if (!changePass && changeEmail) {
      typeChange = ChangeType.EMAIL;
      return validChangeEmail();
    } else if (changePass && changeEmail) {
      typeChange = ChangeType.BOTH;
      return validChangeBoth();
    } else {
      typeChange = ChangeType.NONE;
      return context
          .getString(R.string.please_input_email_or_password_to_change);
    }
  }

  private String validChangePass() {
    // check old pass
    String error = validOldPass(oldPass);
    if (error != null) {
      return error;
    }

    // check new password
    error = validNewPass(newPass);
    if (error != null) {
      return error;
    }

    // check same new password
    if (!newPass.equals(confirmPass)) {
      return context.getString(R.string.retype_password_is_not_the_same);
    }
    return null;
  }

  private String validChangeEmail() {
    // check old pass
    String error = validOldPass(oldPass);
    if (error != null) {
      return error;
    }
    // check email
    return validNewEmail(newEmail);
  }

  private String validChangeBoth() {
    // check old pass
    String error = validOldPass(oldPass);
    if (error != null) {
      return error;
    }
    // check email
    error = validChangeEmail();
    if (error != null) {
      return error;
    }
    // check new pass
    error = validChangePass();
    return error;
  }

  private String validOldPass(String oldPass) {
    if (TextUtils.isEmpty(oldPass)) {
      return context.getString(R.string.old_password_is_empty);
    }
    if (oldPass.length() < UserSetting.MIN_PASSWORD_LENGTH
        || oldPass.length() > UserSetting.MAX_PASSWORD_LENGTH) {
      return context.getString(R.string.password_out_of_range);
    }
    return null;
  }

  @Override
  public RequestParams buildRequest() {
    newEmail = edtNewEmail.getText().toString();
    oldPass = Utility.encryptPassword(edtOldPassword.getText().toString());
    String originPass = edtNewPassword.getText().toString();
    newPass = Utility.encryptPassword(originPass);

    String token = UserPreferences.getInstance().getToken();
    ChangeEmailRequest requestEmail = new ChangeEmailRequest(token);
    if (typeChange == ChangeType.EMAIL) {
      requestEmail.setOldPass(oldPass);
      requestEmail.setEmail(newEmail);
      return requestEmail;
    } else if (typeChange == ChangeType.BOTH) {
      requestEmail.setOldPass(oldPass);
      requestEmail.setEmail(newEmail);
      requestEmail.setNewOriginalPass(originPass);
      requestEmail.setNewPass(newPass);
      return requestEmail;
    } else if (typeChange == ChangeType.PASSWORD) {
      return new ChangePasswordRequest(token, oldPass, originPass,
          newPass);
    }
    return null;
  }

  @Override
  public void handleResponseSuccess() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    if (typeChange == ChangeType.EMAIL) {
      userPreferences.saveEmail(newEmail);
    } else if (typeChange == ChangeType.PASSWORD) {
      userPreferences.savePassword(newPass);
    } else if (typeChange == ChangeType.BOTH) {
      userPreferences.saveEmail(newEmail);
      userPreferences.savePassword(newPass);
    }
  }

  private enum ChangeType {
    NONE, EMAIL, PASSWORD, BOTH
  }

}

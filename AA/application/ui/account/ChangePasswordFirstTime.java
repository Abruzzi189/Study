package com.application.ui.account;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import com.application.connection.request.ChangeEmailRequest;
import com.application.connection.request.RequestParams;
import com.application.ui.account.ChangePasswordActivity.ChangePasswordHandler;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


/**
 * This class handle register new email for user
 */

public class ChangePasswordFirstTime extends ChangePasswordHandler {

  private String newEmail;
  private String newPass;

  public ChangePasswordFirstTime(Context context) {
    super(context);
  }

  @Override
  public void showViews() {
    Resources resources = rootView.getContext().getResources();
    rootView.findViewById(R.id.current_email_layout).setVisibility(View.GONE);
    rootView.findViewById(R.id.old_password_layout).setVisibility(View.GONE);

    rootView.findViewById(R.id.new_email_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.new_password_layout).setVisibility(View.VISIBLE);
    rootView.findViewById(R.id.confirm_password_layout).setVisibility(View.VISIBLE);

    ((EditText) rootView
        .findViewById(R.id.activity_changepassword_edt_newemail))
        .setHint(resources.getString(R.string.email_hint_first));

    ((EditText) rootView
        .findViewById(R.id.activity_changepassword_edt_newpassword))
        .setHint(resources.getString(R.string.first_password_hint));

  }

  @Override
  public String validParams() {

    String newEmail = edtNewEmail.getText().toString();
    newEmail = newEmail.replace("\u3000", " ").trim();
    edtNewEmail.setText(newEmail);

    String newPass = edtNewPassword.getText().toString();
    String confirmPass = edtConfirmPassword.getText().toString();

    // check email
    String error = validNewEmail(newEmail);
    if (error != null) {
      return error;
    }
    // check password
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

  @Override
  public RequestParams buildRequest() {
    newEmail = edtNewEmail.getText().toString();
    String originPass = edtNewPassword.getText().toString();
    newPass = Utility.encryptPassword(originPass);
    String oldPass = UserPreferences.getInstance().getPassword();
    oldPass = Utility.encryptPassword(oldPass);
    String token = UserPreferences.getInstance().getToken();
    ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(token);
    changeEmailRequest.setEmail(newEmail);
    changeEmailRequest.setOldPass(oldPass);
    changeEmailRequest.setNewPass(newPass);
    changeEmailRequest.setNewOriginalPass(originPass);
    return changeEmailRequest;
  }

  @Override
  public void handleResponseSuccess() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.saveEmail(newEmail);
    userPreferences.savePassword(newPass);
  }

}

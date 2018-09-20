package com.application.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.account.ChangePasswordActivity;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

public class DeactivateAccountConfirmFragment extends BaseFragment {

  private static final String TAG = DeactivateAccountConfirmFragment.class.getSimpleName();

  public static DeactivateAccountConfirmFragment newInstance() {
    return new DeactivateAccountConfirmFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_deactivate_account_confirm, container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    TextView txtMessage = (TextView) view
        .findViewById(R.id.tv_fragment_deactivate_account_confirm_message);
    Button no = (Button) view.findViewById(R.id.bt_fragment_deactivate_account_confirm_no);
    Button yes = (Button) view.findViewById(R.id.bt_fragment_deactivate_account_confirm_yes);

    String email = UserPreferences.getInstance().getEmail();
    if (TextUtils.isEmpty(email)) {
      txtMessage.setText(String
          .format(getString(R.string.settings_account_deactivate_confirm_message),
              getString(R.string.settings_account_deactivate_confirm_message_link)));
    } else {
      String textLink = getString(R.string.settings_account_deactivate_confirm_message_link);
      SpannableString link = Utility.makeLinkSpan(textLink,
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(getActivity(),
                  ChangePasswordActivity.class);
              startActivity(intent);
            }
          });
      String message = getString(R.string.settings_account_deactivate_confirm_message);
      txtMessage.setText(message);
      Utility.appendText(txtMessage, Utility.format1, link);
      Utility.focusableLink(txtMessage);
    }

    no.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mNavigationManager.goBack();
      }
    });

    yes.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Navigate to Deactivate Account screen
        DeactivateAccountFragment deactivateAccountFragment = new DeactivateAccountFragment();
        replaceFragment(deactivateAccountFragment,
            MainActivity.TAG_FRAGMENT_SETTING_DEACTIVATE_ACCOUNT);
      }
    });
  }
}

package com.application.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.application.ui.BaseFragment;
import com.application.ui.account.ChangePasswordFragment;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

public class AccountSettingsFragment extends BaseFragment implements View.OnClickListener {


  public static AccountSettingsFragment newInstance() {
    return new AccountSettingsFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
    initViews(view);
    return view;
  }

  private void initViews(View rootView) {
    RelativeLayout layoutChangeEmail = (RelativeLayout) rootView.findViewById(R.id.rlChangeEmail);
    RelativeLayout layoutBlockList = (RelativeLayout) rootView.findViewById(R.id.rlBlockList);
    RelativeLayout layoutDeactive = (RelativeLayout) rootView.findViewById(R.id.rlDeactive);

    layoutChangeEmail.setOnClickListener(this);
    layoutBlockList.setOnClickListener(this);
    layoutDeactive.setOnClickListener(this);

    // In login by Facebook case, hide [Change Password] field.
    if (TextUtils.isEmpty(UserPreferences.getInstance().getEmail())) {
      layoutChangeEmail.setVisibility(View.GONE);
    } else {
      layoutChangeEmail.setVisibility(View.VISIBLE);
    }
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.rlChangeEmail:
        openChangeEmailOrPasswordFragment();
        break;
      case R.id.rlBlockList:
        openBlockListFragment();
        break;
      case R.id.rlDeactive:
        openDeactiveAccountFragment();
        break;
      default:
        break;
    }
  }

  private void openChangeEmailOrPasswordFragment() {
    mNavigationManager.addPage(ChangePasswordFragment.newInstance(), true);
  }

  private void openBlockListFragment() {
    mNavigationManager.addPage(BlockedUsersFragment.newInstance(), true);
  }

  private void openDeactiveAccountFragment() {
    mNavigationManager.addPage(
        DeactivateAccountConfirmFragment.newInstance(), true);
  }
}

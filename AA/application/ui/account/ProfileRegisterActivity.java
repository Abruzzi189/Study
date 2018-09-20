package com.application.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.application.actionbar.CustomActionBar;
import com.application.actionbar.CustomActionBarFactory;
import com.application.entity.User;
import com.application.navigationmanager.NavigationManager;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CustomActionBarActivity;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.region.ChooseRegionFragment;
import com.application.ui.region.RegionSettingFragment;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ProfileRegisterActivity extends BaseFragmentActivity implements
    OnNavigationClickListener, OnClickListener, CustomActionBarActivity {

  private Button mBtnDone;
  private EditProfileFragment mEditProfileFragment;
  private CustomActionBar mActionBar;
  private NavigationManager mNavigationManager;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    startHomePage();
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    initNavigationManager(bundle);
    initCustomActionBar();
    setContentView(R.layout.activity_profile_register);
    initViews();
    User me = UserPreferences.getInstance().getMe();

    FragmentManager fm = getSupportFragmentManager();
    if (fm != null) {
      Fragment fragment = fm.findFragmentById(R.id.fr_edit_profile);

      if (fragment != null
          && (fragment instanceof ProfileTextFragment
          || fragment instanceof RegionSettingFragment
          || fragment instanceof ChooseRegionFragment)) {

        mEditProfileFragment = (EditProfileFragment) fragment
            .getParentFragment();
        fm.beginTransaction().remove(fragment).commit();
      }

      if (fragment != null && fragment instanceof EditProfileFragment) {
        mEditProfileFragment = (EditProfileFragment) fragment;
      }
    }

    if (mEditProfileFragment == null) {
      mEditProfileFragment = EditProfileFragment.newInstance(me);
    }
    mNavigationManager.switchPage(mEditProfileFragment);
    mActionBar.syncActionBar(mEditProfileFragment);
  }

  private void startHomePage() {
    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    Preferences pre = Preferences.getInstance();
    if (googleReviewPreference.isEnableBrowser()) {
      String url = pre.getHomePageUrl();
      if (TextUtils.isEmpty(url)) {
        return;
      }

      if (!pre.isSendCMCode()) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        pre.removeHomePageUrl();
        pre.saveSendCMCode();
      } else {
        pre.saveSendCMCode();
      }
    } else {
      if (pre.isSendCMCode()) {
        pre.saveSendCMCode();
      }
    }
  }

  private void initViews() {
    mBtnDone = (Button) findViewById(R.id.btn_done);
    mBtnDone.setOnClickListener(this);
  }

  @Override
  public void onNavigationLeftClick(View view) {
  }

  @Override
  public void onNavigationRightClick(View view) {
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_done) {
      if (mEditProfileFragment != null && mEditProfileFragment.isAdded()) {
        mEditProfileFragment.editProfile();
      }
    }
  }

  @Override
  public NavigationManager getNavigationManager() {
    return mNavigationManager;
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  public CustomActionBar getCustomActionBar() {
    return mActionBar;
  }

  @Override
  public boolean isStateSaved() {
    return false;
  }

  @Override
  public FragmentManager getCustomFragmentManager() {
    return super.getSupportFragmentManager();
  }

  @Override
  public void initNavigationManager(Bundle savedInstanceState) {
    mNavigationManager = new NavigationManager(this);
    if (savedInstanceState != null) {
      mNavigationManager.deserialize(savedInstanceState);
    }
  }

  @Override
  public void initCustomActionBar() {
    mActionBar = CustomActionBarFactory.getInstance(this);
    mActionBar.initialize(mNavigationManager, this);
  }

  @Override
  protected boolean isNoTitle() {
    return false;
  }

  @Override
  public int getPlaceHolder() {
    return R.id.fr_edit_profile;
  }

  @Override
  public void onBackPressed() {
    Fragment activePage = mNavigationManager.getActivePage();
    if (activePage instanceof EditProfileFragment) {
      ((EditProfileFragment) activePage).backToFirstScreen();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
  }

}

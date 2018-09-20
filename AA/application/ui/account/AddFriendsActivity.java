package com.application.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.MainActivity;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.LogUtils;
import com.facebook.Session;
import glas.bbsystem.R;


public class AddFriendsActivity extends BaseFragmentActivity implements
    OnNavigationClickListener {

  public static final String HAS_BACK_NAVIGATION = "has_back_navigation";
  private final String TAG = "AddFriendActivitiy";
  private boolean hasBackNavigation = false;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    LogUtils.e(TAG, TAG + " onCreate()");
    setContentView(R.layout.activity_addfriend);
    Bundle bundleExtras = getIntent().getExtras();
    if (bundleExtras != null) {
      hasBackNavigation = bundleExtras.getBoolean(HAS_BACK_NAVIGATION);
    }
    if (bundle != null) {
      hasBackNavigation = bundle.getBoolean(HAS_BACK_NAVIGATION);
    }
    initNavigationBar();
    setUpNavigationBar();
    initView();
    initialNotificationVew();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(HAS_BACK_NAVIGATION, hasBackNavigation);
  }

  private void initView() {
    AddFriendsFragment fragment = (AddFriendsFragment) getSupportFragmentManager()
        .findFragmentByTag("fragment_getfriend");
    if (fragment == null) {
      fragment = new AddFriendsFragment();
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.activity_addfriend_frm_content, fragment,
              "fragment_getfriend").commit();
    }
  }

  private void setUpNavigationBar() {
    getNavigationBar().setCenterTitle(R.string.add_friends_title);
    if (hasBackNavigation) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
      getNavigationBar().setNavigationRightVisibility(View.INVISIBLE);
    } else {
      getNavigationBar().setNavigationLeftVisibility(View.INVISIBLE);
      getNavigationBar().setNavigationRightTitle(
          R.string.add_friends_skip);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // save session for first login facebook
    if (resultCode == Activity.RESULT_OK) {
      Session.getActiveSession().onActivityResult(this, requestCode,
          resultCode, data);
    }
  }

  @Override
  public void onNavigationLeftClick(View view) {
    if (hasBackNavigation) {
      finish();
    }
  }

  @Override
  public void onNavigationRightClick(View view) {
    if (hasBackNavigation) {
    } else {
      startCustomeActivityForResult(new Intent(this, MainActivity.class));
      customeFinishActivity();
    }
  }

  public void onSendCompleted() {
    if (hasBackNavigation) {
      // Back to Connections screen
      finish();
    } else {
      // Navigate to Meet People screen
      startCustomeActivityForResult(new Intent(this, MainActivity.class));
      customeFinishActivity();
    }
  }

  @Override
  protected void onDestroy() {
    // Stop call service
		/*if (ServiceUtils.isServiceRunning(this, CallService.class.getName())){
			Intent intentCallService = new Intent(this, CallService.class);
			stopService(intentCallService);
		}*/

    super.onDestroy();
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }
}

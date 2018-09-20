package com.application.common.webview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.account.SignUpActivity;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class WebViewActivity extends BaseFragmentActivity implements
    OnNavigationClickListener {

  private int mPageType;
  private String mUrl = "";
  private String mTitle = "";
  private Fragment fragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initial content view
    setContentView(R.layout.ac_web);

    // Get bundle data set from another activity
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mPageType = bundle.getInt(WebViewFragment.INTENT_PAGE_TYPE);
      mUrl = bundle.getString(WebViewFragment.INTENT_PAGE_URL);
      if (mUrl == null) {
        mUrl = "";
      }
      mTitle = bundle.getString(WebViewFragment.INTENT_PAGE_TITLE);
      if (mTitle == null) {
        mTitle = "";
      }
    }

    // Initial data for action navigation bar
    initNavigationBar();
    setUpNavigationBar();

    // Initial web page
    startWebPage();
  }

  private void setUpNavigationBar() {
    getCenterTitle().setText(R.string.common_app_name);
    findViewById(R.id.cv_navigation_bar_img_left).setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            onBackPressed();
          }
        });
  }

  private void startWebPage() {
    handleDefaultFragment(false);
  }

  public void handleDefaultFragment(boolean hasAnimation) {
    final int PLACE_HOLDER = R.id.activity_main_content;
    FragmentManager manager = getSupportFragmentManager();
    fragment = manager.findFragmentById(PLACE_HOLDER);

    if (fragment == null) {
      if (mUrl != null && mUrl.trim().length() > 0) {
        fragment = WebViewFragment.newInstance(mPageType, mUrl, mTitle);
      } else {
        fragment = WebViewFragment.newInstance(mPageType);
      }
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(PLACE_HOLDER, fragment);
    transaction.commit();
  }

  @Override
  public void onNavigationLeftClick(View view) {
    onBackPressed();
  }

  @Override
  public void onNavigationRightClick(View view) {
  }

  public TextView getCenterTitle() {
    return (TextView) findViewById(R.id.cv_navigation_bar_txt_center);
  }

  public void backToFirstScreen() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.clear();

    Intent intent = new Intent(this, SignUpActivity.class);
    startActivity(intent);

    finish();
  }

  @Override
  public void onBackPressed() {
    if (fragment instanceof WebViewFragment) {
      WebViewFragment webViewFragment = (WebViewFragment) fragment;
      if (webViewFragment.getPageType() == WebViewFragment.PAGE_TYPE_AUTO_VERIFY_AGE
          || webViewFragment.getPageType() == WebViewFragment.PAGE_TYPE_VERIFY_AGE) {
        backToFirstScreen();
      } else {
        super.onBackPressed();
      }
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected boolean isNoTitle() {
    return true;
  }
}
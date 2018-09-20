package com.application.ui.point.freepoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.application.actionbar.NoFragmentActionBar;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.constant.Constants;
import com.application.ui.BaseFragmentActivity;
import glas.bbsystem.R;


public class FreePointGetActivity extends BaseFragmentActivity implements
    OnClickListener {

  private static int sFromActivity; // 1=singUp, 2=buy point
  private LinearLayout mLLOptionFist;
  private LinearLayout mLLOptionSecond;

  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_freepoint_get);

    readData(bundle);
    initViews();
    // initNavigationBar();
    // setUpNavigationBar();
  }

  /**
   * read data from intent to set UI
   */
  private void readData(Bundle bundle) {
    Bundle intent = getIntent().getExtras();
    if (intent != null) {
      sFromActivity = intent.getInt(Constants.FROM_FREE_POINT);
    }
    if (bundle != null) {
      sFromActivity = bundle.getInt(Constants.FREE_POINT_SAVE_INSTANCE);
    }
  }

  /**
   * init view
   */
  private void initViews() {
    mLLOptionFist = (LinearLayout) findViewById(R.id.freepoint_get_option1);
    mLLOptionSecond = (LinearLayout) findViewById(R.id.freepoint_get_option2);

    mLLOptionFist.setOnClickListener(this);
    mLLOptionSecond.setOnClickListener(this);

  }

  /**
   * setup navigationBar with title
   */
  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.free_point_title);
  }

  /*
   * @Override public void onNavigationLeftClick(View view) { finish(); }
   *
   * @Override public void onNavigationRightClick(View view) { // do nothing }
   */

  /**
   * handle event click on option
   */
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.freepoint_get_option1:
        getPointEnd();
        break;
      case R.id.freepoint_get_option2:
        getPointEnd();
        break;
      default:
        break;
    }
  }

  /**
   * change activity to FreePointEnd screen
   */
  private void getPointEnd() {
    Intent intent = new Intent(this, WebViewActivity.class);
    intent.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
        WebViewFragment.PAGE_TYPE_FREE_POINT);
    startActivity(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(Constants.FREE_POINT_SAVE_INSTANCE, sFromActivity);
    super.onSaveInstanceState(outState);
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

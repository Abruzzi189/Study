package com.application.ui.point.freepoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.application.actionbar.NoFragmentActionBar;
import com.application.constant.Constants;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.point.BuyPointActivity;
import glas.bbsystem.R;
import java.text.MessageFormat;


public class FreePointGetEndActivity extends BaseFragmentActivity implements
    OnClickListener {

  private static int sFromActivity;
  private Button mBtnGetPointEnd;
  private TextView mGetPoint;

  private int mPointFree = 0;

  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    initActionBar();
    setContentView(R.layout.activity_freepoint_get_end);

    initViews();
    readData(bundle);
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
      mPointFree = intent.getInt(Constants.POINT_GET_FREE);
    }

    if (bundle != null) {
      sFromActivity = bundle.getInt(Constants.FREE_POINT_SAVE_INSTANCE);
      mPointFree = bundle.getInt(Constants.POINT_GET_FREE_SAVE_INSTANCE);
    }

    if (mPointFree != 0) {
      String format = getResources().getString(R.string.point_suffix);
      StringBuilder text = new StringBuilder()
          .append(getString(R.string.free_point_end_head_line1_1))
          .append(MessageFormat.format(format, mPointFree))
          .append(getString(R.string.free_point_end_head_line1_3));
      mGetPoint.setText(text.toString());
    }

  }

  /**
   * init view
   */
  private void initViews() {
    mBtnGetPointEnd = (Button) findViewById(R.id.btn_free_point_end);
    mGetPoint = (TextView) findViewById(R.id.free_point_end_txt_point_end);

    mBtnGetPointEnd.setOnClickListener(this);
    StringBuilder text = new StringBuilder()
        .append(getString(R.string.free_point_end_head_line1_1))
        .append(getString(R.string.free_point_end_head_line1_2))
        .append(getString(R.string.free_point_end_head_line1_3));
    mGetPoint.setText(text.toString());
  }

  /**
   * setup navigationBar with title
   */
  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setCenterTitle(R.string.free_point_end_title);
  }

	/*@Override
	public void onNavigationLeftClick(View view) {
		// do nothing
	}

	@Override
	public void onNavigationRightClick(View view) {
		// do nothing
	}*/

  /**
   * handle event click on free point end button
   */
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_free_point_end:
        customeFinishActivity();
        // changeActivity();
        break;
      default:
        break;
    }
  }

  /**
   * change activity to EditProfile screen
   */
  @SuppressWarnings("unused")
  private void changeActivity() {

    if (sFromActivity == Constants.FROM_SIGNUP) {
      customeFinishActivity();
    } else {
      Intent intent = new Intent(this, BuyPointActivity.class);
      startActivity(intent);
      customeFinishActivity();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(Constants.FREE_POINT_SAVE_INSTANCE, sFromActivity);
    outState.putInt(Constants.POINT_GET_FREE_SAVE_INSTANCE, mPointFree);
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

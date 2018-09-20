package com.application.ui.customeview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.application.util.preferece.Preferences;
import glas.bbsystem.R;


public class UnlockDialog extends Dialog {

  public static final int TYPE_WHO_CHECK_ME_OUT = 0;
  public static final int TYPE_WHO_FAVORITE_ME = 1;

  public int mUnlockType = 0;
  public int mTargets = 0;
  public int mUnlockDuration;
  public int mMyPoints;
  public int mUnlockPoints;

  private Context mContext;

  public UnlockDialog(Context context) {
    super(context);
    mContext = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.fragment_unlock);
  }

  @Override
  protected void onStart() {
    super.onStart();

    TextView tvInAction = (TextView) findViewById(R.id.tv_fragment_unlock_in_action);
    TextView tvTargets = (TextView) findViewById(R.id.tv_fragment_unlock_number);
    TextView tvUnlockDuration = (TextView) findViewById(R.id.tv_fragment_unlock_duration);
    TextView tvMyPoints = (TextView) findViewById(R.id.tv_fragment_unlock_your_balance);
    TextView tvUnlockPoints = (TextView) findViewById(R.id.tv_fragment_unlock_points);
    Preferences preferences = Preferences.getInstance();
    switch (mUnlockType) {
      case TYPE_WHO_CHECK_ME_OUT:
        tvInAction
            .setText(mContext.getString(R.string.meet_people_unlock_who_check_me_out_someone));
        mUnlockDuration = preferences.getTimeCheckout();
        break;
      case TYPE_WHO_FAVORITE_ME:
        tvInAction.setText(mContext.getString(R.string.connection_unlock_who_favorite_me_someone));
        mUnlockDuration = preferences.getTimeFavourite();
        break;
      default:
        break;
    }

    if (mTargets <= 1) {
      tvTargets.setVisibility(View.GONE);
    } else {
      tvTargets.setVisibility(View.VISIBLE);
      tvTargets.setText(String.format(mContext.getString(R.string.common_unlock_for_targets),
          mTargets - 1));
    }

    tvUnlockDuration.setText(
        String.format(mContext.getString(R.string.common_unlock_duration), mUnlockDuration));

    tvMyPoints.setText("" + mMyPoints);

    tvUnlockPoints.setText("" + mUnlockPoints);
  }
}

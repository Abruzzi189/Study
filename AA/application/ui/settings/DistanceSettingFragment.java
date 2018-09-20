package com.application.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.application.constant.Constants;
import com.application.ui.BaseFragment;
import com.application.util.preferece.Preferences;
import glas.bbsystem.R;

public class DistanceSettingFragment extends BaseFragment implements
    OnClickListener {

  private CheckedTextView mctvMile;
  private CheckedTextView mctvKilometer;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_setting_distance,
        container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mctvMile = (CheckedTextView) view
        .findViewById(R.id.fragment_setting_distance_ctv_mile);
    mctvKilometer = (CheckedTextView) view
        .findViewById(R.id.fragment_setting_distance_ctv_kilometer);
    mctvKilometer.setOnClickListener(this);
    mctvMile.setOnClickListener(this);

    int unit = Preferences.getInstance().getDistanceUnit();
    if (unit == Constants.DISTANCE_UNIT_MILE) {
      setDistanceInMile(true);
    } else {
      setDistanceInKilo(true);
    }
  }

  private void setDistanceInMile(boolean value) {
    mctvMile.setChecked(value);
    mctvKilometer.setChecked(!value);
  }

  private void setDistanceInKilo(boolean value) {
    mctvKilometer.setChecked(value);
    mctvMile.setChecked(!value);
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.distance_in);
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setShowUnreadMessage(true);
  }

  @Override
  public void onNavigationRightClick(View view) {
    super.onNavigationRightClick(view);
    getSlidingMenu().showSecondaryMenu(true);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.fragment_setting_distance_ctv_mile:
        setDistanceInMile(true);
        Preferences.getInstance().saveDistanceUnit(
            Constants.DISTANCE_UNIT_MILE);
        mNavigationManager.goBack();
        break;
      case R.id.fragment_setting_distance_ctv_kilometer:
        setDistanceInKilo(true);
        Preferences.getInstance().saveDistanceUnit(
            Constants.DISTANCE_UNIT_KILOMETER);
        mNavigationManager.goBack();
        break;

      default:
        break;
    }
  }
}

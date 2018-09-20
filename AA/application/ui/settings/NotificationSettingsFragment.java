package com.application.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import com.application.ui.BaseFragment;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

public class NotificationSettingsFragment extends BaseFragment implements View.OnClickListener,
    OnCheckedChangeListener {

  public static NotificationSettingsFragment newInstance() {
    return new NotificationSettingsFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
    initViews(view);
    return view;
  }

  private void initViews(View rootView) {
    RelativeLayout mllNotification = (RelativeLayout) rootView
        .findViewById(R.id.tr_settings_chat_and_notifications_notifications);
    CheckBox ckbSound = (CheckBox) rootView
        .findViewById(R.id.cb_settings_chat_and_notifications_sound_on);
    CheckBox ckbVibration = (CheckBox) rootView
        .findViewById(R.id.cb_settings_chat_and_notifications_vibration_on);

    UserPreferences userPreferences = UserPreferences.getInstance();
    ckbSound.setChecked(userPreferences.isSoundOn());
    ckbVibration.setChecked(userPreferences.isVibration());

    ckbSound.setOnCheckedChangeListener(this);
    ckbVibration.setOnCheckedChangeListener(this);
    mllNotification.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tr_settings_chat_and_notifications_notifications:
        replaceFragment(new PushNotificationSettingsFragment(), TAB_BACKSTACK);
        break;

      default:
        break;
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    switch (buttonView.getId()) {
      case R.id.cb_settings_chat_and_notifications_sound_on:
        userPreferences.saveSoundOn(isChecked);
        break;
      case R.id.cb_settings_chat_and_notifications_vibration_on:
        userPreferences.saveVibration(isChecked);
        break;
      default:
        break;
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    Utility.hideSoftKeyboard(baseFragmentActivity);
  }
}

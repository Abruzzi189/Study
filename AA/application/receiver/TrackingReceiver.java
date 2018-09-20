package com.application.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;

public class TrackingReceiver extends BroadcastReceiver {

  private static final String REFER = "com.android.vending.INSTALL_REFERRER";

//	private InstallReceiver mAPT_InstallReceiver;

  public TrackingReceiver() {
//		mAPT_InstallReceiver = new InstallReceiver();
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    LogUtils.i("Tracking", "prefers");
//		mAPT_InstallReceiver.onReceive(context, intent);
    if (intent != null && intent.getAction().equals(REFER)) {
      Bundle bundle = intent.getExtras();
      String inviteCode = bundle.getString("referrer");
      LogUtils.i("Tracking", "bundle=" + inviteCode);
      UserPreferences.getInstance().saveInviteCode(inviteCode);
    }
  }
}

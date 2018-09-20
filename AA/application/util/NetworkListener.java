package com.application.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkListener extends BroadcastReceiver {

  private ConnectivityManager connectivityManager = null;
  private NetworkInfo activeNetInfo = null;
  private Activity mActivity;
  private OnNetworkListener mOnNetworkListener;
  public NetworkListener(Activity activity, OnNetworkListener networkListener) {
    mActivity = activity;
    mOnNetworkListener = networkListener;
  }

  public void register() {
    if (mActivity == null) {
      return;
    }
    IntentFilter filter = new IntentFilter(
        ConnectivityManager.CONNECTIVITY_ACTION);
    mActivity.registerReceiver(this, filter);

  }

  public void unRegister() {
    if (mActivity == null) {
      return;
    }
    mActivity.unregisterReceiver(this);
    mActivity = null;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (mActivity == null) {
      return;
    }
    connectivityManager = (ConnectivityManager) mActivity
        .getApplicationContext().getSystemService(
            Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      activeNetInfo = connectivityManager.getActiveNetworkInfo();
    }
    if (activeNetInfo != null && activeNetInfo.isConnected()) {
      if (mOnNetworkListener != null) {
        mOnNetworkListener.onNetworkConnected();
      }
    } else {
      if (mOnNetworkListener != null) {
        mOnNetworkListener.onNetworkDisconnected();
      }
    }

  }

  public interface OnNetworkListener {

    public void onNetworkConnected();

    public void onNetworkDisconnected();
  }
}

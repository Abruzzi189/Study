package com.application.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

  private ScreenListener screenListener;

  public ScreenReceiver(ScreenListener screenListener) {
    this.screenListener = screenListener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
      if (screenListener != null) {
        screenListener.onScreenOn();
      }
    }
    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
      if (screenListener != null) {
        screenListener.onScreenOff();
      }
    }
    if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
      if (screenListener != null) {
        screenListener.onScreenPresent();
      }
    }
  }

  public interface ScreenListener {

    public void onScreenOn();

    public void onScreenOff();

    public void onScreenPresent();
  }
}
package com.application.util;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeUtil {

  /**
   * update badge for supported launchers
   *
   * @param appContext application context
   * @param notification show badge for xiaomi device
   * @param count number of badge
   */
  public static void updateBadge(Context appContext, Notification notification, int count) {
    String manufacturer = Build.MANUFACTURER;
    if (isXiaomiDevice(manufacturer)) {
      ShortcutBadger.applyNotification(appContext, notification, count);
    } else {
      ShortcutBadger.applyCount(appContext, count);
    }
  }

  /**
   * @return true: xiaomi, false: other
   */
  private static boolean isXiaomiDevice(String manufacturer) {
    // available value: Xiaomi, xiaomi
    // if use custom rom it may not work
    return manufacturer.equalsIgnoreCase("xiaomi");
  }
}

package com.application.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;

public class IntentUtils {

  /**
   * Check that in the system exists application which can handle this intent
   *
   * @param context Application context
   * @param intent Checked intent
   * @return true if intent consumer exists, false otherwise
   */
  public static boolean isIntentAvailable(Context context, Intent intent) {
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
        PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }
}

package com.application.ui.customeview.pulltorefresh;

import com.application.util.LogUtils;

public class Utils {

  static final String LOG_TAG = "PullToRefresh";

  public static void warnDeprecation(String depreacted, String replacement) {
    LogUtils.w(LOG_TAG,
        "You're using the deprecated " + depreacted + " attr, please switch over to "
            + replacement);
  }

}

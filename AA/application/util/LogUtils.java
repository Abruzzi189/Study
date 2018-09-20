package com.application.util;

import android.util.Log;
import com.application.Config;


public class LogUtils {

  public static void v(String tag, String msg) {
    if (Config.DEBUG) {
      Log.v(tag, msg);
    }
  }

  public static void i(String tag, String msg) {
    if (Config.DEBUG) {
      Log.i(tag, msg);
    }
  }

  public static void d(String tag, String msg) {
    if (Config.DEBUG) {
      Log.d(tag, msg);
    }
  }

  public static void w(String tag, String msg) {
    Log.w(tag, msg);
  }


  public static void e(String tag, String msg) {
    Log.e(tag, msg);
  }
}

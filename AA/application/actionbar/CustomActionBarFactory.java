package com.application.actionbar;

import android.app.Activity;
import com.application.common.webview.WebViewActivity;
import com.application.ui.MainActivity;
import com.application.ui.account.ProfileRegisterActivity;

public class CustomActionBarFactory {

  public static CustomActionBar getInstance(Activity activity) {
    CustomActionBar actionBar = null;
    if (activity instanceof MainActivity
        || activity instanceof WebViewActivity) {
      actionBar = new NativeActionBar();
    } else if (activity instanceof ProfileRegisterActivity) {
      actionBar = new ProfileRegisterActionBar();
    }
    return actionBar;
  }
}

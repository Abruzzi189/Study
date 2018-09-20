package com.application.ui.account;

import android.app.NotificationManager;
import android.content.Context;
import com.application.AndGApp;
import com.application.ui.settings.MeetPeopleSetting;
import com.application.util.preferece.ChatMessagePreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.facebook.Session;
import org.linphone.LinphoneService;

public class AuthenticationUtil {

  public static void saveAuthenticationSuccessData(
      AuthenticationData authenData, boolean isFirstLogin) {
    // Save info when login success
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.saveSuccessLoginData(authenData, isFirstLogin);

    Preferences preferences = Preferences.getInstance();
    preferences.saveTimeSetting(authenData);
    preferences.savePointSetting(authenData);

    // Login time
    preferences.saveTimeRelogin(System.currentTimeMillis());
  }

  public static void clearAuthenticationData() {
    // Clear user data
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.clear();

    // Clear Facebook session data
    if (Session.getActiveSession() != null) {
      Session.getActiveSession().closeAndClearTokenInformation();
    }

    // Clear notification
    Context context = AndGApp.get();

    // Clear search setting
    MeetPeopleSetting.getInstance(context).clear();

    NotificationManager notiManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notiManager.cancelAll();

    // Logout from Linphone service
    LinphoneService.startLogout(context);

    // Clear all temp chat message
    ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
    chatMessagePreference.cleverAll();
  }
}
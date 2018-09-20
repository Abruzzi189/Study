package com.application.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.application.util.LogUtils;
import com.application.util.StringUtils;
import com.application.util.Utility;
import com.application.util.preferece.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;


/**
 * @author Created by Robert on 28 Dec 2016
 */
public class FCMHelper {

  private static final String TAG = "FCMHelper";
  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 111;
  private static final boolean showErrorDialog = false;

  public static void register(Activity mActivity, Context mContext) {
    // Check device for Play Services APK. If check succeeds, proceed with FCM registration.
    if (checkPlayServices(mActivity, mContext)) {
      String refreshedToken = FirebaseInstanceId.getInstance().getToken();
      String regId = FirebaseInstanceId.getInstance().getId();

      Log.d(TAG, "Refreshed token: " + refreshedToken);
      Log.d(TAG, "Refreshed regId: " + regId);

      Preferences preferences = Preferences.getInstance();
      String firebaseToken = preferences.getFirebaseRegistrationId();

      Log.d(TAG, "FirebaseTokend from cache=" + firebaseToken);

      if (StringUtils.isEmptyOrNull(firebaseToken) || !firebaseToken.equals(refreshedToken)
          || Utility.getAppVersion(mContext) > Preferences.getInstance().getFcmPerAppVersion()) {

        registerInBackground(mContext);
      }
    } else {
      LogUtils.i(TAG, "Google Play Services Unavailable (-_-)");
    }
  }

  private static boolean checkPlayServices(Activity mActivity, Context mContext) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(mContext);
    if (result == ConnectionResult.SUCCESS) {
      return true;
    }
    if (googleAPI.isUserResolvableError(result)) {
      if (showErrorDialog) {
        googleAPI.getErrorDialog(mActivity, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
      }
    }
    return false;
  }

  // start service register FCM and send to server
  private static void registerInBackground(Context context) {
    LogUtils.i(TAG, "registerInBackground success.!");
    Intent intent = new Intent(context, FirebaseIDService.class);
    context.startService(intent);
  }
}

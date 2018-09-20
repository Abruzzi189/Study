package com.application.fcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.UpdateNotificationRequest;
import com.application.connection.response.UpdateNotificationResponse;
import com.application.util.StringUtils;
import com.application.util.Utility;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.lang.ref.WeakReference;

/**
 * Firebase instance ID service Created by Robert on 2016 Dec 26
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

  private static final int LOADER_ID_SEND_REGISTRATION_ID = 100;
  private static final String TAG = FirebaseIDService.class.getSimpleName();

  public FirebaseIDService() {
    super();
  }

  /**
   * TODO Register firebase token with server if need
   *
   * @author Robert
   */
  public static void registerFirebaseTokenToServerIfNeed(Context mContext) {
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

    String regId = FirebaseInstanceId.getInstance().getId();
    Log.d(TAG, "Refreshed token: " + refreshedToken);
    Log.d(TAG, "Refreshed regId: " + regId);

    //String firebaseToken = getFirebaseRegIdInPref(mContext);
    Preferences preferences = Preferences.getInstance();
    String firebaseToken = preferences.getFirebaseRegistrationId();

    Log.d(TAG, "FirebaseTokend from cache=" + firebaseToken);

    if (StringUtils.isEmptyOrNull(firebaseToken) || !firebaseToken.equals(refreshedToken)
        || Utility.getAppVersion(mContext) > Preferences.getInstance().getFcmPerAppVersion()) {
      // sending reg id to your server
      sendRegistrationToServer(mContext, refreshedToken);

      // Notify UI that registration has completed, so the progress indicator can be hidden.
      Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
      registrationComplete.putExtra("token", refreshedToken);
      LocalBroadcastManager.getInstance(mContext).sendBroadcast(registrationComplete);
    }
  }

  /**
   * TODO Send registration firebase token to server
   *
   * @author Robert
   */
  private static void sendRegistrationToServer(Context mContext, final String token) {
    // TODO: Implement this method to send any registration to your app's servers.
    RegistrationIdSender sender = new RegistrationIdSender(mContext, token);
    sender.execute();

  }

  /**
   * TODO Save firebase token into cache
   */
  private static void storeRegIdInPref(Context mContext, String token) {
    SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString("regId", token);
    editor.commit();
  }

  /**
   * TODO get firebase token from cache
   */
  private static String getFirebaseRegIdInPref(Context mContext) {
    SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
    return pref.getString("regId", "");
  }

  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();

    registerFirebaseTokenToServerIfNeed(getApplicationContext());
  }

  /**
   * Persist token to third-party servers.
   *
   * Modify this method to associate the user's FCM InstanceID token with any server-side account
   * maintained by your application.
   */
  private static class RegistrationIdSender extends AsyncTask<Void, Void, Void> {

    Context mAppContext;
    WeakReference<Context> weakReference;
    private String regIdToken = "";
    private ResponseReceiver mResponseReceiver = new ResponseReceiver() {

      @Override
      public void startRequest(int loaderId) {

      }

      @Override
      public void receiveResponse(Loader<Response> loader, Response response) {

        Log.e(TAG, "ReceiveResponse.regIdToken=" + regIdToken + "|response=" + response.toString());

        // Saving reg id to shared preferences if send to server is successful
        //storeRegIdInPref(mAppContext, regIdToken);
        Preferences preferences = Preferences.getInstance();
        preferences.setFirebaseRegistrationId(regIdToken);
        preferences.saveFcmPerAppVersion(Utility.getAppVersion(mAppContext));
      }

      @Override
      public Response parseResponse(int loaderID, ResponseData data, int requestType) {
        return new UpdateNotificationResponse(data);

      }

      @Override
      public void onBaseLoaderReset(Loader<Response> loader) {

      }
    };

    public RegistrationIdSender(Context context, String regIdToken) {
      this.regIdToken = regIdToken;
      this.weakReference = new WeakReference<Context>(context);
      this.mAppContext = context;//weakReference.get();
    }

    @Override
    protected Void doInBackground(Void... params) {

      UserPreferences userPreferences = UserPreferences.getInstance();
      String token = userPreferences.getToken();

      Log.e(TAG, "ReceiveResponse.token=" + token + "|regIdToken=" + regIdToken);

      if (!StringUtils.isEmptyOrNull(token) && !StringUtils.isEmptyOrNull(regIdToken)) {

        UpdateNotificationRequest requestParams = new UpdateNotificationRequest(token, regIdToken);
        Request request = RequestBuilder.getInstance()
            .makeRequest(RequestType.JSON, requestParams, mResponseReceiver,
                LOADER_ID_SEND_REGISTRATION_ID);

        request.execute();
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
    }
  }
}

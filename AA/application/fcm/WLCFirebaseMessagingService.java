package com.application.fcm;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.application.AndGApp;
import com.application.constant.Constants;
import com.application.entity.NotificationMessage;
import com.application.service.ApplicationNotificationManager;
import com.application.ui.MainActivity;
import com.application.util.LogUtils;
import com.application.util.StringUtils;
import com.application.util.Utils;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneDatabase;
import org.linphone.LinphoneManager;


/**
 * Created by Hiepnk on 12/26/2016.
 */

public class WLCFirebaseMessagingService extends FirebaseMessagingService {

  public static final String ACTION_GCM_RECEIVE_MESSAGE = "com.gcm.receive";
  public static final String EXTRA_NOTIFICATION_MESSAGE = "com_andg_gcm_message";
  private static final String TAG = WLCFirebaseMessagingService.class.getSimpleName();
  private NotificationUtils notificationUtils;

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    // There are two types of messages data messages and notification messages. Data messages are handled
    // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
    // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
    // is in the foreground. When the app is in the background an automatically generated notification is displayed.
    // When the user taps on the notification they are returned to the app. Messages containing both notification
    // and data payloads are treated as notification messages. The Firebase console always sends notification
    // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

    LogUtils.w(TAG, "------------------onMessageReceived()------------------");
    if (remoteMessage == null) {
      return;
    }

    // Check if message contains a data payload.
    LogUtils.d(TAG, "From: " + remoteMessage.getFrom());
    if (remoteMessage.getNotification() != null) {
      LogUtils.e(TAG,
          "Notification Message Body.toString=" + remoteMessage.getNotification().toString());
      LogUtils.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
    if (remoteMessage.getData() != null) {
      Map<String, String> mMapData = remoteMessage.getData();

      LogUtils.i(TAG, Utils.dumpMapData(mMapData));

      String aps = getDataFromRemoteMessageByKeyword(mMapData, "aps");
      if (!StringUtils.isEmptyOrNull(aps)) {
        //Show notification
        showNotification(aps);
      }
    }

    LogUtils.e(TAG, "------------------End onMessageReceived()------------------");
  }


  /**
   * TODO get value of keyword in data Map of remote message via Firebase cloud message
   *
   * @author Created by Robert on 2016 Dec 27
   */
  private String getDataFromRemoteMessageByKeyword(Map<String, String> mMapData, String keyword) {
    if (mMapData == null || !mMapData.containsKey(keyword)) {
      return "";
    }
    return mMapData.get(keyword);
  }

  /**
   * extract data from GCM and determine show notification in app or status bar
   *
   * @param message string
   */
  private void showNotification(String message) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    // if calling ->ignore notification
    if (userPreferences.getInCallingProcess() || StringUtils.isEmptyOrNull(message)) {
      return;
    }

    NotificationMessage notiMessage = new NotificationMessage(message);
    String userIdLoggin = LinphoneDatabase.getInstance(this).getUserId();
    LogUtils.e(TAG, "message=" + message + "|userIdLoggin=" + userIdLoggin);

    if (TextUtils.isEmpty(userIdLoggin)) {
      return;
    }

    LogUtils.e(TAG,
        "getUserid()=" + notiMessage.getUserid() + "|getOwnerId=" + notiMessage.getOwnerId()
            + "|getNotiType=" + notiMessage.getNotiType() + "|isApplicationVisibile()=" + AndGApp
            .isApplicationVisibile() + "|UserPreferences.getInstance().isSoundOn()="
            + UserPreferences.getInstance().isSoundOn());

    if (!userIdLoggin.equalsIgnoreCase(notiMessage.getUserid()) && userIdLoggin
        .equals(notiMessage.getOwnerId())) {
      // Check image approve
      int type = notiMessage.getNotiType();
      if (type == Constants.NOTI_BUZZ_APPROVED) {
        String avaId = notiMessage.getImageId();
        userPreferences.savePendingAva(avaId);
      }

      // Only increase notification when message request call.
      if (notiMessage.getNotiType() == Constants.NOTI_REQUEST_CALL) {
        userPreferences.increaseUnreadMessage(1);
      }

      // Only increase notification when message not chat text.
      /**
       * #6716
       * Khi có cuộc gọi đến notiype trả về 17 ==  Constants.NOTI_VOIP_PING
       *
       * */
      if (notiMessage.getNotiType() != Constants.NOTI_CHAT_TEXT
          && notiMessage.getNotiType() != Constants.NOTI_REQUEST_CALL
          && notiMessage.getNotiType() != Constants.NOTI_VOIP_PING) {

        userPreferences.increaseNotification();
        if (notiMessage.getNotiType() == Constants.NOTI_FAVORITED_UNLOCK) {
          userPreferences.increaseFavoritedMe();
        }
      } else if (ApplicationNotificationManager.isOnlineAlertNotification(notiMessage)) {
        // update point
        int totalPoint = userPreferences.getNumberPoint();
        int priceAlert = Preferences.getInstance().getOnlineAlertPoints();
        int remainPoint = totalPoint - priceAlert;
        userPreferences.saveNumberPoint(remainPoint);
      }

      String lockey = notiMessage.getLockey();
      // Because the notification from free page does not return lockey.
      if (TextUtils.isEmpty(lockey) && type != Constants.NOTI_FROM_FREE_PAGE) {
        return;
      }

      if (lockey != null && lockey.equals(Constants.LOCK_KEY_VOIP_CALLING)) {
        //start linphone
        LinphoneManager.start(this);
        return;
      }

      // only push notification when app invisible
      if (!AndGApp.isApplicationVisibile()) {
        LogUtils.e(TAG, "Only push notification when app invisible");
        new ApplicationNotificationManager(getApplicationContext()).showNotification(notiMessage);
      } else {
        LogUtils
            .e(TAG, "Only sendBroadcastReceiveMessage about notification when app visible (-_-)");
        sendBroadcastReceiveMessage(notiMessage);
      }
    }
  }

  public void sendBroadcastReceiveMessage(NotificationMessage message) {
    Intent intent = new Intent(ACTION_GCM_RECEIVE_MESSAGE);
    intent.putExtra(EXTRA_NOTIFICATION_MESSAGE, message);
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
  }

  /**==================================================**/
  /**==================================================**/
  /**==================================================**/

  /**
   * TODO handle notification Created by Robert on 2016 Dec 27
   */
  private void handleNotification(String message) {
    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
      LogUtils.e(TAG, "==>If the app is run background, firebase itself handles the notification");
      // app is in foreground, broadcast the push message
      Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
      pushNotification.putExtra("message", message);
      LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

      // play notification sound
      NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
      notificationUtils.playNotificationSound();
    } else {
      // If the app is in background, firebase itself handles the notification
      LogUtils.e(TAG, "-->If the app is in background, firebase itself handles the notification");
    }
  }

  private void handleDataMessage(JSONObject json) {
    LogUtils.e(TAG, "push json: " + json.toString());

    try {
      JSONObject data = json.getJSONObject("data");

      String title = data.getString("title");
      String message = data.getString("message");
      boolean isBackground = data.getBoolean("is_background");
      String imageUrl = data.getString("image");
      String timestamp = data.getString("timestamp");
      JSONObject payload = data.getJSONObject("payload");

      LogUtils.e(TAG, "title: " + title);
      LogUtils.e(TAG, "message: " + message);
      LogUtils.e(TAG, "isBackground: " + isBackground);
      LogUtils.e(TAG, "payload: " + payload.toString());
      LogUtils.e(TAG, "imageUrl: " + imageUrl);
      LogUtils.e(TAG, "timestamp: " + timestamp);

      if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
        pushNotification.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
      } else {
        // app is in background, show the notification in notification tray
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.putExtra("message", message);

        // check for image attachment
        if (TextUtils.isEmpty(imageUrl)) {
          showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
        } else {
          // image is present, show notification with image
          showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp,
              resultIntent, imageUrl);
        }
      }
    } catch (JSONException e) {
      LogUtils.e(TAG, "Json Exception: " + e.getMessage());
    } catch (Exception e) {
      LogUtils.e(TAG, "Exception: " + e.getMessage());
    }
  }

  /**
   * Showing notification with text only
   */
  private void showNotificationMessage(Context context, String title, String message,
      String timeStamp, Intent intent) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
  }

  /**
   * Showing notification with text and image
   */
  private void showNotificationMessageWithBigImage(Context context, String title, String message,
      String timeStamp, Intent intent, String imageUrl) {
    notificationUtils = new NotificationUtils(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
  }
}
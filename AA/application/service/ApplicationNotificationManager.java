package com.application.service;

import static com.application.fcm.NotificationUtils.NOTIFICATION_CHANEL_ID;
import static com.application.fcm.NotificationUtils.NOTIFICATION_COLOR;
import static com.application.fcm.NotificationUtils.createChanel;
import static com.application.fcm.NotificationUtils.showNotificationCompat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import com.application.constant.Constants;
import com.application.entity.NotificationMessage;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.ui.MainActivity;
import com.application.util.BadgeUtil;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.io.File;


public class ApplicationNotificationManager {

  public final static int KEY_PUSH_NOTIFICATION = 1;
  private final static String TAG = "ApplicationNotificationManager";
  private Context context;
  private NotificationManagerCompat mNotificationManager;

  public ApplicationNotificationManager(Context context) {
    this.context = context;
    mNotificationManager = NotificationManagerCompat.from(context);
  }

  public static String getMessageNotification(Context context, NotificationMessage notiMessage) {
    String[] logList = notiMessage.getLogArgs();
    String arg1 = "";

    // Get the first argument
    if (logList != null && logList.length > 0) {
      arg1 = logList[0];
    }

    String message = null;

    // get resource type
    int notiType = notiMessage.getNotiType();
    switch (notiType) {
      case Constants.NOTI_REPLY_YOUR_COMMENT:
        message = context
            .getString(R.string.reply_comment_responded_to_notification);
        break;

      case Constants.NOTI_COMMENT_BUZZ:
        message = context
            .getString(R.string.buzz_responded_to_notification);
        break;
      case Constants.NOTI_FAVORITED_CREATE_BUZZ:
        message = String
            .format(context
                    .getString(R.string.buzz_created_notification),
                arg1.length() > Constants.NOTIFICATION_MAX_LENGTH_NAME ? arg1
                    .substring(
                        0,
                        Constants.NOTIFICATION_MAX_LENGTH_NAME)
                    + "..."
                    : arg1);
        break;
      case Constants.NOTI_CHAT_TEXT:
        message = context.getString(R.string.noti_new_chat_msg_text);
        break;
      case Constants.NOTI_ONLINE_ALERT:
        message = String
            .format(context
                    .getString(R.string.came_online_notification),
                arg1.length() > Constants.NOTIFICATION_MAX_LENGTH_NAME ? arg1
                    .substring(
                        0,
                        Constants.NOTIFICATION_MAX_LENGTH_NAME)
                    + "..."
                    : arg1);
        break;
      case Constants.NOTI_DAYLY_BONUS:
        message = String.format(
            context.getString(R.string.earned_point_notification),
            arg1);
        break;
      case Constants.NOTI_BUZZ_APPROVED:
      case Constants.NOTI_BACKSTAGE_APPROVED:
        message = context.getString(R.string.image_approved);
        break;
      case Constants.NOTI_FROM_FREE_PAGE:
        message = notiMessage.getContent();
        break;
      case Constants.NOTI_REQUEST_CALL:
        message = context.getString(R.string.request_call, arg1);
        break;
      case Constants.NOTI_DENIED_BUZZ_IMAGE:
        message = context.getString(R.string.denied_image_buzz);
        break;
      case Constants.NOTI_DENIED_BACKSTAGE:
        message = context.getString(R.string.denied_backstage);
        break;
      case Constants.NOTI_APPROVE_BUZZ_TEXT:
        message = context.getString(R.string.approve_text_buzz);
        break;
      case Constants.NOTI_APPROVE_COMMENT:
        message = context.getString(R.string.approve_comment);
        break;
      case Constants.NOTI_APPROVE_SUB_COMMENT:
        message = context.getString(R.string.approve_sub_comment);
        break;
      case Constants.NOTI_DENI_SUB_COMMENT:
        message = context.getString(R.string.denied_sub_comment);
        break;
      case Constants.NOTI_DENIED_BUZZ_TEXT:
        message = context.getString(R.string.denied_text_buzz);
        break;
      case Constants.NOTI_DENIED_COMMENT:
        message = context.getString(R.string.denied_comment);
        break;
      case Constants.NOTI_APPROVE_USERINFO:
        message = context.getString(R.string.approve_user_info);
        break;
      case Constants.NOTI_APART_OF_USERINFO:
        message = context.getString(R.string.apart_of_user_info);
        break;
      case Constants.NOTI_DENIED_USERINFO:
        message = context.getString(R.string.denied_user_info);
        break;
      default:
        break;
    }
    LogUtils.e(TAG,
        "GetMessageNotification.GCM Message: " + message + " ---- MessageType: " + notiMessage
            .getNotiType());
    return message;
  }

  public static int getIconNotification(NotificationMessage notiMessage) {
    int iconRes;
    switch (notiMessage.getNotiType()) {
      case Constants.NOTI_CHAT_TEXT:
      case Constants.NOTI_REPLY_YOUR_COMMENT:
      case Constants.NOTI_FAVORITED_CREATE_BUZZ:
      case Constants.NOTI_COMMENT_BUZZ:
        iconRes = R.drawable.ic_notifi_message;
        break;
      default:
        iconRes = R.drawable.ic_notifi_others;
        break;
    }
    return iconRes;
  }

  public static boolean isOnlineAlertNotification(NotificationMessage message) {
    return message.getNotiType() == Constants.NOTI_ONLINE_ALERT;
  }

  /**
   * Issues a notification to inform the user that server has sent a message.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void showNotification(NotificationMessage notiMessage) {
    String message = getMessageNotification(context, notiMessage);
    String title = context.getString(R.string.common_app_name);

    Intent notificationIntent = new Intent(context, MainActivity.class);
    notificationIntent.putExtra(
        WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE, notiMessage);
    // set intent so it does not start a new activity
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_NEW_TASK);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    createChanel(this.context);

    int badgeCount = notiMessage.getBadgeNumber();
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    inboxStyle.addLine(Html.fromHtml(message));
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context,
        NOTIFICATION_CHANEL_ID)
        .setTicker(message)
        .setOngoing(false)
        .setUsesChronometer(false)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentTitle(title)
        .setAutoCancel(true)
        .setColor(Color.parseColor(NOTIFICATION_COLOR))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentIntent(pendingIntent);
    if (UserPreferences.getInstance().isSoundOn()) {
      mBuilder.setSound(Uri.parse("android.resource://"
          + context.getPackageName() + File.separator + R.raw.notice));
    }

    // set badge count
    if (badgeCount > 0) {
      mBuilder.setNumber(badgeCount);
    }

    Notification notification = mBuilder.build();
    showNotificationCompat(context, KEY_PUSH_NOTIFICATION, notification);

    // only update badge on chat text
    if (notiMessage.getNotiType() == Constants.NOTI_CHAT_TEXT) {
      // update badge
      BadgeUtil.updateBadge(context.getApplicationContext(), notification, badgeCount);
      mNotificationManager.notify(1,notification);
    }
  }
}
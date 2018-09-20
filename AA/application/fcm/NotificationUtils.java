package com.application.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import glas.bbsystem.R;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * TODO Notification utils Created by Robert on 27 Dec 2016
 */
public class NotificationUtils {

  public static final String NOTIFICATION_COLOR = "#f26149";
  public static final String NOTIFICATION_CHANEL_NAME = "my_chanel_99";
  public static final String NOTIFICATION_CHANEL_ID = "my_chanel_id_98";
  private static String TAG = NotificationUtils.class.getSimpleName();

  private Context mContext;

  public NotificationUtils(Context mContext) {
    this.mContext = mContext;
  }

  /**
   * create notification chanel for android 26+
   */
  public static void createChanel(Context context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANEL_ID,
          NOTIFICATION_CHANEL_NAME, importance);
      channel.setShowBadge(true);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }
  }

  public static void showNotificationCompat(Context context, int notificationId,
      Notification notification) {
    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
    notificationManagerCompat.notify(notificationId, notification);
  }

  /**
   * TODO Method checks if the app is in background or not
   */
  public static boolean isAppIsInBackground(Context context) {
    boolean isInBackground = true;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activeProcess : processInfo.pkgList) {
            if (activeProcess.equals(context.getPackageName())) {
              isInBackground = false;
            }
          }
        }
      }
    } else {
      List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
      ComponentName componentInfo = taskInfo.get(0).topActivity;
      if (componentInfo.getPackageName().equals(context.getPackageName())) {
        isInBackground = false;
      }
    }

    return isInBackground;
  }

  // Clears notification tray messages
  public static void clearNotifications(Context context) {
    NotificationManager notificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  public static long getTimeMilliSec(String timeStamp) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date date = format.parse(timeStamp);
      return date.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * TODO Show notification message
   */
  public void showNotificationMessage(String title, String message, String timeStamp,
      Intent intent) {
    showNotificationMessage(title, message, timeStamp, intent, null);
  }

  /**
   * TODO Show notification message
   */
  public void showNotificationMessage(final String title, final String message,
      final String timeStamp, Intent intent, String imageUrl) {
    // Check for empty push message
    if (TextUtils.isEmpty(message)) {
      return;
    }

    // notification icon
    final int icon = R.drawable.ic_notification;

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    final PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );

    final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,
        NOTIFICATION_CHANEL_ID);

    final Uri alarmSound = Uri.parse("android.resource://"
        + mContext.getPackageName() + File.separator
        + R.raw.notice);//Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification");

    if (!TextUtils.isEmpty(imageUrl)) {

      if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl)
          .matches()) {

        Bitmap bitmap = getBitmapFromURL(imageUrl);

        if (bitmap != null) {
          showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp,
              resultPendingIntent, alarmSound);
        } else {
          showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent,
              alarmSound);
        }
      }
    } else {
      showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent,
          alarmSound);
      playNotificationSound();
    }
  }

  /**
   * TODO Show notification message with small icon
   */
  private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title,
      String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    inboxStyle.addLine(message);

    createChanel(mContext);
    Notification notification = mBuilder.setTicker(title)
        .setChannelId(NOTIFICATION_CHANEL_ID)
        .setWhen(0)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentIntent(resultPendingIntent)
        .setSound(alarmSound)
        .setStyle(inboxStyle)
        .setWhen(getTimeMilliSec(timeStamp))
        .setColor(Color.parseColor(NOTIFICATION_COLOR))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_notification)
        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
        .setContentText(message)
        .build();

    showNotificationCompat(mContext, Config.NOTIFICATION_ID, notification);
  }

  /**
   * TODO Show notification message with big content, big icon
   */
  private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon,
      String title, String message, String timeStamp, PendingIntent resultPendingIntent,
      Uri alarmSound) {
    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
    bigPictureStyle.setBigContentTitle(title);
    bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
    bigPictureStyle.bigPicture(bitmap);
    Notification notification = mBuilder.setTicker(title).setWhen(0)
        .setChannelId(NOTIFICATION_CHANEL_ID)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentIntent(resultPendingIntent)
        .setSound(alarmSound)
        .setStyle(bigPictureStyle)
        .setWhen(getTimeMilliSec(timeStamp))
        .setColor(Color.parseColor(NOTIFICATION_COLOR))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_notification)
        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
        .setContentText(message)
        .build();

    showNotificationCompat(mContext, Config.NOTIFICATION_ID_BIG_IMAGE, notification);
  }

  /**
   * Downloading push notification image before displaying it in the notification tray
   */
  public Bitmap getBitmapFromURL(String strURL) {
    try {
      URL url = new URL(strURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);
      return myBitmap;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Playing notification sound
  public void playNotificationSound() {
    try {
      Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
          + "://" + mContext.getPackageName() + "/raw/notification");
      Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
      r.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

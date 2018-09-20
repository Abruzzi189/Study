package com.application.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.application.AndGApp;
import com.application.Config;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.MessageClient;
import com.application.connection.request.GetUserStatusRequest;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.entity.HumanHeight;
import com.application.service.ChatService;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.MainActivity;
import com.application.ui.chat.ChatAdapter;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;


public class Utility {

  public static final String TAG_MP4 = ".mp4";
  public static final String TAG_MP3 = ".mp3";
  public static final String DATE_ONLY_WITH_SLASH_FORMAT = "yyyy/MM/dd";
  public static final String DATE_ONLY_JP_FORMAT_SHORT = "MM月 dd日 HH:mm";
  public static final String DATE_READ_TIME_FORMAT = "yyyyMMddHHmmss";
  public static final String FULL_DATE_JP_DATETIME_FORMAT = "MM月dd日(EEE) HH時mm分";
  public static final String FULL_DATE_JP_TIME_FORMAT = "dd日(EEE) HH時mm分";
  public static final int REQUEST_VOICE_CALL_OFF = 1;
  public static final int REQUEST_VIDEO_CALL_OFF = 2;
  public static final int REQUEST_VOICE_VIDEO_OFF = 3;
  public static final String format1 = "%1$s";
  public static final String format2 = "%2$s";
  public static final String format3 = "%3$s";
  private static final String TAG = "Utility";
  private static final String CHAT_FOLDER = "chat";
  private static final String STAMP_FOLDER = "stamp";
  private static final String AUDIO_FOLDER = "audio";
  private static final String VIDEO_FOLDER = "video";
  private static final String EMAIL_PATTERN = "^([0-9a-zA-Z!#$%&`*+-\\/=?^_'.{}|~]+@)((\\[(\\d{1,3}\\.){3}\\d{1,3}\\])|(([0-9a-zA-Z-]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,6}))$";
  private static final Pattern mEmailPatten = Pattern.compile(EMAIL_PATTERN);
  private static final int TOTAL_LOOKATME = 100000;
  private static final int TOTAL_BACKSTAGE = 10000;
  private static final int THRESOLD1 = 10;
  private static final int THRESOLD2 = 100;
  private static final int THRESOLD3 = 1000;
  public static SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd",
      Locale.US);
  public static SimpleDateFormat MMDDYYYY_SEPARATOR = new SimpleDateFormat(
      "MM/dd/yyyy", Locale.US);
  public static SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(
      "yyyyMMddHHmmss", Locale.US);
  /**
   * HoanDC create Using for list conversation sort list
   */
  public static SimpleDateFormat YYYYMMDDHHMMSSSSS = new SimpleDateFormat(
      "yyyyMMddHHmmssSSS", Locale.US);
  private static float density;

  public static void enableStrictMode() {
    if (Config.DEBUG) {
    }
  }

  public static void copyStream(InputStream input, OutputStream output)
      throws IOException {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = input.read(buffer)) != -1) {
      output.write(buffer, 0, bytesRead);
    }
  }

  public static boolean isSDCardExist() {
    return Environment.MEDIA_MOUNTED.equals(Environment
        .getExternalStorageState());
  }

  public static boolean isNetworkConnected(Context con) {
    Context context = AndGApp.get();
    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      return true;
    }
    return false;
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }
    Matcher matcher;
    matcher = mEmailPatten.matcher(email);
    return matcher.matches();
  }

  /**
   * Uses static final constants to detect if the device's platform version is Gingerbread or
   * later.
   */
  public static boolean hasGingerbread() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
  }

  /**
   * Uses static final constants to detect if the device's platform version is Honeycomb or later.
   */
  public static boolean hasHoneycomb() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  /**
   * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
   * later.
   */
  public static boolean hasHoneycombMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
  }

  /**
   * Uses static final constants to detect if the device's platform version is ICS or later.
   */
  public static boolean hasICS() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  public static boolean hasJELLY() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  public static String getDeviceId(Context con) {
    Context context = AndGApp.get();
    TelephonyManager telephonyManager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    return telephonyManager.getDeviceId();
  }

  public static String getDeviceId() {
    Context context = AndGApp.get();
    return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  public static int convertHumanHeight2Centimeter(HumanHeight height) {
    if (height != null) {
      return (int) ((height.getFoot() * 12 + height.getInch()) * 2.54);
    } else {
      return 0;
    }
  }

  public static int convertInch2Centimeter(int foot, int inch) {
    return (int) Math.round(((foot * 12 + inch) * 2.54));
  }

  /**
   * preference: http://www.manuelsweb.com/ft_in_cm.htm
   */
  public static HumanHeight convertCentimeter2HumanHeight(int centimeter) {
    int feet = (int) (centimeter / 30.48);
    double inchs = Math.round((centimeter - feet * 30.48) / 2.54);
    if (inchs == 12) {
      feet++;
      inchs = 0;
    }
    return new HumanHeight((int) feet, (int) inchs);
  }

  public static String getDistanceString(Context con,
      double distanceInKilometers) {
    Context context = AndGApp.get();
    String value = "";

    if (distanceInKilometers < Constants.LOCATION_HERE_LIMIT) {
      // TODO : Fix bug 1451.
      value = context.getString(R.string.share_my_buzz_location);
    } else {
      int distanceUnit = Preferences.getInstance().getDistanceUnit();
      double distance = distanceInKilometers;
      if (distanceUnit == Constants.DISTANCE_UNIT_MILE) {
        distance = (double) Math.round(distance * 6.21371) / 10.0;
      } else {
        distance = (double) Math.round(distance * 10) / 10.0;
      }
      DecimalFormat decimalFormat = new DecimalFormat("#.#");
      value = decimalFormat.format(distance) + " "
          + Preferences.getInstance().getDistanceUnitInString();
    }

    return value;
  }

  public static boolean isSimReady(Context con) {
    Context context = AndGApp.get();
    TelephonyManager telephonyManager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
      return true;
    }
    return false;
  }

  public static int getWidthScreen(Activity activity) {
    Context context = AndGApp.get();
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getHeightScreen(Activity activity) {
    Context context = AndGApp.get();
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  public static String getAgeFromBirthday(String birthday) {
    int diff = 0;
    if (birthday.length() > 0) {
      try {
        Date date = YYYYMMDD.parse(birthday);
        Calendar a = getCalendar(date);
        Calendar b = getCalendar(new Date());
        diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
            || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
            .get(Calendar.DATE) > b.get(Calendar.DATE))) {
          diff--;
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return String.valueOf(diff);
  }

  public static String getAgeFromBirthday(long birthday) {
    Calendar a = getCalendar(new Date(birthday));
    Calendar b = getCalendar(new Date());
    int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
        || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
        .get(Calendar.DATE) > b.get(Calendar.DATE))) {
      diff--;
    }
    return String.valueOf(diff);
  }

  public static Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(date);
    return cal;
  }

  public static String getGender(Context con, int gender) {
    Context context = AndGApp.get();
    switch (gender) {
      case 0:
        return context.getString(R.string.male);
      case 1:
        return context.getString(R.string.female);
      default:
        throw new IllegalArgumentException("Gender invalid");
    }
  }

  public static String convertDateYYYYMMDDHHMMSS(long date) {
    Date dateConvert = new Date(date);
    StringBuilder nowYYYYMMDDHHMMSS = new StringBuilder(
        YYYYMMDDHHMMSS.format(dateConvert));
    return nowYYYYMMDDHHMMSS.toString();
  }

  public static Date convertDateYYYYMMDDHHMMSS(String datesString) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date date = null;
    try {
      date = dateFormat.parse(datesString);
    } catch (ParseException e) {
      date = new Date();
      e.printStackTrace();
    }
    return date;
  }

  public static String getTimeInGMT() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    Date date = calendar.getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }

  public static String getTimeInGMT(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }

  public static String getLoginTime() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    Date date = calendar.getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
        Locale.getDefault());
    return dateFormat.format(date);
  }

  public static String getCommentTime() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    Date date = calendar.getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }

  public static String convertGMTtoLocale(String time) {
    String stringFomat = "yyyyMMddHHmmssSSS";
    SimpleDateFormat dateFormat = new SimpleDateFormat(stringFomat,
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    try {
      Date date = dateFormat.parse(time);
      dateFormat = new SimpleDateFormat(stringFomat, Locale.getDefault());
      dateFormat.setTimeZone(TimeZone.getDefault());
      return dateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String convertReadTimeGMTtoLocale(String time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_READ_TIME_FORMAT,
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    try {
      Date date = dateFormat.parse(time);
      dateFormat = new SimpleDateFormat(DATE_READ_TIME_FORMAT, Locale.getDefault());
      dateFormat.setTimeZone(TimeZone.getDefault());
      return dateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String convertGMTtoLocale(String time, String formatServer,
      String formatLocale) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(formatServer,
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    try {
      Date date = dateFormat.parse(time);
      dateFormat = new SimpleDateFormat(formatLocale, Locale.getDefault());
      dateFormat.setTimeZone(TimeZone.getDefault());
      return dateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String convertLocaleToGMT(String time) {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    Date date;
    try {
      date = dateFormat.parse(time);
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      String d = dateFormat.format(date);
      return d;
    } catch (ParseException e) {
      e.printStackTrace();
      return time;
    }
  }

  public static Date convertLocaleToGMTNotMiliseconds(String time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date date = null;
    try {
      date = dateFormat.parse(time);
      return date;
    } catch (ParseException e) {
      date = new Date();
      e.printStackTrace();
    }
    return date;
  }

  public static long convertLocaleToGMT(long milisecond) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milisecond);
    Date date = calendar.getTime();
    String time = dateFormat.format(date);
    return convertTimeToMilisecond(time);

  }

  public static String getTimeInLocale() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    Date date = calendar.getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    return dateFormat.format(date);
  }

  public static long convertTimeToMilisecond(String time) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    Date date;
    try {
      date = dateFormat.parse(time);
      long timeInMili = date.getTime();
      return timeInMili;
    } catch (ParseException e) {
      LogUtils.e(TAG, String.valueOf(e.getMessage()));
    }
    return 0L;
  }

  public static String getTitleForHeaderInListView(Date date, Context context) {
    // Calendar calendar = Calendar.getInstance();
    // int currentYear = calendar.get(Calendar.YEAR);
    // int currentMonth = calendar.get(Calendar.MONTH);
    // int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    // Calendar calendar2 = Calendar.getInstance();
    // calendar2.setTime(date);
    // int year = calendar2.get(Calendar.YEAR);
    // int month = calendar2.get(Calendar.MONTH);
    // int dayOfYear = calendar2.get(Calendar.DAY_OF_YEAR);

    SimpleDateFormat dateFormat;

    // if (currentYear == year && currentMonth == month
    // && currentDayOfYear == dayOfYear) {
    // String format = context.getString(R.string.title_chat_time);
    // dateFormat = new SimpleDateFormat(format, Locale.getDefault());
    // } else {
    // String format = context.getString(R.string.title_chat_time_full);
    // dateFormat = new SimpleDateFormat(format, Locale.getDefault());
    // }
    String format = context.getString(R.string.title_chat_time);
    dateFormat = new SimpleDateFormat(format, Locale.getDefault());
    return dateFormat.format(date);
  }

  public static String getTimeStamp(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    return dateFormat.format(date);
  }

  public static String getTitleWhenListEmpty(Date date, Context context) {
    SimpleDateFormat dateFormat;
    String format = context.getString(R.string.title_chat_time);
    dateFormat = new SimpleDateFormat(format, Locale.getDefault());
    return dateFormat.format(date);
  }

  public static Date getDateFromTimeStamp(String timeStamp) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    try {
      return dateFormat.parse(timeStamp);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return Calendar.getInstance().getTime();
  }

  public static Date getDateTimeInGMT() {
    TimeZone tz = TimeZone.getDefault();
    long number = tz.getRawOffset();
    long local = System.currentTimeMillis();
    return new Date(local - number);

    // SimpleDateFormat dateFormat = new
    // SimpleDateFormat("yyyyMMddHHmmssSSS",
    // Locale.getDefault());
    // Date date = Calendar.getInstance().getTime();
    // try {
    // dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    // String d = dateFormat.format(date);
    //
    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
    // "yyyyMMddHHmmssSSS", Locale.getDefault());
    // Date date2 = simpleDateFormat.parse(d);
    // return date2;
    //
    // } catch (ParseException e) {
    // e.printStackTrace();
    // // if exception -> get time local
    // return null;
    // }
  }

  public static String measureTextSize(TextView textView, String text,
      float textViewWidth) {
    if (text == null) {
      return "";
    }
    String newText = text;
    float textWidth = textView.getPaint().measureText(newText);
    while (textWidth > textViewWidth) {
      if (text.length() == 0) {
        return "...";
      }
      text = text.substring(0, text.length() - 1);
      newText = text.replace("\u3000", " ").trim() + "...";
      textWidth = textView.getPaint().measureText(newText);
    }
    return newText;
  }

  public static String getDifference(Context con, Calendar startDateTime,
      Calendar endDateTime) {
    Context context = AndGApp.get();
    long milliseconds1 = startDateTime.getTimeInMillis();
    long milliseconds2 = endDateTime.getTimeInMillis();

    long diff = milliseconds2 - milliseconds1;

    long year = (diff / ((long) 1000 * (long) 60 * (long) 60 * (long) 24
        * (long) 28 * (long) 12));
    if (year >= 1) {
      // return String.format("%d mo", month);
      return (year + context.getString(R.string.time_year_ago));
    }

    long month = (diff / ((long) 1000 * (long) 60 * (long) 60 * (long) 24 * (long) 28));
    if (month >= 1) {
      // return String.format("%d mo", month);
      return (month + context.getString(R.string.time_month_ago));
    }

    int week = (int) (diff / (1000 * 60 * 60 * 24 * 7));
    if (week >= 1) {
      // return String.format("%d w", week);
      return (week + context.getString(R.string.time_week_ago));
    }

    int days = (int) (diff / (1000 * 60 * 60 * 24));
    if (days >= 1) {
      // return String.format("%d d", days);
      return (days + context.getString(R.string.time_day_ago));
    }

    int hours = (int) (diff / (1000 * 60 * 60));

    // Thoi gian 59 gio thi thanh mot ngay
    if (hours > 59) {
      // return String.format("%d h", hours);
      return (String.valueOf(1) + context
          .getString(R.string.time_day_ago));
    }

    // Thoi gian 1h den 1h59 hien thi la 2 gio
    if (hours > 0) {
      // return String.format("%d h", hours);
      return (String.valueOf(hours + 1) + context
          .getString(R.string.time_hour_ago));
    }

    // Tren 30 phut van hien thi theo so gio
    int minutes = (int) (diff / (1000 * 60));
    if (minutes > 30) {
      // return String.format("%d m", minutes);
      return (String.valueOf(1) + context
          .getString(R.string.time_hour_ago));
    }

    // Duoi 30 phut van hien thi theo so phut
    if (minutes > 0) {
      // return String.format("%d m", minutes);
      return (minutes + context.getString(R.string.time_minute_ago));
    }

    // Duoi 1 phut van hien thi la now
    // int seconds = (int) (diff / (1000));
    // if (seconds > 0) {
    // // return String.format("%d s", seconds);
    // return (1 + context.getString(R.string.time_minute_ago));
    // }

    // return "0 s";
    return (context.getString(R.string.common_now));
  }

  public static String getTimelineDif(Calendar startDateTime,
      Calendar endDateTime) {
    Context context = AndGApp.get();
    long milliseconds1 = startDateTime.getTimeInMillis();
    long milliseconds2 = endDateTime.getTimeInMillis();

    long diff = milliseconds2 - milliseconds1;

    if (endDateTime.get(Calendar.YEAR) - startDateTime.get(Calendar.YEAR) > 0) {
      return getTimelineAfterYear(startDateTime.getTime());
    }

    int hours = (int) (diff / (1000 * 60 * 60));
    if (hours >= 24) {
      return getTimeLineAfter24h(startDateTime.getTime());
    }

    if (hours > 0) {
      return (String.valueOf(hours + 1) + context
          .getString(R.string.time_hour_ago));
    }

    int minutes = (int) (diff / (1000 * 60));
    if (minutes > 30) {
      return (String.valueOf(1) + context
          .getString(R.string.time_hour_ago));
    }

    if (minutes > 0) {
      return (minutes + context.getString(R.string.time_minute_ago));
    }

    return (context.getString(R.string.common_now));
  }

  private static String getTimeLineAfter24h(Date date) {
    SimpleDateFormat format = new SimpleDateFormat(DATE_ONLY_JP_FORMAT_SHORT, Locale.US);
    return format.format(date);
  }

  private static String getTimelineAfterYear(Date date) {
    SimpleDateFormat format = new SimpleDateFormat(DATE_ONLY_WITH_SLASH_FORMAT, Locale.US);
    return format.format(date);
  }

  public static boolean checkGreaterThenDay(long lastTime, long newTime) {
    Date lastDate = new Date(lastTime);
    Date newDate = new Date(newTime);
    Calendar lastCalendar = Calendar.getInstance();
    lastCalendar.setTime(lastDate);

    Calendar newCalendar = Calendar.getInstance();
    newCalendar.setTime(newDate);

    int lastYear = lastCalendar.get(Calendar.YEAR);
    int newYear = newCalendar.get(Calendar.YEAR);
    if (lastYear == newYear) {
      int dayOfLastYear = lastCalendar.get(Calendar.DAY_OF_YEAR);
      int dayOfNewYear = newCalendar.get(Calendar.DAY_OF_YEAR);
      if (dayOfLastYear < dayOfNewYear) {
        return true;
      }
    } else if (lastYear < newYear) {
      return true;
    }
    return false;
  }

  public static boolean checkThisDay(long lastTime, long newTime) {
    Date lastDate = new Date(lastTime);
    Date newDate = new Date(newTime);
    Calendar lastCalendar = Calendar.getInstance();
    lastCalendar.setTime(lastDate);

    Calendar newCalendar = Calendar.getInstance();
    newCalendar.setTime(newDate);

    int lastYear = lastCalendar.get(Calendar.YEAR);
    int newYear = newCalendar.get(Calendar.YEAR);
    if (lastYear == newYear) {
      int dayOfLastYear = lastCalendar.get(Calendar.DAY_OF_YEAR);
      int dayOfNewYear = newCalendar.get(Calendar.DAY_OF_YEAR);
      if (dayOfLastYear == dayOfNewYear) {
        return true;
      }
    }
    return false;
  }

  public static String convertLocalDate(Date date) {
    if (date == null) {
      return null;
    }
    Utility.YYYYMMDDHHMMSSSSS.setTimeZone(TimeZone.getTimeZone("GMT"));
    String dateConver = Utility.YYYYMMDDHHMMSSSSS.format(date);
    Date dateSend = new Date();
    try {
      dateSend = Utility.YYYYMMDDHHMMSSSSS.parse(dateConver);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Utility.YYYYMMDDHHMMSSSSS.setTimeZone(TimeZone.getDefault());
    return Utility.YYYYMMDDHHMMSSSSS.format(dateSend);
  }

  // An ban phim khi goi method nay
  public static void hideSoftKeyboard(Context context) {
    Context con = AndGApp.get();
    if (con == null) {
      return;
    }
    InputMethodManager inputMethodManager = (InputMethodManager) con
        .getSystemService(Activity.INPUT_METHOD_SERVICE);
    View view = ((Activity) context).getCurrentFocus();
    if (view == null) {
      return;
    }
    IBinder iBinder = view.getWindowToken();
    if (iBinder != null) {
      inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
    }
  }

  /**
   * An ban phim khi click vao view khac' ngoai editext
   *
   * @param view root of activity
   */
  // private static OnTouchListener mTouchListener = new OnTouchListener() {
  //
  // @Override
  // public boolean onTouch(View v, MotionEvent event) {
  // hideSoftKeyboard(mContext);
  // return false;
  // }
  // };

  // private static Context mContext;
  public static void hideKeyboard(final Activity activity, View view) {
    final Context mContext = activity;
    if (activity == null) {
      return;
    }

    // if (mContext == null || !(mContext instanceof BaseFragmentActivity))
    // {
    // mContext = activity;
    // }

    // Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText) && !(view instanceof Button)) {
      view.setOnTouchListener(new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
          hideSoftKeyboard(mContext);
          return false;
        }
      });
    }

    // If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      int size = ((ViewGroup) view).getChildCount();
      for (int i = 0; i < size; i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        hideKeyboard(activity, innerView);
      }
    }
  }

  /**
   * Show keyboard after a delay
   */
  public static Handler showDelayKeyboard(final View view, long timeDelay) {
    Handler handler = new Handler();
    if (view == null || view.getContext() == null) {
      return handler;
    }

    if (timeDelay < 0) {
      timeDelay = 0;
    }
    view.requestFocus();
    Runnable delayRunnable = new Runnable() {

      @Override
      public void run() {
        InputMethodManager keyboard = (InputMethodManager) view
            .getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(view, InputMethodManager.SHOW_FORCED);
      }
    };
    handler.postDelayed(delayRunnable, timeDelay);
    return handler;
  }

  public static String encryptPassword(String unencryptedPassword) {
    String encryptedPassword = unencryptedPassword;
    StringBuilder stringBuilder = new StringBuilder();

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] bytes = unencryptedPassword.getBytes("UTF-8");
      digest.update(bytes, 0, bytes.length);
      bytes = digest.digest();
      int length = bytes.length;

      for (int i = 0; i < length; i++) {
        byte b = bytes[i];
        stringBuilder.append(String.format("%02x", b));
      }
      encryptedPassword = stringBuilder.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } finally {
      stringBuilder.delete(0, stringBuilder.length());
    }

    return encryptedPassword;
  }

  public static File getStampFolder() {
    String folderRootPath = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + File.separator + CHAT_FOLDER;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append(Environment.getExternalStorageDirectory()
            .getAbsolutePath()).append(File.separator)
        .append(CHAT_FOLDER).append(File.separator)
        .append(STAMP_FOLDER);
    String folderStampPath = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());

    File folderRoot = new File(folderRootPath);
    File folderStamp = new File(folderStampPath);

    if (!folderRoot.exists()) {
      folderRoot.mkdir();
    }
    if (!folderStamp.exists()) {
      folderStamp.mkdir();
    }
    return folderStamp;
  }

  public static File getAudioFolder(String userId) {
    String storageFolder = Environment.getExternalStorageDirectory()
        .getAbsolutePath();
    String separator = File.separator;
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(storageFolder).append(separator)
        .append(CHAT_FOLDER);
    String folderRootPath = stringBuilder.toString();

    stringBuilder.append(separator).append(AUDIO_FOLDER);
    String folderAudio = stringBuilder.toString();

    stringBuilder.append(separator).append(userId);
    String folderUser = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());

    File folderRoot = new File(folderRootPath);
    File fileAudoi = new File(folderAudio);
    File fileUser = new File(folderUser);

    if (!folderRoot.exists()) {
      folderRoot.mkdir();
    }
    if (!fileAudoi.exists()) {
      fileAudoi.mkdir();
    }
    if (!fileUser.exists()) {
      fileUser.mkdir();
    }

    return fileUser;
  }

  public static File getVideoFolder(String userId) {
    String storageFolder = Environment.getExternalStorageDirectory()
        .getAbsolutePath();
    String separator = File.separator;
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(storageFolder).append(separator)
        .append(CHAT_FOLDER);
    String folderRootPath = stringBuilder.toString();

    stringBuilder.append(separator).append(VIDEO_FOLDER);
    String folderVideo = stringBuilder.toString();

    stringBuilder.append(separator).append(userId);
    String folderUser = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());

    File folderRoot = new File(folderRootPath);
    File fileAudoi = new File(folderVideo);
    File fileUser = new File(folderUser);

    if (!folderRoot.exists()) {
      folderRoot.mkdir();
    }
    if (!fileAudoi.exists()) {
      fileAudoi.mkdir();
    }
    if (!fileUser.exists()) {
      fileUser.mkdir();
    }
    return fileUser;
  }

  public static boolean existAudioFile(String userId, String fileId) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(Utility.getAudioFolder(userId).getAbsolutePath())
        .append(File.separator).append(fileId).append(TAG_MP3);
    String path = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());
    File file = new File(path);
    if (file.exists()) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean existVideoFile(String userId, String fileId) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(Utility.getVideoFolder(userId).getAbsolutePath())
        .append(File.separator).append(fileId).append(TAG_MP4);
    String path = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());
    File file = new File(path);
    if (file.exists()) {
      return true;
    } else {
      return false;
    }
  }

  public static Bitmap fixOrientation(Bitmap mBitmap) {
    if (mBitmap.getWidth() > mBitmap.getHeight()) {
      Matrix matrix = new Matrix();
      matrix.postRotate(90);
      mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
          mBitmap.getHeight(), matrix, true);
    }
    return mBitmap;
  }

  public static Bitmap decodeSampledBitmapFromPath(String filePath,
      int reqWidth, int reqHeight) {

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filePath, options);

    // Calculate inSampleSize
    options.inSampleSize = ImageUtil.calculateInSampleSize(options,
        reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;

    return BitmapFactory.decodeFile(filePath, options);
  }

  public static void showToastMessage(Context context, String message) {
    Context con = AndGApp.get();
    if (con != null) {
      int duration = Toast.LENGTH_SHORT;
      Toast toast = Toast.makeText(context, message, duration);
      toast.show();
    }
  }

  public static boolean isBlockedWithUser(Context context, String userId) {
    boolean blocked = false;
    Context con = AndGApp.get();
    if (con != null) {
      String blockedUsersList = BlockUserPreferences.getInstance()
          .getBlockedUsersList();
      if (blockedUsersList != null && blockedUsersList.contains(userId)) {
        blocked = true;
      }
    }

    return blocked;
  }

  public static String getTimeString(long millis) {
    if (millis == 0) {
      return "0:00";
    }
    StringBuffer buf = new StringBuffer();
    int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
    int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
    if ((((millis % (1000 * 60 * 60)) % (1000 * 60)) % 1000) >= 500) {
      seconds++;
    }

    buf.append(String.format("%01d", minutes)).append(":");

    if (seconds < 10) {
      buf.append(String.format("%02d", seconds));
    } else {
      buf.append(String.format("%01d", seconds));
    }

    return buf.toString();
  }

  public static String replaceStartEndString(String string) {
    String result = string;
    String newstr = "";
    while (result.startsWith(" ")) {
      if (result.length() > 1) {
        newstr = result.substring(1, result.length());
        result = newstr;
      } else {
        break;
      }
    }
    while (result.endsWith(" ")) {
      if (result.length() > 2) {
        newstr = result.substring(0, result.length() - 1);
        result = newstr;
      } else {
        break;
      }
    }
    return result;
  }

  public static String getAudioDurationTimeString(String path) {
    String result = "0:00";
    if (path != null && path.length() > 0) {
      try {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        long duration = mMediaPlayer.getDuration();
        result = Utility.getTimeString(duration);
        if (mMediaPlayer.isPlaying()) {
          mMediaPlayer.pause();
        }
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public static long getAudioDurationTimeLong(String path) {
    long result = 0;
    if (path != null && path.length() > 0) {
      MediaPlayer mMediaPlayer = null;
      try {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        result = mMediaPlayer.getDuration();
      } catch (Exception e) {
        LogUtils.e(TAG, e.getMessage());
      } finally {
        if (mMediaPlayer != null) {
          mMediaPlayer.reset();
          mMediaPlayer.release();
        }
      }
    }
    return result;
  }

  public static double convertCentimeterToMiles(double centimeter) {
    return Math.ceil(centimeter / 0.00254);
  }

  public static void sendLocalBroadcast(Context context, Intent intent) {
    if (context == null || intent == null) {
      return;
    }
    Context con = AndGApp.get();
    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(con);
    lbm.sendBroadcast(intent);
  }

  public static String measureText(String result, TextView textView,
      Resources resource) {
    if (TextUtils.isEmpty(result)) {
      return "";
    }
    float widthView = resource.getDisplayMetrics().widthPixels / 3;
    float textWidth = textView.getPaint().measureText(result);
    boolean isLongName = false;
    while (widthView < textWidth) {
      isLongName = true;
      int length = result.length();
      if (length < 1) {
        break;
      }
      result = result.substring(0, length - 1);
      textWidth = textView.getPaint().measureText(result + "...");
    }
    if (isLongName) {
      result = result + "...";
    }
    return result;
  }

  public static ArrayList<Integer> generateLookAtMePoints() {
    ArrayList<Integer> listPoint = new ArrayList<Integer>();
    int value = THRESOLD1;
    do {
      listPoint.add(value);
      if (value < 1000) {
        value += THRESOLD1;
      } else if (value >= 1000 && value < 10000) {
        value += THRESOLD2;
      } else {
        value += THRESOLD3;
      }
    } while (value <= TOTAL_LOOKATME);
    return listPoint;
  }

  public static ArrayList<Integer> generateBackstagePoints() {
    ArrayList<Integer> listPoint = new ArrayList<Integer>();
    int value = THRESOLD1;
    do {
      listPoint.add(value);
      if (value < 1000) {
        value += THRESOLD1;
      } else {
        value += THRESOLD2;
      }
    } while (value <= TOTAL_BACKSTAGE);
    return listPoint;
  }

  public static boolean isContainDirtyWord(Activity activity, TextView txtView) {
    String text = txtView.getText().toString();
    if (text.length() > 0) {
      String sentence = text.toLowerCase(Locale.US);
      DirtyWords dirtyWord = DirtyWords.getInstance(txtView.getContext());
      ArrayList<String> keys = dirtyWord.getAllKey();
      int size = keys.size();

      for (int i = 0; i < size; i++) {
        String key = keys.get(i);
        if (sentence.contains(key)) {
          ErrorApiDialog.showDirtyWordAlert(activity, key);
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isContainDirtyWord(Activity activity, String text) {
    if (text.length() > 0) {
      String sentence = text.toLowerCase(Locale.US);
      DirtyWords dirtyWord = DirtyWords.getInstance(activity);
      ArrayList<String> keys = dirtyWord.getAllKey();
      int size = keys.size();

      for (int i = 0; i < size; i++) {
        String key = keys.get(i);
        if (sentence.contains(key)) {
          ErrorApiDialog.showDirtyWordAlert(activity, key);
          return true;
        }
      }
    }
    return false;
  }

  public static int getAppVersion(Context context) {
    Context con = AndGApp.get();
    PackageInfo info;
    try {
      info = con.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      return info.versionCode;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String getUserIdFromSipId(String sipId) {

    String userId = "";

    try {
      int start = sipId.indexOf(":");
      int end = sipId.indexOf("@");
      userId = sipId.substring(start + 1, end);
    } catch (NullPointerException npe) {
      LogUtils.d(TAG, npe.getMessage());
    } catch (IndexOutOfBoundsException ioobe) {
      LogUtils.d(TAG, ioobe.getMessage());
    }

    return userId;
  }

  public static SpannableString makeLinkSpan(CharSequence text,
      View.OnClickListener listener) {
    SpannableString link = new SpannableString(text);
    int size = text.length();
    link.setSpan(new Utility().new ClickableString(listener), 0, size,
        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
    return link;
  }

  public static void focusableLink(TextView tv) {
    if (tv.getLinksClickable()) {
      tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }

  /**
   * @param time1 unix time 1 to compare
   * @param time2 unix time 2 to compare
   * @return true: same day
   */
  public static boolean isSameDay(long time1, long time2) {
    Date date1 = new Date(time1);
    Date date2 = new Date(time2);
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
    return fmt.format(date1).equals(fmt.format(date2));
  }

  public static void linkSpanFormat(TextView tv, SpannableString s1,
      SpannableString s2, SpannableString s3) {
    appendText(tv, format1, s1);
    appendText(tv, format2, s2);
    appendText(tv, format3, s3);
  }

  public static void appendText(TextView tv, String format,
      SpannableString span) {
    int index = -1;
    do {
      CharSequence text = tv.getText();
      index = text.toString().indexOf(format);
      if (index >= 0) {
        CharSequence buffer = text.subSequence(index + format.length(),
            text.length());
        tv.setText(tv.getText().subSequence(0, index));
        tv.append(span);
        tv.append(buffer);
      } else {
        break;
      }
    } while (true);
  }

  public static boolean isInPhoneCall(Context context) {
    TelephonyManager telephonyManager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
      return false;
    }
    return true;
  }

  public static Bitmap rotateImage(Bitmap mBitmap, int angle) {
    if (mBitmap != null && angle != 0) {
      Matrix mat = new Matrix();
      mat.postRotate(angle);

      mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
          mBitmap.getHeight(), mat, true);
    }
    return mBitmap;
  }

  public static int getAngle(String path) {
    int angle = 0;
    ExifInterface exif;
    try {
      exif = new ExifInterface(path);
      int orientation = exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);

      if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
        angle = 90;
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
        angle = 180;
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
        angle = 270;
      }
      return angle;
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }
  }

  @SuppressWarnings("deprecation")
  public static boolean isCameraUseByOtherApp() {
    Camera camera = null;
    try {
      camera = Camera.open();
    } catch (RuntimeException e) {
      return true;
    } finally {
      if (camera != null) {
        camera.release();
      }
    }
    return false;
  }

  public static String getCallingDuration(int duration) {
    String callingDuration = "";
    int hour = duration / 3600;
    int minute = (duration / 60) % 60;
    int second = duration % 60;

    if (hour <= 0) {
      callingDuration = String.format(Locale.US, "%02d:%02d", minute,
          second);
    } else if (hour <= 99) {
      callingDuration = String.format(Locale.US, "%02d:%02d:%02d", hour,
          minute, second);
    } else {
      callingDuration = String.format(Locale.US, "%d:%02d:%02d", hour,
          minute, second);
    }

    return callingDuration;
  }

  public static float convertDpToPixel(float dp, Context context) {
    Context con = AndGApp.get();
    if (density == 0) {
      Resources resources = con.getResources();
      DisplayMetrics metrics = resources.getDisplayMetrics();
      density = (metrics.densityDpi / 160f);
    }
    float px = dp * density;
    return px;
  }

  /**
   * Change locale settings in the app.
   */
  public static void setDefaultLanguage(Resources res, Locale locale) {
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = new Locale(locale.getLanguage());
    res.updateConfiguration(conf, dm);
  }

  /**
   * Parse string to integer
   *
   * @return 0 if catch NumberFormatException, otherwise Interger.parseInt(value)
   */
  public static int parseInt(String value) {
    int result = 0;
    try {
      result = Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      Log.e(TAG, nfe.toString());
    }
    return result;
  }

  /**
   * Convert time string to other format
   */
  public static String convertFormatDateTime(String time, String fromFormat,
      String toFormat) {
    SimpleDateFormat from = new SimpleDateFormat(fromFormat);
    SimpleDateFormat to = new SimpleDateFormat(toFormat);
    try {
      Date date = from.parse(time);
      return to.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
      return "";
    }
  }

  @SuppressLint("NewApi")
  public static int getScreenWidth(Context context) {
    int screenWidth = 0;
    WindowManager wm = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    if (android.os.Build.VERSION.SDK_INT < 13) {
      screenWidth = display.getWidth();
    } else {
      Point outSize = new Point();
      display.getSize(outSize);
      screenWidth = outSize.x;
    }
    return screenWidth;
  }

  @SuppressLint("NewApi")
  public static int[] getScreenSize(Context context) {
    int[] size = new int[2];
    WindowManager wm = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    if (android.os.Build.VERSION.SDK_INT < 13) {
      size[0] = display.getWidth();
      size[1] = display.getHeight();
    } else {
      Point outSize = new Point();
      display.getSize(outSize);
      size[0] = outSize.x;
      size[1] = outSize.y;
    }
    return size;
  }

  public static int getVideoDuration(Context context, Uri uri, String path) {
    if (path != null && path.length() > 0) {
      MediaPlayer mMediaPlayer = null;
      int duration = 0;
      try {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        duration = mMediaPlayer.getDuration();
        LogUtils.e(TAG, "getDuration: " + duration);

        if (duration == 0) {
          // remember in some case MediaMetadataRetriever will not work correct
          MediaMetadataRetriever retriever = new MediaMetadataRetriever();
          //use one of overloaded setDataSource() functions to set your data source
          retriever.setDataSource(context, uri);
          String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
          long timeInMillisecond = Long.parseLong(time);

          duration = Math.round(timeInMillisecond / 1000f);
          retriever.release();
        }
      } catch (Exception e) {
        LogUtils.e(TAG, e.getMessage());
      } finally {
        if (mMediaPlayer != null) {
          mMediaPlayer.reset();
          mMediaPlayer.release();
        }
      }
      return duration;
    }
    return 0;
  }

  public static int getAge(Date birthday) {
    Calendar a = Calendar.getInstance(Locale.getDefault());
    a.setTime(birthday);
    Calendar b = Calendar.getInstance(Locale.getDefault());
    int age = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
        || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
        .get(Calendar.DATE) > b.get(Calendar.DATE))) {
      age--;
    }
    return age;
  }

  public static String parse(int timestamp) {
    String time = "";
    int hour = timestamp / (60 * 60);
    int minute = (timestamp % (60 * 60)) / 60;
    int second = (timestamp % (60 * 60)) % 60;
    if (hour < 10) {
      time = time + "0" + String.valueOf(hour);
    } else {
      time = time + String.valueOf(hour);
    }
    time = time + ":";
    if (minute < 10) {
      time = time + "0" + String.valueOf(minute);

    } else {
      time = time + String.valueOf(minute);

    }
    time = time + ":";
    if (second < 10) {
      time = time + "0" + String.valueOf(second);

    } else {
      time = time + String.valueOf(second);

    }
    return time;
  }

  public static String getDistanceTime(Context context, String time,
      SimpleDateFormat sd) throws ParseException {
    // Current time
    Calendar calendarNow = Calendar.getInstance();
    sd.setTimeZone(TimeZone.getTimeZone("GMT"));

    // Send time
    if (TextUtils.isEmpty(time)) {
      return Utility.getDifference(context, calendarNow, calendarNow);
    }
    Date dateSend = sd.parse(time);
    Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
    calendarSend.setTime(dateSend);

    return Utility.getDifference(context, calendarSend, calendarNow);
  }

  public static void showDialogAskingVoiceCall(final Context context,
      final CallUserInfo userInfo,
      DialogInterface.OnClickListener listener) {

    LayoutInflater inflater = LayoutInflater.from(context);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(context, true);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.voice_call);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.voice_call);
    builder.setMessage(R.string.voip_voice_call_message);

    builder.setNegativeButton(R.string.voip_call_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    builder.setPositiveButton(R.string.voip_call_yes, listener);

    builder.create();
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  //TODO process show dialog call request
  public static void showDialogRequestCall(final Activity activity, int callTyle, int callRequest,
      final String
          currentUserId, String currentUserName, final String userIdToSend,
      final ChatAdapter adapter) {
    String message;
    if (callRequest == REQUEST_VOICE_CALL_OFF) {
      message = activity.getString(R.string.message_voice_call_off, currentUserName);
    } else if (callRequest == REQUEST_VIDEO_CALL_OFF) {
      message = activity.getString(R.string.message_video_call_off, currentUserName);
    } else if (callRequest == REQUEST_VOICE_VIDEO_OFF) {
      message = activity.getString(R.string.message_voice_video_off, currentUserName);
    } else {
      return;
    }

    final String messageContent;
    if (callTyle == Constants.CALL_TYPE_VOICE) {
      messageContent = ChatMessage.CALLREQUEST_VOICE;
    } else {
      messageContent = ChatMessage.CALLREQUEST_VIDEO;
    }
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, true);
    builder.setMessage(message);

    builder.setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    builder.setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (activity instanceof MainActivity) {
          ChatService chatService = ((MainActivity) activity).getChatService();
          sendCallRequestMessage(chatService, currentUserId, userIdToSend, messageContent, adapter);
        }
      }
    });

    builder.create().show();
  }

  private static void sendCallRequestMessage(ChatService chatService, String currentUserId,
      String userIdToSend, String content, ChatAdapter adapter) {
    content = content.trim();
    if (content.length() > 0) {
      LogUtils.d(TAG,
          "Sent CallRequest, CurrentUser=" + currentUserId + " vs UserToSend=" + userIdToSend
              + " value=" + content);
      Date date = Utility.getDateTimeInGMT();
      String time = Utility.getTimeStamp(date);
      String messageId = currentUserId + "&" + userIdToSend + "&" + time;
      ChatMessage chatMessage = new ChatMessage(messageId, currentUserId, true, content, time,
          ChatMessage.CALLREQUEST);
      //TODO TYPE MESSAGE REQUEST CALL
      Message message = new Message(date, currentUserId, userIdToSend, MessageType.CALLREQ,
          chatMessage.getMessageToSend());
      /**
       * TODO Chuẩn hóa chỉ sử dụng một messageId xuyên suốt quá trình xl message
       * Updated by Robert on 22 Feb 2017
       */
      message.id = messageId;
      chatMessage.setContent(content);
      ChatManager chatManager = null;

      if (chatService != null) {
        chatManager = chatService.getChatManager();
      }

      if (adapter != null) {
        adapter.appendNewMessage(chatMessage);
      }

      if (chatManager == null) {
        return;
      }

      chatManager.sendGenericMessage(message);
      // sent broadcast to ConversationList
      chatManager.sendBroadcastMessage(ChatManager.ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION,
          new MessageClient(message));
    }
  }

  public static void showDialogAskingVideoCall(final Context context,
      final CallUserInfo userInfo,
      DialogInterface.OnClickListener listener) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(context, true);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.video_call);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.video_call);
    builder.setMessage(R.string.voip_video_call_message);

    builder.setNegativeButton(R.string.voip_call_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    builder.setPositiveButton(R.string.voip_call_yes, listener);

    builder.create();
    AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  @SuppressLint("SimpleDateFormat")
  @SuppressWarnings("deprecation")
  public static String getLastLoginString(String lastLogin,
      Resources resources) {
    StringBuilder time = new StringBuilder();
    DateFormat lastLoginFormatSever = new SimpleDateFormat("yyyyMMddHHmmss");
    int year = 0;
    int month = 0;
    int day = 0;
    int hour = 0;
    int minute = 0;
    try {
      Date lastLoginDate = lastLoginFormatSever.parse(lastLogin);
      Date currentDate = new Date();
      year = currentDate.getYear() - lastLoginDate.getYear();
      month = currentDate.getMonth() - lastLoginDate.getMonth();
      day = currentDate.getDate() - lastLoginDate.getDate();
      hour = currentDate.getHours() - lastLoginDate.getHours();
      minute = currentDate.getMinutes() - lastLoginDate.getMinutes();
    } catch (ParseException e) {
      LogUtils.e(TAG, String.valueOf(e.getMessage()));
      e.printStackTrace();
    }
    if (year > 0) {
      time.append(year).append(" ")
          .append(resources.getString(R.string.time_year_ago));
    } else if (year == 0 && month > 0) {
      time.append(month).append(" ")
          .append(resources.getString(R.string.time_month_ago));
    } else if (year == 0 && month == 0 && day < 4 * 7 && day > 6) {
      time.append(day / 7).append(" ")
          .append(resources.getString(R.string.time_week_ago));
    } else if (year == 0 && month == 0 && day < 7 && day > 0) {
      time.append(day).append(" ")
          .append(resources.getString(R.string.time_day_ago));
    } else if (year == 0 && month == 0 && day == 0 && hour > 1) {
      time.append(hour).append(" ")
          .append(resources.getString(R.string.time_hour_ago));
    } else if (year == 0 && month == 0 && day == 0 && hour == 0
        && minute > 0) {
      time.append(minute).append(" ")
          .append(resources.getString(R.string.time_minute_ago));
    } else {
      time.append("1 ").append(
          resources.getString(R.string.time_minute_ago));
    }
    return time.toString();
  }

  public static Bitmap createSquareBitmap(Bitmap source) {
    if (source == null) {
      return null;
    }

    if (source.getWidth() >= source.getHeight()) {

      return Bitmap.createBitmap(source,
          source.getWidth() / 2 - source.getHeight() / 2, 0,
          source.getHeight(), source.getHeight());

    } else {

      return Bitmap.createBitmap(source, 0, source.getHeight() / 2
              - source.getWidth() / 2, source.getWidth(),
          source.getWidth());
    }
  }

  public static String getApplicationName(Context context) {
    ApplicationInfo info = context.getApplicationInfo();
    int idApp = info.labelRes;
    return idApp == 0 ? info.nonLocalizedLabel.toString() : context.getString(idApp);
  }

  public static String getAppVersionName(Context context) {
    String packageName = context.getPackageName();
    PackageInfo packageInfo = null;
    try {
      packageInfo = context.getPackageManager().getPackageInfo(
          packageName, 0);
    } catch (Exception e) {
      LogUtils.e(TAG, String.valueOf(e.getMessage()));
    }
    String appVersion = "";
    if (packageInfo != null && packageInfo.versionName != null) {
      String version = packageInfo.versionName;
      if (version.contains(" ")) {
        appVersion = packageInfo.versionName.substring(0,
            packageInfo.versionName.indexOf(" "));
      } else {
        appVersion = version;
      }
    }
    return appVersion;
  }

  public static int isNeededGetUserStatus() {
    UserPreferences prefers = UserPreferences.getInstance();
    LogUtils.d("STATUS ", TAG + "isNeededGetUserStatus : " + prefers.getRegEmail());
    if (!TextUtils.isEmpty(prefers.getFacebookId()) && prefers.isLogout()) {
      return GetUserStatusRequest.TYPE_FACEBOOK;
    } else if (!TextUtils.isEmpty(prefers.getMocomId()) && prefers.isLogout()) {
      return GetUserStatusRequest.TYPE_MOCOM;
    } else if (!TextUtils.isEmpty(prefers.getFamuId()) && prefers.isLogout()) {
      return GetUserStatusRequest.TYPE_FAMU;
    } else if (!TextUtils.isEmpty(prefers.getRegEmail()) && prefers.isLogout()) {
      return GetUserStatusRequest.TYPE_EMAIL;
    } else {
      return GetUserStatusRequest.TYPE_NONE;
    }
  }

  //hiepuh
  public static boolean checkbirthday(Date birthday) {
    Calendar bd = Calendar.getInstance(Locale.getDefault());
    bd.setTime(birthday);
    Calendar real = Calendar.getInstance(TimeZone
        .getTimeZone("GMT"));
    int AgeYear = 0;
    if ((real.get(Calendar.MONTH) < 3)
        || (real.get(Calendar.MONTH) == 3) && real.get(Calendar.DAY_OF_MONTH) < 2) {
      AgeYear = real.get(Calendar.YEAR) - 19;
      if ((bd.get(Calendar.MONTH) == 3 && bd.get(Calendar.DAY_OF_MONTH) < 2
          && bd.get(Calendar.YEAR) <= AgeYear)
          || (bd.get(Calendar.MONTH) < 3 && bd.get(Calendar.YEAR) <= AgeYear)
          || (bd.get(Calendar.YEAR) < AgeYear)) {
        return true;
      }
    } else if ((real.get(Calendar.MONTH) > 3)
        || (real.get(Calendar.MONTH) == 3) && real.get(Calendar.DAY_OF_MONTH) >= 2) {
      AgeYear = real.get(Calendar.YEAR) - 18;
      if ((bd.get(Calendar.MONTH) == 3 && bd.get(Calendar.DAY_OF_MONTH) < 2
          && bd.get(Calendar.YEAR) <= AgeYear)
          || (bd.get(Calendar.MONTH) < 3 && bd.get(Calendar.YEAR) <= AgeYear)
          || (bd.get(Calendar.YEAR) < AgeYear)) {
        return true;
      }
    }

    return false;
  }

  /**
   * @param template the pattern.
   */
  public static String convertDateToJapanDate(@NonNull String template, String datesString) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm",
        Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date date;
    try {
      date = dateFormat.parse(datesString);
    } catch (ParseException e) {
      date = new Date();
      e.printStackTrace();
    }

    SimpleDateFormat dateJPFormat = new SimpleDateFormat(template,
        Locale.JAPAN);
    return dateJPFormat.format(date);
  }

  public static int getSoftNavigationBarSize(Activity activity) {
    // getRealMetrics is only available with API 17 and +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      DisplayMetrics metrics = new DisplayMetrics();
      activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
      int usableHeight = metrics.heightPixels;
      activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
      int realHeight = metrics.heightPixels;
      if (realHeight > usableHeight) {
        return realHeight - usableHeight;
      } else {
        return 0;
      }
    }
    return 0;
  }
  //end

  private class ClickableString extends ClickableSpan {

    private View.OnClickListener mListener;

    public ClickableString(View.OnClickListener listener) {
      mListener = listener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
      ds.setColor(Color.BLUE);
    }

    @Override
    public void onClick(View v) {
      mListener.onClick(v);
    }
  }

}
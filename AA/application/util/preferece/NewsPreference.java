package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.application.util.Utility;

/**
 * share preference for setting show news, cause user preference will clear on logout
 */
public class NewsPreference {

  public static final String KEY_SHOW_NEWS_POPUP_HOT_PAGE = "show_news_hot_page";
  public static final String KEY_SHOW_NEWS_POPUP_MEET_PEOPLE = "show_news_meet_people";
  private static final String PREF_NEWS = "pref_news";

  /**
   * save time to disable dialog in a day.
   *
   * @param context Context of caller activity
   * @param userId Key of value to save against
   * @param which NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE | NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE
   * @param time Value to save
   * @see #getTimeSettingNews(Context, String, String)
   */
  public static void saveTimeSettingNews(Context context, String userId, String which, Long time) {
    SharedPreferences prefs = context.getSharedPreferences(PREF_NEWS, Context.MODE_PRIVATE);
    String key = userId + which;
    final SharedPreferences.Editor editor = prefs.edit();
    editor.putLong(key, time).apply();
  }

  /**
   * get time saved to temporary disable show news dialog
   *
   * @param context Context of caller activity
   * @param userId Key of value to save against
   * @param which NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE | NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE
   * @see #saveTimeSettingNews(Context, String, String, Long)
   */
  public static long getTimeSettingNews(Context context, String userId, String which) {
    // check if userId empty -> return 0
    // cause KEY_SHOW_NEWS_POPUP_HOT_PAGE, KEY_SHOW_NEWS_POPUP_MEET_PEOPLE defined as boolean at #setShowNews(Context context, String which, boolean isShow)
    if (TextUtils.isEmpty(userId)) {
      return 0;
    }
    SharedPreferences prefs = context.getSharedPreferences(PREF_NEWS, Context.MODE_PRIVATE);
    String key = userId + which;
    return prefs.getLong(key, 0L);
  }

  /**
   * set flag to show news dialog on hot page
   *
   * @param context Context of caller activity
   * @param which identify page: {@link #KEY_SHOW_NEWS_POPUP_HOT_PAGE}, {@link
   * #KEY_SHOW_NEWS_POPUP_MEET_PEOPLE}
   * @param isShow true: show
   */
  public static void setShowNews(Context context, String which, boolean isShow) {
    SharedPreferences prefs = context.getSharedPreferences(PREF_NEWS, Context.MODE_PRIVATE);
    final SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(which, isShow);
    editor.apply();
  }

  /**
   * @param context Context of caller activity
   * @param which identify page: {@link #KEY_SHOW_NEWS_POPUP_HOT_PAGE}, {@link
   * #KEY_SHOW_NEWS_POPUP_MEET_PEOPLE}
   * @return true: yeah you should do it
   */
  private static boolean shouldShowNews(Context context, String which) {
    SharedPreferences prefs = context.getSharedPreferences(PREF_NEWS, Context.MODE_PRIVATE);
    return prefs.getBoolean(which, false);
  }

  /**
   * @param context Context of caller activity
   * @param userId which user to get disable time
   * @param which identify page: {@link #KEY_SHOW_NEWS_POPUP_HOT_PAGE}, {@link
   * #KEY_SHOW_NEWS_POPUP_MEET_PEOPLE}
   * @return false: tick hide news today or {@link #shouldShowNews(Context, String)} = false
   */
  public static boolean isShowNews(Context context, String userId, String which) {
    long disableNewTime = NewsPreference.getTimeSettingNews(context, userId, which);
    long currentTime = System.currentTimeMillis();
    boolean isSameDay = Utility.isSameDay(disableNewTime, currentTime);
    boolean showDialog = NewsPreference.shouldShowNews(context, which);
    return !isSameDay && showDialog;
  }
}

package com.application.ui.settings;

import android.content.Context;
import com.application.connection.response.MeetPeopleSettingResponse;
import com.application.util.preferece.Preferences;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MeetPeopleSetting {

  public final static int SORT_LOGIN_TIME = 1;
  public final static int SORT_REGISTER_TIME = 2;

  public final static int FILTER_ALL = 0;
  public final static int FILTER_NEW_REGISTER = 1;
  public final static int FILTER_CALL_WAITING = 2;

  public final static int NEAR_VALUE = 0;
  public final static int CITY_VALUE = 1;
  public final static int REGION_VALUE = 2;
  public final static int COUNTRY_VALUE = 3;
  public final static int WORLD_VALUE = 4;

  private final static int MIN_AGE_DEFAULT = 18;
  private final static int MAX_AGE_DEFAULT = 120;
  private final static int DISTANCE_DEFAULT = WORLD_VALUE;
  private final static int FILTER_DEFAULT = FILTER_ALL;
  private final static int SORT_DEFAULT = SORT_LOGIN_TIME;
  private final static boolean IS_NEW_LOGIN_DEFAULT = false;
  private static MeetPeopleSetting instance;
  private Context context;

  public MeetPeopleSetting(Context context) {
    this.context = context;
  }

  public static MeetPeopleSetting getInstance(Context context) {
    if (instance == null) {
      instance = new MeetPeopleSetting(context);
    }
    return instance;
  }

  public void saveMinAge(int minAge) {
    Preferences.getInstance().getEditor().putInt("min_age", minAge).commit();
  }

  public int getMinAge() {
    return Preferences.getInstance().getSharedPreferences()
        .getInt("min_age", MIN_AGE_DEFAULT);
  }

  public void saveMaxAge(int maxAge) {
    Preferences.getInstance().getEditor().putInt("max_age", maxAge).commit();
  }

  public int getMaxAge() {
    return Preferences.getInstance().getSharedPreferences()
        .getInt("max_age", MAX_AGE_DEFAULT);
  }

  public void saveDistance(int distance) {
    Preferences.getInstance().getEditor()
        .putInt("distance", distance).commit();
  }

  public int getDistance() {
    return Preferences.getInstance().getSharedPreferences()
        .getInt("distance", DISTANCE_DEFAULT);
  }

  public void saveRegion(int[] regions) {
    StringBuffer regionsBf = new StringBuffer();
    for (int region : regions) {
      regionsBf.append(region).append(",");
    }
    Preferences.getInstance().getEditor()
        .putString("region", regionsBf.toString()).commit();
  }

  public int[] getRegion() {
    String listRegionStr = Preferences.getInstance()
        .getSharedPreferences().getString("region", "");
    List<String> listRegion = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(listRegionStr, ",");
    while (st.hasMoreElements()) {
      listRegion.add((String) st.nextElement());
    }
    int[] regions = new int[listRegion.size()];
    int i = 0;
    for (String regionStr : listRegion) {
      regions[i] = Integer.valueOf(regionStr);
      i++;
    }
    return regions;
  }

  public void saveFilter(int filter) {
    Preferences.getInstance().getEditor().putInt("filter", filter).commit();
  }

  public int getFilter() {
    return Preferences.getInstance().getSharedPreferences()
        .getInt("filter", FILTER_DEFAULT);
  }

  public void saveSortType(int sortType) {
    Preferences.getInstance().getEditor()
        .putInt("sort_type", sortType).commit();
  }

  public int getSortType() {
    return Preferences.getInstance().getSharedPreferences()
        .getInt("sort_type", SORT_DEFAULT);
  }

  public boolean isNewLogin() {
    return Preferences.getInstance().getSharedPreferences()
        .getBoolean("is_new_login", IS_NEW_LOGIN_DEFAULT);
  }

  public void setNewLogin(boolean isNewLogin) {
    Preferences.getInstance().getEditor()
        .putBoolean("is_new_login", isNewLogin).commit();
  }

  public MeetPeopleSettingResponse getResponse() {
    int minAge = getMinAge();
    int maxAge = getMaxAge();
    int distance = getDistance();
    int[] region = getRegion();
    int filter = getFilter();
    int sortType = getSortType();
    boolean isNewLogin = isNewLogin();
    return new MeetPeopleSettingResponse(minAge, maxAge, distance, region,
        filter, sortType, isNewLogin);
  }

  public void saveResponse(MeetPeopleSettingResponse response) {
    saveMinAge(response.getLowerAge());
    saveMaxAge(response.getUpperAge());
    saveDistance(response.getDistance());
    saveRegion(response.getRegion());
    saveFilter(response.getFilter());
    saveSortType(response.getSortType());
    setNewLogin(response.isNewLogin());
  }

  public void clearMinAge() {
    Preferences.getInstance().getEditor().remove("min_age").commit();
  }

  public void clearMaxAge() {
    Preferences.getInstance().getEditor().remove("max_age").commit();
  }

  public void clearDistance() {
    Preferences.getInstance().getEditor().remove("distance").commit();
  }

  public void clearRegion() {
    Preferences.getInstance().getEditor().remove("region").commit();
  }

  public void clearFilter() {
    Preferences.getInstance().getEditor().remove("filter").commit();
  }

  public void clearSortType() {
    Preferences.getInstance().getEditor().remove("sort_type").commit();
  }

  public void clearIsNewLogin() {
    Preferences.getInstance().getEditor().remove("is_new_login").commit();
  }

  public void clear() {
    clearMinAge();
    clearMaxAge();
    clearDistance();
    clearRegion();
    clearFilter();
    clearSortType();
    clearIsNewLogin();
  }
}

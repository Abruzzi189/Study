package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import com.application.AndGApp;
import com.application.util.LogUtils;

public class LocationPreferences {

  private static final String FILE_PREFERENCES = "Eazy.location.preference";
  private static final String KEY_LOCATION_NAME = "key.location.name";
  private static String TAG = "LocationPreferences";
  private static LocationPreferences mPreferences = new LocationPreferences();
  private static Context mContext;
  private final String KEY_LONGITUDE = "key.longitude";
  private final String KEY_LATITUDE = "key.latitude";

  protected LocationPreferences() {
  }

  public static LocationPreferences getInstance() {
    mContext = AndGApp.get();
    return mPreferences;
  }

  private SharedPreferences.Editor getEditor() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE).edit();
  }

  private SharedPreferences getSharedPreferences() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE);
  }

  public void apply() {
    getEditor().apply();
  }

  public void clear() {
    getEditor().clear().commit();
  }

  // ====== ====== Longitude ===== ======
  public boolean setLongitude(double longitude) {
    LogUtils.d(TAG, "Longitude: " + longitude);
    return getEditor().putString(KEY_LONGITUDE, String.valueOf(longitude))
        .commit();
  }

  public Double getLongtitude() {
    String value = getSharedPreferences().getString(KEY_LONGITUDE, null);
    if (value == null) {
      return Double.valueOf(0);
    } else {
      return Double.valueOf(value);
    }
  }

  // ====== ====== Latitude ===== ======
  public boolean setLatitude(double latitude) {
    LogUtils.d(TAG, "Latitude:" + latitude);
    return getEditor().putString(KEY_LATITUDE, String.valueOf(latitude))
        .commit();
  }

  public Double getLatitude() {
    String value = getSharedPreferences().getString(KEY_LATITUDE, null);
    if (value == null) {
      return Double.valueOf(0);
    } else {
      return Double.valueOf(value);
    }
  }

  // ====== ====== Location name ===== ======
  public void saveLocationName(String addressName) {
    getEditor().putString(KEY_LOCATION_NAME, addressName).commit();
    LogUtils.d(TAG, "LocationName:" + addressName);
  }

  public String getLocationName() {
    return getSharedPreferences().getString(KEY_LOCATION_NAME, null);
  }
}
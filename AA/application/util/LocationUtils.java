package com.application.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.application.Config;
import com.application.connection.RestCaller;
import com.application.entity.GoogleAddressResponse;
import com.application.entity.GoogleAddressResponse.Result;
import com.application.ui.CenterButtonDialogBuilder;
import glas.bbsystem.R;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;


/**
 * Utility for location
 *
 * @author tungdx
 */
public class LocationUtils {

  // Debugging tag for the application
  public static final String APPTAG = "LocationUtils";
  // Name of shared preferences repository that stores persistent state
  public static final String SHARED_PREFERENCES = "location_prefers";
  // Key for storing the "updates requested" flag in shared preferences
  public static final String KEY_UPDATES_REQUESTED = "location.KEY_UPDATES_REQUESTED";
  /*
   * Define a request code to send to Google Play services This code is
   * returned in Activity.onActivityResult
   */
  public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  /*
   * Constants for location update parameters
   */
  // Milliseconds per second
  public static final int MILLISECONDS_PER_SECOND = 1000;
  // The update interval
  public static final int UPDATE_INTERVAL_IN_SECONDS = Config.TIME_UPDATE_LOCATION;
  // A fast interval ceiling
  public static final int FAST_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
      * UPDATE_INTERVAL_IN_SECONDS;
  // Update interval in milliseconds
  public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
      * UPDATE_INTERVAL_IN_SECONDS;
  public static final int REQUEST_CODE_LOCATION_SETTING = 100;
  private static LocationUtils mLocationUtils;
  private static Context mContext;

  public static LocationUtils getInstance(Context context) {
    mContext = context;
    if (mLocationUtils == null) {
      mLocationUtils = new LocationUtils();
    }
    return mLocationUtils;
  }

  public String getAddressName(Location location) {
    try {
      Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
      List<Address> listAddresses = geocoder.getFromLocation(
          location.getLatitude(), location.getLongitude(), 5);
      int size = listAddresses.size();
      if (null != listAddresses && listAddresses.size() > 0) {
        int index = 0;
        if (size >= 4) {
          index = 3;
        } else {
          index = size;
        }
        Address address = listAddresses.get(index);
        int max = address.getMaxAddressLineIndex();
        String local = "";
        for (int i = 0; i <= max; i++) {
          local += address.getAddressLine(i) + ", ";
        }
        if (local.contains(",")) {
          if (local.length() > 2) {
            local = local.substring(0, local.length() - 2);
          }
        }
        return local;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NullPointerException npe) {
      npe.printStackTrace();
    } catch (IllegalArgumentException iae) {
      iae.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public Region getRegionFromAddress(String address) {
    LogUtils.i("Get current address", "Address:" + address);
    if (TextUtils.isEmpty(address)) {
      return Region.getErrorRegion();
    }
    Region r = new Region();
    RegionUtils regionUtils = new RegionUtils(mContext);
    String[] regions = regionUtils.getRegionAlias();
    int length = regions.length;
    int numOfListCheck = length - 1;
    String[] listRegionName = regionUtils.getRegionNames();
    for (int i = 0; i < numOfListCheck; i++) {
      String[] tags = regions[i].split(",");
      for (String tag : tags) {
        if (address.contains(tag)) {
          r.regionCode = regionUtils.getRegionCodeFromAlias(tag);
          r.regionName = regionUtils.getRegionName(r.regionCode);
          return r;
        }
      }
    }
    r.regionName = listRegionName[length - 1];
    r.regionCode = regionUtils.getRegionCodeFromRegionName(r.regionName);
    return r;
  }

  public boolean isLocationServiceEnabled() {
    boolean gps_enabled = false, network_enabled = false;
    LocationManager lm = (LocationManager) mContext
        .getSystemService(Context.LOCATION_SERVICE);
    try {
      gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
      network_enabled = lm
          .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return (gps_enabled || network_enabled);
  }

  public AlertDialog createDialogLocationServiceDisabled(
      final Activity activity) {
    AlertDialog.Builder alertDialogBuilder = new CenterButtonDialogBuilder(activity, true);
    // set dialog message
    alertDialogBuilder
        .setMessage(
            activity.getResources().getString(
                R.string.message_location_disable))
        .setCancelable(false)
        .setPositiveButton(
            activity.getResources().getString(R.string.common_yes),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                activity.startActivityForResult(
                    new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_CODE_LOCATION_SETTING);
              }
            })
        .setNegativeButton(
            activity.getResources().getString(R.string.common_no),
            null);

    return alertDialogBuilder.create();
  }

  /**
   * Callback when get address location success
   *
   * @author tungdx
   */
  public interface OnGetAddressFinish {

    public void onGetAddressSuccess(String address);

    public void onGetAddressError();
  }

  public interface OnGetLocationAddressListener {

    public void onGetLocationAdressSuccess(String location);

    public void onGetLocationAdressFailure();
  }

  public static class Region {

    // User region not set
    public static final int REGION_NOT_SET = -1;
    public int regionCode;
    public String regionName;

    public Region() {
    }

    public Region(int regionCode, String regionName) {
      this.regionCode = regionCode;
      this.regionName = regionName;
    }

    public static boolean isInvalidRegion(Region region) {
      return REGION_NOT_SET == region.regionCode;
    }

    public static Region getErrorRegion() {
      return new Region(REGION_NOT_SET, "");
    }
  }

  // By documentation:
  // https://developers.google.com/maps/documentation/geocoding/#ReverseGeocoding
  public static class GetAddressTask extends
      AsyncTask<Object, Object, GoogleAddressResponse> {

    private static final String TAG = "GetAddressTask";
    private double longtitude;
    private double lattitude;
    private WeakReference<OnGetAddressFinish> mWeakGetAddressTask;

    public GetAddressTask(Context context, double longtitude,
        double lattitude, OnGetAddressFinish getAddressFinish) {
      this.lattitude = lattitude;
      this.longtitude = longtitude;
      mWeakGetAddressTask = new WeakReference<LocationUtils.OnGetAddressFinish>(
          getAddressFinish);
    }

    @Override
    protected GoogleAddressResponse doInBackground(Object... params) {
      // always use locale=japan for dectect region
      String language = "ja";
      LogUtils.i(TAG, "Language=" + language);
      String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
          + lattitude
          + ","
          + longtitude
          + "&sensor=false&language="
          + language;
      String response;
      try {
        response = RestCaller.execute(url);
        return (GoogleAddressResponse) JsonUtil.fromJson(response,
            GoogleAddressResponse.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(GoogleAddressResponse result) {
      super.onPostExecute(result);
      if (result != null && result.status.equals("OK")) {
        if (mWeakGetAddressTask.get() != null) {
          if (result.results != null && result.results.size() > 0) {
            mWeakGetAddressTask.get().onGetAddressSuccess(
                result.results.get(0).formatted_address);
          } else {
            mWeakGetAddressTask.get().onGetAddressError();
          }
        }
      } else {
        if (mWeakGetAddressTask.get() != null) {
          mWeakGetAddressTask.get().onGetAddressError();
        }
      }
    }
  }

  public static class GetSharedLocation extends
      AsyncTask<Object, Object, GoogleAddressResponse> {

    private static final String TAG = "GetSharedLocation";

    private Context mContext;
    private double latitude;
    private double longitude;
    private OnGetLocationAddressListener mOnGetSharedLocationFinish;

    public GetSharedLocation(Context context, double latitude,
        double longitude,
        OnGetLocationAddressListener getSharedLocationFinish) {
      LogUtils.d(TAG, "GetSharedLocation Started");

      mContext = context;
      this.latitude = latitude;
      this.longitude = longitude;
      mOnGetSharedLocationFinish = getSharedLocationFinish;

      LogUtils.d(TAG, "GetSharedLocation Ended");
    }

    @Override
    protected GoogleAddressResponse doInBackground(Object... params) {
      LogUtils.d(TAG, "doInBackground Started");

      Locale locale = Locale.getDefault();
      String language = locale.getLanguage();
      String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
          + latitude
          + ","
          + longitude
          + "&sensor=false&language="
          + language;
      String response;

      try {
        response = RestCaller.execute(url);

        LogUtils.d(TAG, "doInBackground Ended (1)");

        return (GoogleAddressResponse) JsonUtil.fromJson(response,
            GoogleAddressResponse.class);
      } catch (IOException e) {
        e.printStackTrace();
      }

      LogUtils.d(TAG, "doInBackground Ended (2)");

      return null;
    }

    @Override
    protected void onPostExecute(GoogleAddressResponse response) {
      LogUtils.d(TAG, "onPostExecute Started");

      super.onPostExecute(response);

      if (response == null) {
        LogUtils.d(TAG, "onPostExecute Ended (1)");
        return;
      }

      try {
        boolean found = false;

        if (mOnGetSharedLocationFinish != null) {
          if (response.status.equals("OK")) {
            if (response.results != null
                && response.results.size() > 0) {
              Result sharedLocation = response.results.get(0);
              if (sharedLocation != null) {
                LogUtils.d(TAG, String.format(
                    "onPostExecute: Location = %s",
                    sharedLocation.formatted_address));
                mOnGetSharedLocationFinish
                    .onGetLocationAdressSuccess(sharedLocation.formatted_address);
                found = true;
              }
            }
          }

          if (!found) {
            LogUtils.d(TAG, String.format(
                "onPostExecute: Location = %s", ""));
            mOnGetSharedLocationFinish.onGetLocationAdressFailure();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      LogUtils.d(TAG, "onPostExecute Ended (2)");
    }
  }
}

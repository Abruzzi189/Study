package com.application.ui.region;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import com.application.util.LocationUtils;
import com.application.util.LocationUtils.GetAddressTask;
import com.application.util.LocationUtils.OnGetAddressFinish;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import glas.bbsystem.R;

public class RegionRequester implements ConnectionCallbacks,
    OnConnectionFailedListener, OnGetAddressFinish {

  private final int WAITING_LOCATION = 10 * 1000;// miliseconds
  protected boolean isConnected = false;
  private Location mLocation;
  private ProgressDialog mProgressDialog;
  private GetAddressTask mAddressTask;
  private Handler mHandler;
  private Activity mActivity;
  private GoogleApiClient mGoogleApiClient;
  private LocationManager mLocationManager;
  private RegionListener mRegionListener;
  private LocationListener oldLocationListener = new LocationListener() {

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
      if (mLocation == null) {
        isConnected = true;
        mLocation = location;
        getAddress();
      }
      cancleHandler();
      mLocationManager.removeUpdates(oldLocationListener);
    }
  };
  private Runnable canNotGetLocation = new Runnable() {

    @Override
    public void run() {
      if (!isConnected) {
        mGoogleApiClient.disconnect();
        onGetLocationFailed();
      }
    }
  };

  public RegionRequester(Activity context, RegionListener regionListener) {
    mActivity = context;
    mRegionListener = regionListener;
  }

  public void onCreate() {
    mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
        .addApi(LocationServices.API).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).build();
    mLocationManager = (LocationManager) mActivity
        .getSystemService(Context.LOCATION_SERVICE);
  }

  @Override
  public void onConnectionFailed(ConnectionResult arg0) {
    // tungdx: not show error, case this device hasn't google play service
    // (Because use 2 method to get location)
    // doTaskWhenConnectFailed();
  }

  @Override
  public void onConnected(Bundle bundle) {
    if (mLocation == null) {
      isConnected = true;
      mLocation = LocationServices.FusedLocationApi
          .getLastLocation(mGoogleApiClient);
      getAddress();
    }
    cancleHandler();
  }

  private void doTaskWhenConnectFailed() {
    isConnected = false;
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    onGetLocationFailed();
    cancleHandler();
  }

  private void cancleHandler() {
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
      mHandler = null;
    }
  }

  public void onStop() {
    mLocationManager.removeUpdates(oldLocationListener);
    if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  public void requestLocation() {
    if (!LocationUtils.getInstance(mActivity).isLocationServiceEnabled()) {
      AlertDialog alertDialog;
      alertDialog = LocationUtils.getInstance(mActivity)
          .createDialogLocationServiceDisabled(mActivity);
      alertDialog.show();
      onGetLocationFailed();
      return;
    }
    if (isConnected && mLocation != null) {
      getAddress();
    } else {
      if (mGoogleApiClient.isConnected()
          || mGoogleApiClient.isConnecting()) {
        mGoogleApiClient.reconnect();
      } else {
        mGoogleApiClient.connect();
      }
      mLocationManager.requestLocationUpdates(
          LocationManager.NETWORK_PROVIDER, 1000, 0F,
          oldLocationListener);
    }
    mHandler = new Handler();
    mHandler.postDelayed(canNotGetLocation, WAITING_LOCATION);
    mProgressDialog = ProgressDialog.show(mActivity, null,
        mActivity.getString(R.string.waiting));

  }

  public void onGetLocationFailed() {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    mRegionListener.onGetRegionFailed();
  }

  public Location getLocation() {
    return mLocation;
  }

  public boolean isConnected() {
    return isConnected;
  }

  @Override
  public void onGetAddressSuccess(String address) {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    LocationUtils.Region r = LocationUtils.getInstance(
        mActivity.getApplicationContext())
        .getRegionFromAddress(address);
    int regionCode = r.regionCode;
    if (regionCode <= 0) {
      mRegionListener.onGetRegionFailed();
    } else {
      mRegionListener.onGetRegionSuccess(r.regionName, r.regionCode);
    }
  }

  @Override
  public void onGetAddressError() {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    mRegionListener.onGetRegionFailed();
  }

  private void getAddress() {
    if (isConnected && mLocation != null) {
      mAddressTask = new GetAddressTask(
          mActivity.getApplicationContext(),
          mLocation.getLongitude(), mLocation.getLatitude(), this);
      mAddressTask.execute();
    } else {
      if (mProgressDialog != null) {
        mProgressDialog.dismiss();
      }
      onGetLocationFailed();
    }
  }

  public void onDestroy() {
    if (mAddressTask != null) {
      mAddressTask.cancel(true);
      mAddressTask = null;
    }
    cancleHandler();
    mGoogleApiClient.unregisterConnectionCallbacks(this);
    mGoogleApiClient.unregisterConnectionFailedListener(this);
  }

  @Override
  public void onConnectionSuspended(int arg0) {

  }

  public interface RegionListener {

    public void onGetRegionSuccess(String regionName, int regionCode);

    public void onGetRegionFailed();
  }
}

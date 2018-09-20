/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.ServerRequest;
import com.application.connection.request.UpdateLocationRequest;
import com.application.connection.response.UpdateLocationResponse;
import com.application.util.LocationUtils;
import com.application.util.LocationUtils.GetAddressTask;
import com.application.util.LocationUtils.OnGetAddressFinish;
import com.application.util.LogUtils;
import com.application.util.preferece.LocationPreferences;
import com.application.util.preferece.UserPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import glas.bbsystem.R;


public class LocationFragment extends BaseFragment implements
    ConnectionCallbacks, OnConnectionFailedListener {

  private static final String TAG = "LocationFragment";
  private static final int LOADER_ID_SYNC_LOCATION = 1000;
  private final int REQUEST_CODE_LOCATION_SETTING = 1;
  protected Location mLocation;
  private GoogleApiClient mGoogleApiClient;
  private LocationManager mLocationManager;
  private AlertDialog mAlertDialog;
  private OnGetAddressFinish onGetAddressFinish = new OnGetAddressFinish() {
    @Override
    public void onGetAddressSuccess(String address) {
      LocationPreferences.getInstance().saveLocationName(address);
    }

    @Override
    public void onGetAddressError() {
    }
  };
  /**
   * Callback when update location to server
   */
  private ResponseReceiver updateLocationResponse = new ResponseReceiver() {

    @Override
    public void startRequest(int loaderId) {
    }

    @Override
    public Response parseResponse(int loaderID, ResponseData data,
        int requestType) {
      return new UpdateLocationResponse(data);
    }

    @Override
    public void receiveResponse(
        android.support.v4.content.Loader<Response> loader,
        Response response) {
      if (response != null) {
        if (response.getCode() == Response.SERVER_SUCCESS) {
          LogUtils.i(TAG, "Update location to server Success");
        } else {
          LogUtils.e(TAG, "Update location to server fail, code="
              + response.getCode());
        }
      }
    }

    @Override
    public void onBaseLoaderReset(
        android.support.v4.content.Loader<Response> loader) {

    }
  };
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
        mLocation = location;
        LocationFragment.this.onLocationChanged(location);
      }
      mLocationManager.removeUpdates(oldLocationListener);
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
        .addApi(LocationServices.API).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).build();
    mLocationManager = (LocationManager) getActivity().getSystemService(
        Context.LOCATION_SERVICE);
  }

  @Override
  public void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
    mLocationManager
        .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
            0F, oldLocationListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    checkLocationService();
  }

  @Override
  public void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
    mLocationManager.removeUpdates(oldLocationListener);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (mAlertDialog != null) {
      mAlertDialog.dismiss();
      mAlertDialog = null;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mGoogleApiClient.unregisterConnectionCallbacks(this);
    mGoogleApiClient.unregisterConnectionFailedListener(this);
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(getActivity(),
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

      } catch (IntentSender.SendIntentException e) {
        e.printStackTrace();
      }
    } else {
      // If no resolution is available, display a dialog to the user with
      // the error.
      showErrorDialog(connectionResult.getErrorCode());
    }
  }

  /**
   * Report location updates to the UI.
   *
   * @param location The updated location.
   */
  protected void onLocationChanged(Location location) {
    if (location == null) {
      return;
    }
    GetAddressTask addressTask2 = new GetAddressTask(mAppContext,
        location.getLongitude(), location.getLatitude(),
        onGetAddressFinish);
    addressTask2.execute();
    updateLocationToServer(location);
    LocationPreferences preferences = LocationPreferences.getInstance();
    preferences.setLongitude(location.getLongitude());
    preferences.setLatitude(location.getLatitude());
  }

  /**
   * Show a dialog returned by Google Play services for the connection error code
   *
   * @param errorCode An error code returned from onConnectionFailed
   */
  private void showErrorDialog(int errorCode) {
    Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
        getActivity(),
        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
    if (errorDialog != null) {
      ErrorDialogFragment errorFragment = new ErrorDialogFragment();
      errorFragment.setDialog(errorDialog);
      errorFragment.show(getChildFragmentManager(), LocationUtils.APPTAG);
    }
  }

  /**
   * sync location to server
   *
   * @param location Location to sync to server
   */
  private void updateLocationToServer(Location location) {
    String token = UserPreferences.getInstance().getToken();
    UpdateLocationRequest locationRequest = new UpdateLocationRequest(
        token, location.getLongitude(), location.getLatitude());
    ServerRequest serverRequest = new ServerRequest(getLoaderManager(),
        mAppContext, updateLocationResponse);
    serverRequest.initLoader(LOADER_ID_SYNC_LOCATION, RequestType.JSON,
        locationRequest);
  }

  protected void checkLocationService() {
    // Neu bo comment se check viec ton tai cua Location service
    // if
    // (!LocationUtils.getInstance(mAppContext).isLocationServiceEnabled())
    // {
    // mAlertDialog = LocationUtils.getInstance(mAppContext)
    // .createDialogLocationServiceDisabled(getActivity());
    // mAlertDialog.show();
    // }
  }

  protected void showDialogServiceDisabled() {
    AlertDialog.Builder alertDialogBuilder = new CenterButtonDialogBuilder(getActivity(), false);
    // set dialog message
    alertDialogBuilder
        .setMessage(
            mAppContext.getResources().getString(
                R.string.message_location_disable))
        .setCancelable(false)
        .setPositiveButton(
            mAppContext.getResources().getString(
                R.string.common_yes),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                startActivityForResult(
                    new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_CODE_LOCATION_SETTING);
              }
            })
        .setNegativeButton(
            mAppContext.getResources()
                .getString(R.string.common_no), null);
    mAlertDialog = alertDialogBuilder.create();
    mAlertDialog.show();
  }

  @Override
  public void onConnectionSuspended(int code) {
    showErrorDialog(code);
  }

  @Override
  public void onConnected(Bundle bundle) {
    if (mLocation == null) {
      mLocation = LocationServices.FusedLocationApi
          .getLastLocation(mGoogleApiClient);
      onLocationChanged(mLocation);
    }
    mGoogleApiClient.disconnect();
  }

  /**
   * Define a DialogFragment to display the error dialog generated in showErrorDialog.
   */
  public static class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;

    /**
     * Default constructor. Sets the dialog field to null
     */
    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }

    /**
     * Set the dialog to display
     *
     * @param dialog An error dialog
     */
    public void setDialog(Dialog dialog) {
      mDialog = dialog;
    }

    /*
     * This method must return a Dialog to the DialogFragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return mDialog;
    }
  }

}

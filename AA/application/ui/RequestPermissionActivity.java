package com.application.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import glas.bbsystem.R;

public class RequestPermissionActivity extends AppCompatActivity {

  private static final int REQUEST_PERMISSION = 33;
  private String[] requiredPermissions = new String[]{
      Manifest.permission.GET_ACCOUNTS,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.CALL_PHONE,
      Manifest.permission.PROCESS_OUTGOING_CALLS,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_request_permission);

    if (!checkRequiredPermissions(this, requiredPermissions)) {
      ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSION);
    } else {
      openSplash();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION && isGrantedAll(grantResults)) {
      // all permission required
      openSplash();
    } else {
      // don\'t have enough permission => finish
      Toast.makeText(this, R.string.permission_required_error, Toast.LENGTH_LONG).show();
      openSetting();

      finish();
    }
  }

  /**
   * open setting of app
   */
  private void openSetting() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
    intent.setData(uri);
    startActivity(intent);
  }

  /**
   * @param grantResults array grant result to check
   * @return true: if all granted
   */
  private boolean isGrantedAll(int[] grantResults) {
    if (grantResults.length <= 0) {
      return false;
    }
    for (int gr : grantResults) {
      if (gr != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  /**
   * when all permission granted
   */
  private void openSplash() {
    Intent intent = new Intent(this, SplashActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  /**
   * @param context to check permission
   * @param requiredPermissions array of required permission
   * @return true: all allowed, else otherwise
   */
  private boolean checkRequiredPermissions(Context context, String[] requiredPermissions) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return true;
    }
    for (String permission : requiredPermissions) {
      if (ContextCompat.checkSelfPermission(context, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }
}

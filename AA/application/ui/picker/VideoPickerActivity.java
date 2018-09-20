package com.application.ui.picker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.Config;
import com.application.provider.UriCompat;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.util.LogUtils;
import com.application.util.StorageUtil;
import com.application.util.Utility;
import glas.bbsystem.R;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class VideoPickerActivity extends FragmentActivity {

  public static final String EXTRA_VIDEO_URI = "com.application.ui.picker.extra_video_uri";
  public static final String EXTRA_VIDEO_PATH = "com.application.ui.picker.extra_video_path";
  public static final String EXTRA_TYPE = "com.application.ui.picker.video_type";
  public static final int GALLERY = 100;
  public static final int CAMERA = 200;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  private static final String TAG = "VidePickerActivity";
  private static final int REQUEST_CODE_VIDEO = 100;
  private static final int REQUEST_CODE_TAKE_VIDEO = 200;
  private static final String KEY_VIDEO_PATH = "video_path";
  private static final String KEY_VIDEO_URI = "video_uri";
  private String mVideoPath;
  private Uri mVideoUri;

  public static boolean isIntentAvailable(Context context, String action) {
    final PackageManager packageManager = context.getPackageManager();
    final Intent intent = new Intent(action);
    List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
        PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }

  /**
   * Create a file Uri for saving an image or video
   */
  private static Uri getOutputMediaFileUri(Context context, int type) {
    return UriCompat.fromFile(context, getOutputMediaFile(type));
  }

  /**
   * Create a File for saving an image or video
   */
  private static File getOutputMediaFile(int type) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir;
    mediaStorageDir = new File(
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "Switch");
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        LogUtils.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        .format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator
          + "Switch_" + timeStamp + ".jpg");
    } else if (type == MEDIA_TYPE_VIDEO) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator
          + "Switch_" + timeStamp + ".mp4");
    } else {
      return null;
    }

    return mediaFile;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_loading);
    if (savedInstanceState != null) {
      mVideoPath = savedInstanceState.getString(KEY_VIDEO_PATH);
      mVideoUri = savedInstanceState.getParcelable(KEY_VIDEO_URI);
    } else {
      int extra = getIntent().getIntExtra(EXTRA_TYPE, GALLERY);
      if (extra == GALLERY) {
        openVideoPicker();
      } else {
        takeVideo();
      }

    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_VIDEO_PATH, mVideoPath);
    outState.putParcelable(KEY_VIDEO_URI, mVideoUri);
  }

  @SuppressLint("NewApi")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == REQUEST_CODE_VIDEO) {
        if (data != null) {
          mVideoUri = data.getData();
          mVideoPath = getRealPathFromURI(mVideoUri);
          data.putExtra(EXTRA_VIDEO_PATH, mVideoPath);
          data.putExtra(EXTRA_VIDEO_URI, mVideoUri);
          LogUtils.i(TAG, "Video uri=" + mVideoUri);
          LogUtils.i(TAG, "Video path=" + mVideoPath);
          long duration = 0;
          String time = "Api lvl 9";
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            // When chose an error file from sd card will have an
            // error here. Dont fix.
            retriever.setDataSource(mVideoPath);
            time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            try {
              duration = Long.parseLong(time);
            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
          } else {
            duration = Utility.getAudioDurationTimeLong(mVideoPath);
          }

          if (duration > Config.TIME_LIMIT_VIDEO * 1000) {
            setResult(RESULT_CANCELED);
            AlertDialog confirmDialog = new CustomConfirmDialog(
                this, "",
                getString(R.string.can_not_select_video), false)
                .setPositiveButton(0, new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    finish();
                  }
                })
                .create();
            confirmDialog.show();

            int dividerId = confirmDialog.getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null);
            View divider = confirmDialog.findViewById(dividerId);
            if (divider != null) {
              divider.setBackgroundColor(
                  confirmDialog.getContext().getResources().getColor(R.color.transparent));
            }
          } else {
            LogUtils.i(TAG, "Length video=" + time);
            setResult(RESULT_OK, data);
            finish();
          }
        }
      } else if (requestCode == REQUEST_CODE_TAKE_VIDEO) {
        if (data == null) {
          data = new Intent();
        } else {
          if (data.getData() != null) {
            mVideoUri = data.getData();
          }
        }
        mVideoPath = getRealPathFromURI(mVideoUri);
        data.putExtra(EXTRA_VIDEO_PATH, mVideoPath);
        data.putExtra(EXTRA_VIDEO_URI, mVideoUri);
        LogUtils.i(TAG, "Video uri=" + mVideoUri);
        LogUtils.i(TAG, "Video path=" + mVideoPath);
        setResult(RESULT_OK, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          finish();
        } else {
          Intent mediaScanIntent = new Intent(
              Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
          File file = new File(mVideoPath);
          Uri contentUri = UriCompat.fromFile(this, file);
          mediaScanIntent.setData(contentUri);
          this.sendBroadcast(mediaScanIntent);

          final ProgressDialog dialog = new ProgressDialog(this);
          dialog.setCanceledOnTouchOutside(false);
          dialog.setMessage(getString(R.string.waiting));
          dialog.show();
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              if (dialog.isShowing()) {
                dialog.dismiss();
              }
              finish();
            }
          }, 3000);
        }
      }
    } else {
      finish();
    }
  }

  private void openVideoPicker() {
    Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
    videoPickerIntent.setType("video/*");
    videoPickerIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,
        Config.TIME_LIMIT_VIDEO);
    videoPickerIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
        Config.VIDEO_QUALITY);
    videoPickerIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,
        Config.TIME_LIMIT_VIDEO);
    try {
      startActivityForResult(videoPickerIntent, REQUEST_CODE_VIDEO);
    } catch (Exception e) {
      setResult(RESULT_OK, null);
    }
  }

  private void takeVideo() {
    if (!Utility.isSDCardExist()) {
      AlertDialog element = createDialogRequireSDCard().show();

      int dividerId = element.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = element.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
      return;
    }
    if (isIntentAvailable(getApplicationContext(),
        MediaStore.ACTION_VIDEO_CAPTURE)) {
      mVideoUri = UriCompat.fromFile(getApplicationContext(), StorageUtil
          .getVideoFileTempByUser(getApplicationContext()));
      Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
          Config.VIDEO_QUALITY);
      takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,
          Config.TIME_LIMIT_VIDEO);
      // tungdx: do not put output uri, it isn't work on all devices(ex:
      // Android 2.3.3 HTC)=> should use default and get Uri from data in
      // onActivityResult() method
      // takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
      // takeVideoIntent.putExtra("return-data", true);
      startActivityForResult(takeVideoIntent, REQUEST_CODE_TAKE_VIDEO);
    }
  }

  // TODO: xay ra loi khong lay duoc path, invalid row
  private String getRealPathFromURI(Uri contentURI) {
    Cursor cursor = getContentResolver().query(contentURI, null, null,
        null, null);
    if (cursor == null) {
      return contentURI.getPath();
    } else {
      cursor.moveToFirst();
      int idx = cursor
          .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      return cursor.getString(idx);
    }
  }

  public Builder createDialogRequireSDCard() {
    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    Builder builder = new CenterButtonDialogBuilder(this, false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.common_error);
    builder.setCustomTitle(customTitle);
    //builder.setTitle(R.string.common_error);
    builder.setMessage(R.string.require_a_sdcard);
    builder.setPositiveButton(R.string.common_ok, new OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
    builder.setCancelable(false);
    return builder;
  }

}

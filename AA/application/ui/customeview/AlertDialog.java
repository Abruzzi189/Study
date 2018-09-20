package com.application.ui.customeview;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.MainActivity;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.util.ErrorString;
import com.application.util.LogUtils;
import glas.bbsystem.R;


/**
 * @author tungdx
 */
public class AlertDialog {

  private static final String TAG = "AlertDialog";
  private static android.app.AlertDialog mDialog;

  // private static Intent intent;

  public static void showAlert(final Activity activity, int title, int code) {
    showAlert(activity, title, code, null, true);
  }

  public synchronized static void showAlert(final Activity activity,
      int title, final int code,
      final DialogInterface.OnClickListener onClickListener,
      boolean cancleable) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    if (mDialog == null || !mDialog.isShowing()) {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
      int message = R.string.alert;
      message = ErrorString.getDescriptionOfErrorCode(code);
      if (message == R.string.alert) {
        return;
      }
      configAlertBuilder(builder, cancleable, customTitle, title, message, onClickListener,
          activity, code);
      if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
        builder = new CenterButtonDialogBuilder(activity, true);
        configAlertBuilder(builder, cancleable, customTitle, title, message, onClickListener,
            activity, code);
        builder.setNegativeButton(R.string.buy_points,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                if (activity instanceof ManageBackstageActivity) {
                  activity.finish();
                }

                if (activity instanceof MainActivity) {
                  Fragment fragment = WebViewFragment
                      .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
                  ((MainActivity) activity)
                      .getNavigationManager().addPage(
                      fragment);
                } else {
                  Intent intent = new Intent(activity,
                      WebViewActivity.class);
                  intent.putExtra(
                      WebViewFragment.INTENT_PAGE_TITLE,
                      WebViewFragment.PAGE_TYPE_BUY_PONIT);
                  activity.startActivity(intent);
                }
              }
            });
      }

      mDialog = builder.create();
    } else {
      return;
    }
    mDialog.show();

    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
    }

  }

  private static void configAlertBuilder(Builder builder, boolean cancleable, View customTitle,
      int title, int message, final DialogInterface.OnClickListener onClickListener,
      final Activity activity, final int code) {
    builder.setCancelable(cancleable);
    if (code == Response.SERVER_OUT_OF_DATE_API) {
      builder.setCancelable(false);
    }

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (onClickListener != null) {
              onClickListener.onClick(dialog, which);
            }
            if (activity instanceof ManageBackstageActivity) {
              activity.finish();
            }
            if (code == Response.SERVER_OUT_OF_DATE_API) {
              String packageName = activity.getPackageName();
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setData(Uri.parse("market://details?id="
                  + packageName));
              activity.startActivity(intent);
            }

          }
        });
  }

  public static void showAlert(Activity activity, String title, String message) {
    LayoutInflater inflater = LayoutInflater.from(activity);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok, null);
    android.app.AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
    }

  }

  public static void showDirtyWordAlert(Context context, String dirtyWord) {
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(context, false);
    String format = context.getResources().getString(
        R.string.dirty_word_alert_content);
    builder.setMessage(String.format(format, dirtyWord));
    builder.setPositiveButton(R.string.common_ok, null);
    builder.show();
  }

  public static void showUploadImageErrorAlert(Activity activity) {
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
    String message = activity.getResources().getString(
        R.string.upload_image_error_content);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok, null);
    builder.show();
  }

  public static void showUploadChecksumFailAlert(Activity activity) {
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
    String message = activity.getResources().getString(R.string.upload_fail);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok, null);
    builder.show();
  }

  public synchronized static void showAlertNotEnoughPointsToComment(
      final Activity activity, int title) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    if (mDialog == null || !mDialog.isShowing()) {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, true);
      int message = R.string.alert;
      message = R.string.not_enough_point_msg_comment_buzz;
      if (message == R.string.alert) {
        return;
      }
      builder.setCancelable(true);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
      builder.setCustomTitle(customTitle);

      //builder.setTitle(title);
      builder.setMessage(message);

      builder.setPositiveButton(R.string.common_ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      builder.setNegativeButton(R.string.buy_points,
          new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

              if (activity instanceof ManageBackstageActivity) {
                activity.finish();
              }

              if (activity instanceof MainActivity) {

                Fragment fragment = WebViewFragment
                    .newInstance(WebViewFragment.PAGE_TYPE_FREE_POINT);
                ((MainActivity) activity)
                    .getNavigationManager().addPage(
                    fragment);
              } else {
                Intent intent = new Intent(activity,
                    WebViewActivity.class);
                intent.putExtra(
                    WebViewFragment.INTENT_PAGE_TITLE,
                    WebViewFragment.PAGE_TYPE_BUY_PONIT);
                activity.startActivity(intent);
              }
            }
          });

      mDialog = builder.create();
    } else {
      return;
    }
    mDialog.show();
    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
    }

  }
}

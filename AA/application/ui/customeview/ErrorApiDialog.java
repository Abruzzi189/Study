package com.application.ui.customeview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.util.ErrorString;
import com.application.util.LogUtils;
import glas.bbsystem.R;


/**
 * @author tungdx
 */
public class ErrorApiDialog {

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
      View customTitle = inflater.inflate(R.layout.dialog_customize_no_line, null);

      android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
      int message = R.string.alert;
      message = ErrorString.getDescriptionOfErrorCode(code);
      if (message == R.string.alert) {
        return;
      }
      builder.setCancelable(cancleable);
      if (code == Response.SERVER_OUT_OF_DATE_API) {
        builder.setCancelable(false);
      }
      if (title != 0) {
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize_no_Line))
            .setText(title);
        if (code == 6) {
          ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize_no_Line))
              .setVisibility(View.GONE);
        } else {
          builder.setCustomTitle(customTitle);
        }
      }
//			builder.setTitle(title);
      builder.setMessage(message);
      builder.setPositiveButton(R.string.common_yes,
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

      mDialog = builder.create();
    } else {
      return;
    }
    mDialog.show();
    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public static void showAlert(Activity activity, String title, String message) {
    View customTitle;
    LayoutInflater inflater = LayoutInflater.from(activity);
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
    if (!TextUtils.isEmpty(title)) {
      customTitle = inflater.inflate(R.layout.dialog_customize, null);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    } else {
      customTitle = inflater.inflate(R.layout.dialog_customize_no_line, null);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize_no_Line)).setText(title);
    }
    builder.setCustomTitle(customTitle);
    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok, null);
    android.app.AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public static void showAlert(Activity activity, String title,
      String message, DialogInterface.OnClickListener clickListener) {
    View customTitle;
    LayoutInflater inflater = LayoutInflater.from(activity);
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, false);
    builder.setCancelable(false);

    if (!TextUtils.isEmpty(title)) {
      customTitle = inflater.inflate(R.layout.dialog_customize, null);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    } else {
      customTitle = inflater.inflate(R.layout.dialog_customize_no_line, null);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize_no_Line)).setText(title);
    }

    builder.setCustomTitle(customTitle);
    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(R.string.common_ok, clickListener);
    AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public static void showDirtyWordAlert(Activity activity, String dirtyWord) {
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(activity, true);
    String format = activity.getResources().getString(
        R.string.dirty_word_alert_content);
    builder.setMessage(String.format(format, dirtyWord));
    builder.setPositiveButton(R.string.common_ok, null);
    builder.show();
  }
}
package com.application.ui.backstage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import glas.bbsystem.R;


public class BackstageDialog {

  public static void showDialogTimeLock(final Activity activity) {
    LayoutInflater inflater = LayoutInflater.from(activity);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder buider = new Builder(activity);
    String title = activity.getString(R.string.common_warning);
    String message = activity
        .getString(R.string.activity_manage_backstage_dialog_unlock_time_out_content);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    buider.setCustomTitle(customTitle);

    //buider.setTitle(title);
    buider.setMessage(message);
    buider.setNegativeButton(R.string.ok, null);
    AlertDialog element = buider.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public static void showDialogUploadImageDone(Activity activity,
      boolean isDone) {
    LayoutInflater inflater = LayoutInflater.from(activity);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder buider = new Builder(activity);
    String message = activity.getString(R.string.upload_fail);
    if (isDone) {
      String title = activity
          .getString(R.string.activity_manage_backstage_dialog_title);

      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
      buider.setCustomTitle(customTitle);

      //buider.setTitle(title);
      message = activity
          .getString(R.string.activity_manage_backstage_dialog_content);
    }
    buider.setMessage(message);
    buider.setNegativeButton(R.string.ok, null);
    AlertDialog element = buider.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }
}
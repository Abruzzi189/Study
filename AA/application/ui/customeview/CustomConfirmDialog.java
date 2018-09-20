package com.application.ui.customeview;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.ui.CenterButtonDialogBuilder;
import glas.bbsystem.R;


public class CustomConfirmDialog extends CenterButtonDialogBuilder {

  private final boolean isYesNo;

  public CustomConfirmDialog(Context context, String title, String msg, boolean isYesNo) {
    super(context, isYesNo);
    this.isYesNo = isYesNo;
    LayoutInflater inflater = LayoutInflater.from(context);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    if (!TextUtils.isEmpty(title)) {
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
      setCustomTitle(customTitle);
      //setTitle(title);
    }
    if (!TextUtils.isEmpty(msg)) {
      setMessage(msg);
    }
  }

  @Override
  public AlertDialog.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
    return super.setPositiveButton(isYesNo ? getContext().getText(R.string.common_yes)
        : getContext().getText(R.string.common_ok), listener);
  }

  @Override
  public AlertDialog.Builder setPositiveButton(@StringRes int textId, OnClickListener listener) {
    return super.setPositiveButton(isYesNo ? R.string.common_yes : R.string.common_ok, listener);
  }

  @Override
  public AlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
    return super.setNegativeButton(isYesNo ? text : null, listener);
  }

  @Override
  public AlertDialog.Builder setNegativeButton(@StringRes int textId, OnClickListener listener) {
    return super.setNegativeButton(isYesNo ? R.string.common_cancel : 0, listener);
  }
}

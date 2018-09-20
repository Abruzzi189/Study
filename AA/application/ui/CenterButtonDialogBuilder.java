package com.application.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import glas.bbsystem.R;

/**
 * custom alert dialog for center buttons
 */
public class CenterButtonDialogBuilder extends AlertDialog.Builder implements View.OnClickListener,
    DialogInterface {

  private final boolean isEnableBoth;
  private Button positiveButton;
  private OnClickListener positiveListener;
  private Button negativeButton;
  private OnClickListener negativeListener;
  private AlertDialog mDialog = null;

  /**
   * @param context required for parent class
   * @param isEnableBoth true: enable 2 button, else: only show positive button
   */
  public CenterButtonDialogBuilder(@NonNull Context context, boolean isEnableBoth) {
    super(context);
    this.isEnableBoth = isEnableBoth;
    init();
  }

  /**
   * init alert dialog
   */
  private void init() {
    // at this time, i have no idea for toggle visible view so i decide to inflate separate views it's stupid, pls replace better solution if you can
    View mView = LayoutInflater.from(getContext())
        .inflate(isEnableBoth ? R.layout.dialog_multiple_buttons : R.layout.dialog_single_button,
            null, false);
    positiveButton = (Button) mView.findViewById(R.id.positive);
    positiveButton.setOnClickListener(this);

    if (isEnableBoth) {
      negativeButton = (Button) mView.findViewById(R.id.negative);
      negativeButton.setOnClickListener(this);
    }
    setView(mView);
  }

  @Override
  public AlertDialog.Builder setPositiveButton(CharSequence text,
      DialogInterface.OnClickListener listener) {
    setMyPositiveButton(text, listener);
    return super.setPositiveButton(null, listener);
  }

  @Override
  public AlertDialog.Builder setPositiveButton(@StringRes int textId,
      DialogInterface.OnClickListener listener) {
    setMyPositiveButton(getContext().getString(textId), listener);
    return super.setPositiveButton(null, listener);
  }

  /**
   * same as positive button
   *
   * @param text of button
   * @param listener when click button
   * @see #setPositiveButton(CharSequence, OnClickListener)
   * @see #setPositiveButton(int, OnClickListener)
   */
  private void setMyPositiveButton(CharSequence text, OnClickListener listener) {
    positiveListener = listener;
    positiveButton.setText(text);
    positiveButton.setVisibility(View.VISIBLE);
  }

  @Override
  public AlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
    setMyNegativeButton(text, listener);
    return super.setNegativeButton(null, listener);
  }

  @Override
  public AlertDialog.Builder setNegativeButton(@StringRes int textId, OnClickListener listener) {
    setMyNegativeButton(getContext().getString(textId), listener);
    return super.setNegativeButton(null, listener);
  }

  /**
   * same as negative button
   *
   * @param text of button
   * @param listener when click button
   * @see #setNegativeButton(CharSequence, OnClickListener)
   * @see #setNegativeButton(int, OnClickListener)
   */
  private void setMyNegativeButton(CharSequence text, OnClickListener listener) {
    if (!isEnableBoth) {
      return;
    }
    negativeButton.setVisibility(View.VISIBLE);
    negativeButton.setText(text);
    negativeListener = listener;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.positive:
        if (positiveListener != null) {
          positiveListener.onClick(this, AlertDialog.BUTTON_POSITIVE);
        }
        if (mDialog != null) {
          mDialog.dismiss();
        }
        break;
      default:
        if (negativeListener != null) {
          negativeListener.onClick(this, AlertDialog.BUTTON_NEGATIVE);
        }
        if (mDialog != null) {
          mDialog.dismiss();
        }
        break;
    }
  }

  @Override
  public AlertDialog show() {
    return super.show();
  }

  @Override
  public AlertDialog create() {
    mDialog = super.create();
    return mDialog;
  }

  @Override
  public void cancel() {
  }

  @Override
  public void dismiss() {
  }
}
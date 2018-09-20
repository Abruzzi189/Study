package com.application.ui.customeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.application.util.LogUtils;

public class SendCommentEditTextView extends EditText {

  private Context mContext;
  private OnHiddenKeyboardListener mListener;

  public SendCommentEditTextView(Context context) {
    super(context);
  }

  public SendCommentEditTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setOnHiddenKeyboardListener(Context context, OnHiddenKeyboardListener listener) {
    this.mListener = listener;
    this.mContext = context;
  }

  @Override
  public boolean dispatchKeyEventPreIme(KeyEvent event) {
    if (mContext != null) {
      InputMethodManager imm = (InputMethodManager) mContext
          .getSystemService(Context.INPUT_METHOD_SERVICE);

      if (imm.isActive() && imm.isAcceptingText() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
        LogUtils.d("DND", "dispatchKeyEventPreIme");
        mListener.onHiddenKeyboard();
      }
    }
    return super.dispatchKeyEventPreIme(event);
  }


  public interface OnHiddenKeyboardListener {

    public void onHiddenKeyboard();
  }

}

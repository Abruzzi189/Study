package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseChatReceiveView extends LinearLayout {

  protected TextView mtxtTime;
  protected ImageView mimgStatus;

  public BaseChatReceiveView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initChildView(getContext());
  }

  public BaseChatReceiveView(Context context) {
    super(context);
    initChildView(getContext());
  }

  private void initChildView(Context context) {
    mtxtTime = new TextView(context);
    mimgStatus = new ImageView(context);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
    addView(mtxtTime, params);
    addView(mimgStatus, params);
  }
}

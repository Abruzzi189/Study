package com.application.ui.customeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class DispatchTouchLayout extends FrameLayout {

  private OnDispatchListener dispatchListener;

  public DispatchTouchLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        if (dispatchListener != null) {
          dispatchListener.onTouchDown(ev.getX(), ev.getY());
        }
        break;
      default:
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  public void setDispatchListener(OnDispatchListener listener) {
    dispatchListener = listener;
  }

  public void removeDispatchListener() {
    dispatchListener = null;
  }

  public interface OnDispatchListener {

    void onTouchDown(float x, float y);
  }

}

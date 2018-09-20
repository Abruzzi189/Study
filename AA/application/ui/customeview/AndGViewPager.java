package com.application.ui.customeview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Use for display ImageView that can zoom in/out
 *
 * @author tungdx
 */
public class AndGViewPager extends ViewPager {

  public AndGViewPager(Context context) {
    super(context);
  }

  public AndGViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    }
  }

}

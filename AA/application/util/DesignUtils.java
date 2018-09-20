package com.application.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * Created by hnc on 26/05/2017.
 */

public class DesignUtils {

  public static void setTextViewDrawableColor(TextView textView, int color) {
    for (Drawable drawable : textView.getCompoundDrawables()) {
      if (drawable != null) {
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
      }
    }
  }

  public static void setTextViewDrawableColor(TextView textView, String color) {
    for (Drawable drawable : textView.getCompoundDrawables()) {
      if (drawable != null) {
        drawable.setColorFilter(
            new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN));
      }
    }
  }
}

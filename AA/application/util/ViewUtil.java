package com.application.util;

import android.view.View;

public class ViewUtil {

  public static boolean isViewContains(View parentView, View view, int rx,
      int ry) {
    int[] l = new int[2];
    view.getLocationOnScreen(l);
    int x = l[0];
    int y = l[1];
    parentView.getLocationOnScreen(l);
    x -= l[0];
    y -= l[1];
    int w = view.getWidth();
    int h = view.getHeight();
    if (rx < x || rx > x + w || ry < y || ry > y + h) {
      return false;
    }
    return true;
  }
}

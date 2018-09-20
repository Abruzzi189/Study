package com.application.util;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {

  public static Animation animationSlide(int location, int viewSize,
      boolean show) {
    AnimationSet set = new AnimationSet(true);
    Animation animation = new TranslateAnimation(0, 0, 0, 0);
    if (show) {
      animation = new TranslateAnimation(0, 0, viewSize * location, 0);
    } else {
      animation = new TranslateAnimation(0, 0, 0, viewSize * location);
    }
    animation.setDuration(200);
    set.addAnimation(animation);
    return animation;
  }
}

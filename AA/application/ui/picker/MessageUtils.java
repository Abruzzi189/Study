package com.application.ui.picker;

import android.content.Context;
import glas.bbsystem.R;

/**
 * @author TUNGDX Get warning, error message for media picker module.
 */
public class MessageUtils {

  /**
   * @param maxDuration in seconds.
   * @return message before record video.
   */
  public static String getWarningMessageVideoDuration(Context context,
      int maxDuration) {
    return context.getResources().getQuantityString(
        R.plurals.picker_video_duration_warning, maxDuration, maxDuration);
  }

  /**
   * @return message when record and select video that has duration larger than max options.
   */
  public static String getInvalidMessageMaxVideoDuration(Context context,
      int maxDuration) {
    return context.getResources().getQuantityString(
        R.plurals.picker_video_duration_max, maxDuration, maxDuration);
  }

  /**
   * @return message when record and select video that has duration smaller than min options.
   */
  public static String getInvalidMessageMinVideoDuration(Context context,
      int minDuration) {
    return context.getResources().getQuantityString(
        R.plurals.picker_video_duration_min, minDuration, minDuration);
  }
}
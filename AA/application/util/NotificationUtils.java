package com.application.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import com.application.common.webview.WebViewFragment;
import com.application.constant.Constants;
import com.application.ui.MainActivity;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

public class NotificationUtils {

  public static void playNotificationSound(Context context) {
    if (!UserPreferences.getInstance().isSoundOn()) {
      return;
    }

    MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.notice);
    try {
      mPlayer.setOnCompletionListener(new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
          mp.stop();
          mp.reset();
          mp.release();
          mp = null;
        }
      });
      mPlayer.start();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  public static void vibarateNotification(Context context) {
    if (!UserPreferences.getInstance().isVibration()) {
      return;
    }
    Vibrator vibrator = (Vibrator) context
        .getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(Constants.NOTIFICATION_VIBRATOR_TIME);
  }

  public static boolean handleUrl(String url, FragmentActivity activity) {
    if (activity instanceof MainActivity) {
      MainActivity mainActivity = (MainActivity) activity;
      WebViewFragment fragment = WebViewFragment.newInstance(url, "");
      mainActivity.getNavigationManager().addPage(fragment);
    }
    return true;
  }
}

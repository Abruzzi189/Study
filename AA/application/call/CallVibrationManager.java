package com.application.call;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;

public interface CallVibrationManager {

  public void vibrate();

  public void stop();

  public static class CallVibrationManagerImpl implements
      CallVibrationManager {

    Vibrator mVibrator;
    AudioManager mAudioManager;

    public CallVibrationManagerImpl(Context context) {
      mVibrator = (Vibrator) context.getApplicationContext()
          .getSystemService(Context.VIBRATOR_SERVICE);
      mAudioManager = (AudioManager) context.getApplicationContext()
          .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void vibrate() {
      int ringMode = mAudioManager.getRingerMode();
      if (ringMode == AudioManager.RINGER_MODE_SILENT) {
        // Silent vibrate
      } else if (ringMode == AudioManager.RINGER_MODE_VIBRATE
          || ringMode == AudioManager.RINGER_MODE_NORMAL) {
        long[] patern = {0, 1000, 1000};
        mVibrator.vibrate(patern, 1);
      } else {
        // Silent vibrate
      }
    }

    @Override
    public void stop() {
      mVibrator.cancel();
    }

  }
}

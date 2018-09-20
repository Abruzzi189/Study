package com.application.call;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.application.util.LogUtils;
import glas.bbsystem.R;
import java.io.File;
import java.io.IOException;


public interface CallSoundManager {

  public void playIncommingSound();

  public void playOutgoingSound();

  public void playEndSound();

  public void stopSound();

  public static class CallSoundManagerImpl implements CallSoundManager {

    private final String TAG = "CallSoundManager";
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private Uri mIncomingCallSoundUri;
    private Uri mOutgoingCallSoundUri;
    private Uri mEndingCallSoundUri;
    private Context mContext;

    public CallSoundManagerImpl(Context context) {
      mContext = context;

      // Setting incoming sound path
      mIncomingCallSoundUri = Uri.parse("android.resource://"
          + context.getPackageName() + File.separator
          + R.raw.call_ringin);

      // Setting outgoing sound path
      mOutgoingCallSoundUri = Uri.parse("android.resource://"
          + context.getPackageName() + File.separator
          + R.raw.call_ringout);

      // Setting end call sound path
      mEndingCallSoundUri = Uri.parse("android.resource://"
          + context.getPackageName() + File.separator
          + R.raw.call_ended);
      mMediaPlayer = new MediaPlayer();
      mAudioManager = (AudioManager) context
          .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    /** Playing the incoming sound */
    public void playIncommingSound() {
      // Setting streaming mode: Stream_music for speaker
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      playSound(mIncomingCallSoundUri, true);
    }

    @Override
    /** Playing the out going sound */
    public void playOutgoingSound() {
      // Setting streaming mode: Stream_voice_call for earpiece
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
      playSound(mOutgoingCallSoundUri, true);
    }

    @Override
    /** Playing the end call sound */
    public void playEndSound() {
      playSound(mEndingCallSoundUri, false);
      // Reset audio manager mode
      mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * Playing the sound from URI with the looping setting
     *
     * @param soundUri URI of the sound
     * @param looping is looping pattern
     */
    private void playSound(Uri soundUri, boolean looping) {
      try {
        if (mMediaPlayer != null) {
          if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
          }
          mMediaPlayer.reset();
          mMediaPlayer.setDataSource(mContext, soundUri);
          mMediaPlayer.setLooping(looping);
          mMediaPlayer.prepare();
          mMediaPlayer.start();
          if (mMediaPlayer.isPlaying()) {
            LogUtils.i(TAG, "Playing: " + soundUri);
          }
        }
      } catch (IllegalArgumentException e) {
        LogUtils.e(TAG, String.valueOf(e.getMessage()));
      } catch (SecurityException e) {
        LogUtils.e(TAG, String.valueOf(e.getMessage()));
      } catch (IllegalStateException e) {
        LogUtils.e(TAG, String.valueOf(e.getMessage()));
      } catch (IOException e) {
        LogUtils.e(TAG, String.valueOf(e.getMessage()));
      }
    }

    @Override
    /** Stop playing ring tone sound */
    public void stopSound() {
      if (mMediaPlayer != null) {
        if (mMediaPlayer.isPlaying()) {
          mMediaPlayer.pause();
        }
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
      }
    }
  }
}
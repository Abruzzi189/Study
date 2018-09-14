package android.gpuimage.com.notification;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ThoNh on 5/9/2018.
 */

public class MusicHandler {
    //region variable
    private static final String TAG = "MusicHandler";
    public static MediaPlayer mediaPlayer;
    public static MusicModel mMusicModel;
    public static Context mContext;
    public static android.os.Handler handler;
    public static Runnable runnable;
    //endregion

    //region function
    public static void playMusic(Context context, MusicModel musicModel) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, musicModel.getMusicID());
        mediaPlayer.start();
        mMusicModel = musicModel;
        mContext = context;
    }

    public static void playPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                NotificationMusic.remoteViews.setImageViewResource(R.id.iv_playPause, R.drawable.ic_play_arrow_black_24dp);

            } else {
                mediaPlayer.start();
                NotificationMusic.remoteViews.setImageViewResource(R.id.iv_playPause, R.drawable.ic_pause_black_24dp);
            }
            NotificationMusic.notificationManager.notify(NotificationMusic.NOTIFCATION_ID, NotificationMusic.builder.build());
        }

    }

    public static void nextTime() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        int ADD_TIME = 5000;

        if (currentPosition + ADD_TIME < duration) {
            mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
        NotificationMusic.notificationManager.notify(NotificationMusic.NOTIFCATION_ID, NotificationMusic.builder.build());
    }

    public static void loopingMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        } else {
            playMusic(mContext, mMusicModel);
            NotificationMusic.remoteViews.setImageViewResource(R.id.iv_playPause, R.drawable.ic_pause_black_24dp);

        }
        NotificationMusic.notificationManager.notify(NotificationMusic.NOTIFCATION_ID, NotificationMusic.builder.build());
    }

    public static void backTime() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        int SUBTRACT_TIME = 5000;
        if (currentPosition - SUBTRACT_TIME > 0) {
            mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
        NotificationMusic.notificationManager.notify(NotificationMusic.NOTIFCATION_ID, NotificationMusic.builder.build());

    }

    public static void updateUIRealtime() {
        NotificationMusic.remoteViews.setTextViewText(R.id.tv_duration, convertTime(mediaPlayer.getDuration()));

        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
              //  NotificationMusic.updateNotification();
                //update UI
                if (mediaPlayer != null) {
                    Log.d(TAG, "run: ");
                    NotificationMusic.remoteViews.setProgressBar(R.id.pb_time, mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition(), false);
                    NotificationMusic.remoteViews.setTextViewText(R.id.tv_current, convertTime(mediaPlayer.getCurrentPosition()));
                    if (convertTime(mediaPlayer.getCurrentPosition()).equals(convertTime(mediaPlayer.getDuration()))) {
                        loopingMusic();
                    }
                }

                handler.postDelayed(this, 1000);
               NotificationMusic.notificationManager.notify(NotificationMusic.NOTIFCATION_ID,NotificationMusic.builder.build());
            }
        };
        runnable.run();
    }

    public static void stopHandler() {
        handler.removeCallbacks(runnable);
    }

    public static String convertTime(long time) {
        long min = time / 60000;
        long sec = (time - min * 60000) / 1000;

        return String.format("%02d:%02d", min, sec);
    }
    //endregion
}

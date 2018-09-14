package android.gpuimage.com.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ThoNh on 5/9/2018.
 */

public class MusicService extends Service {

    //region function
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        if (action.equals(NotificationMusic.PLAY_PAUSE_ID)) {
            MusicHandler.playPause();
        } else if (action.equals(NotificationMusic.NEXT_TIME_ID)) {

            MusicHandler.nextTime();
        } else if (action.equals(NotificationMusic.BACK_TIME_ID)) {

            MusicHandler.backTime();
        }else if (action.equals(NotificationMusic.LOOPING_ID)) {

           MusicHandler.loopingMusic();
        }

        return START_NOT_STICKY;
    }
    //endregion



}

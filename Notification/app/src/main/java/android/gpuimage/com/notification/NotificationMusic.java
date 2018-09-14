package android.gpuimage.com.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

/**
 * Created by ThoNh on 5/9/2018.
 */

public class NotificationMusic {
    //region variable
    private static final String TAG = "NotificationMusic";
    private static final int DONE_LOAD_IMAGE = 1;
    public static int NOTIFCATION_ID = 0;
    public static String PLAY_PAUSE_ID = "1";
    public static String NEXT_TIME_ID = "2";
    public static String BACK_TIME_ID = "3";
    public static String LOOPING_ID = "4";
    public static String CANCEL_NOTIFICATION_ID = "5";
    public static String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    public static String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    public static RemoteViews remoteViews;
    public static NotificationCompat.Builder builder;
    public static NotificationManager notificationManager;

    static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == DONE_LOAD_IMAGE)
            {
                Bitmap bitmap = (Bitmap) msg.obj;
                remoteViews.setImageViewBitmap(R.id.iv_smallImage, bitmap);
            }
            return false;
        }
    });

    //end region


    public static void setupNewNotification(final Context context, final MusicModel musicModel) {
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notifcation_music);
        remoteViews.setTextViewText(R.id.tv_song_name, musicModel.getSongName());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification Channel
        CharSequence channelName = NOTIFICATION_CHANNEL_NAME;
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);

        }
        builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                 .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_fast_forward_black_24dp)
                .setDeleteIntent(createOnDismissedIntent(context, NOTIFCATION_ID))
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})

                .setSound(null)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        MusicHandler.updateUIRealtime();

        updateNotification();
        builder.getNotification().flags = Notification.FLAG_AUTO_CANCEL;

        new Thread(new Runnable() {
                @Override
                public void run() {
                try {

                    Bitmap bitmap = Glide.with(context.getApplicationContext()).load(musicModel.getUrlImage()).asBitmap().into(-1, -1).get();
                    Message message = new Message();
                    message.what = DONE_LOAD_IMAGE;
                    message.obj = bitmap;

                    mHandler.sendMessage(message);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //Picasso.with(context).load(musicModel.getUrlImage()).into(remoteViews, R.id.iv_smallImage, NOTIFCATION_ID, builder.build());
        setOnClickPlayPause(context);
        setOnClickNextTime(context);
        setOnClickBackTime(context);
        setOnClickLoopingMusic(context);

        notificationManager.notify(NOTIFCATION_ID, builder.build());


    }



    private static PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(CANCEL_NOTIFICATION_ID);
        PendingIntent pendingIntent =
                PendingIntent.getService(context.getApplicationContext(),notificationId, intent, 0);
        return pendingIntent;
    }

    public static void updateNotification() {
        if (MusicHandler.mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.iv_playPause, R.drawable.ic_pause_black_24dp);
            builder.setOngoing(true);
        } else {
            remoteViews.setImageViewResource(R.id.iv_playPause, R.drawable.ic_play_arrow_black_24dp);
            builder.setOngoing(false);
        }
    }

        private static void setOnClickLoopingMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(LOOPING_ID);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_looping, pendingIntent);
    }

    private static void setOnClickNextTime(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(NEXT_TIME_ID);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_next_time, pendingIntent);
    }

    private static void setOnClickBackTime(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(BACK_TIME_ID);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_back_time, pendingIntent);
    }

    private static void setOnClickPlayPause(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(PLAY_PAUSE_ID);
        PendingIntent pendingIntent = PendingIntent.getService(context, 2, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_playPause, pendingIntent);
    }
    //endregion


}

package gvn.com.servicedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

  private static final String TAG = "MyService";
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
          nm.createNotificationChannel(
              new NotificationChannel("linphone", getString(R.string.app_name),
                  NotificationManager.IMPORTANCE_NONE));
        }

        int defaultColor = ContextCompat.getColor(this, R.color.colorAccent);
        Notification notification = new NotificationCompat.Builder(this,
            "linphone")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOnlyAlertOnce(true)
            .setColor(defaultColor)
            .build();
        startForeground(123, notification);
      }



    Log.e(TAG, "onCreate: ");
    Log.e("ThangPham","onCreate");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.e(TAG, "onStartCommand: ");
    Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
//    if(intent!=null){
//      String a = intent.getStringExtra(MainActivity.THANGTHANG);
//      Toast.makeText(this, "HAHAHAHAHHAHAHHAHAH", Toast.LENGTH_SHORT).show();
//      Log.e("ThangPham","Name = "+a);
//    }else
//      Log.e("ThangPham","intent = null");
//    Log.e("ThangPham","onStartCommand");
    return START_STICKY;
    
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e(TAG, "onDestroy: ");
    Log.e("ThangPham","onDestroy");
  }
}

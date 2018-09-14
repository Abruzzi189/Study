package android.gpuimage.com.demoservicetypes;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ThoNh on 5/23/2018.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Log.d(TAG, "bindService: ");
        return super.bindService(service, conn, flags);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String a ="";
        if(intent!=null)
        {
            a = intent.getStringExtra("123");
        }
        Log.d(TAG, "onStartCommand: "+a);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }
}

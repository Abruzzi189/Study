package com.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v4.util.LruCache;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.ntq.adjust.AdjustLifecycleCallbacks;
import com.ntq.adjust.AdjustSdk;
import com.ntq.api.DfeApi;
import com.ntq.api.DfeApiImpl;
import com.ntq.api.DfeApiProvider;
import glas.bbsystem.BuildConfig;
import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.IOException;
import net.nex8.tracking.android.Nex8Tracker;
import net.nex8.tracking.android.Nex8Tracking;
import org.linphone.NetworkManager;

public class AndGApp extends Application implements DfeApiProvider {

  public static String advertId, device_name, versionApp, os_version;
  private static AndGApp mAndGApp;
  private static boolean sApplicationVisible = false;
  private RequestQueue mRequestQueue;
  private Cache mCache;
  private ImageLoader mImageLoader;
  private Nex8Tracker mTracker;
  private BroadcastReceiver connectionReceiver;
  private ImageCache mImageCache = new ImageCache() {
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
        10);

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
      cache.put(url, bitmap);
    }

    @Override
    public Bitmap getBitmap(String url) {
      return cache.get(url);
    }
  };

  public static boolean isApplicationVisibile() {
    return sApplicationVisible;
  }

  public static AndGApp get() {
    return mAndGApp;
  }

  private static Network createNetwork() {
    return new BasicNetwork(new HurlStack());
  }

  private static File getCacheDir(String name) {
    File file = new File(mAndGApp.getCacheDir(), name);
    file.mkdirs();
    return file;
  }

  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mAndGApp = this;

    // Set up Crashlytics, disabled for debug builds
    Crashlytics crashlyticsKit = new Crashlytics.Builder()
        .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
        .build();

    // fabric crash
    Fabric.with(this, crashlyticsKit);

    registerConnectionChange();

    // initialize Adjust
    AdjustSdk.initializeSDK(this);
    registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

    // workaround for use AsyncTask in get location address
    // https://code.google.com/p/android/issues/detail?id=20915
    try {
      Class.forName("android.os.AsyncTask");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    mCache = new DiskBasedCache(getCacheDir("images"));
    mRequestQueue = new RequestQueue(mCache, createNetwork());
    mImageLoader = new ImageLoader(mRequestQueue, mImageCache);

    PackageInfo pInfo = null;
    try {
      pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    versionApp = pInfo.versionName;
    device_name = android.os.Build.MODEL;
    os_version = "android " + Build.VERSION.SDK_INT;
    //GET GPS_ADID
    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        AdvertisingIdClient.Info idInfo = null;
        try {
          idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
        } catch (GooglePlayServicesNotAvailableException e) {
          e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        advertId = null;
        try {
          advertId = idInfo.getId();
        } catch (NullPointerException e) {
          e.printStackTrace();
        }
        return advertId;
      }

      @Override
      protected void onPostExecute(String advertId) {
      }

    };
    task.execute();

    mRequestQueue.start();
    mTracker = Nex8Tracking.newTracker(this, "1ffde6819c46168acc326769039657db");
  }

  /**
   * apps that target the API level 26 or higher can no longer register broadcast receivers for
   * implicit broadcasts in their manifest receiver will die when app close
   */
  private void registerConnectionChange() {
    connectionReceiver = new NetworkManager();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(connectionReceiver, intentFilter);
  }

  public Nex8Tracker getTracker() {
    return mTracker;
  }

  public void activityResume() {
    sApplicationVisible = true;
  }

  public void activityPause() {
    sApplicationVisible = false;
  }

  public ImageLoader getImageLoader() {
    return mImageLoader;
  }

  @Override
  public DfeApi getDfeApi() {
    return new DfeApiImpl(mRequestQueue);
  }
}

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.ImageRequestWithSize;
import com.application.connection.request.PhotoThumbRequest;
import com.application.connection.request.VideoThumbRequest;
import com.application.util.LogUtils;
import com.application.util.Utility;
import glas.bbsystem.BuildConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;


/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcher extends ImageResizer {

  public static final String AVATAR_CACHE_DIR = "avatar";
  public static final String LARGE_CACHE_DIR = "large";
  private static final String TAG = "ImageFetcher";
  private static final int HTTP_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
  private static final String HTTP_CACHE_DIR = "http";
  private static final int IO_BUFFER_SIZE = 8 * 1024;
  private static final int DISK_CACHE_INDEX = 0;
  private final Object mHttpDiskCacheLock = new Object();
  private DiskLruCache mHttpDiskCache;
  private File mHttpCacheDir;
  private boolean mHttpDiskCacheStarting = true;

  /**
   * Initialize providing a target image width and height for the processing images.
   */
  public ImageFetcher(Context context) {
    super(context);
    init(context);
  }

  /**
   * Workaround for bug pre-Froyo, see here for more info: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
   */
  public static void disableConnectionReuseIfNecessary() {
    // HTTP connection reuse which was buggy pre-froyo
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
      System.setProperty("http.keepAlive", "false");
    }
  }

  private void init(Context context) {
    checkConnection(context);
    mHttpCacheDir = ImageCache.getDiskCacheDir(context, HTTP_CACHE_DIR);
  }

  @Override
  protected void initDiskCacheInternal() {
    super.initDiskCacheInternal();
    initHttpDiskCache();
  }

  private void initHttpDiskCache() {
    if (!mHttpCacheDir.exists()) {
      mHttpCacheDir.mkdirs();
    }
    synchronized (mHttpDiskCacheLock) {
      if (ImageCache.getUsableSpace(mHttpCacheDir) > HTTP_CACHE_SIZE) {
        try {
          mHttpDiskCache = DiskLruCache.open(mHttpCacheDir, 1, 1,
              HTTP_CACHE_SIZE);
          if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "HTTP cache initialized");
          }
        } catch (IOException e) {
          mHttpDiskCache = null;
        }
      }
      mHttpDiskCacheStarting = false;
      mHttpDiskCacheLock.notifyAll();
    }
  }

  @Override
  protected void clearCacheInternal() {
    super.clearCacheInternal();
    synchronized (mHttpDiskCacheLock) {
      if (mHttpDiskCache != null && !mHttpDiskCache.isClosed()) {
        try {
          mHttpDiskCache.delete();
          if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "HTTP cache cleared");
          }
        } catch (IOException e) {
          LogUtils.e(TAG, "clearCacheInternal - " + e);
        }
        mHttpDiskCache = null;
        mHttpDiskCacheStarting = true;
        initHttpDiskCache();
      }
    }
  }

  @Override
  protected void flushCacheInternal() {
    super.flushCacheInternal();
    synchronized (mHttpDiskCacheLock) {
      if (mHttpDiskCache != null) {
        try {
          mHttpDiskCache.flush();
          if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "HTTP cache flushed");
          }
        } catch (IOException e) {
          LogUtils.e(TAG, "flush - " + e);
        }
      }
    }
  }

  @Override
  protected void closeCacheInternal() {
    super.closeCacheInternal();
    synchronized (mHttpDiskCacheLock) {
      if (mHttpDiskCache != null) {
        try {
          if (!mHttpDiskCache.isClosed()) {
            mHttpDiskCache.close();
            mHttpDiskCache = null;
            if (BuildConfig.DEBUG) {
              LogUtils.d(TAG, "HTTP cache closed");
            }
          }
        } catch (IOException e) {
          LogUtils.e(TAG, "closeCacheInternal - " + e);
        }
      }
    }
  }

  /**
   * Simple network connection check.
   */
  private void checkConnection(Context context) {
    final ConnectivityManager cm = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
      LogUtils.e(TAG, "checkConnection - no connection found");
    }
  }

  /**
   * The main process method, which will be called by the ImageWorker in the AsyncTask background
   * thread.
   *
   * @param data The data to load the bitmap, in this case, a regular http URL
   * @return The downloaded and resized bitmap
   */
  private Bitmap processBitmap(String data, int width, int height,
      String urlCache) {
    // if (BuildConfig.DEBUG) {
    // LogUtils.d(TAG, "processBitmap - " + data);
    // }

    final String key = ImageCache.hashKeyForDisk(urlCache);
    FileDescriptor fileDescriptor = null;
    FileInputStream fileInputStream = null;
    DiskLruCache.Snapshot snapshot;
    synchronized (mHttpDiskCacheLock) {
      // Wait for disk cache to initialize
      while (mHttpDiskCacheStarting) {
        try {
          mHttpDiskCacheLock.wait();
        } catch (InterruptedException e) {
        }
      }

      if (mHttpDiskCache != null) {
        try {
          snapshot = mHttpDiskCache.get(key);
          if (snapshot == null) {
            LogUtils.d(TAG,
                "processBitmap, not found in http cache, downloading...");
            DiskLruCache.Editor editor = mHttpDiskCache.edit(key);
            if (editor != null) {
              if (downloadUrlToStream2(data,
                  editor.newOutputStream(DISK_CACHE_INDEX))) {
                editor.commit();
              } else {
                editor.abort();
              }
            }
            snapshot = mHttpDiskCache.get(key);
          }
          if (snapshot != null) {
            // if (BuildConfig.DEBUG) {
            // LogUtils.d(TAG, HTTP_CACHE_DIR + " cache hit");
            // }
            fileInputStream = (FileInputStream) snapshot
                .getInputStream(DISK_CACHE_INDEX);
            fileDescriptor = fileInputStream.getFD();
          }
        } catch (IOException e) {
          LogUtils.e(TAG, "processBitmap - " + e);
        } catch (IllegalStateException e) {
          LogUtils.e(TAG, "processBitmap - " + e);
        } finally {
          if (fileDescriptor == null && fileInputStream != null) {
            try {
              fileInputStream.close();
            } catch (IOException e) {
            }
          }
        }
      }
    }
    Bitmap bitmap = null;
    if (fileDescriptor != null) {
      bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, width,
          height, getImageCache());
    }
    if (fileInputStream != null) {
      try {
        fileInputStream.close();
      } catch (IOException e) {
      }
    }
    return bitmap;
  }

  private Bitmap processVideoThumb(Object data) {
    String path = ((VideoThumbRequest) data).getPath();
    return ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MICRO_KIND);
  }

  private Bitmap processPhotoThumb(Object data, int reqWidth, int reqHeight) {
    String path = ((PhotoThumbRequest) data).getPath();
    Bitmap bitmap = Utility.decodeSampledBitmapFromPath(path, reqWidth,
        reqHeight);
    return fixOrientation(bitmap, path, reqWidth, reqHeight);
  }

  @Override
  protected Bitmap processBitmap(Object data, int width, int height) {
    String url = "";
    String urlCache = getCacheKeyFromObject(data);
    Bitmap bitmap = null;
    if (data instanceof ImageRequest) {
      ImageRequest imageRequest = (ImageRequest) data;
      if (TextUtils.isEmpty(imageRequest.img_id)
          || TextUtils.isEmpty(imageRequest.getToken())) {
        return null;
      }
      url = imageRequest.toURL();
    } else if (data instanceof ImageRequestWithSize) {
      ImageRequestWithSize imageRequest = (ImageRequestWithSize) data;
      if (TextUtils.isEmpty(imageRequest.img_id)
          || TextUtils.isEmpty(imageRequest.getToken())) {
        return null;
      }
      url = imageRequest.toURL();
    } else if (data instanceof VideoThumbRequest) {
      return processVideoThumb(data);
    } else if (data instanceof PhotoThumbRequest) {
      return processPhotoThumb(data, width, height);
    } else if (data instanceof CircleImageRequest) {
      url = ((CircleImageRequest) data).toURL();
    } else {
      url = String.valueOf(data);
    }
    bitmap = processBitmap(url, width, height, urlCache);
    return bitmap;
  }

  /**
   * Download a bitmap from a URL and write the content to an output stream.
   *
   * @param urlString The URL to fetch
   * @return true if successful, false otherwise
   */
  public boolean downloadUrlToStream(String urlString,
      OutputStream outputStream) {
    disableConnectionReuseIfNecessary();
    BufferedOutputStream out = null;
    BufferedInputStream in = null;

    try {
      final AndroidHttpClient client = AndroidHttpClient
          .newInstance("Android");
      final HttpGet getRequest = new HttpGet(urlString);
      HttpResponse response = client.execute(getRequest);
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        LogUtils.w("ImageDownloader", "Error " + statusCode
            + " while retrieving bitmap from " + urlString);
        return false;
      }
      final HttpEntity entity = response.getEntity();
      if (entity != null) {
        try {
          in = new BufferedInputStream(entity.getContent(),
              IO_BUFFER_SIZE);
          out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

          int b;
          while ((b = in.read()) != -1) {
            out.write(b);
          }
        } finally {
          entity.consumeContent();
        }
      }
      return true;
    } catch (final IOException e) {
      LogUtils.e(TAG, "Error in downloadBitmap - " + e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (final IOException e) {
      }
    }
    return false;
  }

  public boolean downloadUrlToStream2(String urlString,
      OutputStream outputStream) {
    disableConnectionReuseIfNecessary();
    HttpURLConnection urlConnection = null;
    BufferedOutputStream out = null;
    BufferedInputStream in = null;

    try {
      final URL url = new URL(urlString);
      urlConnection = (HttpURLConnection) url.openConnection();
      in = new BufferedInputStream(urlConnection.getInputStream(),
          IO_BUFFER_SIZE);
      out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

      int b;
      while ((b = in.read()) != -1) {
        out.write(b);
      }
      return true;
    } catch (final IOException e) {
      LogUtils.e(TAG, "Error in downloadBitmap - " + e);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (final IOException e) {
      }
    }
    return false;
  }

  @Override
  protected String getCacheKeyFromObject(Object object) {
    String key;
    if (object instanceof ImageRequest) {
      ImageRequest imageRequest = (ImageRequest) object;
      key = imageRequest.toURLCache();
    } else if (object instanceof VideoThumbRequest) {
      key = ((VideoThumbRequest) object).getPath();
    } else if (object instanceof PhotoThumbRequest) {
      key = ((PhotoThumbRequest) object).getPath();
    } else if (object instanceof CircleImageRequest) {
      key = ((CircleImageRequest) object).getCircleImageIdUnique();
    } else {
      key = String.valueOf(object);
    }
    return key;
  }

  public File getOriginalImage(Object object) {
    String urlCache = getCacheKeyFromObject(object);
    String key = ImageCache.hashKeyForDisk(urlCache);
    File file = new File(mHttpCacheDir, key + "." + DISK_CACHE_INDEX);
    return file;
  }

  /**
   * only use for fix orientaion
   */
  private File getFileFromUrlCache(String urlCache) {
    String key = ImageCache.hashKeyForDisk(urlCache);
    File file = new File(mHttpCacheDir, key + "." + DISK_CACHE_INDEX);
    return file;
  }
}

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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import com.application.connection.request.CircleImageRequest;
import com.application.constant.Constants;
import com.application.util.LogUtils;
import com.application.util.Utility;
import glas.bbsystem.BuildConfig;
import java.lang.ref.WeakReference;


/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public abstract class ImageWorker {

  private static final String TAG = "ImageWorker";
  private static final int FADE_IN_TIME = 300;
  private static final int NO_GENDER = -1;
  private static final int MESSAGE_CLEAR = 0;
  private static final int MESSAGE_INIT_DISK_CACHE = 1;
  private static final int MESSAGE_FLUSH = 2;
  private static final int MESSAGE_CLOSE = 3;
  private final Object mPauseWorkLock = new Object();
  protected boolean mPauseWork = false;
  protected Context mContext;
  protected Resources mResources;
  private ImageCache mImageCache;
  private ImageCache.ImageCacheParams mImageCacheParams;
  private Bitmap mLoadingBitmap;
  private Bitmap mLoadingCircleBitmap;
  private Bitmap mLoadingMaleBitmap;
  private Bitmap mLoadingFemaleBitmap;
  private boolean mFadeInBitmap = true;
  private boolean mExitTasksEarly = false;

  protected ImageWorker(Context context) {
    mResources = context.getResources();
    mContext = context;
  }

  /**
   * Cancels any pending work attached to the provided ImageView.
   */
  public static void cancelWork(ImageView imageView) {
    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
    if (bitmapWorkerTask != null) {
      bitmapWorkerTask.cancel(true);
      if (BuildConfig.DEBUG) {
        final Object bitmapData = bitmapWorkerTask.data;
        LogUtils.d(TAG, "cancelWork - cancelled work for " + bitmapData);
      }
    }
  }

  /**
   * Returns true if the current work has been canceled or if there was no work in progress on this
   * image view. Returns false if the work in progress deals with the same data. The work is not
   * stopped in that case.
   */
  public static boolean cancelPotentialWork(Object data, ImageView imageView) {
    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

    if (bitmapWorkerTask != null) {
      final Object bitmapData = bitmapWorkerTask.data;
      if (bitmapData == null || !bitmapData.equals(data)) {
        bitmapWorkerTask.cancel(true);
        // if (BuildConfig.DEBUG) {
        // LogUtils.d(TAG, "cancelPotentialWork - cancelled work for "
        // + data);
        // }
      } else {
        // The same work is already in progress.
        return false;
      }
    }
    return true;
  }

  /**
   * @param imageView Any imageView
   * @return Retrieve the currently active work task (if any) associated with this imageView. null
   * if there is no such task.
   */
  private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    if (imageView != null) {
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
        final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
        return asyncDrawable.getBitmapWorkerTask();
      }
    }
    return null;
  }

  /**
   * Load image with default placeholder
   */
  public void loadImage(Object data, ImageView imageView, int size) {
    loadImage(data, imageView, size, size, NO_GENDER, true, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param size
   * @param imageListener
   */
  public void loadImage(Object data, ImageView imageView, int size,
      ImageListener imageListener) {
    loadImage(data, imageView, size, size, NO_GENDER, true, imageListener);
  }

  /**
   * Load image with default placeholder when loading
   */
  public void loadImage(Object data, ImageView imageView, int width,
      int height) {
    loadImage(data, imageView, width, height, NO_GENDER, true, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param width
   * @param height
   * @param imageListener
   */
  public void loadImage(Object data, ImageView imageView, int width,
      int height, ImageListener imageListener) {
    loadImage(data, imageView, width, height, NO_GENDER, true,
        imageListener);
  }

  /**
   * Load image without placholder (loading image) showed when loading
   */
  public void loadImageWithoutPlaceHolder(Object data, ImageView imageView,
      int size) {
    loadImage(data, imageView, size, size, NO_GENDER, false, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param size
   * @param imageListener
   */
  public void loadImageWithoutPlaceHolder(Object data, ImageView imageView,
      int size, ImageListener imageListener) {
    loadImage(data, imageView, size, size, NO_GENDER, false, imageListener);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param width
   * @param height
   */
  public void loadImageWithoutPlaceHolder(Object data, ImageView imageView,
      int width, int height) {
    loadImage(data, imageView, width, height, NO_GENDER, false, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param width
   * @param height
   * @param imageListener
   */
  public void loadImageWithoutPlaceHolder(Object data, ImageView imageView,
      int width, int height, ImageListener imageListener) {
    loadImage(data, imageView, width, height, NO_GENDER, false,
        imageListener);
  }

  /**
   * Load image with placeholder by gender, width=height
   */
  public void loadImageByGender(Object data, ImageView imageView, int size,
      int gender) {
    loadImage(data, imageView, size, size, gender, true, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param size
   * @param gender
   * @param imageListener
   */
  public void loadImageByGender(Object data, ImageView imageView, int size,
      int gender, ImageListener imageListener) {
    loadImage(data, imageView, size, size, gender, true, imageListener);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param width
   * @param height
   * @param gender
   */
  public void loadImageByGender(Object data, ImageView imageView, int width,
      int height, int gender) {
    loadImage(data, imageView, width, height, gender, true, null);
  }

  /**
   *
   * @param data
   * @param imageView
   * @param width
   * @param height
   * @param gender
   * @param imageListener
   */
  public void loadImageByGender(Object data, ImageView imageView, int width,
      int height, int gender, ImageListener imageListener) {
    loadImage(data, imageView, width, height, gender, true, imageListener);
  }

  private Bitmap getBitmapCover(boolean hasCover, int gender, boolean isCircle) {
    if (!hasCover) {
      return null;
    }
    Bitmap loadingBitmap;
    switch (gender) {
      case Constants.GENDER_TYPE_MAN:
        loadingBitmap = mLoadingMaleBitmap;
        break;
      case Constants.GENDER_TYPE_WOMAN:
        loadingBitmap = mLoadingFemaleBitmap;
        break;
      default:
        if (isCircle) {
          loadingBitmap = mLoadingCircleBitmap;
        } else {
          loadingBitmap = mLoadingBitmap;
        }
        break;
    }
    if (loadingBitmap != null && isCircle) {
      loadingBitmap = Utils.getBitmapInCircle(loadingBitmap);
    }
    return loadingBitmap;
  }

  /**
   * Load an image specified by the data parameter into an ImageView (override {@link
   * ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk cache
   * will be used if an {@link ImageCache} has been added using {@link
   * ImageWorker#addImageCache(FragmentManager, ImageCache.ImageCacheParams)} . If the image is
   * found in the memory cache, it is set immediately, otherwise an {@link AsyncTask} will be
   * created to asynchronously load the bitmap.
   *
   * @param data The URL of the image to download.
   * @param imageView The ImageView to bind the downloaded image to.
   */
  private void loadImage(Object data, ImageView imageView, int width,
      int height, int gender, boolean hasCover,
      ImageListener imageListener) {

    if (data == null) {
      return;
    }
    BitmapDrawable value = null;
    if (mImageCache != null) {
      value = mImageCache
          .getBitmapFromMemCache(getCacheKeyFromObject(data));
    }
    boolean isCircle = false;
    if (data instanceof CircleImageRequest) {
      isCircle = true;
    }
    Bitmap loadingBitmap = getBitmapCover(hasCover, gender, isCircle);

    if (value != null) {
      // Bitmap found in memory cache
      Bitmap bitmap = value.getBitmap();
      if (isCircle) {
        bitmap = Utils.getBitmapInCircle(bitmap);
      }
      imageView.setImageBitmap(bitmap);
      if (imageListener != null) {
        imageListener.onGetImageSuccess(value.getBitmap());
      }
    } else if (cancelPotentialWork(data, imageView)) {
      final BitmapWorkerTask task = new BitmapWorkerTask(imageView,
          width, height, gender, hasCover, isCircle, imageListener);

      final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources,
          loadingBitmap, task);
      imageView.setImageDrawable(asyncDrawable);

      // NOTE: This uses a custom version of AsyncTask that has been
      // pulled from the
      // framework and slightly modified. Refer to the docs at the top of
      // the class
      // for more info on what was changed.
      task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
    }

  }

  /**
   * Set placeholder bitmap that shows when the the background thread is running.
   */
  public void setLoadingImage(Bitmap bitmap) {
    mLoadingBitmap = bitmap;
  }

  public void setLoadingImage(int resId) {
    mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
  }

  public void setLoadingImage(int resIdMale, int resIdFemale) {
    mLoadingMaleBitmap = BitmapFactory
        .decodeResource(mResources, resIdMale);
    mLoadingFemaleBitmap = BitmapFactory.decodeResource(mResources,
        resIdFemale);
  }

  /**
   * Set placeholder circle bitmap that shows when the the background thread is running.
   */
  public void setLoadingCircleBitmap(Bitmap bitmap) {
    mLoadingCircleBitmap = bitmap;
  }

  public void setLoadingCircleBitmap(int resId) {
    mLoadingCircleBitmap = BitmapFactory.decodeResource(mResources, resId);
  }

  /**
   * Set placeholder bitmap that shows when the the background thread is running.
   */
  public void setLoadingImage(int resId, int resCircleId, int resIdMale,
      int resIdFemale) {
    mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
    mLoadingCircleBitmap = BitmapFactory.decodeResource(mResources,
        resCircleId);
    mLoadingMaleBitmap = BitmapFactory
        .decodeResource(mResources, resIdMale);
    mLoadingFemaleBitmap = BitmapFactory.decodeResource(mResources,
        resIdFemale);
  }

  /**
   * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
   * caching.
   *
   * @param cacheParams The cache parameters to use for the image cache.
   */
  public void addImageCache(FragmentManager fragmentManager,
      ImageCache.ImageCacheParams cacheParams) {
    mImageCacheParams = cacheParams;
    mImageCache = ImageCache
        .getInstance(fragmentManager, mImageCacheParams);
    new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
  }

  /**
   * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
   * caching.
   *
   * @param diskCacheDirectoryName See {@link ImageCache.ImageCacheParams#ImageCacheParams(Context,
   * String)} .
   */
  public void addImageCache(FragmentActivity activity,
      String diskCacheDirectoryName) {
    mImageCacheParams = new ImageCache.ImageCacheParams(activity,
        diskCacheDirectoryName);
    mImageCache = ImageCache.getInstance(
        activity.getSupportFragmentManager(), mImageCacheParams);
    new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
  }

  /**
   * If set to true, the image will fade-in once it has been loaded by the background thread.
   */
  public void setImageFadeIn(boolean fadeIn) {
    mFadeInBitmap = fadeIn;
  }

  public void setExitTasksEarly(boolean exitTasksEarly) {
    mExitTasksEarly = exitTasksEarly;
    setPauseWork(false);
  }

  /**
   * Subclasses should override this to define any processing or work that must happen to produce
   * the final bitmap. This will be executed in a background thread and be long running. For
   * example, you could resize a large bitmap here, or pull down an image from the network.
   *
   * @param data The data to identify which image to process, as provided by {@link
   * ImageWorker#loadImage(Object, ImageView)}
   * @return The processed bitmap
   */
  protected abstract Bitmap processBitmap(Object data, int width, int height);

  /**
   * @return The {@link ImageCache} object currently being used by this ImageWorker.
   */
  protected ImageCache getImageCache() {
    return mImageCache;
  }

  protected abstract String getCacheKeyFromObject(Object object);

  /**
   * Called when the processing is complete and the final drawable should be set on the ImageView.
   */
  // Tamtd - Khong cho phep thay doi background
  // @SuppressWarnings("deprecation")
  // @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void setImageDrawable(ImageView imageView, Drawable drawable,
      int gender, boolean hasCover, boolean isCircle) {
    imageView.setImageDrawable(drawable);

    // tungdx: Do not use below source code because it can't work with
    // CircleImageView, shouldn't use fadeInBitmap cause CircleImageView
    // can't not get Bitmap from TransitionDrawable that set to ImageView.

    /**
     *
     if (mFadeInBitmap) { // Transition drawable with a transparent
     * drawable and the final // drawable Drawable background =
     * imageView.getDrawable(); if(background == null){ background = new
     * ColorDrawable(android.R.color.transparent); } final
     * TransitionDrawable td = new TransitionDrawable( new Drawable[]
     * {background, drawable}); td.setCrossFadeEnabled(true); // Bitmap
     * bitmap = getBitmapCover(hasCover, gender,isCircle); // if
     * (Utility.hasJELLY()) { // imageView.setBackground(new
     * BitmapDrawable(mResources, bitmap)); // } else { //
     * imageView.setBackgroundDrawable(new BitmapDrawable(mResources, //
     * bitmap)); // } imageView.setImageDrawable(td);
     * td.startTransition(FADE_IN_TIME); //
     * imageView.setBackgroundResource(android.R.color.transparent); } else
     * { imageView.setImageDrawable(drawable); }
     */
  }

  /**
   * Pause any ongoing background work. This can be used as a temporary measure to improve
   * performance. For example background work could be paused when a ListView or GridView is being
   * scrolled using a {@link android.widget.AbsListView.OnScrollListener} to keep scrolling smooth.
   * <p> If work is paused, be sure setPauseWork(false) is called again before your fragment or
   * activity is destroyed (for example during {@link android.app.Activity#onPause()}), or there is
   * a risk the background thread will never finish.
   */
  public void setPauseWork(boolean pauseWork) {
    synchronized (mPauseWorkLock) {
      mPauseWork = pauseWork;
      if (!mPauseWork) {
        mPauseWorkLock.notifyAll();
      }
    }
  }

  protected void initDiskCacheInternal() {
    if (mImageCache != null) {
      mImageCache.initDiskCache();
    }
  }

  protected void clearCacheInternal() {
    if (mImageCache != null) {
      mImageCache.clearCache();
    }
  }

  protected void flushCacheInternal() {
    if (mImageCache != null) {
      mImageCache.flush();
    }
  }

  protected void closeCacheInternal() {
    if (mImageCache != null) {
      mImageCache.close();
      mImageCache = null;
    }
  }

  public void clearCache() {
    new CacheAsyncTask().execute(MESSAGE_CLEAR);
  }

  public void flushCache() {
    new CacheAsyncTask().execute(MESSAGE_FLUSH);
  }

  public void closeCache() {
    new CacheAsyncTask().execute(MESSAGE_CLOSE);
  }

  protected Bitmap fixOrientation(Bitmap bitmap, String path, int reqWidth,
      int reqHeight) {
    int angle = Utility.getAngle(path);
    return Utility.rotateImage(bitmap, angle);
  }

  public static interface ImageListener {

    public void onGetImageSuccess(Bitmap bitmap);

    public void onGetImageFailure();
  }

  /**
   * A custom Drawable that will be attached to the imageView while the work is in progress.
   * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
   * required, and makes sure that only the last started worker process can bind its result,
   * independently of the finish order.
   */
  private static class AsyncDrawable extends BitmapDrawable {

    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
        BitmapWorkerTask bitmapWorkerTask) {
      super(res, bitmap);
      bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
          bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
      return bitmapWorkerTaskReference.get();
    }
  }

  /**
   * The actual AsyncTask that will asynchronously process the image.
   */
  private class BitmapWorkerTask extends
      AsyncTask<Object, Void, BitmapDrawable> {

    private final WeakReference<ImageView> imageViewReference;
    private Object data;
    private int mWidth;
    private int mHeight;
    private int mGender;
    private boolean mHasCover;
    private boolean mIsCircle;
    private ImageListener mImageListener;

    public BitmapWorkerTask(ImageView imageView, int width, int height,
        int gender, boolean hasCover, boolean isCircle,
        ImageListener imageListener) {
      imageViewReference = new WeakReference<ImageView>(imageView);
      mWidth = width;
      mHeight = height;
      mGender = gender;
      mHasCover = hasCover;
      mIsCircle = isCircle;
      mImageListener = imageListener;
    }

    /**
     * Background processing.
     */
    @Override
    protected BitmapDrawable doInBackground(Object... params) {
      // if (BuildConfig.DEBUG) {
      // LogUtils.d(TAG, "doInBackground - starting work");
      // }

      data = params[0];
      final String dataString = getCacheKeyFromObject(data);
      Bitmap bitmap = null;
      BitmapDrawable drawable = null;

      // Wait here if work is paused and the task is not cancelled
      synchronized (mPauseWorkLock) {
        while (mPauseWork && !isCancelled()) {
          try {
            mPauseWorkLock.wait();
          } catch (InterruptedException e) {
          }
        }
      }

      // If the image cache is available and this task has not been
      // cancelled by another
      // thread and the ImageView that was originally bound to this task
      // is still bound back
      // to this task and our "exit early" flag is not set then try and
      // fetch the bitmap from
      // the cache
      if (mImageCache != null && !isCancelled()
          && getAttachedImageView() != null && !mExitTasksEarly) {
        bitmap = mImageCache.getBitmapFromDiskCache(dataString, mWidth,
            mHeight);
      }

      // If the bitmap was not found in the cache and this task has not
      // been cancelled by
      // another thread and the ImageView that was originally bound to
      // this task is still
      // bound back to this task and our "exit early" flag is not set,
      // then call the main
      // process method (as implemented by a subclass)
      if (bitmap == null && !isCancelled()
          && getAttachedImageView() != null && !mExitTasksEarly) {
        bitmap = processBitmap(params[0], mWidth, mHeight);
      }

      // If the bitmap was processed and the image cache is available,
      // then add the processed
      // bitmap to the cache for future use. Note we don't check if the
      // task was cancelled
      // here, if it was, and the thread is still running, we may as well
      // add the processed
      // bitmap to our cache as it might be used again in the future
      if (bitmap != null && mIsCircle) {
        bitmap = Utils.getBitmapInCircle(bitmap);
      }
      if (bitmap != null) {
        if (Utils.hasHoneycomb()) {
          // Running on Honeycomb or newer, so wrap in a standard
          // BitmapDrawable
          drawable = new BitmapDrawable(mResources, bitmap);
        } else {
          // Running on Gingerbread or older, so wrap in a
          // RecyclingBitmapDrawable
          // which will recycle automagically
          drawable = new RecyclingBitmapDrawable(mResources, bitmap);
        }

        if (mImageCache != null) {
          mImageCache.addBitmapToCache(dataString, drawable);
        }
      }

      // if (BuildConfig.DEBUG) {
      // LogUtils.d(TAG, "doInBackground - finished work");
      // }

      return drawable;
    }

    /**
     * Once the image is processed, associates it to the imageView
     */
    @Override
    protected void onPostExecute(BitmapDrawable value) {
      // if cancel was called on this task or the "exit early" flag is set
      // then we're done
      if (isCancelled() || mExitTasksEarly) {
        value = null;
      }
      if (mImageListener != null && value != null
          && value.getBitmap() != null) {
        mImageListener.onGetImageSuccess(value.getBitmap());
      }

      final ImageView imageView = getAttachedImageView();
      if (value != null && imageView != null) {
        // if (BuildConfig.DEBUG) {
        // LogUtils.d(TAG, "onPostExecute - setting bitmap");
        // }
        setImageDrawable(imageView, value, mGender, mHasCover,
            mIsCircle);
      }
    }

    @Override
    protected void onCancelled(BitmapDrawable value) {
      super.onCancelled(value);
      synchronized (mPauseWorkLock) {
        mPauseWorkLock.notifyAll();
      }
    }

    /**
     * Returns the ImageView associated with this task as long as the ImageView's task still points
     * to this task as well. Returns null otherwise.
     */
    private ImageView getAttachedImageView() {
      final ImageView imageView = imageViewReference.get();
      final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

      if (this == bitmapWorkerTask) {
        return imageView;
      }

      return null;
    }
  }

  protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
      switch ((Integer) params[0]) {
        case MESSAGE_CLEAR:
          clearCacheInternal();
          break;
        case MESSAGE_INIT_DISK_CACHE:
          initDiskCacheInternal();
          break;
        case MESSAGE_FLUSH:
          flushCacheInternal();
          break;
        case MESSAGE_CLOSE:
          closeCacheInternal();
          break;
      }
      return null;
    }
  }

}

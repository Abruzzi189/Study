package com.application.uploader;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.application.AndGApp;
import com.application.uploader.UploadDao.OnUpLoadListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class UploadManager {

  private final static String TAG = "upload_manager";
  private final static int INTERVAL_PERCENTAGE_UPDATE_DATABASE = 2;

  private static UploadManager instance;
  private DatabaseHelper mDbHelper;
  private UploadDao mUploadDao;
  private Context mContext;

  private UploadManager() {
    mContext = AndGApp.get().getApplicationContext();
    mDbHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
    mUploadDao = new UploadDao(mDbHelper);
  }

  public static UploadManager getInstance() {
    if (instance == null) {
      instance = new UploadManager();
    }
    return instance;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void addRequest(final String id,
      final UploadProgress uploadProgress,
      final UploadRequest uploadRequest) {
    Log.d(TAG, "add request " + id);
    if (TextUtils.isEmpty(id)) {
      return;
    }
    if (uploadProgress == null || uploadRequest == null) {
      return;
    }

    if (mUploadDao.isExist(id)) {
      // uploading
      return;
    }

    final UploadModel uploadModel = new UploadModel(id,
        uploadRequest.toURL(), uploadRequest.getFile()
        .getAbsolutePath());

    Uploader uploader = new Uploader(mContext, new UploadProgress() {

      @Override
      public void uploadSuccess(UploadRequest uploadRequest,
          UploadResponse response) {
        // change status success in database
        uploadModel.setStatus(UploadState.UPLOAD_SUCCESS);
        uploadModel.setProgress(100);
//				mUploadDao.delete(uploadModel);
        mUploadDao.update(uploadModel);
        if (uploadProgress != null) {
          uploadProgress.uploadSuccess(uploadRequest, response);
        }
      }

      @Override
      public void uploadStart() {
        // add upload model to database
        mUploadDao.createIfNotExists(uploadModel);
        uploadModel.setStatus(UploadState.UPLOAD_START);
        if (uploadProgress != null) {
          uploadProgress.uploadStart();
        }
      }

      @Override
      public void uploadFail(int code) {
        // change status fail in database
        uploadModel.setStatus(UploadState.UPLOAD_FAILED);
        mUploadDao.update(uploadModel);
        mUploadDao.delete(uploadModel);
        if (uploadProgress != null) {
          uploadProgress.uploadFail(code);
        }
      }

      @Override
      public void uploadProgress(int percentage) {
        Log.d(TAG, "upload " + uploadModel.getId() + " progress "
            + percentage);
        uploadModel.setStatus(UploadState.UPLOAD_PROGRESS);
        // update progress in database
        if (percentage - uploadModel.getProgress() >= INTERVAL_PERCENTAGE_UPDATE_DATABASE) {
          uploadModel.setProgress(percentage);
          mUploadDao.update(uploadModel);
        }
        if (uploadProgress != null) {
          uploadProgress.uploadProgress(percentage);
        }
      }
    });
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // run multi-asynctask
      uploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
          uploadRequest);
    } else {
      uploader.execute(uploadRequest);
    }
  }

  public boolean isExist(String id) {
    return mUploadDao.isExist(id);
  }

  public void registerUploadListener(OnUpLoadListener uploadListener) {
    mUploadDao.addUploadListener(uploadListener);
  }

  public void removeUploadListener(OnUpLoadListener uploadListener) {
    mUploadDao.removeUploadListener(uploadListener);
  }

  public UploadDao getUploadDao() {
    return mUploadDao;
  }
}

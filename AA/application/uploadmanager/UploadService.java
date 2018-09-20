package com.application.uploadmanager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.application.status.StatusController;
import com.application.uploadmanager.database.UploadDBManager;

public class UploadService extends Service implements IUpload {

  private final IBinder mBinder = new UploadServiceBinder();
  protected UploadDBManager mUploadDBManager;
  protected AbstractUploadManager mUploadManager;

  @Override
  public void onCreate() {
    super.onCreate();
    mUploadDBManager = UploadDBManager.getInstance(getApplicationContext());
    mUploadDBManager.open();
    // initilize UploadManager here.
    mUploadManager = new AndGUploadManager(getApplicationContext(), this);
    StatusController.getInstance(getApplicationContext()).open();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUploadDBManager.close();
    StatusController.getInstance(getApplicationContext()).open();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onPending(long uploadId) {
    mUploadDBManager.updateUploadRequest(uploadId, UploadState.PENDING, 0,
        UploadResponseCode.NO_RESPONSE, null);
  }

  @Override
  public void onRunnning(long uploadId, int progress) {
    mUploadDBManager.updateUploadRequest(uploadId, UploadState.RUNNING,
        progress, UploadResponseCode.NO_RESPONSE, null);
  }

  @Override
  public void onSuccess(long uploadId, int responseCode, Object response) {
    mUploadDBManager.updateUploadRequest(uploadId, UploadState.SUCCESSFUL,
        100, responseCode, response);
  }

  @Override
  public void onFailed(long uploadId, int responseCode, Object response) {
    mUploadDBManager.updateUploadRequest(uploadId, UploadState.FAILED, 0,
        responseCode, response);
  }

  @Override
  public void onAdded(long uploadId, IUploadResource uploadResource) {

  }

  @Override
  public void onPaused(long uploadId, int responseCode) {
    mUploadDBManager.updateUploadRequest(uploadId, UploadState.PAUSED, 0,
        responseCode, null);
  }

  @Override
  public void onCancel(long uploadId) {

  }

  public void cancel(long uploadId) {
    mUploadDBManager.cancelUploadRequest(uploadId);
    mUploadManager.cancel(uploadId);
  }

  public synchronized long executeUpload(IUploadResource uploadResource) {
    long uploadId = mUploadDBManager.addUploadRequest(uploadResource);
    mUploadManager.onAdded(uploadId, uploadResource);
    mUploadManager.upload(uploadId, uploadResource);
    return uploadId;
  }

  public int getState(long uploadId) {
    return mUploadDBManager.queryUploadState(uploadId);
  }

  public boolean isUploadSuccess(long uploadId) {
    return mUploadDBManager.queryUploadState(uploadId) == UploadState.SUCCESSFUL ? true
        : false;
  }

  public int getProgress(long uploadId) {
    return mUploadDBManager.queryUploadProgress(uploadId);
  }

  public class UploadServiceBinder extends Binder {

    public UploadService getService() {
      return UploadService.this;
    }
  }
}

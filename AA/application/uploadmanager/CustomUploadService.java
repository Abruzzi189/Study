package com.application.uploadmanager;

import com.application.status.IStatusChatChanged;
import com.application.status.MessageInDB;
import com.application.status.StatusConstant;
import com.application.status.StatusController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CustomUploadService extends UploadService {

  ConcurrentLinkedQueue<IUploadCustom> linkedQueue = new ConcurrentLinkedQueue<IUploadCustom>();
  IStatusChatChanged updateStatusChat = new IStatusChatChanged() {

    @Override
    public void create(MessageInDB msgInDB) {
    }

    @Override
    public void update(MessageInDB msgInDB) {
      // 0 point => cancel upload
      if (msgInDB.getStatus() == StatusConstant.STATUS_ERROR) {
        cancel(msgInDB.getUploadId());
      }
    }

    @Override
    public void resendFile(MessageInDB msgInDB) {
    }

  };
  private Map<Long, IUploadResource> mUploadResources = new HashMap<Long, IUploadResource>();

  public synchronized void addUploadCustomListener(IUploadCustom uploadCustom) {
    if (!linkedQueue.contains(uploadCustom)) {
      linkedQueue.add(uploadCustom);
    }
  }

  public synchronized void removeUploadCustomListener(
      IUploadCustom uploadCustom) {
    linkedQueue.remove(uploadCustom);
  }

  @Override
  public synchronized void onAdded(long uploadId,
      IUploadResource uploadResource) {
    super.onAdded(uploadId, uploadResource);
    mUploadResources.put(uploadId, uploadResource);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onAdded(uploadId, uploadResource);
    }
  }

  @Override
  public synchronized void onPending(long uploadId) {
    super.onPending(uploadId);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onPending(uploadId, mUploadResources.get(uploadId));
    }
  }

  @Override
  public synchronized void onRunnning(long uploadId, int progress) {
    super.onRunnning(uploadId, progress);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onInprogress(uploadId, mUploadResources.get(uploadId),
          progress);
    }
  }

  @Override
  public synchronized void onSuccess(long uploadId, int responseCode,
      Object response) {
    super.onSuccess(uploadId, responseCode, response);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onSuccess(uploadId, mUploadResources.get(uploadId),
          responseCode, response);
    }
  }

  @Override
  public synchronized void onFailed(long uploadId, int responseCode,
      Object response) {
    super.onFailed(uploadId, responseCode, response);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onFailed(uploadId, mUploadResources.get(uploadId),
          responseCode, response);
    }
  }

  @Override
  public synchronized void onPaused(long uploadId, int responseCode) {
    super.onPaused(uploadId, responseCode);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onPaused(uploadId, mUploadResources.get(uploadId),
          responseCode);
    }
  }

  @Override
  public synchronized void onCancel(long uploadId) {
    super.onCancel(uploadId);
    Iterator<IUploadCustom> iterator = linkedQueue.iterator();
    while (iterator.hasNext()) {
      IUploadCustom custom = iterator.next();
      custom.onCancel(uploadId);
    }
  }

  ;

  public void onCreate() {
    super.onCreate();
    StatusController.getInstance(getApplicationContext())
        .addStatusChangedListener(updateStatusChat);
  }

  ;

  public void onDestroy() {
    super.onDestroy();
    StatusController.getInstance(getApplicationContext())
        .removeStatusChangedListener(updateStatusChat);
  }
}

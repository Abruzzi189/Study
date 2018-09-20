package com.application.uploadmanager;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUploadManager {

  protected IUpload mIUpload;
  protected Map<Long, IUploadResource> mUploadResources = new HashMap<Long, IUploadResource>();

  public AbstractUploadManager(IUpload iUpload) {
    mIUpload = iUpload;
  }

  public void upload(long uploadId, IUploadResource uploadResource) {
    mUploadResources.put(uploadId, uploadResource);
    execute(uploadId, uploadResource);
  }

  protected abstract void execute(long uploadId,
      IUploadResource uploadResource);

  public void cancel(long uploadId) {
    mUploadResources.remove(uploadId);
  }

  public void onAdded(long uploadId, IUploadResource uploadResource) {
    mIUpload.onAdded(uploadId, uploadResource);
  }
}

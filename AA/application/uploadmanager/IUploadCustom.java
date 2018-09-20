package com.application.uploadmanager;

public interface IUploadCustom {

  public void onAdded(long uploadId, IUploadResource uploadResource);

  public void onPending(long uploadId, IUploadResource uploadResource);

  public void onInprogress(long uploadId, IUploadResource uploadResource,
      int progress);

  public void onSuccess(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response);

  public void onFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response);

  public void onPaused(long uploadId, IUploadResource uploadResource,
      int responseCode);

  public void onCancel(long uploadId);
}

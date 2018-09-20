package com.application.uploader;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.ArrayList;
import java.util.List;

public class UploadDao {

  private RuntimeExceptionDao<UploadModel, String> mUploadDao;
  private List<OnUpLoadListener> mUploadListeners = new ArrayList<UploadDao.OnUpLoadListener>();

  public UploadDao(DatabaseHelper dbHelper) {
    mUploadDao = dbHelper.getUploadRuntimeDao();
  }

  public boolean isExist(String id) {
    return mUploadDao.idExists(id);
  }

  public boolean isSuccess(String messageId) {
    if (mUploadDao.idExists(messageId)) {
      UploadModel uploadModel = mUploadDao.queryForId(messageId);
      if (uploadModel == null) {
        return false;
      }
      return uploadModel.getStatus() == UploadState.UPLOAD_SUCCESS ? true
          : false;
    }
    return false;
  }

  public void createIfNotExists(UploadModel uploadModel) {
    mUploadDao.createIfNotExists(uploadModel);
    for (OnUpLoadListener uploadListener : mUploadListeners) {
      if (uploadListener != null) {
        uploadListener.onCreate(uploadModel);
      }
    }
  }

  public void update(UploadModel uploadModel) {
    mUploadDao.update(uploadModel);
    for (OnUpLoadListener uploadListener : mUploadListeners) {
      if (uploadListener != null) {
        uploadListener.onUpdate(uploadModel);
      }
    }
  }

  public void delete(UploadModel uploadModel) {
    mUploadDao.delete(uploadModel);
    for (OnUpLoadListener uploadListener : mUploadListeners) {
      if (uploadListener != null) {
        uploadListener.onDelete(uploadModel);
      }
    }
  }

  public void addUploadListener(OnUpLoadListener uploadListener) {
    if (!mUploadListeners.contains(uploadListener)) {
      mUploadListeners.add(uploadListener);
    }
  }

  public void removeUploadListener(OnUpLoadListener uploadListener) {
    mUploadListeners.remove(uploadListener);
  }

  public static interface OnUpLoadListener {

    void onCreate(UploadModel uploadModel);

    void onUpdate(UploadModel uploadModel);

    void onDelete(UploadModel uploadModel);
  }
}

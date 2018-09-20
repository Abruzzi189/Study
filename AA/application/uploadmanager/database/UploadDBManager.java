package com.application.uploadmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.application.uploadmanager.IUploadResource;
import com.application.uploadmanager.UploadState;

public class UploadDBManager {

  private static UploadDBManager mDbManager;
  private SQLiteDatabase mSqLiteDatabase;
  private UploadDBHelper mDbHelper;

  private UploadDBManager(Context context) {
    mDbHelper = new UploadDBHelper(context);
  }

  public static UploadDBManager getInstance(Context context) {
    if (mDbManager == null) {
      mDbManager = new UploadDBManager(context);
    }
    return mDbManager;
  }

  public UploadDBManager open() {
    mSqLiteDatabase = mDbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    mDbHelper.close();
  }

  public long addUploadRequest(IUploadResource uploadResource) {
    ContentValues values = new ContentValues();
    values.put(UploadTable.FILEPATH, uploadResource.getFilePath());
    values.put(UploadTable.STATE, UploadState.PENDING);
    return mSqLiteDatabase
        .insert(UploadDBHelper.TABLE_UPLOAD, null, values);
  }

  public int queryUploadState(long uploadId) {
    int state = UploadState.UNKNOW;
    String[] columns = new String[]{UploadTable.STATE};
    String selections = UploadTable.ID + "=?";
    String[] selectArgs = new String[]{uploadId + ""};
    Cursor cursor = mSqLiteDatabase.query(UploadDBHelper.TABLE_UPLOAD,
        columns, selections, selectArgs, null, null, null);
    if (cursor != null && cursor.getCount() > 0) {
      cursor.moveToFirst();
      state = cursor.getInt(cursor.getColumnIndex(UploadTable.STATE));
      cursor.close();
      cursor = null;
    }
    return state;
  }

  public void cancelUploadRequest(long uploadId) {
    String whereClauses = UploadTable.ID + "=?";
    String[] args = new String[]{uploadId + ""};
    mSqLiteDatabase.delete(UploadDBHelper.TABLE_UPLOAD, whereClauses, args);
  }

  public void updateUploadRequest(long uploadId, int state, int progress,
      int responseCode, Object response) {
    ContentValues values = new ContentValues();
    values.put(UploadTable.STATE, state);
    values.put(UploadTable.PROGRESS, progress);
    values.put(UploadTable.RESPONSECODE, responseCode);
    if (response != null) {
      values.put(UploadTable.RESPONSE, response.toString());
    }
    String whereClauses = UploadTable.ID + "=?";
    String[] whereArgs = new String[]{uploadId + ""};
    mSqLiteDatabase.update(UploadDBHelper.TABLE_UPLOAD, values,
        whereClauses, whereArgs);
  }

  /**
   * Return progress of uploadId, return 0 if not found
   */
  public int queryUploadProgress(long uploadId) {
    int progress = 0;
    String[] columns = new String[]{UploadTable.PROGRESS};
    String selections = UploadTable.ID + "=?";
    String[] selectArgs = new String[]{uploadId + ""};
    Cursor cursor = mSqLiteDatabase.query(UploadDBHelper.TABLE_UPLOAD,
        columns, selections, selectArgs, null, null, null);
    if (cursor != null && cursor.getCount() > 0) {
      cursor.moveToFirst();
      progress = cursor.getInt(cursor
          .getColumnIndex(UploadTable.PROGRESS));
      cursor.close();
      cursor = null;
    }
    return progress;
  }
}
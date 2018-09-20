package com.application.stickers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.application.service.data.StickerCategoryInfo;
import com.application.uploadmanager.database.UploadDBHelper;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class StickersDBManager {

  private static final String TAG = "StickersDBManager";
  private static StickersDBManager stickersDBManager;
  private SQLiteDatabase mSqLiteDatabase;
  private UploadDBHelper mDbHelper;

  public StickersDBManager(Context context) {
    mDbHelper = new UploadDBHelper(context);
  }

  public static StickersDBManager getInstance(Context context) {
    if (stickersDBManager == null) {
      stickersDBManager = new StickersDBManager(context);
    }

    return stickersDBManager;
  }

  public StickersDBManager open() {
    mSqLiteDatabase = mDbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    mDbHelper.close();
  }

  synchronized public boolean insert(StickerCategoryInfo sticker) {
    open();
    ContentValues values = new ContentValues();

    values.put(StickersConstants.COLUMN_ID, sticker.getId());
    values.put(StickersConstants.COLUMN_NAME, sticker.getName());
    values.put(StickersConstants.COLUMN_NUMBER, sticker.getNum());
    values.put(StickersConstants.COLUMN_VERSION, sticker.getVersion());

    long rowId = mSqLiteDatabase.insert(StickersConstants.TABLE_STICKERS, null, values);

    LogUtils.i(TAG, "Insert sticker " + sticker.getId() + " --- " + (rowId != -1));
    close();
    return (rowId != -1);
  }

  synchronized public boolean update(StickerCategoryInfo sticker) {
    open();
    ContentValues values = new ContentValues();

    values.put(StickersConstants.COLUMN_ID, sticker.getId());
    values.put(StickersConstants.COLUMN_NAME, sticker.getName());
    values.put(StickersConstants.COLUMN_NUMBER, sticker.getNum());
    values.put(StickersConstants.COLUMN_VERSION, sticker.getVersion());

    String whereClause = StickersConstants.COLUMN_ID + "=?";
    String whereArgs[] = new String[]{sticker.getId()};
    int rowsAffected = mSqLiteDatabase
        .update(StickersConstants.TABLE_STICKERS, values, whereClause, whereArgs);

    LogUtils.i(TAG, "Update sticker " + sticker.getId() + " --- " + (rowsAffected > 0));
    close();
    return (rowsAffected > 0);
  }

  synchronized public boolean insertOrUpdate(StickerCategoryInfo sticker) {
    if (sticker == null) {
      return false;
    }

    if (isExist(sticker.getId())) {
      return update(sticker);
    } else {
      return insert(sticker);
    }
  }

  synchronized public boolean delete(StickerCategoryInfo sticker) {
    open();
    String whereClause = StickersConstants.COLUMN_ID + "=?";
    String whereArgs[] = new String[]{sticker.getId()};

    int rowsAffected = mSqLiteDatabase.delete(
        StickersConstants.TABLE_STICKERS, whereClause, whereArgs);

    close();
    return (rowsAffected > 0);
  }

  synchronized public List<StickerCategoryInfo> getListStickerCategoryInfos() {
    open();
    ArrayList<StickerCategoryInfo> stickers = new ArrayList<StickerCategoryInfo>();

    Cursor cursor = mSqLiteDatabase.query(StickersConstants.TABLE_STICKERS,
        null, null, null, null, null, null);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        StickerCategoryInfo sticker = new StickerCategoryInfo();

        sticker.setId(cursor.getString(cursor
            .getColumnIndex(StickersConstants.COLUMN_ID)));
        sticker.setName(cursor.getString(cursor
            .getColumnIndex(StickersConstants.COLUMN_NAME)));
        sticker.setNum(cursor.getInt(cursor
            .getColumnIndex(StickersConstants.COLUMN_NUMBER)));
        sticker.setVersion(cursor.getInt(cursor
            .getColumnIndex(StickersConstants.COLUMN_VERSION)));

        stickers.add(sticker);
      } while (cursor.moveToNext());

      cursor.close();
      cursor = null;
    }

    close();
    return stickers;
  }

  synchronized public boolean isExist(String id) {
    open();
    String selections = StickersConstants.COLUMN_ID + "=?";
    String[] selectArgs = new String[]{id};
    Cursor cursor = mSqLiteDatabase.query(StickersConstants.TABLE_STICKERS,
        null, selections, selectArgs, null, null, null);
    boolean isExist = false;
    if (cursor != null && cursor.getCount() > 0) {
      isExist = true;
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    close();
    return isExist;
  }
}

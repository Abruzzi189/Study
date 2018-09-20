package com.application.uploadmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.application.status.StatusConstant;
import com.application.stickers.StickersConstants;
import com.application.util.LogUtils;

public class UploadDBHelper extends SQLiteOpenHelper {

  public static final String TABLE_UPLOAD = "upload";
  public static final String TABLE_STATUS = "status";
  private static final String TAG = "DatabaseEazy";
  private static final String DB_NAME = "upload_db";
  private static final int DB_VERSION = 2; // 16-01-2015
  private static String UPLOAD_CREATE;
  private static String STATUS_CREATE;
  private static String STICKERS_CREATE;

  static {
    StringBuilder builder = new StringBuilder();
    builder.append("create table ");
    builder.append(TABLE_UPLOAD);
    builder.append("(");
    builder.append(UploadTable.ID);
    builder.append(" integer primary key autoincrement,");
    builder.append(UploadTable.STATE);
    builder.append(" integer not null,");
    builder.append(UploadTable.PROGRESS);
    builder.append(" integer,");
    builder.append(UploadTable.FILEPATH);
    builder.append(" text,");
    builder.append(UploadTable.RESPONSECODE);
    builder.append(" integer,");
    builder.append(UploadTable.RESPONSE);
    builder.append(" text");
    builder.append(");");
    UPLOAD_CREATE = builder.toString();
  }

  static {
    StringBuilder builder = new StringBuilder();
    builder.append("create table ");
    builder.append(StatusConstant.TABLE_STATUS);
    builder.append("(");
    builder.append(StatusConstant.COLUMN_ID);
    builder.append(" text primary key,");
    builder.append(StatusConstant.COLUMN_TIME_START);
    builder.append(" integer,");
    builder.append(StatusConstant.COLUMN_TIME_SEND);
    builder.append(" integer,");
    builder.append(StatusConstant.COLUMN_FROM);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_TO);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_TYPE);
    builder.append(" integer,");
    builder.append(StatusConstant.COLUMN_VALUE);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_STATUS);
    builder.append(" integer,");
    builder.append(StatusConstant.COLUMN_UPLOAD_ID);
    builder.append(" integer,");
    builder.append(StatusConstant.COLUMN_CHAT_CLIENT_ID);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_FILE_ID);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_FILE_TYPE);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_FILE_PATH);
    builder.append(" text,");
    builder.append(StatusConstant.COLUMN_TIME_AUDIO);
    builder.append(" integer");
    builder.append(");");
    STATUS_CREATE = builder.toString();
  }

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("create table if not exists ");
    sb.append(StickersConstants.TABLE_STICKERS);
    sb.append("(");
    sb.append(StickersConstants.COLUMN_ID);
    sb.append(" text primary key,");
    sb.append(StickersConstants.COLUMN_NAME);
    sb.append(" text,");
    sb.append(StickersConstants.COLUMN_NUMBER);
    sb.append(" integer,");
    sb.append(StickersConstants.COLUMN_VERSION);
    sb.append(" integer");
    sb.append(");");
    STICKERS_CREATE = sb.toString();
  }

  public UploadDBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(UPLOAD_CREATE);
    db.execSQL(STATUS_CREATE);
    db.execSQL(STICKERS_CREATE);

    LogUtils.i(TAG, "onCreate DB_VERSION --- " + DB_VERSION);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    int upgradeTo = oldVersion + 1;

    while (upgradeTo <= newVersion) {
      switch (upgradeTo) {
        case 2:
          upgradeDatabaseFrom1To2(db);
          break;

        default:
          break;
      }
      upgradeTo++;
    }

    LogUtils.i(TAG, "onUpgrade --- " + oldVersion + " " + newVersion);
  }

  private void upgradeDatabaseFrom1To2(SQLiteDatabase db) {
    db.execSQL(STICKERS_CREATE);
    LogUtils.i(TAG, "upgradeDatabaseFrom1To2");
  }
}

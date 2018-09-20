package com.application.uploader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also
 * usually provides the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

  private final static String TAG = "upload_database";

  private static final String DATABASE_NAME = "andG.db";
  private static final int DATABASE_VERSION = 1;

  private RuntimeExceptionDao<UploadModel, String> uploadRuntimeDao = null;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * This is called when the database is first created. Usually you should call createTable
   * statements here to create the tables that will store your data.
   */
  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      Log.i(TAG, "database onCreate");
      TableUtils.createTable(connectionSource, UploadModel.class);
    } catch (SQLException e) {
      Log.e(TAG, "Can't create database " + e);
      throw new RuntimeException(e);
    }

  }

  /**
   * This is called when your application is upgraded and it has a higher version number. This
   * allows you to adjust the various data to match the new version number.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
      int oldVersion, int newVersion) {
    try {
      Log.i(TAG, "database onUpgrade");
      TableUtils.dropTable(connectionSource, UploadModel.class, true);
      // after we drop the old databases, we create the new ones
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.e(TAG, "Can't drop databases " + e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData
   * class. It will create it or just give the cached value. RuntimeExceptionDao only through
   * RuntimeExceptions.
   */
  public RuntimeExceptionDao<UploadModel, String> getUploadRuntimeDao() {
    if (uploadRuntimeDao == null) {
      uploadRuntimeDao = getRuntimeExceptionDao(UploadModel.class);
    }
    return uploadRuntimeDao;
  }

  /**
   * Close the database connections and clear any cached DAOs.
   */
  @Override
  public void close() {
    super.close();
    uploadRuntimeDao = null;
  }
}

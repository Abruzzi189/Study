package com.application.status;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.application.uploadmanager.database.UploadDBHelper;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public class StatusDBManager {

  private static StatusDBManager mDbManager;
  private SQLiteDatabase mSqLiteDatabase;
  private UploadDBHelper mDbHelper;
  private List<IStatusChatChanged> dbChangeListener = new ArrayList<IStatusChatChanged>();

  private StatusDBManager(Context context) {
    mDbHelper = new UploadDBHelper(context);
  }

  public static StatusDBManager getInstance(Context context) {
    if (mDbManager == null) {
      mDbManager = new StatusDBManager(context);
    }
    return mDbManager;
  }

  StatusDBManager open() {
    mSqLiteDatabase = mDbHelper.getWritableDatabase();
    return this;
  }

  void close() {
    mDbHelper.close();
  }

  synchronized void createIfNoExist(Message message, int status) {
    if (!isExist(message.id)) {
      create(message, status);
    }
  }

  synchronized void create(Message message, int status) {
    ContentValues values = new ContentValues();
    values.put(StatusConstant.COLUMN_ID, message.id);
    // values.put(StatusConstant.COLUMN_DATE, message.originTime.getTime());
    long currentTime = System.currentTimeMillis();
    values.put(StatusConstant.COLUMN_TIME_START, currentTime);
    values.put(StatusConstant.COLUMN_TIME_SEND, currentTime);
    values.put(StatusConstant.COLUMN_FROM, message.from);
    values.put(StatusConstant.COLUMN_TO, message.to);
    values.put(StatusConstant.COLUMN_TYPE,
        StatusConstant.getMsgType(message.msgType));
    values.put(StatusConstant.COLUMN_VALUE, message.value);
    values.put(StatusConstant.COLUMN_STATUS, status);
    values.put(StatusConstant.COLUMN_UPLOAD_ID, 0);
    values.put(StatusConstant.COLUMN_CHAT_CLIENT_ID, message.id);
    values.put(StatusConstant.COLUMN_FILE_ID, "");
    values.put(StatusConstant.COLUMN_FILE_TYPE, "");
    values.put(StatusConstant.COLUMN_FILE_PATH, "");
    values.put(StatusConstant.COLUMN_TIME_AUDIO, 0);
    long rowId = mSqLiteDatabase.insert(StatusConstant.TABLE_STATUS, null,
        values);
    LogUtils.d(StatusConstant.TAG, "create " + message + " with status "
        + StatusConstant.getStatus(status) + " result " + (rowId >= 0));
    if (rowId > 0) {
      MessageInDB msgInDB = query(message.id);
      for (IStatusChatChanged dbChange : dbChangeListener) {
        dbChange.create(msgInDB);
      }
    }
  }

  /**
   * Only change status to STATUS_DELETE. Don't delete message from database
   */
  synchronized void detele(String id) {
    long rowId = updateStatus(id, StatusConstant.STATUS_DELETE);
    LogUtils.d(StatusConstant.TAG, "detele " + id + " result "
        + (rowId >= 0));
  }

  /**
   * Delete list message of conversation in database
   */
  synchronized void deleteListSend(String fromId, String[] listSendId) {
    if (listSendId == null || listSendId.length == 0) {
      return;
    }
    StringBuilder query = new StringBuilder(StatusConstant.COLUMN_FROM
        + "=" + "'" + fromId + "'" + " AND ");
    query.append(StatusConstant.COLUMN_TO + " IN ");
    int size = listSendId.length;
    query.append("(");
    for (int i = 0; i < size; i++) {
      query.append("'").append(listSendId[i]).append("'");
      if (i != size - 1) {
        query.append(",");
      }
    }
    query.append(")");
    int rowEffect = mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS,
        query.toString(), null);
    LogUtils.d(StatusConstant.TAG, "rows effect delete " + rowEffect);
  }

  public synchronized long updateStatus(String id, int status) {
    ContentValues values = new ContentValues();
    values.put(StatusConstant.COLUMN_STATUS, status);
    String whereClause = StatusConstant.COLUMN_ID + "=?";
    String[] whereArgs = new String[]{id};
    long numberRow = mSqLiteDatabase.update(StatusConstant.TABLE_STATUS,
        values, whereClause, whereArgs);
    LogUtils.d(StatusConstant.TAG, "update " + id + " with status "
        + StatusConstant.getStatus(status) + " result "
        + (numberRow >= 0));
    if (numberRow > 0) {
      MessageInDB messge = query(id);
      if (messge != null) {
        for (IStatusChatChanged dbChange : dbChangeListener) {
          dbChange.update(messge);
        }
      }
    }
    return numberRow;
  }

  synchronized public long updateStatus2(String id, int status) {
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS, null,null,null, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      LogUtils.d(StatusConstant.TAG, "updateStatus2-------------------------------------->(1) cursor COLUMN_ID=" + cursor.getString(cursor.getColumnIndex(StatusConstant.COLUMN_ID)));
      LogUtils.d(StatusConstant.TAG, "updateStatus2-------------------------------------->(1) cursor COLUMN_CHAT_CLIENT_ID=" + cursor.getString(cursor.getColumnIndex(StatusConstant.COLUMN_CHAT_CLIENT_ID)));
    }
    return 0;
  }
  /**
   * Change status -> retry, change retry -> nothing (retry to timeout by TimeoutController)
   */
  synchronized void updateStatusError(String id) {
    String selections = StatusConstant.COLUMN_ID + "=?";
    String[] selecArgs = new String[]{id};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      int status = cursor.getInt(6);
      if (status == StatusConstant.STATUS_START) {
        status = StatusConstant.STATUS_RETRY;
        updateStatus(id, StatusConstant.STATUS_RETRY);
      }
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }

  }

  synchronized long update(MessageInDB message) {
    ContentValues values = new ContentValues();
    values.put(StatusConstant.COLUMN_TIME_START, message.getTimeStart());
    values.put(StatusConstant.COLUMN_TIME_SEND, message.getTimeSend());
    values.put(StatusConstant.COLUMN_FROM, message.getFrom());
    values.put(StatusConstant.COLUMN_TO, message.getTo());
    values.put(StatusConstant.COLUMN_TYPE, message.getType());
    values.put(StatusConstant.COLUMN_VALUE, message.getValue());
    values.put(StatusConstant.COLUMN_STATUS, message.getStatus());
    values.put(StatusConstant.COLUMN_UPLOAD_ID, message.getUploadId());
    values.put(StatusConstant.COLUMN_CHAT_CLIENT_ID,
        message.getChatClientId());
    values.put(StatusConstant.COLUMN_FILE_ID, message.getFileId());
    values.put(StatusConstant.COLUMN_FILE_TYPE, message.getFileType());
    values.put(StatusConstant.COLUMN_FILE_PATH, message.getFilePath());
    values.put(StatusConstant.COLUMN_TIME_AUDIO, message.getAudioTime());

    String whereClause = StatusConstant.COLUMN_ID + "=?";
    String[] whereArgs = new String[]{message.getId()};
    long rowId = mSqLiteDatabase.update(StatusConstant.TABLE_STATUS,
        values, whereClause, whereArgs);
    LogUtils.d(StatusConstant.TAG, "update " + message + " result "
        + (rowId >= 0));
    if (rowId > 0) {
      for (IStatusChatChanged dbChange : dbChangeListener) {
        dbChange.update(message);
      }
    }
    return rowId;
  }

  synchronized boolean isExist(String id) {
    String selections = StatusConstant.COLUMN_ID + "=?";
    String[] selectArgs = new String[]{id};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selectArgs, null, null, null);
    boolean isExist = false;
    if (cursor != null && cursor.getCount() > 0) {
      isExist = true;
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    Log.d(StatusConstant.TAG, "check exist " + id + " " + isExist);
    return isExist;
  }

  synchronized MessageInDB query(String id) {
    String selections = StatusConstant.COLUMN_ID + "=?";
    String[] selecArgs = new String[]{id};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    MessageInDB message = null;
    if (cursor != null && cursor.moveToFirst()) {
      message = genMessageFromCursor(cursor);
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return message;
  }

  synchronized MessageInDB queryUploadId(long uploadId) {
    String selections = StatusConstant.COLUMN_UPLOAD_ID + "=?";
    String[] selecArgs = new String[]{String.valueOf(uploadId)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    MessageInDB message = null;
    if (cursor != null && cursor.moveToFirst()) {
      message = genMessageFromCursor(cursor);
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return message;
  }

  synchronized MessageInDB queryFileId(String fileId) {
    String selections = StatusConstant.COLUMN_FILE_ID + "=?";
    String[] selecArgs = new String[]{fileId};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    MessageInDB message = null;
    if (cursor != null && cursor.moveToFirst()) {
      message = genMessageFromCursor(cursor);
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return message;
  }

  synchronized MessageInDB queryChatMessageId(String chatMessageId) {
    String selections = StatusConstant.COLUMN_CHAT_CLIENT_ID + "=?";
    String[] selecArgs = new String[]{String.valueOf(chatMessageId)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    MessageInDB message = null;
    if (cursor != null && cursor.moveToFirst()) {
      message = genMessageFromCursor(cursor);
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return message;
  }

  synchronized boolean hasMsgError(String fromId, String toId) {
    String selections = StatusConstant.COLUMN_FROM + "=? AND "
        + StatusConstant.COLUMN_TO + "=? AND "
        + StatusConstant.COLUMN_STATUS + "=?";
    String[] selecArgs = new String[]{fromId, toId,
        String.valueOf(StatusConstant.STATUS_ERROR)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      return true;
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return false;
  }

  synchronized void changeUploadToStatus(long uploadId, int status) {
    String selections = StatusConstant.COLUMN_UPLOAD_ID + "=?";
    String[] selecArgs = new String[]{String.valueOf(uploadId)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      String id = cursor.getString(0);
      updateStatus(id, status);
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
  }

  synchronized List<MessageInDB> queryMsgFromUserIdWithStatus(String fromId,
      String sendId, int[] statusArr) {
    if (statusArr == null || statusArr.length == 0) {
      return new ArrayList<MessageInDB>();
    }
    StringBuilder query = new StringBuilder("SELECT * FROM "
        + StatusConstant.TABLE_STATUS + " WHERE ");
    query.append(StatusConstant.COLUMN_FROM + "=" + "'" + fromId + "'"
        + " AND " + StatusConstant.COLUMN_TO + "=" + "'" + sendId + "'"
        + " AND ");
    query.append(StatusConstant.COLUMN_STATUS + " IN ");
    int size = statusArr.length;
    query.append("(");
    for (int i = 0; i < size; i++) {
      query.append("'").append(statusArr[i]).append("'");
      if (i < size - 1) {
        query.append(",");
      }
    }
    query.append(")");
    query.append(" ORDER BY " + StatusConstant.COLUMN_TIME_SEND + " ASC");
    Cursor cursor = mSqLiteDatabase.rawQuery(query.toString(), null);
    List<MessageInDB> messages = new ArrayList<MessageInDB>();
    MessageInDB messageInDb;
    if (cursor != null && cursor.moveToFirst()) {
      do {
        messageInDb = genMessageFromCursor(cursor);
        messages.add(messageInDb);
        LogUtils.d(StatusConstant.TAG, "query message with status "
            + StatusConstant.getStatus(messageInDb.getStatus())
            + ": " + messageInDb);
      } while (cursor.moveToNext());
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return messages;
  }

  /**
   * Clear all success message from database
   */
  synchronized void clearMessageSuccess() {
    String whereClauses = StatusConstant.COLUMN_STATUS + "=?";
    String[] args = new String[]{String
        .valueOf(StatusConstant.STATUS_SUCCESS)};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, whereClauses, args);
    String[] argsRead = new String[]{String
        .valueOf(StatusConstant.STATUS_READ)};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, whereClauses,
        argsRead);
    LogUtils.d(StatusConstant.TAG, "clear message sent success");
  }

  /**
   * Clear all message in database with user
   */
  synchronized void clearMessageFromId(String userId) {
    String whereClauses = StatusConstant.COLUMN_FROM + "=?";
    String[] args = new String[]{String.valueOf(String.valueOf(userId))};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, whereClauses, args);

    whereClauses = StatusConstant.COLUMN_TO + "=?";
    args = new String[]{String.valueOf(String.valueOf(userId))};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, whereClauses, args);
    LogUtils.d(StatusConstant.TAG,
        "clear message from: " + String.valueOf(userId));
  }

  /**
   * Clear all my message
   */
  synchronized void clearMyMessage() {
    String whereClauses = StatusConstant.COLUMN_FROM + "!=?";
    String[] args = new String[]{};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, null, null);
    LogUtils.d(StatusConstant.TAG, "clear message my success");
  }

  synchronized List<MessageInDB> checkResend(long time) {
    String selections = StatusConstant.COLUMN_TIME_SEND + "<=? AND "
        + StatusConstant.COLUMN_STATUS + "=?";
    String[] selecArgs = new String[]{String.valueOf(time),
        String.valueOf(StatusConstant.STATUS_RETRY)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    List<MessageInDB> messages = new ArrayList<MessageInDB>();
    MessageInDB message;
    if (cursor != null && cursor.moveToFirst()) {
      do {
        message = genMessageFromCursor(cursor);
        messages.add(message);
      } while (cursor.moveToNext());
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
    return messages;
  }

  synchronized void changeStartToRetry(long time) {
    String selections = StatusConstant.COLUMN_TIME_START + "<=? AND "
        + StatusConstant.COLUMN_STATUS + "=?";
    String[] selecArgs = new String[]{String.valueOf(time),
        String.valueOf(StatusConstant.STATUS_START)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      do {
        String id = cursor.getString(0);
        int type = cursor.getInt(5);
        if (type == StatusConstant.MSG_TYPE_FILE) {
          updateStatus(id, StatusConstant.STATUS_ERROR);
        } else {
          updateStatus(id, StatusConstant.STATUS_RETRY);
        }
      } while (cursor.moveToNext());
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
  }

  synchronized void changeRetryToError(long time) {
    String selections = StatusConstant.COLUMN_TIME_START + "<=? AND "
        + StatusConstant.COLUMN_STATUS + "=?";
    String[] selecArgs = new String[]{String.valueOf(time),
        String.valueOf(StatusConstant.STATUS_RETRY)};
    Cursor cursor = mSqLiteDatabase.query(StatusConstant.TABLE_STATUS,
        null, selections, selecArgs, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      do {
        String id = cursor.getString(0);
        updateStatus(id, StatusConstant.STATUS_ERROR);
      } while (cursor.moveToNext());
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
  }

  /**
   * Resend for file message
   */
  synchronized void changeFileErrorToResend(MessageInDB msgInDB) {
    if (msgInDB.getType() != StatusConstant.MSG_TYPE_FILE) {
      return;
    }
    // delete old message
    String whereClauses = StatusConstant.COLUMN_ID + "=?";
    String[] args = new String[]{msgInDB.getId()};
    mSqLiteDatabase.delete(StatusConstant.TABLE_STATUS, whereClauses, args);
    // implement resend from ChatFragment
    for (IStatusChatChanged dbChange : dbChangeListener) {
      dbChange.resendFile(msgInDB);
    }
  }

  private MessageInDB genMessageFromCursor(Cursor cursor) {
    MessageInDB message = new MessageInDB();
    message.setId(cursor.getString(0));
    message.setTimeStart(cursor.getLong(1));
    message.setTimeSend(cursor.getLong(2));
    message.setFrom(cursor.getString(3));
    message.setTo(cursor.getString(4));
    message.setType(cursor.getInt(5));
    message.setValue(cursor.getString(6));
    message.setStatus(cursor.getInt(7));
    message.setChatClientId(cursor.getString(8));
    message.setUploadId(cursor.getLong(8));
    message.setChatClientId(cursor.getString(9));
    message.setFileId(cursor.getString(10));
    message.setFileType(cursor.getString(11));
    message.setFilePath(cursor.getString(12));
    message.setAudioTime(cursor.getLong(13));
    return message;
  }

  void addDbChangeListener(IStatusChatChanged listener) {
    if (!dbChangeListener.contains(listener)) {
      dbChangeListener.add(listener);
    }
  }

  void removeDbChangeListener(IStatusChatChanged listener) {
    dbChangeListener.remove(listener);
  }

}
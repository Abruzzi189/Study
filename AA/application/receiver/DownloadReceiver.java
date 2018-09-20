package com.application.receiver;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.application.util.preferece.DownloadFileTempPrefers;

public class DownloadReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    // Neu download file thanh cong, thi luu lai mapping giua file download
    // thanh cong va messageId
    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
      long downloadId = intent.getLongExtra(
          DownloadManager.EXTRA_DOWNLOAD_ID, -1);
      if (downloadId == -1) {
        return;
      }
      DownloadManager downloadManager = (DownloadManager) context
          .getSystemService(Context.DOWNLOAD_SERVICE);
      Query query = new Query();
      query.setFilterById(downloadId);
      Cursor cursor = downloadManager.query(query);
      if (cursor != null && cursor.getCount() > 0) {
        try {
          // Lay thong tin luu trong cursor ra.
          cursor.moveToFirst();
          int status = cursor.getInt(cursor
              .getColumnIndex(DownloadManager.COLUMN_STATUS));
          if (status == DownloadManager.STATUS_SUCCESSFUL) {
            String path = cursor
                .getString(cursor
                    .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            path = Uri.parse(path).getPath();
            DownloadFileTempPrefers prefers = new DownloadFileTempPrefers(
            );
            prefers.mapMessageIdAndFilePath(downloadId, path);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
      if (cursor != null) {
        cursor.close();
        cursor = null;
      }
    }
  }
}

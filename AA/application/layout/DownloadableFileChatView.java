package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import com.application.chat.ChatMessage;
import com.application.downloadmanager.DownloadState;
import com.application.ui.ChatFragment;
import com.application.util.LogUtils;
import glas.bbsystem.R;


public class DownloadableFileChatView extends BaseFileChatView {

  private ProgressBar mDownloadProgress;

  public DownloadableFileChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  public DownloadableFileChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mDownloadProgress = (ProgressBar) findViewById(R.id.progress);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    if (!chatMessage.isOwn()) {
      LogUtils.d(TAG, String.format(
          "fillData, messageId=%s, downloadState=%s, percent=%s",
          chatMessage.getMessageId(), mFileMessage.downloadState,
          mFileMessage.downloadProgress));
      switch (mFileMessage.downloadState) {
        case DownloadState.RUNNING:
          mDownloadProgress.setVisibility(View.VISIBLE);
          mDownloadProgress.setIndeterminate(false);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.PENDING:
          mDownloadProgress.setVisibility(View.VISIBLE);
          mDownloadProgress.setIndeterminate(true);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.SUCCESSFUL:
          mDownloadProgress.setVisibility(View.GONE);
          mDownloadProgress.setIndeterminate(false);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.FAILED:
          // download error
          warning.setVisibility(View.VISIBLE);
          LogUtils.d(TAG, "reason=" + mFileMessage.errorReason);
          break;
        default:
          mDownloadProgress.setVisibility(View.GONE);
          warning.setVisibility(View.GONE);
          break;
      }
      // (3) display downloading
      mDownloadProgress.setProgress(mFileMessage.downloadProgress);
    } else {
      switch (mFileMessage.downloadState) {
        case DownloadState.RUNNING:
          uploadProgress.setVisibility(View.VISIBLE);
          uploadProgress.setIndeterminate(false);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.PENDING:
          uploadProgress.setVisibility(View.VISIBLE);
          uploadProgress.setIndeterminate(true);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.SUCCESSFUL:
          uploadProgress.setVisibility(View.GONE);
          uploadProgress.setIndeterminate(false);
          warning.setVisibility(View.GONE);
          break;
        case DownloadState.FAILED:
          // download error
          warning.setVisibility(View.VISIBLE);
          LogUtils.d(TAG, "reason=" + mFileMessage.errorReason);
          break;
        default:
          uploadProgress.setVisibility(View.GONE);
          warning.setVisibility(View.GONE);
          break;
      }
      // (3) display downloading
      uploadProgress.setProgress(mFileMessage.downloadProgress);
    }
  }

}

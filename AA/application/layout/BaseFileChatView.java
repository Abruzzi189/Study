package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import com.application.chat.ChatMessage;
import com.application.chat.FileMessage;
import com.application.status.StatusConstant;
import com.application.ui.ChatFragment;
import com.application.uploadmanager.UploadState;
import glas.bbsystem.R;


public class BaseFileChatView extends BaseChatView {

  protected ProgressBar uploadProgress;
  protected FileMessage mFileMessage;

  public BaseFileChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  public BaseFileChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    uploadProgress = (ProgressBar) findViewById(R.id.progress);
  }

  @Override
  public void fillData(final ChatFragment chatFragment,
      ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    mFileMessage = chatMessage.getFileMessage();
    // update: don't show warning, use common status of message
    if (!chatMessage.isEnoughPointToSend()) {
      setUploadVisibility(View.GONE);
      return;
    }
    if (chatMessage.isOwn()) {
      boolean hasUploadState = false;
      // (1) update upload state
      switch (mFileMessage.uploadState) {
        case UploadState.INITIAL:
          setUploadVisibility(View.VISIBLE);
          uploadProgress.setProgress(mFileMessage.uploadProgress);
          hasUploadState = true;
          break;
        case UploadState.SUCCESSFUL:
          setUploadVisibility(View.GONE);
          hasUploadState = true;
          break;
        case UploadState.RUNNING:
          setUploadVisibility(View.VISIBLE);
          uploadProgress.setProgress(mFileMessage.uploadProgress);
          hasUploadState = true;
          break;
        case UploadState.FAILED:
          setUploadVisibility(View.GONE);
          hasUploadState = true;
          break;
        case UploadState.CANCEL:
          setUploadVisibility(View.GONE);
          hasUploadState = true;
          break;
        default:
          hasUploadState = false;
          setUploadVisibility(View.GONE);
          break;
      }
      // (2) check isStartSent
      if (mFileMessage.isStartSent()) {
        if (!hasUploadState) {
          setUploadVisibility(View.GONE);
        } else {
          if (mFileMessage.uploadState == UploadState.FAILED
              || mFileMessage.uploadState == UploadState.CANCEL
              || chatMessage.isTimeOutForFileMessage()) {
            setUploadVisibility(View.GONE);
            mErrorStatusView.setVisibility(View.VISIBLE);
          } else {
            if(mFileMessage.uploadState==UploadState.SUCCESSFUL){
              setUploadVisibility(View.GONE);
            }else
              setUploadVisibility(View.VISIBLE);
          }
        }
      } else {
        // do nothing
      }
      // (3) check message status
      switch (chatMessage.getStatusSend()) {
        case StatusConstant.STATUS_ERROR:
          setUploadVisibility(View.GONE);
          break;
        default:
          break;
      }

    } else {
      // (1) not need update upload state

      // (2) check isStartSent
      if (mFileMessage.isStartSent()) {
        // hide all views upload failed, uploading
        hideChildViews();
      } else {
        showChildViews();
      }
    }

  }

  private void setUploadVisibility(int visibility) {
    uploadProgress.setVisibility(visibility);
    mSendingStatusView.setVisibility(visibility);
  }
}
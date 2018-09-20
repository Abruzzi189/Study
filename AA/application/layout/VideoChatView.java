package com.application.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.connection.request.ImageRequestWithSize;
import com.application.connection.request.VideoThumbRequest;
import com.application.ui.ChatFragment;
import com.application.ui.chat.ChatAdapter.IOnGetVideoURL;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import glas.bbsystem.R;


public class VideoChatView extends BaseFileChatView {

  private int mVideoPhotoSize;
  private ImageView thumbVideo;
  private ProgressBar progress;
  private View videoLayout;
  private TextView txtUserName;
  private IOnGetVideoURL onGetVideoURL;

  public VideoChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initData(context);
  }

  public VideoChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
    initData(context);
  }

  public VideoChatView(Context context, int contentLayout,
      boolean isSendMessage, IOnGetVideoURL onGetVideoURL) {
    super(context, contentLayout, isSendMessage);
    this.onGetVideoURL = onGetVideoURL;
    initData(context);
  }

  private void initData(Context context) {
    mVideoPhotoSize = getResources().getDimensionPixelSize(
        R.dimen.item_list_chat_thumb_size);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    thumbVideo = (ImageView) findViewById(R.id.image);
    progress = (ProgressBar) findViewById(R.id.progressView);
    videoLayout = findViewById(R.id.rootLayout);
    txtUserName = (TextView) findViewById(R.id.name);
  }

  @Override
  public void fillData(final ChatFragment chatFragment,
      final ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    final Context appContext = chatFragment.getActivity()
        .getApplicationContext();
    // found video path
    String path = mFileMessage.getFilePath();
    if (!TextUtils.isEmpty(path)) {
      LogUtils.i(TAG, "File path: " + String.valueOf(path));
      progress.setVisibility(View.GONE);

      Glide.with(appContext)
          .load(path)
          .into(thumbVideo);
//      VideoThumbRequest thumbRequest = new VideoThumbRequest(path);
//      chatFragment.getImageFetcher().loadImageWithoutPlaceHolder(
//          thumbRequest, thumbVideo, mVideoPhotoSize);
    } else {
      String fileId = mFileMessage.getFileId();
      if (!TextUtils.isEmpty(fileId)) {
        LogUtils.i(TAG, "File ID: " + String.valueOf(fileId));
        progress.setVisibility(View.GONE);
        String android = UserPreferences.getInstance().getToken();
        ImageRequestWithSize imageRequestWithSize = new ImageRequestWithSize(
            android, fileId, mVideoPhotoSize);
        Picasso.with(appContext).load(imageRequestWithSize.toURL())
            .into(thumbVideo);
      } else {
        progress.setVisibility(View.GONE);
        thumbVideo.setImageResource(android.R.color.transparent);
      }

    }

    videoLayout.setOnClickListener(v -> {
      String id = mFileMessage.getFileId();

      if (chatMessage.isFileDelete()) {
        chatFragment.showAlertFileDeleteDialog();
        return;
      }
      //Only play video after uploaded and server ready to play
      if (id != null && id.length() > 0) {
        if (onGetVideoURL != null) {
          onGetVideoURL.onGetURL(chatMessage, id, chatMessage.isOwn(), chatMessage.isExpired());
        }
      }else {
        String filePath = mFileMessage.getFilePath();
        if (!TextUtils.isEmpty(filePath)) {
          if (onGetVideoURL != null) {
            onGetVideoURL.onFilePath(filePath);
          }
        } else {
          if (onGetVideoURL != null) {
            onGetVideoURL.onGetURLError();
          }
        }
      }
    });
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }
}

package com.application.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.connection.request.ImageRequestWithSize;
import com.application.connection.request.PhotoThumbRequest;
import com.application.imageloader.ImageWorker.ImageListener;
import com.application.ui.ChatFragment;
import com.application.ui.chat.ChatAdapter.IOnOpenImage;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class PhotoChatView extends BaseFileChatView {

  private ImageView imageView;
  private ProgressBar progressView;
  private TextView txtUserName;
  private IOnOpenImage onOpenImage;
  private int mPhotoSize;

  public PhotoChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initData(context);
  }

  public PhotoChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
    initData(context);
  }

  public PhotoChatView(Context context, int contentLayout,
      boolean isSendMessage, IOnOpenImage onOpenImage) {
    super(context, contentLayout, isSendMessage);
    initData(context);
    this.onOpenImage = onOpenImage;
  }

  public PhotoChatView(Context context) {
    super(context, null);
    initData(context);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    imageView = (ImageView) findViewById(R.id.image);
    progressView = (ProgressBar) findViewById(R.id.progressView);
    txtUserName = (TextView) findViewById(R.id.name);
  }

  private void initData(Context context) {
    mPhotoSize = getResources().getDimensionPixelSize(
        R.dimen.item_list_chat_thumb_size);
  }

  @Override
  public void fillData(final ChatFragment chatFragment,
      final ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);
    if (!TextUtils.isEmpty(mFileMessage.getFilePath())) {
      progressView.setVisibility(View.VISIBLE);
      PhotoThumbRequest thumbRequest = new PhotoThumbRequest(
          mFileMessage.getFilePath());
      chatFragment.getImageFetcher().loadImageWithoutPlaceHolder(
          thumbRequest, imageView, mPhotoSize, new ImageListener() {
            @Override
            public void onGetImageSuccess(Bitmap bitmap) {
              progressView.setVisibility(View.GONE);
            }

            @Override
            public void onGetImageFailure() {
              progressView.setVisibility(View.VISIBLE);
            }
          });
      imageView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onOpenImage != null) {
            String msgId = mFileMessage.getFileId();
            if (msgId != null) {
              onOpenImage.onImageClick(chatMessage, msgId, chatMessage.isOwn(),chatMessage.isExpired());
            }
          }
        }

      });

    } else if (!TextUtils.isEmpty(mFileMessage.getFileId())) {
      progressView.setVisibility(View.VISIBLE);
      String token = UserPreferences.getInstance().getToken();
      ImageRequestWithSize imageRequestWithSize = new ImageRequestWithSize(
          token, mFileMessage.getFileId(), mPhotoSize);
      chatFragment.getImageFetcher().loadImageWithoutPlaceHolder(
          imageRequestWithSize, imageView, mPhotoSize,
          new ImageListener() {

            @Override
            public void onGetImageSuccess(Bitmap bitmap) {
              progressView.setVisibility(View.GONE);
            }

            @Override
            public void onGetImageFailure() {
              progressView.setVisibility(View.VISIBLE);
            }
          });

      imageView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onOpenImage != null) {
            String msgId = mFileMessage.getFileId();
            onOpenImage.onImageClick(chatMessage, msgId, chatMessage.isOwn(),chatMessage.isExpired());
          }
        }
      });
    } else {
      progressView.setVisibility(View.GONE);
      imageView.setImageResource(android.R.color.transparent);
      // must set this
      imageView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        }
      });
    }

    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }
}

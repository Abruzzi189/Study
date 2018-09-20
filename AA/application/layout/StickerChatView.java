package com.application.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.connection.request.ImageRequest;
import com.application.ui.ChatFragment;
import com.application.ui.customeview.EmojiTextView;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import glas.bbsystem.R;
import java.io.File;


public class StickerChatView extends BaseChatView {

  private ImageView stickerView;
  private EmojiTextView errorText;
  private TextView txtUserName;

  public StickerChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StickerChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    stickerView = (ImageView) findViewById(R.id.image);
    errorText = (EmojiTextView) findViewById(R.id.title);
    txtUserName = (TextView) findViewById(R.id.name);

    if (stickerView == null || errorText == null) {
      return;
    }

    float size = getResources().getDimension(R.dimen.item_list_chat_gift_size);
    ViewGroup.LayoutParams stickerLayoutParam = stickerView.getLayoutParams();
    stickerLayoutParam.width = (int) size;
    stickerLayoutParam.height = (int) size;

    int maxSize = (Utility.getScreenWidth(getContext()) * 3) / 4;
    stickerView.setMaxWidth(maxSize);
    stickerView.setMaxHeight(maxSize);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);

    // Get image content
    String mediaContent = chatMessage.getContent();
    LogUtils.d(TAG, "mediaContent=" + mediaContent);

    // Get application context
    Context appContext = chatFragment.getActivity().getApplicationContext();

    // default: all media has .png
    String pathImage = "";
    if (!mediaContent.contains(ChatUtils.IMG_EXTENSION)) {
      pathImage = ChatUtils.getPathStickerByPackageAndId(appContext,
          mediaContent + ChatUtils.IMG_EXTENSION);
    } else {
      pathImage = ChatUtils.getPathStickerByPackageAndId(appContext,
          mediaContent);
    }
    LogUtils.d(TAG, "Path media=" + pathImage);

    try {
      File file = new File(pathImage);
      if (file.canRead()) {
//				Picasso.with(appContext).load(file).into(stickerView);
        // TODO: 3/9/2018 http://10.64.100.201/issues/11544 fixed by ThoNH
        Glide.with(appContext).load(file).into(new SimpleTarget<Drawable>() {
          @Override
          public void onResourceReady(@NonNull Drawable resource,
              @Nullable Transition<? super Drawable> transition) {
            stickerView.setImageDrawable(resource);
          }
        });

      } else {
        String token = UserPreferences.getInstance().getToken();
        final ImageRequest request = new ImageRequest(token,
            ChatUtils.getStickerId(mediaContent),
            ImageRequest.STICKER);
//				Picasso.with(appContext).load(request.toURL())
//						.into(stickerView);
        // TODO: 3/9/2018 http://10.64.100.201/issues/11544 fixed by ThoNH
        Log.e("ThoNH111", "url:" + request.toURL());

        Glide.with(appContext).load(request.toURL()).into(new SimpleTarget<Drawable>() {
          @Override
          public void onResourceReady(@NonNull Drawable resource,
              @Nullable Transition<? super Drawable> transition) {
            stickerView.setImageDrawable(resource);
          }
        });

      }
    } catch (Exception exception) {
      LogUtils.e(TAG, "Fail load media to view");
      stickerView.setVisibility(View.GONE);
      errorText.setVisibility(View.VISIBLE);
      errorText.setEmojiText(appContext
          .getString(R.string.not_found_sticker));
    }

    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }
}
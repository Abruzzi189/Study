package com.application.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.connection.request.ImageRequest;
import com.application.ui.ChatFragment;
import com.application.util.preferece.UserPreferences;
import com.squareup.picasso.Picasso;
import glas.bbsystem.R;
import java.text.MessageFormat;


public class GiftChatView extends BaseChatView {

  private ImageView imageGif;
  private TextView messageGif;
  private TextView txtUserName;

  public GiftChatView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GiftChatView(Context context, int contentLayout,
      boolean isSendMessage) {
    super(context, contentLayout, isSendMessage);
  }

  @Override
  public void onFindId() {
    super.onFindId();
    imageGif = (ImageView) findViewById(R.id.image);
    messageGif = (TextView) findViewById(R.id.message);
    txtUserName = (TextView) findViewById(R.id.name);
    if (imageGif == null || messageGif == null) {
      return;
    }
    imageGif.setMaxWidth(mMaxWidthContent / 2);
    messageGif.setMaxWidth(mMaxWidthContent / 2);
  }

  @Override
  public void fillData(ChatFragment chatFragment, ChatMessage chatMessage) {
    super.fillData(chatFragment, chatMessage);

    String value = chatMessage.getContent();
    String split[] = value.split("\\|");
    String giftId = split[0];
    String display = "";

    int point = 0;
    if (split != null && split.length >= 4) {
      point = Integer.valueOf(split[3]);
    }

    Context appContext = chatFragment.getActivity().getApplicationContext();
    String token = UserPreferences.getInstance().getToken();
    ImageRequest giftRequest = new ImageRequest(token, giftId,
        ImageRequest.GIFT);
    // getImageFetcher().loadImageWithoutPlaceHolder(giftRequest,
    // holder.imgGift, mGiftSize);
    Picasso.with(appContext).load(giftRequest.toURL()).into(imageGif);
    String nameUserToSend;
    String currentUserName;
    nameUserToSend = chatFragment.getNameUserToSend();
    currentUserName = chatFragment.getUserName();

    if (chatMessage.isOwn()) {
      String format = getResources().getString(
          R.string.send_gift_price_not_free);
      String pointStr = MessageFormat.format(format, point);

      display = appContext.getString(
          R.string.gift_message_display_format, currentUserName,
          nameUserToSend, pointStr);
    } else {
      String format = getResources().getString(
          R.string.send_gift_price_not_free);
      String pointStr = MessageFormat.format(format, point);

      display = appContext.getString(
          R.string.gift_message_display_format, nameUserToSend,
          currentUserName, pointStr);
    }
    messageGif.setText(display);
    if (!chatMessage.isOwn()) {
      txtUserName.setText(getUserName());
    }
  }

}

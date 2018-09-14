package com.example.thangpham.testfirebasenhap.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.widget.TextView;

public class EmojiTextView extends TextView {

  private Context mContext;
  private ImageGetter emojiGetter = new ImageGetter() {

    @Override
    public Drawable getDrawable(String source) {
      if (source == null) {
        return getDefaultDrawable();
      }
      int id = getResources().getIdentifier(source, "drawable",
          mContext.getPackageName());
      Drawable emoji = getResources().getDrawable(id);
      if (emoji != null) {
        int w = (int) (emoji.getIntrinsicWidth() * 0.8);
        int h = (int) (emoji.getIntrinsicHeight() * 0.8);
        emoji.setBounds(0, 0, w, h);
      } else {
        emoji = getDefaultDrawable();
      }
      return emoji;
    }

    public Drawable getDefaultDrawable() {
      return new ColorDrawable(Color.TRANSPARENT);
    }
  };

  public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    setCompoundDrawablePadding(5);
  }

  public EmojiTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setCompoundDrawablePadding(5);
  }

  public EmojiTextView(Context context) {
    super(context);
    mContext = context;
    setCompoundDrawablePadding(5);
  }

  public void setEmojiText(String text, boolean isMultiLine) {


    setText(text);

  }


  public void setEmojiText(String text) {
    setEmojiText(text, false);
  }
}

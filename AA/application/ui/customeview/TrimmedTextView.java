package com.application.ui.customeview;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class TrimmedTextView extends TextView {

  private Spanned spannedText;
  private SpannableStringBuilder builder = new SpannableStringBuilder();
  public TrimmedTextView(Context context) {
    super(context);
  }

  public TrimmedTextView(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public static CharSequence ellipsizeText(String text) {
    SpannableString s = new SpannableString(text);
    s.setSpan(TrimmedTextView.EllipsizeRange.ELLIPSIS_AT_END, 0,
        s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    return s;
  }

  public void setTextSpanned(Spanned text) {
    spannedText = text;
    setText(text);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right,
      int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    Layout layout = getLayout();
    final CharSequence text = spannedText;
    if (text instanceof Spanned) {
      Spanned spanned = (Spanned) text;
      int ellipsisStart;
      int ellipsisEnd;
      TruncateAt where = null;
      ellipsisStart = spanned
          .getSpanStart(EllipsizeRange.ELLIPSIS_AT_START);
      if (ellipsisStart >= 0) {
        where = TruncateAt.START;
        ellipsisEnd = spanned
            .getSpanEnd(EllipsizeRange.ELLIPSIS_AT_START);
      } else {
        ellipsisStart = spanned
            .getSpanStart(EllipsizeRange.ELLIPSIS_AT_END);
        if (ellipsisStart >= 0) {
          where = TruncateAt.END;
          ellipsisEnd = spanned
              .getSpanEnd(EllipsizeRange.ELLIPSIS_AT_END);
        } else {
          // No EllipsisRange spans in this text
          return;
        }
      }

      builder.clear();
      builder.append(text, 0, ellipsisStart).append(text, ellipsisEnd,
          text.length());
      float consumed = Layout.getDesiredWidth(builder, layout.getPaint());
      CharSequence ellipsisText = text.subSequence(ellipsisStart,
          ellipsisEnd);
      CharSequence ellipsizedText = TextUtils.ellipsize(ellipsisText,
          layout.getPaint(), (right - left) - consumed, where);
      if (!ellipsizedText.equals(ellipsisText)) {
        // text is ellipsized
        builder.clear();
        builder.append(text, 0, ellipsisStart).append(ellipsizedText)
            .append(text, ellipsisEnd, text.length());
        setText(builder);
        requestLayout();
        invalidate();
      }
    }
  }

  public static enum EllipsizeRange {
    ELLIPSIS_AT_START, ELLIPSIS_AT_END;
  }

}
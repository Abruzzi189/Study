package com.application.ui.customeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import glas.bbsystem.R;


public class BadgeTextView extends AutofitTextView {

  private final int MAX_NUMBER = 20;
  private final String MAX_NUMBER_STRING = "20+";
  private int minNumberToVisible = 1;

  public BadgeTextView(Context context) {
    super(context);
    init(context);
  }

  public BadgeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    setVisibility(View.INVISIBLE);
  }

  public void setMinNumberToVisible(int minValue) {
    minNumberToVisible = minValue;
  }

  public void setTextNumber(int numUnreadMessage) {
    setVisibility(View.VISIBLE);
    setBackgroundResource(R.drawable.bg_notification);
    String textShowed;
    if (numUnreadMessage >= minNumberToVisible) {
      if (numUnreadMessage > MAX_NUMBER) {
        textShowed = MAX_NUMBER_STRING;
      } else {
        textShowed = String.valueOf(numUnreadMessage);
      }
      setText(textShowed);
    } else {
      setVisibility(View.INVISIBLE);
      setBackgroundResource(android.R.color.transparent);
    }
  }
}

package com.application.call;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnSingleClickListener implements OnClickListener {

  private boolean clickable = true;

  @Override
  public synchronized void onClick(View v) {
    if (clickable) {
      clickable = false;
      onOneClick(v);
    }
  }

  public abstract void onOneClick(View view);

  public synchronized void reset() {
    clickable = true;
  }
}

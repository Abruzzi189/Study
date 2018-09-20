package com.application.ui.hotpage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class LinearHorizontalLayoutManager extends LinearLayoutManager {

  private boolean isEnable = true;

  public LinearHorizontalLayoutManager(Context context) {
    super(context);
  }

  public LinearHorizontalLayoutManager(Context context, boolean reverseLayout) {
    super(context, HORIZONTAL, reverseLayout);
  }

  public void setScrollEnable(boolean isEnable) {
    this.isEnable = isEnable;
  }

  @Override
  public boolean canScrollHorizontally() {
    return isEnable && super.canScrollHorizontally();
  }
}

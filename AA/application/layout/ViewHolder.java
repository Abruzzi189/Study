package com.application.layout;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {

  public ImageView mAvatar;
  public TextView mTxtTime;
  public ImageView mImgStatus;
  public TextView mWarning;

  public ViewHolder() {
  }

  public ViewHolder(ViewHolder viewHolder) {
    mAvatar = viewHolder.mAvatar;
    mTxtTime = viewHolder.mTxtTime;
    mImgStatus = viewHolder.mImgStatus;
    mWarning = viewHolder.mWarning;
  }

}

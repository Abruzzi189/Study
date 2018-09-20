package com.application.chat;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import glas.bbsystem.R;


public class PreviewStickerView extends RelativeLayout implements
    OnClickListener {

  private ImageView mStickerImgView;
  private OnHandleStickerListener handleStickerListener;
  private String content;

  public PreviewStickerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context);
  }

  public PreviewStickerView(Context context) {
    super(context);
    initialize(context);
  }

  private void initialize(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.view_sticker_preview, this);
    mStickerImgView = (ImageView) findViewById(R.id.sticker_img);
    mStickerImgView.setOnClickListener(this);
    findViewById(R.id.close_layout).setOnClickListener(this);
    setOnClickListener(this);
  }

  public void setContent(Uri uri, String content) {
    mStickerImgView.setImageURI(uri);
    this.content = content;
  }

  public void setHandleStickerListener(OnHandleStickerListener listener) {
    handleStickerListener = listener;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {

      case R.id.sticker_img:
        if (handleStickerListener != null) {
          handleStickerListener.sendGift(this.content);
        }
        setVisibility(View.GONE);
        break;
      case R.id.close_layout:
        setVisibility(View.GONE);
        break;
      default:
        break;
    }
  }

  public interface OnHandleStickerListener {

    public void sendGift(String content);
  }

}

package com.application.call;

public interface OnCallButtonClickListener {

  public void onMuteClicked(boolean state);

  public void onSpeackerClicked(boolean state);

  public void onEndCallClicked();
}

package com.application.call;

public interface OnVideoCallButtonClickListener extends
    OnCallButtonClickListener {

  public void onSwitchCameraClicked(boolean state);

  public void onOnOffVideoClicked(boolean state);
}

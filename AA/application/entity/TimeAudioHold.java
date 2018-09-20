package com.application.entity;

public class TimeAudioHold {

  private String display = "0:00";
  private int currentPosition = 0;

  public TimeAudioHold() {
  }

  public TimeAudioHold(String display, int currentPosition) {
    this.display = display;
    this.currentPosition = currentPosition;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public int getCurrentPosition() {
    return currentPosition;
  }

  public void setCurrentPosition(int currentPosition) {
    this.currentPosition = currentPosition;
  }

}

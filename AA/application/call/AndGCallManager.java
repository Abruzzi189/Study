package com.application.call;

public interface AndGCallManager {

  public void toggleMute();

  public void toggleSpeaker();

  public void hangup();

  public void answer();

  public void makeVoiceCall();

  public void makeVideoCall();

  public void turnOnVideo();

  public void turnOffVideo();

  public void enableSpeaker();

  public void disableSpeaker();

  public boolean isEnableSpeaker();
}

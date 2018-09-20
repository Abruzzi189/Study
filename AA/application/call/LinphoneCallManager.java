package com.application.call;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

public class LinphoneCallManager implements AndGCallManager {

  private LinphoneCall mLinphoneCall;
  private String mUserId;
  private String mUserName;
  private LinphoneCore mLinphoneCore;

  public LinphoneCallManager(String userId, String userName) {
    mLinphoneCore = LinphoneManager.getLc();
    mLinphoneCall = mLinphoneCore.getCurrentCall();
    mUserId = userId;
    mUserName = userName;

  }

  @Override
  public void toggleMute() {
    mLinphoneCore.muteMic(!mLinphoneCore.isMicMuted());
  }

  @Override
  public void toggleSpeaker() {
    mLinphoneCore.enableSpeaker(!mLinphoneCore.isSpeakerEnabled());
  }

  @Override
  public void hangup() {
    LinphoneAndG.hangUp();
  }

  @Override
  public void answer() {
    if (mLinphoneCall != null) {
      LinphoneManager.getInstance().acceptCall(mLinphoneCall);
    } else {
      LinphoneAndG.hangUp();
    }
  }

  @Override
  public void makeVoiceCall() {
    LinphoneManager.getInstance().newOutgoingCall(mUserId, mUserName);
  }

  @Override
  public void makeVideoCall() {
    LinphoneManager.getInstance().newOutgoingCall(mUserId, mUserName, true);

  }

  @Override
  public void turnOnVideo() {
    switchOnOffVideo(true);
  }

  @Override
  public void turnOffVideo() {
    switchOnOffVideo(false);
  }

  private void switchOnOffVideo(boolean enableVideo) {
    final LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
    if (call == null) {
      return;
    }
    call.enableCamera(enableVideo);
  }

  @Override
  public void enableSpeaker() {
    mLinphoneCore.enableSpeaker(true);
  }

  @Override
  public void disableSpeaker() {
    mLinphoneCore.enableSpeaker(false);
  }

  @Override
  public boolean isEnableSpeaker() {
    return mLinphoneCore.isSpeakerEnabled();
  }
}

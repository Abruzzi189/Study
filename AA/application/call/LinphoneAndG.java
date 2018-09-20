package com.application.call;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

public class LinphoneAndG {

  public static void hangUp() {
    LinphoneCore lc = LinphoneManager.getLc();
    LinphoneCall currentCall = lc.getCurrentCall();

    if (currentCall != null) {
      lc.terminateCall(currentCall);
    } else if (lc.isInConference()) {
      lc.terminateConference();
    } else {
      lc.terminateAllCalls();
    }
  }
}

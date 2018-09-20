package com.application.status;

import android.content.Context;
import com.application.util.LogUtils;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutController {

  private final static int TIME_INTERVAL_SCAN = 2 * 1000;
  private final static int TIME_TIMEOUT = 10 * 1000;
  private final static int TIME_RETRY = 30 * 1000 + TIME_TIMEOUT;

  private Timer mTimer;
  private Context mContext;

  public TimeoutController(Context context) {
    mContext = context;
  }

  private void scanTimeout() {
    long minTimeout = System.currentTimeMillis() - TIME_TIMEOUT;
    long minRetry = System.currentTimeMillis() - TIME_RETRY;
    // change status
    StatusDBManager.getInstance(mContext).changeStartToRetry(minTimeout);
    StatusDBManager.getInstance(mContext).changeRetryToError(minRetry);
  }

  public void requestTimeout() {
    LogUtils.d(StatusConstant.TAG, "start request timeout");
    long minTimeout = System.currentTimeMillis() - TIME_TIMEOUT;
    long minRetry = System.currentTimeMillis() - TIME_RETRY;
    // resend message
    List<MessageInDB> messageResends = StatusDBManager
        .getInstance(mContext).checkResend(minTimeout);
    for (MessageInDB messageInDb : messageResends) {
      StatusController.getInstance(mContext).autoResendMsg(
          messageInDb.getId());
    }
    // change status
    StatusDBManager.getInstance(mContext).changeStartToRetry(minTimeout);
    StatusDBManager.getInstance(mContext).changeRetryToError(minRetry);
  }

  public void start() {
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        scanTimeout();
      }
    };
    mTimer = new Timer("timeout controller");
    mTimer.schedule(task, 0, TIME_INTERVAL_SCAN);
  }

  public void stop() {
    mTimer.cancel();
    mTimer = null;
  }
}

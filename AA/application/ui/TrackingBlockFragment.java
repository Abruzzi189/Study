package com.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import com.application.AndGApp;
import com.application.chat.ChatManager;
import com.application.chat.MessageClient;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vn.com.ntqsolution.chatserver.pojos.message.Message;

public abstract class TrackingBlockFragment extends BaseFragment {

  private BroadcastReceiver mBlockedReceiver;

  @Override
  public void onStart() {
    super.onStart();
    registerBlockedReceiver();
  }

  @Override
  public void onStop() {
    super.onStop();
    unregisterBlockedReceiver();
  }

  private void registerBlockedReceiver() {
    mBlockedReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        MessageClient compat = (MessageClient) intent
            .getSerializableExtra(ChatManager.EXTRA_DATA);
        Message message = compat.getMessage();
        handleBlockMessage(message);
      }
    };
    IntentFilter filter = new IntentFilter(ChatManager.ACTION_MESSAGE_CMD);
    LocalBroadcastManager.getInstance(mAppContext).registerReceiver(
        mBlockedReceiver, filter);
  }

  private void unregisterBlockedReceiver() {
    if (mBlockedReceiver != null) {
      LocalBroadcastManager.getInstance(mAppContext).unregisterReceiver(
          mBlockedReceiver);
    }
  }

  private void handleBlockMessage(Message message) {
    if (getActivity() == null) {
      return;
    }
    String patternBlock = "\\Ablock&";
    Pattern ppp = Pattern.compile(patternBlock, Pattern.DOTALL);
    Matcher m = ppp.matcher(message.value);
    boolean blocked = false;

    if (m.find()) {
      blocked = true;
    }

    if (blocked) {
      if (message.from.equals(getUserIdTracking())) {
        // Navigate to Meet People screen if current in chat screen and
        // saveInstance not called
        if (AndGApp.isApplicationVisibile() && !isSaveInstanceCalled) {
          exitMeWhenBlocked();
        }
      }
    }
  }

  protected abstract String getUserIdTracking();

  protected void exitMeWhenBlocked() {
    Runnable run = new Runnable() {
      @Override
      public void run() {
        // Navigate to Meet People screen
        if (mAppContext != null) {
          ((MainActivity) baseFragmentActivity).replaceAllFragment(
              new HomeFragment(),
              MainActivity.TAG_FRAGMENT_MEETPEOPLE);
        }
      }
    };

    Handler handler = new Handler();
    handler.post(run);

    // Show right menu
    SlidingMenu sm = getSlidingMenu();
    if (sm != null) {
      sm.showSecondaryMenu();
    }
  }
}

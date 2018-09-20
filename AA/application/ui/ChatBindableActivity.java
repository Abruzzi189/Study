package com.application.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.application.service.ChatService;
import com.application.service.ChatService.LocalBinder;

public abstract class ChatBindableActivity extends BaseFragmentActivity
    implements ServiceConnection {

  protected ChatService mChatService;
  boolean mBound = false;
  private boolean mUnBindOnStop = true;

  @Override
  protected void onStart() {
    super.onStart();
    if (!mBound) {
      Intent i = new Intent(this, ChatService.class);
      bindService(i, this, Context.BIND_AUTO_CREATE);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mBound && mUnBindOnStop) {
      unbindService(this);
      mBound = false;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mBound) {
      unbindService(this);
      mBound = false;
    }
  }

  public ChatService getChatService() {
    return mChatService;
  }

  public void setUnbindChatOnStop(boolean unbindOnStop) {
    this.mUnBindOnStop = unbindOnStop;
  }

  public boolean getUnbindOnStop() {
    return mUnBindOnStop;
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    LocalBinder localBinder = (LocalBinder) service;
    mChatService = (ChatService) localBinder.getService();
    mBound = true;
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    mBound = false;
    mChatService = null;
  }
}

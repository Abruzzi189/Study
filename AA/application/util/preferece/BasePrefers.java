package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import com.application.AndGApp;


public abstract class BasePrefers {

  protected Context mContext;

  public BasePrefers() {
    mContext = AndGApp.get();
  }

  protected SharedPreferences getPreferences() {
    return mContext.getSharedPreferences(getFileNamePrefers(),
        Context.MODE_PRIVATE);
  }

  protected SharedPreferences.Editor getEditor() {
    return getPreferences().edit();
  }

  protected abstract String getFileNamePrefers();
}
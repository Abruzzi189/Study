package com.application.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.application.AndGApp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class DirtyWords {

  private static final String FILE_PREFERENCES = "andG_dirty_word";
  private static DirtyWords mDirtyWords = new DirtyWords();
  private static Context mContext;
  private final String TAG = "DirtyWords";

  public static DirtyWords getInstance(Context context) {
//		mContext = context;
    mContext = AndGApp.get();
    return mDirtyWords;
  }

  private SharedPreferences.Editor getEditor() {
    if (mContext == null) {
      mContext = AndGApp.get();
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE).edit();
  }

  public SharedPreferences getSharedPreferences() {
    if (mContext == null) {
      mContext = AndGApp.get();
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE);
  }

  public boolean saveWord(String dirtyWord) {
    return getEditor().putString(dirtyWord, "").commit();
  }

  public boolean saveAllWord(ArrayList<String> listWords) {
    boolean isSuccess = true;
    int size = listWords.size();

    for (int i = 0; i < size; i++) {
      String text = listWords.get(i);
      boolean isSaveDone = saveWord(text.toLowerCase(Locale.US));
      if (!isSaveDone) {
        isSuccess = isSaveDone;
        break;
      }
    }
    return isSuccess;
  }

  @SuppressWarnings("unchecked")
  public ArrayList<String> getAllKey() {
    Map<String, ?> keys = getSharedPreferences().getAll();
    Set<?> set = keys.entrySet();
    Iterator<?> iterator = set.iterator();
    ArrayList<String> result = new ArrayList<String>();

    while (iterator.hasNext()) {
      Map.Entry<String, ?> entry = (Entry<String, ?>) iterator.next();
      String key = (String) entry.getKey();
      result.add(key);
    }

    return result;
  }

  public boolean isContainWord(String text) {
    return getSharedPreferences().contains(text);
  }

  @SuppressWarnings("unchecked")
  public void clear() {
    Editor editor = getEditor();
    Map<String, ?> keys = getSharedPreferences().getAll();
    Set<?> set = keys.entrySet();
    Iterator<?> iterator = set.iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, ?> entry = (Entry<String, ?>) iterator.next();
      editor.remove(entry.getKey());
    }

    editor.commit();
  }
}
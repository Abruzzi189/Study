package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.application.AndGApp;
import com.application.util.LogUtils;

public class BlockUserPreferences {

  private static final String FILE_PREFERENCES = "Eazy.block.user.preference";
  private static final String KEY_BLOCKED_USERS_LIST = "blocked_users_list";
  private static String TAG = "BlockUserPreferences";
  private static BlockUserPreferences mPreferences = new BlockUserPreferences();
  private static Context mContext;
  private final String COMMA = ",";

  protected BlockUserPreferences() {
  }

  public static BlockUserPreferences getInstance() {
    mContext = AndGApp.get();
    return mPreferences;
  }

  private SharedPreferences.Editor getEditor() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE).edit();
  }

  private SharedPreferences getSharedPreferences() {
    if (mContext == null) {
      return null;
    }
    return mContext.getSharedPreferences(FILE_PREFERENCES,
        Context.MODE_PRIVATE);
  }

  public void registerOnChange(OnSharedPreferenceChangeListener listener) {
    if (listener != null) {
      getSharedPreferences().registerOnSharedPreferenceChangeListener(
          listener);
    }
  }

  public void unregisterOnChange(OnSharedPreferenceChangeListener listener) {
    if (listener != null) {
      getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
          listener);
    }
  }

  public void apply() {
    getEditor().apply();
  }

  public void clear() {
    getEditor().clear().commit();
  }

  // ====== ====== List block user ===== ======
  public boolean saveBlockedUsersList(String listUser) {
    LogUtils.d(TAG, "List user blocked: " + listUser);
    return getEditor().putString(KEY_BLOCKED_USERS_LIST, listUser).commit();
  }

  public String getBlockedUsersList() {
    return getSharedPreferences().getString(KEY_BLOCKED_USERS_LIST, "");
  }

  public boolean insertBlockedUser(String userId) {
    String blockedUsersList = getBlockedUsersList();
    boolean result = false;
    if (blockedUsersList != null && blockedUsersList.length() > 0) {
      if (blockedUsersList.contains(userId)) {
        result = false;
      } else {
        blockedUsersList = blockedUsersList + ",\"" + userId + "\"";
        result = saveBlockedUsersList(blockedUsersList);
      }
    } else {
      result = saveBlockedUsersList("\"" + userId + "\"");
    }

    return result;
  }

  public boolean removeBlockedUser(String userId) {
    String blockedUsersList = getBlockedUsersList();
    boolean result = false;
    StringBuilder builder = new StringBuilder();
    if (blockedUsersList != null && blockedUsersList.length() > 0) {
      if (blockedUsersList.contains(userId)) {
        String[] blockedList = blockedUsersList.split(COMMA);
        for (int i = 0; i < blockedList.length; i++) {
          if (!blockedList[i].contains(userId)) {
            builder.append(blockedList[i]);
            builder.append(COMMA);
          }
        }

        String tmp = builder.toString();
        int length = tmp.length();
        if (tmp.length() > 0) {
          int startIndex = 0;
          int endIndex = length - 1;
          tmp = tmp.substring(startIndex, endIndex);
        }
        result = saveBlockedUsersList(tmp);
      }
    }
    return result;
  }
}
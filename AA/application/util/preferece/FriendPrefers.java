package com.application.util.preferece;

import com.application.util.LogUtils;

public class FriendPrefers extends BasePrefers {

  public FriendPrefers() {
    super();
  }

  @Override
  protected String getFileNamePrefers() {
    return "friends_prefers";
  }

  public void saveFriend(String userId) {
    getEditor().putString(userId, userId).commit();
  }

  public void removeFriend(String userId) {
    getEditor().remove(userId).commit();
  }

  public void saveFriends(String[] userIdList) {
    for (int i = 0; i < userIdList.length; i++) {
      LogUtils.i("FRIEND", userIdList[i]);
      saveFriend(userIdList[i]);
    }
  }

  public boolean hasContainFriend(String userId) {
    return getPreferences().contains(userId);
  }

  public void cleverAll() {
    getEditor().clear().commit();
  }

}

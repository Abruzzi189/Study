package com.application.util.preferece;

import com.application.util.LogUtils;

public class FavouritedPrefers extends BasePrefers {

  private static final String FILE_PREFERENCES = "Eazy.favorite.preference";
  private static String TAG = "FavouritedPrefers";
  private static FavouritedPrefers favouritedPrefers;

  private FavouritedPrefers() {
    // Do nothing
  }

  public static synchronized FavouritedPrefers getInstance() {
    if (favouritedPrefers == null) {
      favouritedPrefers = new FavouritedPrefers();
    }

    return favouritedPrefers;
  }

  @Override
  protected String getFileNamePrefers() {
    return FILE_PREFERENCES;
  }

  public void saveFav(String userId) {
    getEditor().putString(userId, userId).commit();
  }

  public void removeFav(String userId) {
    getEditor().remove(userId).commit();
  }

  public void saveFavs(String[] userIdList) {
    for (int i = 0; i < userIdList.length; i++) {
      LogUtils.i(TAG, userIdList[i]);
      saveFav(userIdList[i]);
    }
  }

  public boolean hasContainFav(String userId) {
    return getPreferences().contains(userId);
  }

  public void clearAll() {
    getEditor().clear().commit();
  }
}
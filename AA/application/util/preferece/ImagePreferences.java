package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author tungdx
 */
public class ImagePreferences {

  private SharedPreferences.Editor mEditor;
  private SharedPreferences mPreferences;

  public ImagePreferences(Context context) {
    mPreferences = context.getSharedPreferences("image_prefs",
        Context.MODE_PRIVATE);
    mEditor = mPreferences.edit();
  }

  /**
   * save id of image has been deleted
   */
  public void saveImageDeleted(String imageId) {
    mEditor.putString(imageId, imageId).commit();
  }

  /**
   * check image has deleted or not
   */
  public boolean hasImageDeleted(String imageId) {
    return mPreferences.contains(imageId);
  }
}

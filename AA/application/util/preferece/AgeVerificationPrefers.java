package com.application.util.preferece;

import com.application.util.LogUtils;

public class AgeVerificationPrefers extends BasePrefers {

  public AgeVerificationPrefers() {
    super();
  }

  @Override
  protected String getFileNamePrefers() {
    return "age_verification_prefers";
  }

  public void saveAgeVerification(String userId) {
    getEditor().putString(userId, userId).commit();
  }

  public void removeAgeVerification(String userId) {
    getEditor().remove(userId).commit();
  }

  public void saveAgeVerifications(String[] userIdList) {
    for (int i = 0; i < userIdList.length; i++) {
      LogUtils.i("AVF", userIdList[i]);
      saveAgeVerification(userIdList[i]);
    }
  }

  public boolean hasContainAgeVerification(String userId) {
    return getPreferences().contains(userId);
  }

  public void clearAll() {
    getEditor().clear().commit();
  }

}

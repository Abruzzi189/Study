package com.application.util.preferece;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;
import com.application.AndGApp;
import com.application.constant.Constants;
import com.application.constant.NotificationSetting;
import com.application.entity.User;
import com.application.ui.account.AuthenticationData;
import com.application.util.LogUtils;
import com.application.util.Utility;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.linphone.LinphoneDatabase;

public class UserPreferences {

  public static final String KEY_TOKEN = "key.token";
  public static final String KEY_USER_ID = "key.user.id";
  public static final String KEY_USER_NAME = "key.user.name";
  public static final String KEY_EMAIL = "key.email";
  public static final String KEY_PASSWORD = "key.password";
  public static final String KEY_FACEBOOK_ID = "key.facebookid";
  public static final String KEY_MOCOM_ID = "key.mocom_id";
  public static final String KEY_FAMU_ID = "key.famu_id";
  public static final String KEY_AVA_ID = "key.ava.id";
  public static final String KEY_PENDING_AVA_ID = "key.pending.ava.id";
  public static final String KEY_GENDER = "key.gender";
  public static final String KEY_BIRTHDAY = "key.birthday";
  public static final String KEY_TEMP_FACEBOOK_ID = "temp_facebook_id";
  public static final String KEY_FB_AVATAR = "fb_avatar";
  public static final String KEY_EDIT_PROFILE_SHOWED = "key.edit.profile.showed";
  public static final String KEY_SHOW_NEWS_NOTIFICATIONS = "key.show.new.notifications";
  public static final String KEY_LAST_TIME_SETUP_PROFILE_DIALOG_SHOWED = "key.last.time.setup.profile.dialog.showed";
  public static final String KEY_IS_PLEASE_FILL_PROFILE_DIALOG_SHOWABLE = "key.is.please.fill.profile.dialog.showable";
  public static final String KEY_IS_UPDATE_EMAIL = "key.is.update.email";
  public static final String KEY_NUMBER_POINT = "key.number.point";
  public static final String KEY_NUMBER_FRIEND = "key.number.friend";
  public static final String KEY_NUMBER_FAVORITE = "key.number.favorite";
  public static final String KEY_NUMBER_FAVORITED_ME = "key.number.favorited.me";
  public static final String KEY_NUMBER_CONNECTION = "key.number.connection";
  public static final String KEY_NUMBER_UNREAD_MESSAGE = "key.number.unread.message";
  public static final String KEY_NUMBER_NOTIFICATION = "key.number.notification";
  public static final String KEY_LASTTIME_UPDATE_LOCATION = "key.last.time.get.location";
  public static final String KEY_IS_AUTO_DETECT_REGION = "key.is.auto.detect.region";
  public static final String KEY_AGE_VERIFICATION = "key.age.verification";
  public static final String KEY_FINISH_REGISTER_FLAG = "key.finish.register.flag";
  public static final String KEY_CURRENT_CHAT_FRIEND_ID = "key.current.chat.friend.id";
  public static final String KEY_CURRENT_MSG_CHAT_ID = "key.current.msg.chat.id";
  public static final String KEY_IS_ENABLE_VOICE_CALL = "is.enable.voice_call";
  public static final String KEY_IS_ENABLE_VIDEO_CALL = "is.enable.video_call";
  public static final String KEY_CHAT_NOTIFICATION = "chat_notification";
  public static final String KEY_IN_CALLING_PROCESS = "in_calling_process";
  public static final String KEY_IN_RECORDING_PROCESS = "in_recording_process";
  public static final String KEY_CALLING_USER_ID = "calling_user_id";
  public static final String KEY_STARTED_CALL_MESSAGE_ID = "started_call_message_id";
  public static final String KEY_EDIT_PROFILE_FIRST_TIME = "edit_profile_first_time";
  public static final String KEY_UNLOCK_WHO_CHECK_ME_OUT = "key.unlock.who.check.me.out";
  public static final String KEY_UNLOCK_FAVORITE = "key.unlock.favorite";
  public static final String KEY_WINK_BOMB_NUMBER = "key.wink.bomb.number";
  public static final String KEY_WINK_MESSAGE_INDEX = "key.wink.message.index";
  public static final String KEY_SOUND_SETTING = "sound_setting";
  public static final String KEY_VIBRATION_SETTING = "vibration_setting";
  public static final String KEY_INVITE_URL = "invite_url";
  public static final String KEY_INVITE_CODE = "invite_code";
  public static final String KEY_VIDEO_CAPTION_LOCATION = "video_caption_location";
  public static final String KEY_LOGOUT = "log_out";
  public static final String KEY_IS_NEWLY_ACCOUNT = "is.newly.account";
  public static final String KEY_IS_SHOW_NEWS = "is.show.news";
  public static final String KEY_DATE_SETTING_SHOW_NEWS = "date.setting.show.news";
  public static final String KEY_SHOW_NEWS_POPUP = "key.show.news.popup";
  public static final String KEY_LAST_TIME_CHAT_CHECK_AVATAR_DIALOG_SHOWED = "key.last.time.chat.check.avatar.dialog.showed";
  public static final String KEY_REVIEW_AVATAR = "key.review.avatar";
  private static final String FILE_PREFERENCES = "User.preference";
  // ====== ====== Video video location ===== ======
  public static int TOP_LEFT = 1;
  public static int TOP_RIGHT = 2;
  public static int BOTTOM_RIGHT = 3;
  public static int BOTTOM_LEFT = 4;
  private static String TAG = "UserPreferences";
  private static UserPreferences mPreferences = new UserPreferences();
  private static Context mContext = AndGApp.get();
  // ====== ====== Pending avatar Id ===== ======
  private final String SPLIT = "/";

  protected UserPreferences() {
  }

  public static UserPreferences getInstance() {
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
    LinphoneDatabase.getInstance(mContext).clear();
    getEditor().clear().commit();
  }

  public User getMe() {
    User me = new User();
    me.setAvatar(getAvaId());
    me.setEmail(getEmail());
    if (getFacebookId() != null) {
      me.setAnotherSystemId(getFacebookId());
    } else if (getMocomId() != null) {
      me.setAnotherSystemId(getMocomId());
    }
    me.setGender(getGender());
    me.setName(getUserName());
    me.setPassword(getPassword());

    // Birthday
    String birtString = getBirthday();
    SimpleDateFormat format = new SimpleDateFormat(
        Constants.DATE_FORMAT_SEND_TO_SERVER, Locale.getDefault());
    Date date = null;
    try {
      date = format.parse(birtString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    me.setBirthday(date);

    return me;
  }

  public void saveAuthentication(String email, String password) {
    saveEmail(email);
    savePassword(password);
  }

  public void saveSuccessLoginData(AuthenticationData authenData,
      boolean isFirstLogin) {
    if (authenData == null) {
      return;
    }
    // Toogle logout flag
    setIsLogout(false);

    // Basic data
    String token = authenData.getToken();
    String userName = authenData.getUserName();
    String userId = authenData.getUserId();
    String avartarId = authenData.getAvartarId();
    int gender = authenData.getGender();
    int numFriend = authenData.getNumFriend();
    int numFavourite = authenData.getNumFavourite();
    int numFavouriteMe = authenData.getNumFavouriteMe();
    int numMyChat = authenData.getNumMyChat();
    int numNotification = authenData.getNumNotification();
    int numPoint = authenData.getNumPoint();
    String inviteUrl = authenData.getInviteUrl();
    boolean isEnableVoice = authenData.isEnableVoice();
    boolean isEnableVideo = authenData.isEnableVideo();
    saveBasicData(token, userName, userId, avartarId, gender, numFriend,
        numFavourite, numFavouriteMe, numMyChat, numNotification,
        numPoint, inviteUrl, isEnableVoice, isEnableVideo);

    int finishRegister = authenData.getFinishRegister();
    int verificationFlag = authenData.getVerificationFlag();
    boolean isUpdateEmail = authenData.isUpdateEmail();

    saveUpdateEmail(isUpdateEmail);
    saveFinishRegister(finishRegister);
    saveAgeVerification(verificationFlag);

    // Check login be re-login or sign up
    setShowNewNotifications(isFirstLogin);
    if (isFirstLogin) {
      setShowNewsPopup(authenData.isShowPopupNews());
    }
  }

  public void saveBasicData(String token, String name, String id,
      String avatar, int gender, int numOfFriend, int numOfFavorite,
      int numFavoritedMe, int unreadMessage, int numberNotify,
      int numberPoint, String inviteURL, boolean isEnableVoice,
      boolean isEnableVideo) {
    if (!TextUtils.isEmpty(token)) {
      saveToken(token);
    }
    if (!TextUtils.isEmpty(id)) {
      saveUserId(id);
    }
    saveUserName(name);
    saveAvaId(avatar);
    saveGender(gender);
    saveNumberConnection(numOfFriend, numOfFavorite);
    saveNumberFavoritedMe(numFavoritedMe);
    saveNumberUnreadMessage(unreadMessage);
    saveNumberNotification(numberNotify);
    saveNumberPoint(numberPoint);
    saveInviteUrl(inviteURL);
    saveEnableVoiceCall(isEnableVoice);
    saveEnableVideoCall(isEnableVideo);
  }

  // ====== ====== Token ===== ======
  public boolean saveToken(String token) {
    LogUtils.d(TAG, "Token: " + token);
    return getEditor().putString(KEY_TOKEN, token).commit();
  }

  public String getToken() {
    return getSharedPreferences().getString(KEY_TOKEN, "");
  }

  // ====== ====== User ID ===== ======
  public boolean saveUserId(String userId) {
    LogUtils.d(TAG, "User id: " + userId);
    LinphoneDatabase.getInstance(mContext).saveUserId(userId);
    return getEditor().putString(KEY_USER_ID, userId).commit();
  }

  public String getUserId() {
    return getSharedPreferences().getString(KEY_USER_ID, "");
  }

  // ====== ====== User name ===== ======
  public void saveUserName(String userName) {
    LogUtils.d(TAG, "User name: " + userName);

    // fix #12818 do not change if user name is null or empty
    if (!TextUtils.isEmpty(userName)) {
      getEditor().putString(KEY_USER_NAME, userName).commit();
    }
  }

  public String getUserName() {
    return getSharedPreferences().getString(KEY_USER_NAME, "");
  }

  // ====== ====== Authentication location ===== ======
  public boolean saveEmail(String email) {
    removeFacebookId();
    removeMocomId();
    removeFamuId();
    LinphoneDatabase.getInstance(mContext).saveMail(email);
    return getEditor().putString(KEY_EMAIL, email).commit();
  }

  public String getRegEmail() {
    return getSharedPreferences().getString(KEY_EMAIL, "");
  }

  public String getEmail() {
    String currentEmail = getSharedPreferences().getString(KEY_EMAIL, "");
    if (!TextUtils.isEmpty(currentEmail)) {
      return currentEmail;
    }

    String facebookId = getFacebookId();
    String mocomId = getMocomId();
    String famuId = getFamuId();
    if (!TextUtils.isEmpty(famuId) || !TextUtils.isEmpty(facebookId)
        || !TextUtils.isEmpty(mocomId)) {
      return "";
    }

    Preferences preferences = Preferences.getInstance();
    String oldEmail = preferences.getSharedPreferences().getString("email",
        "");
    if (!TextUtils.isEmpty(oldEmail)) {
      saveEmail(oldEmail);
      return oldEmail;
    }

    return getSharedPreferences().getString(KEY_EMAIL, "");
  }

  public void removeEmail() {
    getEditor().remove(KEY_EMAIL).commit();
  }

  public boolean savePassword(String password) {
    LinphoneDatabase.getInstance(mContext).savePassword(password);
    return getEditor().putString(KEY_PASSWORD, password).commit();
  }

  public String getPassword() {
    String currentPassword = getSharedPreferences().getString(KEY_PASSWORD,
        "");
    if (!TextUtils.isEmpty(currentPassword)) {
      return currentPassword;
    }

    Preferences preferences = Preferences.getInstance();
    String oldPassword = preferences.getSharedPreferences().getString(
        "password", "");
    if (!TextUtils.isEmpty(oldPassword)) {
      savePassword(oldPassword);
      return oldPassword;
    }

    return getSharedPreferences().getString(KEY_PASSWORD, "");
  }

  public void removePassword() {
    getEditor().remove(KEY_PASSWORD).commit();
  }

  // ====== ====== Facebook ===== ======
  public boolean saveFacebookId(String facebookId) {
    removeEmail();
    removeMocomId();
    removeFamuId();
    return getEditor().putString(KEY_FACEBOOK_ID, facebookId).commit();
  }

  public String getFacebookId() {
    return getSharedPreferences().getString(KEY_FACEBOOK_ID, "");
  }

  public void removeFacebookId() {
    getEditor().remove(KEY_FACEBOOK_ID).commit();
  }

  // ====== ====== Mocom ===== ======
  public boolean saveMocomId(String mocomId) {
    removeEmail();
    removeFacebookId();
    removeFamuId();
    return getEditor().putString(KEY_MOCOM_ID, mocomId).commit();
  }

  public String getMocomId() {
    return getSharedPreferences().getString(KEY_MOCOM_ID, "");
  }

  public void removeMocomId() {
    getEditor().remove(KEY_MOCOM_ID).commit();
  }

  // ====== ====== Famu ===== ======
  public boolean saveFamuId(String famuId) {
    removeEmail();
    removeFacebookId();
    removeMocomId();
    return getEditor().putString(KEY_FAMU_ID, famuId).commit();
  }

  public String getFamuId() {
    return getSharedPreferences().getString(KEY_FAMU_ID, "");
  }

  public void removeFamuId() {
    getEditor().remove(KEY_FAMU_ID).commit();
  }

  // ====== ====== Avatar Id ===== ======
  public boolean savePendingAvaId(String avaId) {
    LogUtils.d(TAG, "Avata id: " + avaId);
    if (removeExistPendingAvatar(avaId)) {
      return getEditor().putString(KEY_AVA_ID, avaId).commit();
    }
    return false;
  }

  public boolean saveAvaId(String avaId) {
    LogUtils.d(TAG, "Avata id: " + avaId);
    return getEditor().putString(KEY_AVA_ID, avaId).commit();
  }

  public String getAvaId() {
    return getSharedPreferences().getString(KEY_AVA_ID, "");
  }

  public void removeAvaId() {
    getEditor().remove(KEY_AVA_ID).commit();
  }

  public boolean savePendingAva(String avaId) {
    LogUtils.d(TAG, "Pending avatar: " + avaId);
    String listPendingAvatar = getListPendingAva();
    if (!TextUtils.isEmpty(listPendingAvatar)) {
      listPendingAvatar += (SPLIT + avaId);
    } else {
      listPendingAvatar = avaId;
    }

    LogUtils.d(TAG,
        "List avatar: " + listPendingAvatar.replace(SPLIT, "\n"));
    return getEditor().putString(KEY_PENDING_AVA_ID, listPendingAvatar)
        .commit();
  }

  private String getListPendingAva() {
    return getSharedPreferences().getString(KEY_PENDING_AVA_ID, "");
  }

  public boolean removeExistPendingAvatar(String avaId) {
    LogUtils.d(TAG, "Avatar id to check pending: " + avaId);
    if (avaId == null) {
      return false;
    }

    String[] avatarArray = getListPendingAva().split(SPLIT);

    StringBuilder builder = new StringBuilder();
    boolean isConstant = false;
    for (String pendingAvatar : avatarArray) {
      LogUtils.d(TAG, "Pending avatar in list: " + pendingAvatar);
      if (avaId.equals(pendingAvatar)) {
        isConstant = true;
      } else {
        builder.append(SPLIT).append(pendingAvatar);
      }
    }

    if (builder.length() > 0) {
      builder.delete(0, 1);
    }

    getEditor().putString(KEY_PENDING_AVA_ID, builder.toString()).commit();
    return isConstant;
  }

  // ====== ====== User birthday ===== ======
  public void saveBirthday(String birthday) {
    LogUtils.d(TAG, "Birthday: " + birthday);
    getEditor().putString(KEY_BIRTHDAY, birthday).commit();
  }

  public String getBirthday() {
    return getSharedPreferences().getString(KEY_BIRTHDAY, "");
  }

  // ====== ====== User gender ===== ======
  public boolean saveGender(int gender) {
    LogUtils.d(TAG, "Gender: " + gender);
    return getEditor().putInt(KEY_GENDER, gender).commit();
  }

  public int getGender() {
    return getSharedPreferences().getInt(KEY_GENDER, 0);
  }

  // ====== ====== User total point ===== ======
  public void saveNumberPoint(int numberPoint) {
    LogUtils.d(TAG, "Number total point:" + numberPoint);
    getEditor().putInt(KEY_NUMBER_POINT, numberPoint).commit();
  }

  public int getNumberPoint() {
    return getSharedPreferences().getInt(KEY_NUMBER_POINT, 0);
  }

  // ====== ====== Edit profile screen showed ===== ======
  public boolean saveEditProfileIsShowed() {
    LogUtils.d(TAG, "Edit profile showed: ");
    return getEditor().putBoolean(KEY_EDIT_PROFILE_SHOWED, true).commit();
  }

  public boolean getEditProfileIsShow() {
    return getSharedPreferences()
        .getBoolean(KEY_EDIT_PROFILE_SHOWED, false);
  }

  public boolean getShowNewNotifications() {
    return getSharedPreferences().getBoolean(KEY_SHOW_NEWS_NOTIFICATIONS,
        false);
  }

  // ====== ====== Setting show new notification ===== ======
  public void setShowNewNotifications(boolean show) {
    getEditor().putBoolean(KEY_SHOW_NEWS_NOTIFICATIONS, show).commit();
  }

  public boolean getShowNewsPopup() {
    return getSharedPreferences().getBoolean(KEY_SHOW_NEWS_POPUP, false);
  }

  // ====== ====== show news popup ===== ======
  public void setShowNewsPopup(boolean show) {
    getEditor().putBoolean(KEY_SHOW_NEWS_POPUP, show).apply();
  }

  // ====== ====== Setting new account sign up ===== ======
  public void setIsNewlyAccount(boolean show) {
    getEditor().putBoolean(KEY_IS_NEWLY_ACCOUNT, show).commit();
  }

  public boolean isNewlyAccount() {
    return getSharedPreferences().getBoolean(KEY_IS_NEWLY_ACCOUNT, false);
  }

  // ====== ====== Setting on show popup news ===== ======
  public void setIsShowNews(boolean show) {
    getEditor().putBoolean(KEY_IS_SHOW_NEWS, show).commit();
  }

  public boolean isShowNews() {
    return getSharedPreferences().getBoolean(KEY_IS_SHOW_NEWS, true);
  }

  /**
   * temporary disable show news dialog
   *
   * @param time disable dialog in a day
   */
  public void saveDateSettingNews(Long time) {
    getEditor().putLong(KEY_DATE_SETTING_SHOW_NEWS, time).commit();
  }

  public Long getDateSettingNews() {
    return getSharedPreferences().getLong(KEY_DATE_SETTING_SHOW_NEWS, 0);
  }

  public boolean isOutOfDateSettingNews() {
    if (getDateSettingNews() == 0) {
      return true;
    }
    return !Utility.checkThisDay(getDateSettingNews(), System.currentTimeMillis());
  }

  // ====== ====== Last time setup profile dialog showed ===== ======
  public boolean saveTimeShowSetupProfile(String time) {
    return getEditor().putString(KEY_LAST_TIME_SETUP_PROFILE_DIALOG_SHOWED,
        time).commit();
  }

  public String getTimeShowSetupProfile() {
    return getSharedPreferences().getString(
        KEY_LAST_TIME_SETUP_PROFILE_DIALOG_SHOWED, "");
  }

  // ====== ====== Number of friend ===== ======
  public boolean saveIsFillProfileDialogShowable(boolean isShowed) {
    LogUtils.d(TAG, "Please fill profile dialog showable: " + isShowed);
    return getEditor().putBoolean(
        KEY_IS_PLEASE_FILL_PROFILE_DIALOG_SHOWABLE, isShowed).commit();
  }

  public boolean getIsFillProfileDialogShowable() {
    return getSharedPreferences().getBoolean(
        KEY_IS_PLEASE_FILL_PROFILE_DIALOG_SHOWABLE,
        Constants.IS_NOT_SHOWED_FLAG);
  }

  // ====== ====== Update email or not ===== ======
  public boolean isUpdateEmail() {
    return getSharedPreferences().getBoolean(KEY_IS_UPDATE_EMAIL, false);
  }

  public void saveUpdateEmail(boolean isUpdateEmail) {
    getEditor().putBoolean(KEY_IS_UPDATE_EMAIL, isUpdateEmail).commit();
  }

  // ====== ====== Number of friend ===== ======
  public void saveNumberFriend(int numberFriend) {
    LogUtils.d(TAG, "Number of friend: " + numberFriend);
    getEditor().putInt(KEY_NUMBER_FRIEND, numberFriend).commit();
  }

  public void decreaseFriend() {
    int numOfFriend = getNumberFriend();
    int numOfFavorite = getNumberFavorite();
    if (numOfFriend > 0) {
      numOfFriend--;
    } else {
      numOfFriend = 0;
    }
    saveNumberFriend(numOfFriend);

    int numOfConnection = getNumberConnection();
    int sumOfConnection = numOfFriend + numOfFavorite;
    if (numOfConnection > sumOfConnection) {
      numOfConnection--;
    } else {
      numOfConnection = sumOfConnection;
    }
    saveNumberConnection(numOfConnection);
  }

  public void increaseFriend() {
    int numOfFriend = getNumberFriend();
    numOfFriend++;
    saveNumberFriend(numOfFriend);

    int numOfConnection = getNumberConnection();
    numOfConnection++;
    saveNumberConnection(numOfConnection);
  }

  public int getNumberFriend() {
    return getSharedPreferences().getInt(KEY_NUMBER_FRIEND, 0);
  }

  // ====== ====== Number of favorite ===== ======
  public void saveNumberFavorite(int numberOfFavorite) {
    LogUtils.d(TAG, "Number of favorite: " + numberOfFavorite);
    getEditor().putInt(KEY_NUMBER_FAVORITE, numberOfFavorite).commit();
  }

  public void decreaseFavorite() {
    int numOfFriend = getNumberFriend();
    int numOfFavorite = getNumberFavorite();
    if (numOfFavorite > 0) {
      numOfFavorite--;
    } else {
      numOfFavorite = 0;
    }
    saveNumberFavorite(numOfFavorite);

    int numOfConnection = getNumberConnection();
    int sumOfConnection = numOfFriend + numOfFavorite;
    if (numOfConnection > sumOfConnection) {
      numOfConnection--;
    } else {
      numOfConnection = sumOfConnection;
    }
    saveNumberConnection(numOfConnection);
  }

  public void increaseFavorite() {
    int numOfFavorite = getNumberFavorite();
    numOfFavorite++;
    saveNumberFavorite(numOfFavorite);

    int numOfConnection = getNumberConnection();
    numOfConnection++;
    saveNumberConnection(numOfConnection);
  }

  public int getNumberFavorite() {
    return getSharedPreferences().getInt(KEY_NUMBER_FAVORITE, 0);
  }

  // ====== ====== Number of connection ===== ======
  public void saveNumberFavoritedMe(int numberFavoritedMe) {
    LogUtils.d(TAG, "Number favorited me: " + numberFavoritedMe);
    getEditor().putInt(KEY_NUMBER_FAVORITED_ME, numberFavoritedMe).commit();
  }

  public void increaseFavoritedMe() {
    int num = getSharedPreferences().getInt(KEY_NUMBER_FAVORITED_ME, 0);
    num++;
    saveNumberFavoritedMe(num);
  }

  public int getNumberFavoritedMe() {
    return getSharedPreferences().getInt(KEY_NUMBER_FAVORITED_ME, 0);
  }

  // ====== ====== Number of connection ===== ======
  public void saveNumberConnection(int numberConnection) {
    LogUtils.d(TAG, "Number of connection: " + numberConnection);
    getEditor().putInt(KEY_NUMBER_CONNECTION, numberConnection).commit();
  }

  public void saveNumberConnection(int numOfFriend, int numOfFavorite) {
    saveNumberFriend(numOfFriend);
    saveNumberFavorite(numOfFavorite);
    saveNumberConnection(numOfFriend + numOfFavorite);
  }

  public int getNumberConnection() {
    return getSharedPreferences().getInt(KEY_NUMBER_CONNECTION, 0);
  }

  // ====== ====== Number of unread message ===== ======
  public void saveNumberUnreadMessage(int unreadMessage) {
    LogUtils.d(TAG, "Number of unread mesage: " + unreadMessage);
    getEditor().putInt(KEY_NUMBER_UNREAD_MESSAGE, unreadMessage).commit();
  }

  public void increaseUnreadMessage(int unreadMessage) {
    int currenMsg = getNumberUnreadMessage();
    currenMsg += unreadMessage;
    getEditor().putInt(KEY_NUMBER_UNREAD_MESSAGE, currenMsg).commit();
  }

  public int getNumberUnreadMessage() {
    return getSharedPreferences().getInt(KEY_NUMBER_UNREAD_MESSAGE, 0);
  }

  // ====== ====== Number of notification ===== ======
  public void saveNumberNotification(int numberNotify) {
    LogUtils.d(TAG, "Number of notification: " + numberNotify);
    getEditor().putInt(KEY_NUMBER_NOTIFICATION, numberNotify).commit();
  }

  public void increaseNotification() {
    int numberNotify = getNumberNotification();
    numberNotify++;
    saveNumberNotification(numberNotify);
  }

  public void decreaseNumberNotification() {
    int numberNotify = getNumberNotification();

    numberNotify--;
    if (numberNotify < 0) {
      numberNotify = 0;
    }

    saveNumberNotification(numberNotify);
  }

  public int getNumberNotification() {
    return getSharedPreferences().getInt(KEY_NUMBER_NOTIFICATION, 0);
  }

  // ====== ====== Last update location time ===== ======
  public boolean saveLastTimeUpdateLocation(long time) {
    return getEditor().putLong(KEY_LASTTIME_UPDATE_LOCATION, time).commit();
  }

  public long getLastTimeUpdateLocation() {
    return getSharedPreferences().getLong(KEY_LASTTIME_UPDATE_LOCATION, -1);
  }

  // ====== ====== Setting auto detect region ===== ======
  public boolean saveAutoDetectRegion(boolean code) {
    return getEditor().putBoolean(KEY_IS_AUTO_DETECT_REGION, code).commit();
  }

  public boolean isAutoDetectRegion() {
    return getSharedPreferences().getBoolean(KEY_IS_AUTO_DETECT_REGION,
        false);
  }

  // ====== ====== Current friend chat with ===== ======
  public boolean saveAgeVerification(int code) {
    LogUtils.d(TAG, "Age veification status: " + code);
    return getEditor().putInt(KEY_AGE_VERIFICATION, code).commit();
  }

  public int getAgeVerification() {
    return getSharedPreferences().getInt(KEY_AGE_VERIFICATION, -3);
  }

  // ====== ====== Register status flag ===== ======
  public boolean saveFinishRegister(int finish) {
    LogUtils.d(TAG, "Fisnish register flag: " + finish);
    return getEditor().putInt(KEY_FINISH_REGISTER_FLAG, finish).commit();
  }

  public int getFinishRegister() {
    return getSharedPreferences().getInt(KEY_FINISH_REGISTER_FLAG,
        Constants.FINISH_REGISTER_NO);
  }

  // ====== ====== Current friend chat with ===== ======
  public boolean saveCurentFriendChat(String userId) {
    LogUtils.i(TAG, "Current friend chat Id: " + userId);
    return getEditor().putString(KEY_CURRENT_CHAT_FRIEND_ID, userId)
        .commit();
  }

  public String getCurentFriendChat() {
    return getSharedPreferences().getString(KEY_CURRENT_CHAT_FRIEND_ID, "");
  }

  /**
   * Must pass currentUserIdToSend. Duoc su dung khi xay ra su kien onDetach() o man hinh chat.
   * (tranh truong hop: dang chat vs A, chuyen sang chat vs B thi onDetach() o A duoc goi sau khi
   * onStart() cua B -> dan toi truong hop userIdToSend bi remove
   */
  public void removeCurentFriendChat() {
    getEditor().remove(KEY_CURRENT_CHAT_FRIEND_ID).commit();
  }

  // ====== ====== The first message chat on chat screen ===== ======
  public boolean saveCurrentMsgChatId(String msgId) {
    return getEditor().putString(KEY_CURRENT_MSG_CHAT_ID, msgId).commit();
  }

  public String getCurrentMsgChatId() {
    return getSharedPreferences().getString(KEY_CURRENT_MSG_CHAT_ID, "");
  }

  public void removeCurrentMsgChatId() {
    getEditor().remove(KEY_CURRENT_MSG_CHAT_ID).commit();
  }

  // ====== ====== Enable voice call ===== ======
  public void saveEnableVoiceCall(boolean isEnable) {
    LogUtils.d(TAG, "Enable voice call: " + isEnable);
    getEditor().putBoolean(KEY_IS_ENABLE_VOICE_CALL, isEnable).commit();
  }

  public boolean isEnableVoiceCall() {
    return getSharedPreferences().getBoolean(KEY_IS_ENABLE_VOICE_CALL,
        false);
  }

  // ====== ====== Enable video call ===== ======
  public void saveEnableVideoCall(boolean isEnable) {
    LogUtils.d(TAG, "Enable video call: " + isEnable);
    getEditor().putBoolean(KEY_IS_ENABLE_VIDEO_CALL, isEnable).commit();
  }

  public boolean isEnableVideoCall() {
    return getSharedPreferences().getBoolean(KEY_IS_ENABLE_VIDEO_CALL,
        false);
  }

  // ====== ====== Show who check me out ===== ======
  public boolean saveUnlockWhoCheckMeOut(int unlockWhoCheckMeOut) {
    LogUtils.d(TAG, "Who check me out: " + unlockWhoCheckMeOut);
    return getEditor().putInt(KEY_UNLOCK_WHO_CHECK_ME_OUT,
        unlockWhoCheckMeOut).commit();
  }

  public int getUnlockWhoCheckMeOut() {
    return getSharedPreferences().getInt(KEY_UNLOCK_WHO_CHECK_ME_OUT, 0);
  }

  // ====== ====== Unlock favorite ===== ======
  public boolean saveUnlockFavorite(int unlockFavorite) {
    LogUtils.d(TAG, "Unlock user list: " + unlockFavorite);
    return getEditor().putInt(KEY_UNLOCK_FAVORITE, unlockFavorite).commit();
  }

  public int getUnlockFavorite() {
    return getSharedPreferences().getInt(KEY_UNLOCK_FAVORITE, 0);
  }

  // ====== ====== Number of wink bomb ===== ======
  public boolean saveWinkBombNumber(int winkBombNumber) {
    LogUtils.d(TAG, "Wink bomb number: " + winkBombNumber);
    return getEditor().putInt(KEY_WINK_BOMB_NUMBER, winkBombNumber)
        .commit();
  }

  public int getWinkBombNumber() {
    return getSharedPreferences().getInt(KEY_WINK_BOMB_NUMBER, 0);
  }

  // ====== ====== Wink bomb message ===== ======
  public boolean saveWinkMessageIndex(int winkMessageIndex) {
    LogUtils.d(TAG, "Wink bomb message index: " + winkMessageIndex);
    return getEditor().putInt(KEY_WINK_MESSAGE_INDEX, winkMessageIndex)
        .commit();
  }

  public int getWinkMessageIndex() {
    return getSharedPreferences().getInt(KEY_WINK_MESSAGE_INDEX, 0);
  }

  // ====== ====== Sound setting ===== ======
  public boolean saveSoundOn(boolean value) {
    LogUtils.d(TAG, "Sound: " + value);
    return getEditor().putBoolean(KEY_SOUND_SETTING, value).commit();
  }

  public boolean isSoundOn() {
    return getSharedPreferences().getBoolean(KEY_SOUND_SETTING, true);
  }

  // ====== ====== Vibration setting ===== ======
  public boolean saveVibration(boolean value) {
    LogUtils.d(TAG, "Vibration: " + value);
    return getEditor().putBoolean(KEY_VIBRATION_SETTING, value).commit();
  }

  public boolean isVibration() {
    return getSharedPreferences().getBoolean(KEY_VIBRATION_SETTING, true);
  }

  // ====== ====== Invite URL ===== ======
  public void saveInviteUrl(String url) {
    getEditor().putString(KEY_INVITE_URL, url).commit();
  }

  public String getInviteUrl() {
    return getSharedPreferences().getString(KEY_INVITE_URL, "");
  }

  // ====== ====== Invite code ===== ======
  public void saveInviteCode(String code) {
    getEditor().putString(KEY_INVITE_CODE, code).commit();
  }

  public String getInviteCode() {
    return getSharedPreferences().getString(KEY_INVITE_CODE, "");
  }

  // ====== ====== Notification chat type ===== ======
  public void saveChatNotificationType(int type) {
    LogUtils.i(TAG, "Notification type: " + type);
    getEditor().putInt(KEY_CHAT_NOTIFICATION, type).commit();
  }

  public int getChatNotificationType() {
    return getSharedPreferences().getInt(KEY_CHAT_NOTIFICATION,
        NotificationSetting.NOTIFY_CHAT_ALL);
  }

  public boolean getInCallingProcess() {
    return getSharedPreferences().getBoolean(KEY_IN_CALLING_PROCESS, false);
  }

  // ====== ====== In calling progress ===== ======
  public void setInCallingProcess(boolean isCalling) {
    LogUtils.i(TAG, "Calling progress: " + isCalling);
    getEditor().putBoolean(KEY_IN_CALLING_PROCESS, isCalling).commit();
  }

  public boolean getInRecordingProcess() {
    return getSharedPreferences().getBoolean(KEY_IN_RECORDING_PROCESS,
        false);
  }

  // ====== ====== In calling progress ===== ======
  public void setInRecordingProcess(boolean isCalling) {
    getEditor().putBoolean(KEY_IN_RECORDING_PROCESS, isCalling).commit();
  }

  public String getCallingUserId() {
    return getSharedPreferences().getString(KEY_CALLING_USER_ID, "");
  }

  // ====== ====== In calling progress ===== ======
  public void setCallingUserId(String userId) {
    getEditor().putString(KEY_CALLING_USER_ID, userId).commit();
  }

  public String getStartedCallMessageId() {
    return getSharedPreferences()
        .getString(KEY_STARTED_CALL_MESSAGE_ID, "");
  }

  // ====== ====== Start call mesage id ===== ======
  public void setStartedCallMessageId(String msdId) {
    getEditor().putString(KEY_STARTED_CALL_MESSAGE_ID, msdId).commit();
  }

  // ====== ====== Is first time edit profile ===== ======
  public void saveEditProfileFirstTime(boolean isProfilEdited) {
    getEditor().putBoolean(KEY_EDIT_PROFILE_FIRST_TIME, isProfilEdited)
        .commit();
    LogUtils.d(TAG, "isProfilEdited: " + isProfilEdited);
  }

  public boolean getEditProfileFirstTime() {
    return getSharedPreferences().getBoolean(KEY_EDIT_PROFILE_FIRST_TIME,
        false);
  }

  // ====== ====== Temp Facebook id ===== ======
  public boolean saveTempFacebookId(String facebookId) {
    LogUtils.d(TAG, "FacebookId:" + facebookId);
    return getEditor().putString(KEY_TEMP_FACEBOOK_ID, facebookId).commit();
  }

  public String getTempFacebookId() {
    return getSharedPreferences().getString(KEY_TEMP_FACEBOOK_ID, "");
  }

  // ====== ====== Facebook avatar id ===== ======
  public boolean saveFacebookAvatar(String url) {
    return getEditor().putString(KEY_FB_AVATAR, url).commit();
  }

  public String getFacebookAvatar() {
    return getSharedPreferences().getString(KEY_FB_AVATAR, "");
  }

  public boolean saveVideoLocation(int location) {
    return getEditor().putInt(KEY_VIDEO_CAPTION_LOCATION, location)
        .commit();
  }

  public int getVideoLocation() {
    return getSharedPreferences().getInt(KEY_VIDEO_CAPTION_LOCATION,
        TOP_LEFT);
  }

  public boolean isLogout() {
    return getSharedPreferences().getBoolean(KEY_LOGOUT, true);
  }

  public void setIsLogout(boolean isLogout) {
    getEditor().putBoolean(KEY_LOGOUT, isLogout).commit();
  }
}
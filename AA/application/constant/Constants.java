package com.application.constant;

public class Constants {

  // Switch is 1
  // Two2Tone is 2
  // U28 is 3
  // メチャツーワ is 4
  public static final int APPLICATION_TYPE = 1;
  // Intent Keys
  public static final String INTENT_RECEIVER_EMAIL = "ReceiverEmail";
  public static final String INTENT_FINISH_REGISTER_FLAG = "intent_finish_register_flag";
  public static final String ARGUMENT_FINISH_REGISTER_FLAG = "argument_finshi_register_flag";
  // Default finish register flag
  public static final int FINISH_REGISTER_YES = 1;
  public static final int FINISH_REGISTER_NO = 0;
  // Default dialog showed
  public static final boolean IS_SHOWED_FLAG = false;
  public static final boolean IS_NOT_SHOWED_FLAG = true;
  public static final int AGE_VERIFICATION_NONE = -2;
  public static final int AGE_VERIFICATION_DINED = -1;
  public static final int AGE_VERIFICATION_PENDING = 0;
  public static final int AGE_VERIFICATION_VERIFIED = 1;
  public static final double LOCATION_HERE_LIMIT = 0.1; // Kilometers
  /**
   * Delay time to send message heartbeat in seconds
   */
  public static final int DELAY_TIME_HEARTBEAT = 60;
  public static final int DELAY_INIT_TIME_HEARTBEAT = DELAY_TIME_HEARTBEAT / 2;

  // GMC
  public static final String GCM_SENDER_ID = "4837388912";
  public static final int GENDER_TYPE_MAN = 0;
  public static final int GENDER_TYPE_WOMAN = 1;
  public static final int SEARCH_SETTING_AGE_MIN_LIMIT = 18;
  public static final int SEARCH_SETTING_AGE_MAX_LIMIT = 120;
  // Settings
  public static final int DISTANCE_UNIT_MILE = 0;
  public static final int DISTANCE_UNIT_KILOMETER = 1;
  // Invite Friends
  public static final String EXTRA_SEND_MAIL_TO = "com.itsherpa.andg.send_mail_to";
  // Direction
  public static final int HORIZONTAL_SHOW = 1;
  public static final int HORIZONTAL_HIDE = 2;
  public static final int VERTICAL_SHOW = 3;
  public static final int VERTICAL_HIDE = 4;
  // Report
  public static final int REPORT_TYPE_IMAGE = 1;
  public static final int REPORT_TYPE_USER = 2;
  // Buzz
  public static final int BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_COMMENTS = 4;
  public static final int BUZZ_LIST_SHOW_LOAD_MORE = 50;
  public static final int BUZZ_LIKE_TYPE_UNKNOW = -1;
  public static final int BUZZ_LIKE_TYPE_UNLIKE = 0;
  public static final int BUZZ_LIKE_TYPE_LIKE = 1;
  public static final int BUZZ_TYPE_STATUS = 0;
  public static final int BUZZ_TYPE_IMAGE = 1;
  public static final int BUZZ_TYPE_GIFT = 2;
  public static final int BUZZ_COMMENT_CAN_DELETE = 1;
  public static final int BUZZ_COMMENT_CANNOT_DELETE = 0;
  public static final int BUZZ_TYPE_NONE = -1;
  public static final int BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_SUB_COMMENTS = 4;
  public static final String FRAGMENT_TAG = "fragment_tag";
  public static final String INTENT_BUZZ_ID = "buzz_id";
  public static final String INTENT_USER_ID = "user_id";
  public static final int UNLOCKED = 1;
  public static final int NOTI_CHECK_OUT_UNLOCK = 2;
  public static final int NOTI_FAVORITED_UNLOCK = 4;
  public static final int NOTI_LIKE_BUZZ = 5; // Still
  public static final int NOTI_LIKE_OTHER_BUZZ = 6;
  public static final int NOTI_COMMENT_BUZZ = 7; // Still
  public static final int NOTI_COMMENT_OTHER_BUZZ = 8;
  public static final int NOTI_UNLOCK_BACKSTAGE = 9;
  public static final int NOTI_FRIEND = 10;
  public static final int NOTI_CHAT_TEXT = 11;
  public static final int NOTI_ONLINE_ALERT = 12; // Still
  public static final int NOTI_DAYLY_BONUS = 13; // Still
  public static final int NOTI_BUZZ_APPROVED = 15; // Still
  public static final int NOTI_BACKSTAGE_APPROVED = 16; // Still
  public static final int NOTI_VOIP_PING = 17;//Voice over Internet Protocol
  public static final int NOTI_FROM_FREE_PAGE = 18;
  public static final int NOTI_FAVORITED_CREATE_BUZZ = 19;
  public static final int NOTI_REPLY_YOUR_COMMENT = 20; // Still
  public static final int NOTI_DENIED_BUZZ_IMAGE = 21;
  public static final int NOTI_DENIED_BACKSTAGE = 22;
  public static final int NOTI_APPROVE_BUZZ_TEXT = 24;
  public static final int NOTI_DENIED_BUZZ_TEXT = 25;
  public static final int NOTI_APPROVE_COMMENT = 26;
  public static final int NOTI_DENIED_COMMENT = 27;
  public static final int NOTI_APPROVE_SUB_COMMENT = 28;
  public static final int NOTI_DENI_SUB_COMMENT = 29;
  public static final int NOTI_APPROVE_USERINFO = 30;
  public static final int NOTI_APART_OF_USERINFO = 31;
  public static final int NOTI_DENIED_USERINFO = 32;
  public static final int NOTI_REQUEST_CALL = 23;
  /**
   * time to show notification in miliseconds
   */
  public static final int NOTI_SHOW_TIME = 5000;
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_TEXT = "noti_new_chat_msg_text";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_PHOTO = "noti_new_chat_msg_photo";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_VIDEO = "noti_new_chat_msg_video";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_AUDIO = "noti_new_chat_msg_audio";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_STICKER = "noti_new_chat_msg_sticker";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_LOCATION = "noti_new_chat_msg_location";
  public static final String LOCK_KEY_CHAT_LOC_MESSAGE_GIFT = "noti_gave_gift";
  public static final String LOCK_KEY_CALLREQUEST = "notification_call_request";
  public static final String LOCK_KEY_VOIP_CALLING = "noti_voip_calling";
  public static final int MANAGE_ONLINE_NEVER = -1;
  public static final int MANAGE_ONLINE_EVERY_TIME = 0;
  public static final int MANAGE_ONLINE_MAX_TEN = 10;
  public static final int MANAGE_ONLINE_MAX_FIVE = 5;
  public static final int MANAGE_ONLINE_ONCE_PER_DAY = 1;
  public static final int NOTIFICATION_MAX_LENGTH_NAME = 20;
  public static final int NOTIFICATION_VIBRATOR_TIME = 400;
  public static final int IS_NOT_APPROVED = 0;
  public static final int IS_APPROVED = 1;
  public static final String DATE_FORMAT_DISPLAY = "yyyy/MM/dd";
  public static final String DATE_FORMAT_SEND_TO_SERVER = "yyyyMMdd";
  // free point
  public static final String FROM_FREE_POINT = "from_free_point";
  public static final String POINT_GET_FREE = "point_free";
  public static final String POINT_GET_FREE_SAVE_INSTANCE = "point_free_save_instance";
  public static final String FREE_POINT_SAVE_INSTANCE = "free_point_save_instance";
  public static final int FROM_SIGNUP = 1; // from sign up to free point
  public static final int MAX_LENGTH_NAME_IN_HALF_SIZE = 14;
  // buzz detail option: 1 = comment
  public static final String INTENT_BUZZ_DETAIL_OPTION = "buzz_detail_option";
  public static final int BUZZ_DETAIL_OPTION_CMT = 1; // comment
  public static final int BUZZ_DETAIL_OPTION_BACK_PROFILE = 2;
  public static final int CALL_TYPE_VOICE = 0; // comment
  public static final int CALL_TYPE_VIDEO = 1; // comment
  public static final String FORMAT_TIME_SHOW_SETUP_PROFILE = "yyyyMMddHHmmss";
  // package point type
  public static final int PACKAGE_DEFAULT = 0;
  public static final int PACKAGE_POINT_CHAT = 1;
  public static final int PACKAGE_POINT_VOICE_CALL = 2;
  public static final int PACKAGE_POINT_VIDEO_CALL = 3;
  public static final int PACKAGE_POINT_GIFT = 4;
  public static final int PACKAGE_POINT_COMMENT = 5;
  public static final int PACKAGE_POINT_SUB_COMMENT = 6;
  public static final int GOOGLE_PURCHASE = 1;
  //cmcode url
  public static boolean checkopenURLcmcode = true;
}
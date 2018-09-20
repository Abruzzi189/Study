package com.application.actionbar.popup.controllers;

/**
 * Created by vn apnic on 4/10/2016.
 */
public interface IPopupRequest {

  // Request code
  static final int REQUEST_PHOTO = 1000;
  static final int REQUEST_VIDEO = 3000;
  static final int REQUEST_IMAGE_ID = 4000;
  static final int REQUEST_GIFT = 5000;

  // Request loader
  static final int LOADER_ID_HISTORY = 1;
  static final int LOADER_ID_MARK_AS_READ = 2;
  static final int LOADER_ID_ADD_BLOCK_USER = 3;
  static final int LOADER_ID_REMOVE_BLOCK_USER = 4;
  static final int LOADER_ID_REPORT_USER = 5;

  static final int LOADER_ID_ADD_TO_FAVORITES = 6;
  static final int LOADER_ID_GET_BASE_USER_INFO = 7;
  static final int LOADER_ID_REMOVE_FROM_FAVORITES = 8;
  static final int LOADER_ID_CHECK_CALL_VIDEO = 9;
  static final int LOADER_ID_CHECK_CALL_VOICE = 10;

  static final int LOADER_ID_GET_VIDEO_URL = 11;
  static final int LOADER_ID_CHECK_UNLOCK = 12;
  static final int LOADER_ID_UNLOCK = 13;
  static final int LOADER_ID_BASIC_USER_INFO_CALL = 14;
  static final int LOADER_ID_CHECK_NEW_MESSAGE = 15;
}

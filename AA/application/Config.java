package com.application;

import glas.bbsystem.BuildConfig;

public class Config {

  /**
   * Debug mode for (true: show log, throw exception, show toast)
   */
  public static final boolean DEBUG = true;
  /**
   * Time delay between the connection and the reconnection.
   */
  public static final long RECONNECT_DELAY_TIME = 30 * 1000;
  /**
   * Read stream on server timeout in millisecond.
   */
  public static final int TIMEOUT_READ = 5 * 60 * 1000;
  /**
   * Connect time out to server in millisecond.
   */
  public static final int TIMEOUT_CONNECT = 60 * 1000;

  /* -----Connection setting----- */
  /**
   * Time interval for updating new location in second.
   */
  public static final int TIME_UPDATE_LOCATION = 30 * 60;
  /**
   * Time threshold to add header to chat
   */
  public static final long THRESHOLD_HEADER_CHAT = 24 * 60 * 60 * 1000;
  /**
   * Video capture limit time in second
   */
  public static final int TIME_LIMIT_VIDEO = 90;

  /* -----Location setting----- */
  /**
   * Video capture quality 0 = low and 1 = high
   */
  public static final int VIDEO_QUALITY = 0;
  /**
   * Video call streaming band width threshold (kbit/s)
   */
  public static final float THRESHOLD_VIDEO_CALL_BANDWIDTH = 0f;
  /**
   * Video call streaming limit time waiting in millisecond
   */
  public static final int TIME_LIMIT_WATING_VIDEO_CALL_STREAMING = 150 * 1000;
  /**
   * Setting for production server (true: product, false: test)
   */
  public final static boolean IS_PRODUCT_SERVER = BuildConfig.IS_PRODUCT;
  // NTQ local server
  public static int SERVER_LOCAL = 0;
  // Customer test server.
  public static int SERVER_STAGING = 1;
  // Product server. The running server on air
  public static int SERVER_PRODUCT = 2;
  /**
   * Brightness threshold for light sensor
   */
  public static int THRESHOLD_BRIGHTNESS = 0;
  /**
   * Proximity threshold for proximity sensor
   */
  public static int THRESHOLD_PROXIMITY = 0;
  // Main server
  public static String SERVER_URL = BuildConfig.SERVER_URL;
  // Counter server
  public static final String COUNTER_SERVER = SERVER_URL
      + "/unique_number=%1$s";
  // Image server
  public static String IMAGE_SERVER_URL = BuildConfig.IMAGE_SERVER_URL;
  // Chat server
  public static String CHAT_SERVER_IP = BuildConfig.CHAT_SERVER_IP;
  public static int CHAT_SERVER_PORT = BuildConfig.CHAT_SERVER_PORT;
  // SIP server
  public static String SIP_SERVER_IP = BuildConfig.SIP_SERVER_IP;
  public static String SIP_SERVER_PORT = BuildConfig.SIP_SERVER_PORT;
}
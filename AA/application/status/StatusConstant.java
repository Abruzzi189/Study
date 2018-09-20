package com.application.status;

import java.util.ArrayList;
import java.util.List;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class StatusConstant {

  public static final String TAG = "status_chat";

  // database info
  public static final String DB_NAME = "status_manager_db";
  public static final int DB_VERSION = 1;
  public static final String TABLE_STATUS = "status";

  // column database
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TIME_START = "c_time_start";
  public static final String COLUMN_TIME_SEND = "c_time_send";
  public static final String COLUMN_FROM = "c_from";
  public static final String COLUMN_TO = "c_to";
  public static final String COLUMN_TYPE = "c_type";
  public static final String COLUMN_VALUE = "c_value";
  public static final String COLUMN_STATUS = "c_status";

  // column database for file message
  public static final String COLUMN_UPLOAD_ID = "c_upload_id";
  public static final String COLUMN_CHAT_CLIENT_ID = "c_chat_client_id";
  public static final String COLUMN_FILE_ID = "c_file_id";
  public static final String COLUMN_FILE_TYPE = "c_file_type";
  public static final String COLUMN_FILE_PATH = "c_file_path";
  public static final String COLUMN_TIME_AUDIO = "c_time_audio";

  // message status
  public static final int STATUS_UNKNOW = -1;
  public static final int STATUS_START = 0;
  public static final int STATUS_SENDING_FILE = 1;
  public static final int STATUS_SUCCESS = 2;
  public static final int STATUS_ERROR = 3;
  public static final int STATUS_RETRY = 4;
  public static final int STATUS_DELETE = 5;
  public static final int STATUS_READ = 6;
  // message type
  public static final int MSG_TYPE_PP = 0;
  public static final int MSG_TYPE_ROOM = MSG_TYPE_PP + 1;
  public static final int MSG_TYPE_CMD = MSG_TYPE_ROOM + 1;
  public static final int MSG_TYPE_AUTH = MSG_TYPE_CMD + 1;
  public static final int MSG_TYPE_PRC = MSG_TYPE_AUTH + 1;
  public static final int MSG_TYPE_MDS = MSG_TYPE_PRC + 1;
  public static final int MSG_TYPE_BCAST = MSG_TYPE_MDS + 1;
  public static final int MSG_TYPE_RT = MSG_TYPE_BCAST + 1;
  public static final int MSG_TYPE_WINK = MSG_TYPE_RT + 1;
  public static final int MSG_TYPE_FILE = MSG_TYPE_WINK + 1;
  public static final int MSG_TYPE_GIFT = MSG_TYPE_FILE + 1;
  public static final int MSG_TYPE_STK = MSG_TYPE_GIFT + 1;
  public static final int MSG_TYPE_LCT = MSG_TYPE_STK + 1;
  public static final int MSG_TYPE_SVOICE = MSG_TYPE_LCT + 1;
  public static final int MSG_TYPE_EVOICE = MSG_TYPE_SVOICE + 1;
  public static final int MSG_TYPE_SVIDEO = MSG_TYPE_EVOICE + 1;
  public static final int MSG_TYPE_EVIDEO = MSG_TYPE_SVIDEO + 1;
  public static final int MSG_TYPE_CALLREQ = MSG_TYPE_EVIDEO + 1;
  // Key to send values via bundle
  public static final String ARG_MSG_ID = "msg_id";
  public static final String ARG_CHAT_MSG_ID = "chat_msg_id";
  public static final String ARG_UPLOAD_ID = "upload_id";
  public static final String ARG_FILE_TYPE = "file_type";
  public static final String ARG_FILE_PATH = "file_path";
  public static final String ARG_AUDIO_TIME = "audio_time";
  private static List<MessageType> msgList;

  static {
    msgList = new ArrayList<MessageType>();
    msgList.add(MessageType.PP);
    msgList.add(MessageType.ROOM);
    msgList.add(MessageType.CMD);
    msgList.add(MessageType.AUTH);
    msgList.add(MessageType.PRC);
    msgList.add(MessageType.MDS);
    msgList.add(MessageType.BCAST);
    msgList.add(MessageType.RT);
    msgList.add(MessageType.WINK);
    msgList.add(MessageType.FILE);
    msgList.add(MessageType.GIFT);
    msgList.add(MessageType.STK);
    msgList.add(MessageType.LCT);
    msgList.add(MessageType.SVOICE);
    msgList.add(MessageType.EVOICE);
    msgList.add(MessageType.SVIDEO);
    msgList.add(MessageType.EVIDEO);
    msgList.add(MessageType.CALLREQ);
  }

  public static String getStatus(int status) {
    switch (status) {
      case STATUS_START:
        return "start";
      case STATUS_SENDING_FILE:
        return "sending";
      case STATUS_SUCCESS:
        return "success";
      case STATUS_ERROR:
        return "error";
      case STATUS_RETRY:
        return "retry";
      case STATUS_READ:
        return "read";
      default:
        return "not found";
    }
  }

  public static int getMsgType(MessageType msgType) {
    return msgList.indexOf(msgType);
  }

  public static MessageType getMsgType(int msgType) {
    if (msgType < 0 && msgType > msgList.size()) {
      return null;
    }
    return msgList.get(msgType);
  }

}

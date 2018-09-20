package com.application.util;

import com.application.connection.Response;
import glas.bbsystem.R;


public class ErrorString {

  public static int getDescriptionOfErrorCode(int code) {
    int message = R.string.alert;
    switch (code) {
      case Response.CLIENT_ERROR_CAN_NOT_CONNECTION:
        message = R.string.msg_common_can_not_connect_to_server;
        break;
      case Response.CLIENT_ERROR_NO_CONNECTION:
        message = R.string.msg_common_no_connection;
        break;
      case Response.CLIENT_ERROR_NO_DATA:
        message = R.string.msg_common_no_data;
        break;
      case Response.CLIENT_ERROR_UNKNOW:
        message = R.string.msg_common_unknown_error;
        break;
      case Response.CLIENT_ERROR_PARSE_JSON:
        message = R.string.msg_common_parse_json_error;
        break;
      case Response.CLIENT_ERROR_AUTHEN_WITH_CHAT_SERVER:
        message = R.string.msg_common_error_authen_chatserver;
        break;
      case Response.CLIENT_ERROR_BILLING_UNAVAIABLE:
        message = R.string.billing_unavaiable;
        break;
      case Response.SERVER_EMAIL_NOT_FOUND:
        message = R.string.msg_common_email_not_found;
        break;
      case Response.SERVER_EMAIL_REGISTERED:
        message = R.string.msg_common_email_has_already;
        break;
      case Response.SERVER_INVALID_USER_NAME:
        message = R.string.msg_common_invalid_username;
        break;
      case Response.SERVER_INVALID_EMAIL:
        message = R.string.msg_common_invalid_email;
        break;
      case Response.SERVER_INVALID_PASSWORD:
        message = R.string.msg_common_invalid_password;
        break;
      case Response.SERVER_UNKNOWN_ERROR:
        message = R.string.msg_common_server_unknown_error;
        break;
      case Response.SERVER_INCORRECT_PASSWORD:
        message = R.string.msg_common_password_is_incorrect;
        break;
      case Response.SERVER_SEND_MAIL_FAIL:
        message = R.string.msg_common_send_email_fail;
        break;
      case Response.SERVER_INCORRECT_CODE:
        message = R.string.msg_common_incorrect_code;
        break;
      case Response.SERVER_WRONG_DATA_FORMAT:
        message = R.string.msg_common_data_format_wrong;
        break;
      case Response.SERVER_NOT_ENOUGHT_MONEY:
        message = R.string.not_enough_point_title;
        break;
      case Response.SERVER_ALREADY_PURCHASE:
        message = R.string.purchase_already_perform;
        break;
      case Response.SERVER_OUT_OF_DATE_API:
        message = R.string.need_update_app;
        break;
      case Response.SERVER_LOOKED_USER:
        message = R.string.account_locked_user;
        break;
      case Response.SERVER_INVALID_BIRTHDAY:
        message = R.string.invalid_birthday;
        break;
      case Response.SERVER_OLD_VERSION:
        message = R.string.application_version_invalid;
        break;
      default:
        break;
    }
    return message;
  }

}

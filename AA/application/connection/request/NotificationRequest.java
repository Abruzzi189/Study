package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class NotificationRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 5099572003368664358L;
  @SerializedName("time_stamp")
  private String time_stamp;
  @SerializedName("take")
  private int take;
  @SerializedName("version")
  private int version = 3;

  public NotificationRequest(String token, int take) {
    super();
    this.api = "lst_noti";
    this.token = token;
    this.take = take;
  }

  public NotificationRequest(String token, String time_stamp, int take) {
    super();
    this.api = "lst_noti";
    this.token = token;
    this.time_stamp = time_stamp;
    this.take = take;
  }

  public NotificationRequest(String token, int take, boolean onlyLike) {
    super();
    if (onlyLike) {
      this.api = "get_like_notification";
    } else {
      this.api = "lst_noti";
    }
    this.token = token;
    this.take = take;
  }

  public NotificationRequest(String token, String time_stamp, int take, boolean onlyLike) {
    super();
    if (onlyLike) {
      this.api = "get_like_notification";
    } else {
      this.api = "lst_noti";
    }
    this.token = token;
    this.time_stamp = time_stamp;
    this.take = take;
  }

}

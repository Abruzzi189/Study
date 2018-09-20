package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class CopyOfBlockedUsersListRequest extends RequestParams {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = -2549080416767798460L;
  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  public CopyOfBlockedUsersListRequest(String token, int skip, int take) {
    super();
    this.api = "lst_blk";
    this.token = token;
    this.skip = skip;
    this.take = take;
  }
}

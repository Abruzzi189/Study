package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class MarkReadsRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6121580403719208117L;
  @SerializedName("frd_id")
  private String[] friends;

  public MarkReadsRequest(String token, String[] friends) {
    super();
    this.api = "mark_reads";
    this.token = token;
    this.friends = friends;
  }

}

package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SetCallSettingRequest extends RequestParams {

  private static final long serialVersionUID = 1L;
  @SerializedName("voice_call_waiting")
  private boolean isVoice;
  @SerializedName("video_call_waiting")
  private boolean isVideo;

  public SetCallSettingRequest(String token, boolean isVoice, boolean isVideo) {
    this.api = "set_call_waiting";
    this.token = token;
    this.isVoice = isVoice;
    this.isVideo = isVideo;
  }
}
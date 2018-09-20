package com.application.connection.request;

import com.application.connection.Method;

public class GetVideoUrlRequest extends RequestParams {

  private static final long serialVersionUID = -2549080416767798460L;
  private String file_id;

  public GetVideoUrlRequest(String token, String id) {
    super();
    this.api = "get_video_url";
    this.token = token;
    this.file_id = id;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("api=");
    builder.append(api);
    builder.append("&token=");
    builder.append(String.valueOf(token));
    builder.append("&file_id=");
    builder.append(String.valueOf(file_id));
    return builder.toString();
  }

  @Override
  public int getMethod() {
    return Method.GET;
  }
}
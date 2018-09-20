package com.application.connection.request;

import com.application.Config;

public class FileRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 5038349547792995988L;
  private String file_id;

  public FileRequest(String token, String file_id) {
    super();
    this.api = "load_file";
    this.token = token;
    this.file_id = file_id;
  }

  public String toURL() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Config.IMAGE_SERVER_URL);
    stringBuilder.append("api");
    stringBuilder.append("=");
    stringBuilder.append(this.api);
    stringBuilder.append("&");
    stringBuilder.append("token");
    stringBuilder.append("=");
    stringBuilder.append(this.token);
    stringBuilder.append("&");
    stringBuilder.append("file_id");
    stringBuilder.append("=");
    stringBuilder.append(this.file_id);
    return stringBuilder.toString();
  }
}

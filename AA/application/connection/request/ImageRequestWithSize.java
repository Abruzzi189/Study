package com.application.connection.request;

import com.application.Config;

public class ImageRequestWithSize extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 2534204818008375741L;
  public String img_id;
  public int size;

  public ImageRequestWithSize(String token, String imageId, int size) {
    this.api = "load_img_with_size";
    this.size = size;
    this.img_id = imageId;
    this.token = token;
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
    stringBuilder.append("img_id");
    stringBuilder.append("=");
    stringBuilder.append(this.img_id);
    stringBuilder.append("&");
    stringBuilder.append("width_size");
    stringBuilder.append("=");
    stringBuilder.append(this.size);
    return stringBuilder.toString();
  }

}

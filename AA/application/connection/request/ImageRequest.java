package com.application.connection.request;

import com.application.Config;

public class ImageRequest extends RequestParams {
  //for API 26
  public static final int THUMBNAIL = 1;
  public static final int ORIGINAL = 2;
  public static final int STICKER = 3;
  public static final int GIFT = 4;
  public static final int STICKER_CATEGORY = 5;
  public static final int BANNER = 6;
  public static final String API = "load_img";
  /**
   *
   */
  private static final long serialVersionUID = 4162076248105963965L;
  public int img_kind;
  public String img_id;

  public ImageRequest(String token, String img_id, int img_kind) {
    this.api = API;
    this.token = token;
    this.img_id = img_id;
    this.img_kind = img_kind;
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
    stringBuilder.append("img_kind");
    stringBuilder.append("=");
    stringBuilder.append(this.img_kind);
    return stringBuilder.toString();
  }

  public String toURLCache() {
    return this.img_id + this.img_kind;
  }
}

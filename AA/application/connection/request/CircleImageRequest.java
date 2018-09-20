package com.application.connection.request;

import com.application.Config;
import com.google.gson.annotations.SerializedName;

public class CircleImageRequest extends RequestParams {

  private static final long serialVersionUID = -5264214331407666135L;

  @SerializedName("img_kind")
  public int img_kind;

  @SerializedName("img_id")
  public String img_id;

  @SerializedName("isCircle")
  public boolean isCircle = true;
  private StringBuilder stringBuilder;

  public CircleImageRequest(String token, String img_id) {
    super();
    this.api = "load_img";
    this.token = token;
    this.img_id = img_id;
    this.img_kind = 1;  //Thumbnail
  }

  public String toURL() {
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
    }
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
    String url = stringBuilder.toString();
    stringBuilder.delete(0, stringBuilder.length());
    return url;
  }

  public String getCircleImageId() {
    return this.img_id;
  }

  public String getCircleImageIdUnique() {
    return this.img_id + this.img_kind + "circle";
  }

}

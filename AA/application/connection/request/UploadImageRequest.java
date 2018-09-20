package com.application.connection.request;

import java.io.File;

public class UploadImageRequest extends RequestParams {

  public static final int PUBLISH_IMAGE = 1;
  public static final int BACKSTAGE = 2;
  public static final int AVATAR = 3;
  public static final int VIDEO_THUMBNAIL = 4;
  public static final int UPLOAD_FILE = 1;
  public static final int UPLOAD_URL = 2;
  private static final long serialVersionUID = 927276821176463479L;
  public int img_cat;
  public String mURL;
  public File mFile;
  public int mType;
  public String mHashSum;

  public UploadImageRequest(String token, int img_cat, String hashSum) {
    this.api = "upl_img_version_2";
    this.token = token;
    this.img_cat = img_cat;
    this.mHashSum = hashSum;
  }

  public UploadImageRequest(String token, int img_cat, File file, String hashSum) {
    this(token, img_cat, hashSum);
    mFile = file;
    mType = UPLOAD_FILE;
  }

  public UploadImageRequest(String token, int img_cat, File file, String url, String hashSum) {
    this(token, img_cat, hashSum);
    mType = UPLOAD_URL;
    mFile = file;
    mURL = url;
  }

  public String toURL() {
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append(Config.IMAGE_SERVER_URL);
//    stringBuilder.append("api=");
//    stringBuilder.append(this.api);
//    stringBuilder.append("&token=");
//    stringBuilder.append(this.token);
//    stringBuilder.append("&img_cat=");
//    stringBuilder.append(this.img_cat);
//    stringBuilder.append("&sum=");
//    stringBuilder.append(this.mHashSum);
//    return stringBuilder.toString();
    String stringBuilder = glas.bbsystem.BuildConfig.IMAGE_SERVER_URL +
        "api=" +
        this.api +
        "&token=" +
        this.token +
        "&img_cat=" +
        this.img_cat +
        "&sum=" +
        this.mHashSum;
    return stringBuilder;

  }

  public String toURLWithFileId(String fileId) {
    return String.format(toURL() + "&file_id=%s", fileId);
  }
}

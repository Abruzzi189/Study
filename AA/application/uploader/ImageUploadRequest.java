package com.application.uploader;

import com.application.chat.ChatManager;
import com.application.connection.ResponseData;
import java.io.File;

public class ImageUploadRequest extends FileUploadRequest {

  public static final int CHAT_IMAGE = 0;
  private static final long serialVersionUID = 3634084676449154571L;
  public int img_cat;

  public ImageUploadRequest(String token, File file, String fileName, int img_cat) {
    super(token, file, fileName, ChatManager.PHOTO);
    this.api = "upl_img";
    this.img_cat = img_cat;
  }

  @Override
  public String toURL() {
    String url = super.toURL();
    StringBuilder builder = new StringBuilder(url);
    builder.append("&img_cat=");
    builder.append(this.img_cat);
    return builder.toString();
  }

  @Override
  public UploadResponse parseResponseData(ResponseData responseData) {
    return new ImageUploadResponse(responseData);
  }
}
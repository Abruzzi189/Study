package com.application.connection.request;

public class SaveImageRequest extends RequestParams {

  private static final long serialVersionUID = 3262536255457660838L;
  private String img_id = "";

  public SaveImageRequest(String token, String imgId) {
    super();
    this.api = "save_img_version_2";
    this.token = token;
    this.img_id = imgId;
  }
}
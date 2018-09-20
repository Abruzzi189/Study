package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class UpdateTemplateRequest extends RequestParams {

  private static final long serialVersionUID = -5530869107698433431L;

  @SerializedName("template_id")
  private String id;
  @SerializedName("template_title")
  private String title;
  @SerializedName("template_content")
  private String content;

  public UpdateTemplateRequest(String token, String id, String title, String content) {
    super();
    this.api = "update_template";
    this.token = "token";
    this.id = id;
    this.title = title;
    this.content = content;
  }
}

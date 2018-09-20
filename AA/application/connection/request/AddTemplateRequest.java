package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddTemplateRequest extends RequestParams {

  private static final long serialVersionUID = 5840550339548319814L;
  @SerializedName("template_title")
  private String title;
  @SerializedName("template_content")
  private String content;

  public AddTemplateRequest(String token, String title, String content) {
    super();
    this.api = "add_template";
    this.token = token;
    this.title = title;
    this.content = content;
  }
}

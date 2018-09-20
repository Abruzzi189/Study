package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeleteTemplateRequest extends RequestParams {

  private static final long serialVersionUID = -512691477043765525L;

  @SerializedName("template_id")
  private String id;

  public DeleteTemplateRequest(String token, String id) {
    this.api = "delete_template";
    this.token = token;
    this.id = id;
  }

}

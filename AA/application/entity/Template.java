package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Template implements Serializable {

  private static final long serialVersionUID = 847099128157811718L;

  @SerializedName("template_id")
  private String tempId;
  @SerializedName("template_title")
  private String tempTitle;
  @SerializedName("template_content")
  private String tempContent;

  public String getTempId() {
    return tempId;
  }

  public void setTempId(String tempId) {
    this.tempId = tempId;
  }

  public String getTempTitle() {
    return tempTitle;
  }

  public void setTempTitle(String tempTitle) {
    this.tempTitle = tempTitle;
  }

  public String getTempContent() {
    return tempContent;
  }

  public void setTempContent(String tempContent) {
    this.tempContent = tempContent;
  }


}

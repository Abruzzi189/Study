package com.application.util;

import java.util.Locale;

public class Emoji {

  private final String TAG_FORMAT = "<img src=\"%1$s\">";
  private Integer resource;
  private String code;
  private String tag;

  public Emoji(int resource, String code, String tag) {
    this.resource = resource;
    this.code = code.toLowerCase(Locale.US);
    this.tag = String.format(TAG_FORMAT, tag);
  }

  public Integer getResource() {
    return resource;
  }

  public String getCode() {
    return code;
  }

  public String getTag() {
    return tag;
  }

  public String tagToCode(String message) {
    return message.replace(tag, code);
  }

  public String codeToTag(String message) {
    return message.replace(code, tag).replace(code.toUpperCase(Locale.US),
        tag);
  }
}
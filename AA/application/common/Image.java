package com.application.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Image implements Serializable {

  private String img_id;
  private String buzz_id;
  private boolean isOwn;

  public String getImg_id() {
    return img_id;
  }

  public void setImg_id(String img_id) {
    this.img_id = img_id;
  }

  public String getBuzz_id() {
    return buzz_id;
  }

  public void setBuzz_id(String buzz_id) {
    this.buzz_id = buzz_id;
  }

  public boolean isOwn() {
    return isOwn;
  }

  public void setOwn(boolean isOwn) {
    this.isOwn = isOwn;
  }
}
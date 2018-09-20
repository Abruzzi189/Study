package com.application.entity;

import com.google.gson.annotations.SerializedName;

public class GiftItem {

  @SerializedName("gift_id")
  private String giftId;
  @SerializedName("cat_id")
  private String categoryId;
  @SerializedName("gift_pri")
  private int price;
  @SerializedName("gift_inf")
  private String info;
  @SerializedName("gift_name")
  private String name;

  public GiftItem(String giftId, String categoryId, int price, String info, String name) {
    super();
    this.giftId = giftId;
    this.categoryId = categoryId;
    this.price = price;
    this.info = info;
    this.name = name;
  }

  public String getGiftId() {
    return giftId;
  }

  public void setGiftId(String giftId) {
    this.giftId = giftId;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

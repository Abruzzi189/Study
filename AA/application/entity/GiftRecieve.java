package com.application.entity;

public class GiftRecieve {

  private String giftId;
  private int number;

  public GiftRecieve(String giftId, int number) {
    this.giftId = giftId;
    this.number = number;
  }

  public String getGiftId() {
    return giftId;
  }

  public void setGiftId(String giftId) {
    this.giftId = giftId;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
}

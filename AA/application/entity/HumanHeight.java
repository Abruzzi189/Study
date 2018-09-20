package com.application.entity;

public class HumanHeight {

  private int mFoot = 0;
  private int mInch = 0;

  public HumanHeight(int foot, int inch) {
    this.mFoot = foot;
    this.mInch = inch;
  }

  public int getFoot() {
    return mFoot;
  }

  public void setFoot(int foot) {
    mFoot = foot;
  }

  public int getInch() {
    return mInch;
  }

  public void setInch(int inch) {
    mInch = inch;
  }
}

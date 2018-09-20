package com.application.entity;

/**
 * @author HoanDC
 */
public class ItemCommonAdapter {

  private String display;
  private boolean check;
  private int value;

  public ItemCommonAdapter() {
  }

  /*
   * public ItemCommonAdapter(String display, boolean check) { this.display =
   * display; this.check = check; }
   */

  public ItemCommonAdapter(String display, boolean check, int value) {
    super();
    this.display = display;
    this.check = check;
    this.value = value;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public boolean isCheck() {
    return check;
  }

  public void setCheck(boolean value) {
    this.check = value;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}

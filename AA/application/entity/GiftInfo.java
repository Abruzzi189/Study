package com.application.entity;

import java.util.ArrayList;

public class GiftInfo extends ProfileItem {

  private ArrayList<GiftRecieve> list;

  public GiftInfo() {
    super();
  }

  public GiftInfo(ArrayList<GiftRecieve> list) {
    super();
    this.list = list;
  }

  public ArrayList<GiftRecieve> getList() {
    return list;
  }

  public void setList(ArrayList<GiftRecieve> list) {
    this.list = list;
  }
}

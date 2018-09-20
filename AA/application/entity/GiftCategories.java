package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * @author HoanDC
 */
public class GiftCategories implements Serializable {

  @SerializedName("cat_id")
  private String id;
  @SerializedName("cat_num")
  private int number;
  @SerializedName("cat_name")
  private String name;
  private int type;

  public GiftCategories() {
  }

  /**
   * @param id is category's id;
   * @param number category's number
   * @param name is category's name
   * @param tpye define: type=1 is all, type = 0 other.
   */
  public GiftCategories(String id, int number, String name, int tpye) {
    this.id = id;
    this.number = number;
    this.name = name;
    this.type = tpye;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}

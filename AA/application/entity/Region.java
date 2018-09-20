package com.application.entity;

public class Region {

  private boolean isSelected = false;
  private int code;
  private String alias;
  private String name;

  public Region(int code, String alias, String name) {
    super();
    this.code = code;
    this.alias = alias;
    this.name = name;
  }

  public Region() {
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + code;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Region other = (Region) obj;
    if (code != other.code) {
      return false;
    }
    return true;
  }

}

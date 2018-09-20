package com.application.entity;

import java.util.List;


public class RegionGroup {

  private List<Region> region;
  private String name;

  public RegionGroup() {
  }

  public RegionGroup(List<Region> region, String name) {
    super();
    this.region = region;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Region> getRegion() {
    return region;
  }

  public void setRegion(List<Region> region) {
    this.region = region;
  }

  @Override
  public String toString() {
    return "RegionGroup [region=" + region + ", name=" + name + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((region == null) ? 0 : region.hashCode());
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
    RegionGroup other = (RegionGroup) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (region == null) {
      if (other.region != null) {
        return false;
      }
    } else if (!region.equals(other.region)) {
      return false;
    }
    return true;
  }

}

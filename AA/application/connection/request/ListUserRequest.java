package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class ListUserRequest extends RequestParams {

  private static final long serialVersionUID = -4025224625268031146L;

  @SerializedName("lower_age")
  private int lower_age;
  @SerializedName("upper_age")
  private int upper_age;
  @SerializedName("long")
  private double longitude;
  @SerializedName("lat")
  private double latitude;
  @SerializedName("distance")
  private int distance;
  @SerializedName("skip")
  private int skip;
  @SerializedName("take")
  private int take;
  @SerializedName("is_new_login")
  private boolean is_new_login;
  @SerializedName("sort_type")
  private int sort_type;
  @SerializedName("filter")
  private int filter;
  private int[] region;

  public ListUserRequest(String token, int sort_type, int filter,
      boolean is_new_login, int lower_age, int upper_age,
      double longitude, double latitude, int distance, int[] regions,
      int skip, int take) {
    super();
    this.api = "list_user_profile";
    this.token = token;
    this.sort_type = sort_type;
    this.lower_age = lower_age;
    this.upper_age = upper_age;
    this.longitude = longitude;
    this.latitude = latitude;
    this.distance = distance;
    this.skip = skip;
    this.take = take;
    this.is_new_login = is_new_login;
    this.filter = filter;
    this.region = regions;
  }

  public int getLower_age() {
    return lower_age;
  }

  public void setLower_age(int lower_age) {
    this.lower_age = lower_age;
  }

  public int getUpper_age() {
    return upper_age;
  }

  public void setUpper_age(int upper_age) {
    this.upper_age = upper_age;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public int getSkip() {
    return skip;
  }

  public void setSkip(int skip) {
    this.skip = skip;
  }

  public int getTake() {
    return take;
  }

  public void setTake(int take) {
    this.take = take;
  }

  public int getSort_type() {
    return sort_type;
  }

  public void setSort_type(int sort_type) {
    this.sort_type = sort_type;
  }

  public int getFilter() {
    return filter;
  }

  public void setFilter(int filter) {
    this.filter = filter;
  }

  public int[] getRegions() {
    return region;
  }

  public void setRegions(int[] regions) {
    this.region = regions;
  }

}

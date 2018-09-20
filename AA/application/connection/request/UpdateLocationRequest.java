package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class UpdateLocationRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 3498548494778933044L;
  @SerializedName("long")
  private double lon;
  private double lat;

  public UpdateLocationRequest(String token, double lon, double lat) {
    this.token = token;
    this.api = "upd_loc";
    this.lon = lon;
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

}

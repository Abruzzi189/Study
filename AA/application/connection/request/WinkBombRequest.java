package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class WinkBombRequest extends RequestParams {

  private static final long serialVersionUID = -5736590660515705978L;


  @SerializedName("bomb_num")
  private int winkBombNumber;

  @SerializedName("message")
  private String message;

  @SerializedName("long")
  private double longitude;

  @SerializedName("lat")
  private double latitude;

  public WinkBombRequest(String token, int winkBombNumber, String message) {
    super();
    this.api = "wink_bomb";
    this.token = token;
    this.winkBombNumber = winkBombNumber;
    this.message = message;
  }

  public WinkBombRequest(String token, int winkBombNumber, String message, double longitude,
      double latitude) {
    super();
    this.api = "wink_bomb";
    this.token = token;
    this.winkBombNumber = winkBombNumber;
    this.message = message;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}

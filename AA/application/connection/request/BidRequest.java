package com.application.connection.request;

public class BidRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -1569753484279745028L;
  private int point;

  public BidRequest(String token, int point) {
    this.api = "bid";
    this.token = token;
    this.point = point;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

}

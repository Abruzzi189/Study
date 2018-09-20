package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class AddBuzzRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 1466194191366455809L;
  @SerializedName("buzz_val")
  private String buzzValue;
  @SerializedName("buzz_type")
  private int buzzType;
  @SerializedName("cmt_val")
  private String cmtValue;

  /**
   * "token buzz_val buzz_type (0: status| 1: image| 2: gift) cmt_val (optional) buzz_type = 0 ==>
   * buzz_val = status buzz_type = 1 ==> buzz_val = imageId buzz_type = 2 ==> buzz_val = giftId In
   * case buzz_type = 1 : Use upload image api (api 25) with img_cat = 1 to get imageId In case
   * buzz_type = 2 : Choose gift, take giftId
   */

  public AddBuzzRequest(String token, String buzzValue, int buzzType) {
    super();
    this.api = "add_buzz";
    this.token = token;
    this.buzzValue = buzzValue;
    this.buzzType = buzzType;
  }

  public AddBuzzRequest(String token, String buzzValue, int buzzType,
      String cmtValue) {
    super();
    this.api = "add_buzz";
    this.token = token;
    this.buzzValue = buzzValue;
    this.buzzType = buzzType;
    this.cmtValue = cmtValue;
  }
}

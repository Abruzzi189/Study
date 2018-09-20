/**
 *
 */
package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author TuanPQ
 */
public class BuzzListRequest extends RequestParams {

  /**
   *
   */
  public static final int USER = 0;
  public static final int LOCAL = 1;
  public static final int FRIENDS = 2;
  public static final int FAVORITES = 3;
  private static final long serialVersionUID = 5590797105213400398L;
  @SerializedName("req_user_id")
  private String req_user_id;

  @SerializedName("buzz_kind")
  private int buzz_kind;

  @SerializedName("long")
  private double lon;

  @SerializedName("lat")
  private double lat;

  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  public BuzzListRequest(String token, String req_user_id, int buzz_kind, int skip, int take) {
    super();
    this.api = "get_buzz";
    this.token = token;
    if (req_user_id == null || req_user_id.length() == 0) {
      this.req_user_id = null;
    } else {
      this.req_user_id = req_user_id;
    }
    this.buzz_kind = buzz_kind;
    this.skip = skip;
    this.take = take;
  }

  public BuzzListRequest(String token, String req_user_id, int buzz_kind, double lon, double lat,
      int skip, int take) {
    super();
    this.api = "get_buzz";
    this.token = token;
    if (req_user_id == null || req_user_id.length() == 0) {
      this.req_user_id = null;
    } else {
      this.req_user_id = req_user_id;
    }
    this.buzz_kind = buzz_kind;
    this.lon = lon;
    this.lat = lat;
    this.skip = skip;
    this.take = take;
  }
}

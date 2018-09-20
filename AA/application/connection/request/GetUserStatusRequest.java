package com.application.connection.request;


import com.google.gson.annotations.SerializedName;

public class GetUserStatusRequest extends RequestParams {

  public static final int TYPE_NONE = 0;
  public static final int TYPE_EMAIL = 1;
  public static final int TYPE_FACEBOOK = 2;
  public static final int TYPE_MOCOM = 3;
  public static final int TYPE_FAMU = 4;
  private static final long serialVersionUID = -8273875603872201230L;
  @SerializedName("email")
  private String email;
  @SerializedName("fb_id")
  private String fb_id;
  @SerializedName("mocom_id")
  private String mocom_id;
  @SerializedName("famu_id")
  private String famu_id;

  public GetUserStatusRequest(int type, String data) {
    super();
    this.api = "get_user_status_by_email";
    switch (type) {
      case TYPE_EMAIL:
        this.email = data;
        break;
      case TYPE_FACEBOOK:
        this.fb_id = data;
        break;
      case TYPE_MOCOM:
        this.mocom_id = data;
        break;
      case TYPE_FAMU:
        this.famu_id = data;
        break;

      default:
        break;
    }
  }

}

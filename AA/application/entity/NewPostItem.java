package com.application.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ThoNh on 12/12/2017.
 */

public class NewPostItem {

  /**
   * buzz_id : 5a2e5aede4b0535a8d2c21dd user_id : 58fd744be4b06ed17577009a
   */

  @SerializedName("buzz_id")
  public String buzzId;
  @SerializedName("user_id")
  public String userId;
  @SerializedName("ava_id")
  public String avaId;
  @SerializedName("buzz_type")
  public int buzzType;
}

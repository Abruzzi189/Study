package com.application.connection.request;

import com.application.constant.UnlockType;
import com.google.gson.annotations.SerializedName;

public class CheckUnlockRequest extends RequestParams {

  private static final long serialVersionUID = -911302767641306610L;

  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("type")
  private int type;
  @SerializedName("image_id")
  private String imageId;
  @SerializedName("file_id")
  private String fileId;

  public CheckUnlockRequest(String token, String req_user_id) {
    super();
    this.api = "chk_unlck_version_3";
    this.token = token;
    this.req_user_id = req_user_id;
    type = UnlockType.BACKSTAGE;
  }

  public CheckUnlockRequest(String token, String req_user_id, int type,
      String id) {
    super();
    this.api = "chk_unlck_version_3";
    this.token = token;
    this.req_user_id = req_user_id;
    this.type = type;
    if (type == UnlockType.IMAGE) {
      imageId = id;
    } else {
      fileId = id;
    }
  }
}
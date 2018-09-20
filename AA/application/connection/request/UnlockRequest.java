package com.application.connection.request;

import com.application.constant.UnlockType;
import com.google.gson.annotations.SerializedName;

public class UnlockRequest extends RequestParams {

  private static final long serialVersionUID = 8744030392515324102L;

  @SerializedName("type")
  private int unlockType;
  @SerializedName("req_user_id")
  private String req_user_id;
  @SerializedName("image_id")
  private String imageId;
  @SerializedName("file_id")
  private String fileId;

  public UnlockRequest(String token, String req_user_id) {
    super();
    this.api = "unlck_version_3";
    this.token = token;
    this.unlockType = UnlockType.BACKSTAGE;
    this.req_user_id = req_user_id;
  }

  public UnlockRequest(String token, int unlockType, String req_user_id,
      String id) {
    super();
    this.api = "unlck_version_3";
    this.token = token;
    this.unlockType = unlockType;
    this.req_user_id = req_user_id;

    if (unlockType == UnlockType.IMAGE) {
      this.imageId = id;
    } else {
      this.fileId = id;
    }
  }
}
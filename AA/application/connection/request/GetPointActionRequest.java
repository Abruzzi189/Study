package com.application.connection.request;

public class GetPointActionRequest extends RequestParams {

  private static final long serialVersionUID = 3262536255457660838L;
  private String img_id = "";
  private String frd_id = "";

  public GetPointActionRequest(String token, String imgId) {
    super();
    this.api = "get_connection_point_action";
    this.token = token;
    this.img_id = imgId;
  }

  public GetPointActionRequest(String token, String imgId, String userId) {
    super();
    this.api = "get_connection_point_action";
    this.token = token;
    if (imgId != null && imgId.length() > 0) {
      this.img_id = imgId;
    }
    this.frd_id = userId;
  }
}
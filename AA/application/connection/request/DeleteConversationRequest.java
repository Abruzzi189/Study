package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class DeleteConversationRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 3054139656926562608L;
  @SerializedName("frd_id")
  private String[] userId;

  public DeleteConversationRequest(String token, String[] userId) {
    super();
    this.api = "del_conversations";
    this.token = token;
    this.userId = userId;
  }

}

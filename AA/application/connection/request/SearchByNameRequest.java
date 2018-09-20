package com.application.connection.request;

import com.google.gson.annotations.SerializedName;

public class SearchByNameRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 9004349603350446401L;

  @SerializedName("user_name")
  private String query;

  @SerializedName("skip")
  private int skip;

  @SerializedName("take")
  private int take;

  public SearchByNameRequest(String token, String query, int skip, int take) {
    super();
    this.api = "search_by_name";
    this.token = token;
    this.query = query;
    this.skip = skip;
    this.take = take;
  }
}

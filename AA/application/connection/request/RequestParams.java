package com.application.connection.request;

import com.application.connection.Method;
import com.google.gson.Gson;
import java.io.Serializable;

public abstract class RequestParams implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -2061820381644681434L;

  protected String api;
  protected String token;

  public String getApi() {
    return api;
  }

  public void setApi(String api) {
    this.api = api;
  }

  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getMethod() {
    return Method.POST;
  }
}

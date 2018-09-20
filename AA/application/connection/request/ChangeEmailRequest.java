package com.application.connection.request;

import com.application.constant.Constants;
import com.google.gson.annotations.SerializedName;

public class ChangeEmailRequest extends RequestParams {

  private static final long serialVersionUID = -5465377980387287519L;
  private final String API = "change_email";
  @SerializedName("email")
  private String email;
  @SerializedName("old_pwd")
  private String oldPass;
  @SerializedName("new_pwd")
  private String newPass;
  @SerializedName("original_pwd")
  private String newOriginalPass;
  @SerializedName("application")
  private int application;


  public ChangeEmailRequest(String token) {
    super();
    this.api = API;
    this.token = token;
    this.application = Constants.APPLICATION_TYPE;

  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setOldPass(String oldPass) {
    this.oldPass = oldPass;
  }

  public void setNewPass(String newPass) {
    this.newPass = newPass;
  }

  public void setNewOriginalPass(String newPass) {
    this.newOriginalPass = newPass;
  }
}
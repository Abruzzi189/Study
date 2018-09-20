package com.application.connection.request;

/**
 * @author tungdx
 */
public class LookAtMeRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 5117053671312213828L;
  private int skip;
  private int take;

  public LookAtMeRequest(int skip, int take, String token) {
    super();
    this.api = "look_at_me";
    this.skip = skip;
    this.take = take;
    this.token = token;
  }

  public int getSkip() {
    return skip;
  }

  public void setSkip(int skip) {
    this.skip = skip;
  }

  public int getTake() {
    return take;
  }

  public void setTake(int take) {
    this.take = take;
  }

}

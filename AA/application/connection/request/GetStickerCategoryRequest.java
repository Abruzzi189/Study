package com.application.connection.request;

import com.application.connection.Method;

public class GetStickerCategoryRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = 6676191827554198849L;
  public String cat_id;

  /**
   * Get sticker category,
   *
   * @param cat_id category of sticker, if cat_id=null -> get default category
   */
  public GetStickerCategoryRequest(String token, String cat_id) {
    super();
    this.api = "down_stk_cat";
    this.token = token;
    this.cat_id = cat_id;
  }

  @Override
  public int getMethod() {
    return Method.GET;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("api");
    builder.append("=");
    builder.append(this.api);
    builder.append("&");
    builder.append("token");
    builder.append("=");
    builder.append(this.token);
    builder.append("&");
    builder.append("sticker_cat_id");
    builder.append("=");
    if (cat_id != null) {
      builder.append(this.cat_id);
    }
    return builder.toString();
  }
}

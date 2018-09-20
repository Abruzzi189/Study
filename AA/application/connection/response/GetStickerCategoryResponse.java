package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class GetStickerCategoryResponse extends Response {

  private String data;
  private String packageId;
  private String order;
  public GetStickerCategoryResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    data = responseData.getText();
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        JSONObject object = jsonObject.getJSONObject("data");
        data = object.optString("zip_file");
        packageId = object.optString("cat_id");
        order = object.getJSONArray("order").toString();
        order = "{\"order\":" + order + "}";
        LogUtils.i("Order", order);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }

  }

  public String getOrder() {
    return order;
  }

  public String getData() {
    return data;
  }

  public String getPackageId() {
    return packageId;
  }

}

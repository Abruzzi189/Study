package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONObject;

public class GetPointResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 2940071428983607482L;
  private int point;

  public GetPointResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    int code = jsonObject.optInt("code");
    setCode(code);
    JSONObject dataJson = jsonObject.optJSONObject("data");
    if (dataJson != null) {
      point = dataJson.optInt("point");
    }
  }

  public int getPoint() {
    return point;
  }

}

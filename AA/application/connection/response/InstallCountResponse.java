package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class InstallCountResponse extends Response implements Serializable {

  private static final long serialVersionUID = -5397649281333266963L;

  public InstallCountResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}

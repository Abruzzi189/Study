package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.ui.ExtraPage;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetExtraPageResponse extends Response {

  private List<ExtraPage> mPageList;

  public GetExtraPageResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    mPageList = new ArrayList<ExtraPage>();
    try {
      int code = jsonObject.getInt("code");
      setCode(code);
      if (jsonObject.has("data")) {
        JSONArray array = jsonObject.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
          JSONObject jsonObject2 = array.getJSONObject(i);
          ExtraPage extraPage = new ExtraPage();
          extraPage.id = jsonObject2.optString("id");
          extraPage.title = jsonObject2.optString("title");
          extraPage.url = jsonObject2.optString("url");
          mPageList.add(extraPage);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<ExtraPage> getPageList() {
    return mPageList;
  }

}

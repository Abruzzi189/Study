package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.service.data.StickerCategoryInfo;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListDefaultStickerCategoryResponse extends Response {

  private static final long serialVersionUID = 1L;
  private List<StickerCategoryInfo> list;

  public ListDefaultStickerCategoryResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    list = new ArrayList<StickerCategoryInfo>();
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONArray dataJson = jsonObject.getJSONArray("data");
          int size = dataJson.length();
          for (int i = 0; i < size; i++) {
            JSONObject item = (JSONObject) dataJson.get(i);
            if (item != null) {
              StickerCategoryInfo info = new StickerCategoryInfo();
              if (item.has("cat_id")) {
                info.setId(item.getString("cat_id"));
              }

              if (item.has("cat_name")) {
                info.setName(item.getString("cat_name"));
              }

              if (item.has("stk_num")) {
                info.setNum(item.getInt("stk_num"));
              }

              if (item.has("version")) {
                info.setVersion(item.getInt("version"));
              }

              list.add(info);
            }
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<StickerCategoryInfo> getListCategory() {
    return list;
  }
}
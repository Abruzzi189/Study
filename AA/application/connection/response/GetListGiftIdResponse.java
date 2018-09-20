package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.GiftItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetListGiftIdResponse extends Response {

  private ArrayList<GiftItem> arrayList;

  public GetListGiftIdResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONArray dataJson = jsonObject.getJSONArray("data");
          Gson gson = new Gson();
          Type listType = new TypeToken<List<GiftItem>>() {
          }.getType();
          ArrayList<GiftItem> data = gson.fromJson(
              dataJson.toString(), listType);
          setArrayList(data);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<GiftItem> getArrayList() {
    return arrayList;
  }

  public void setArrayList(ArrayList<GiftItem> arrayList) {
    this.arrayList = arrayList;
  }

}

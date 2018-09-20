package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.GiftCategories;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AllCategoriesGiftResponse extends Response {

  private ArrayList<GiftCategories> categories;

  public AllCategoriesGiftResponse(ResponseData responseData) {
    super(responseData);
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
          Type listType = new TypeToken<List<GiftCategories>>() {
          }.getType();
          ArrayList<GiftCategories> data = gson.fromJson(
              dataJson.toString(), listType);
          setCategories(data);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void parseData(ResponseData responseData) {

  }

  public ArrayList<GiftCategories> getCategories() {
    return categories;
  }

  public void setCategories(ArrayList<GiftCategories> categories) {
    this.categories = categories;
  }

}

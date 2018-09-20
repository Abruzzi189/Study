package com.application.connection.response;

import com.application.common.Image;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListPublicImageResponse extends Response {

  private ArrayList<Image> listImage;

  public ListPublicImageResponse(ResponseData responseData) {
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
      if (jsonObject.has("data")) {
        JSONArray dataJsonArray = jsonObject.getJSONArray("data");
        ArrayList<Image> listImg = new ArrayList<Image>();
        for (int i = 0; i < dataJsonArray.length(); i++) {
          JSONObject dataJson = (JSONObject) dataJsonArray.get(i);
          Image image = new Image();
          if (dataJson.has("img_id")) {
            image.setImg_id(dataJson.getString("img_id"));
          }
          if (dataJson.has("buzz_id")) {
            image.setBuzz_id(dataJson.getString("buzz_id"));
          }
          LogUtils.d("image.getImg_id()", image.getImg_id());
          LogUtils.d("image.getBuzz_id()", image.getBuzz_id());
          listImg.add(image);
        }
        this.listImage = listImg;
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public ArrayList<Image> getListImage() {
    return listImage;
  }
}
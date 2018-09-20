package com.application.connection.response;

import com.application.common.Image;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListBackstageImageResponse extends Response {

  private ArrayList<Image> listImage;

  public ListBackstageImageResponse(ResponseData responseData) {
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
        ArrayList<Image> listImgId = new ArrayList<Image>();
        for (int i = 0; i < dataJsonArray.length(); i++) {
          String imgId = dataJsonArray.getString(i);
          Image image = new Image();
          image.setImg_id(imgId);
          image.setBuzz_id("");
          LogUtils.d("image.getImg_id()", image.getImg_id());
          listImgId.add(image);
        }
        this.listImage = listImgId;
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
package com.application.connection.response;

import com.application.common.Image;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListSendImageResponse extends Response {

  private ArrayList<Image> listImage;

  private int total;

  public ListSendImageResponse(ResponseData responseData) {
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
        JSONObject jsonData = jsonObject.getJSONObject("data");

        if (jsonData.has("list")) {
          JSONArray dataJsonArray = jsonData.getJSONArray("list");
          ArrayList<Image> listImgId = new ArrayList<Image>();
          for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject imgJson = dataJsonArray.getJSONObject(i);
            Image image = new Image();
            if (imgJson.has("img_id")) {
              String imgId = imgJson.getString("img_id");
              image.setImg_id(imgId);
              image.setBuzz_id("");
            }
            if (imgJson.has("is_own")) {
              image.setOwn(imgJson.getBoolean("is_own"));
            }
            LogUtils.d("image.getImg_id()", image.getImg_id());
            listImgId.add(image);
          }
          this.listImage = listImgId;
        }

        if (jsonData.has("total")) {
          setTotal(jsonData.getInt("total"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public ArrayList<Image> getListImage() {
    return listImage;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.payment.PointPackage;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PointPackageResponse extends Response {

  private List<PointPackage> pointPackages;

  public PointPackageResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        pointPackages = new ArrayList<>();
        JSONArray object = jsonObject.getJSONArray("data");
        for (int i = 0; i < object.length(); i++) {
          JSONObject object2 = object.getJSONObject(i);
          String packageId = object2.getString("pck_id");
          String price = null;
          if (object2.has("pri")) {
            price = object2.getString("pri");
          }
          int point = 0;
          if (object2.has("point")) {
            point = object2.getInt("point");
          }
          String des = "";
          if (object2.has("des")) {
            des = object2.getString("des");
          }
          String productId = null;
          if (object2.has("pro_id")) {
            productId = object2.getString("pro_id");
          }
          PointPackage pointPackage = new PointPackage(packageId,
              point, des, productId);
          pointPackage.setPrice(price);
          pointPackages.add(pointPackage);
        }
      }
    } catch (JSONException exception) {
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<PointPackage> getPointPackages() {
    return pointPackages;
  }

  public String[] getProductIdList() {
    if (pointPackages == null) {
      return null;
    }
    String[] ids = new String[pointPackages.size()];
    for (int i = 0; i < pointPackages.size(); i++) {
      ids[i] = pointPackages.get(i).getProductId();
    }
    return ids;
  }

  public void setPointPackageList(List<PointPackage> list) {
    this.pointPackages = list;
  }
}

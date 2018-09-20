package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.OnlineAlertItem;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListOnlineAlertResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String TAG = "OnlineAlertListResponse";

  private List<OnlineAlertItem> mOnlineAlertList;

  public ListOnlineAlertResponse(ResponseData responseData) {
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
        JSONArray jsonData = jsonObject.getJSONArray("data");
        this.mOnlineAlertList = new ArrayList<OnlineAlertItem>();
        int len = jsonData.length();
        for (int i = 0; i < len; i++) {
          OnlineAlertItem bui = new OnlineAlertItem();
          JSONObject bliJson = jsonData.getJSONObject(i);

          if (bliJson.has("user_id")) {
            bui.setUserId(bliJson.getString("user_id"));
          }
          if (bliJson.has("user_name")) {
            bui.setUserName(bliJson.getString("user_name"));
          }
          if (bliJson.has("age")) {
            bui.setAge(bliJson.getInt("age"));
          }
          if (bliJson.has("gender")) {
            bui.setGender(bliJson.getInt("gender"));
          }

          if (bliJson.has("ethn")) {
            bui.setEthn(bliJson.getInt("ethn"));
          }

          if (bliJson.has("inters_in")) {
            bui.setEthn(bliJson.getInt("inters_in"));
          }

          if (bliJson.has("ava_id")) {
            bui.setAvaId(bliJson.getString("ava_id"));
          }
          this.mOnlineAlertList.add(bui);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }
  }

  public List<OnlineAlertItem> getOnlineAlertList() {
    return this.mOnlineAlertList;
  }

}

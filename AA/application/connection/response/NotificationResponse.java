package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.entity.NotificationItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationResponse extends Response {

  private ArrayList<NotificationItem> list;

  public NotificationResponse(ResponseData responseData) {
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
        JSONArray dataJson = jsonObject.getJSONArray("data");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<NotificationItem>>() {
        }.getType();
        ArrayList<NotificationItem> data = gson.fromJson(
            dataJson.toString(), listType);

        ArrayList<NotificationItem> filter = new ArrayList<NotificationItem>();

        if (data != null && !data.isEmpty()) {
          for (NotificationItem item : data) {

            if (item.getType() == Constants.NOTI_LIKE_BUZZ
                || item.getType() == Constants.NOTI_COMMENT_BUZZ
                || item.getType() == Constants.NOTI_ONLINE_ALERT
                || item.getType() == Constants.NOTI_DAYLY_BONUS
                || item.getType() == Constants.NOTI_BUZZ_APPROVED
                || item.getType() == Constants.NOTI_BACKSTAGE_APPROVED
                || item.getType() == Constants.NOTI_FAVORITED_CREATE_BUZZ
                || item.getType() == Constants.NOTI_FROM_FREE_PAGE
                || item.getType() == Constants.NOTI_REPLY_YOUR_COMMENT
                || item.getType() == Constants.NOTI_REQUEST_CALL
                || item.getType() == Constants.NOTI_DENIED_BUZZ_IMAGE
                || item.getType() == Constants.NOTI_DENIED_BACKSTAGE
                || item.getType() == Constants.NOTI_APPROVE_BUZZ_TEXT
                || item.getType() == Constants.NOTI_DENIED_BUZZ_TEXT
                || item.getType() == Constants.NOTI_APPROVE_COMMENT
                || item.getType() == Constants.NOTI_DENIED_COMMENT
                || item.getType() == Constants.NOTI_APPROVE_SUB_COMMENT
                || item.getType() == Constants.NOTI_DENI_SUB_COMMENT
                || item.getType() == Constants.NOTI_APPROVE_USERINFO
                || item.getType() == Constants.NOTI_APART_OF_USERINFO
                || item.getType() == Constants.NOTI_DENIED_USERINFO) {
              filter.add(item);
            }
          }
        }

        setList(filter);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<NotificationItem> getList() {
    return list;
  }

  public void setList(ArrayList<NotificationItem> list) {
    this.list = list;
  }

}

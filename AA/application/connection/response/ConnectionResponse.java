package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.PeopleConnection;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionResponse extends Response {

  private static final long serialVersionUID = 1L;
  private List<PeopleConnection> listPeople;

  public ConnectionResponse(ResponseData responseData) {
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
          listPeople = new ArrayList<PeopleConnection>();
          for (int i = 0; i < dataJson.length(); i++) {
            JSONObject jsonObject2 = dataJson.getJSONObject(i);
            String user_id = null;
            String user_name = null;
            int gender = 0;
            int age = 0;
            String avaid = null;
            int isfrd = 0;
            boolean isonline = false;
            double lon = 0;
            double lat = 0;
            double dist = 0;
            long chk_time = 0;
            String status = "";
            int unreadMsg = 0;
            boolean isVoiceWaiting = false;
            boolean isVideoWaiting = false;
            String lastLogin = "";
            String checkOutTime = "";

            if (jsonObject2.has("user_id")) {
              user_id = jsonObject2.getString("user_id");
            }
            if (jsonObject2.has("user_name")) {
              user_name = jsonObject2.getString("user_name");
            }
            if (jsonObject2.has("gender")) {
              gender = jsonObject2.getInt("gender");
            }
            if (jsonObject2.has("age")) {
              age = jsonObject2.getInt("age");
            }

            if (jsonObject2.has("ava_id")) {
              avaid = jsonObject2.getString("ava_id");
            }
            if (jsonObject2.has("is_frd")) {
              isfrd = jsonObject2.getInt("is_frd");
            }
            if (jsonObject2.has("is_online")) {
              isonline = jsonObject2.getBoolean("is_online");
            }

            if (jsonObject2.has("long")) {
              lon = jsonObject2.getDouble("long");
            }
            if (jsonObject2.has("lat")) {
              lat = jsonObject2.getDouble("lat");
            }
            if (jsonObject2.has("dist")) {
              dist = jsonObject2.getDouble("dist");
            }
            if (jsonObject2.has("chk_time")) {
              chk_time = jsonObject2.getLong("chk_time");
            }
            if (jsonObject2.has("status")) {
              status = jsonObject2.getString("status");
            }
            if (jsonObject2.has("unread_num")) {
              unreadMsg = jsonObject2.getInt("unread_num");
            }
            if (jsonObject2.has("voice_call_waiting")) {
              isVoiceWaiting = jsonObject2
                  .getBoolean("voice_call_waiting");
            }
            if (jsonObject2.has("video_call_waiting")) {
              isVideoWaiting = jsonObject2
                  .getBoolean("video_call_waiting");
            }
            if (jsonObject2.has("last_login")) {
              lastLogin = jsonObject2.getString("last_login");
            }
            if (jsonObject2.has("check_out_time")) {
              checkOutTime = jsonObject2.getString("check_out_time");
            }

            PeopleConnection peopleConnection = new PeopleConnection(
                user_id, user_name, gender, age, avaid,
                isonline, isfrd, lon, lat, dist, chk_time,
                status, unreadMsg, isVoiceWaiting,
                isVideoWaiting, lastLogin, checkOutTime);
            listPeople.add(peopleConnection);
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      LogUtils.e("ConnectionResponse", "parse data error");
    }
  }

  public List<PeopleConnection> getListPeople() {
    return listPeople;
  }

}

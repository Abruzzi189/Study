package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.ui.chat.CallLog;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCallLogResponse extends Response {

  private static final long serialVersionUID = 1L;

  private List<CallLog> listCallLog;

  public GetCallLogResponse(ResponseData responseData) {
    super(responseData);
  }

  public List<CallLog> getListCallLog() {
    return this.listCallLog;
  }

  public void setListCallLog(List<CallLog> listCallLog) {
    this.listCallLog = listCallLog;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    listCallLog = new ArrayList<CallLog>();
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONArray arrayJson = jsonObject.getJSONArray("data");
          int length = arrayJson.length();
          for (int i = 0; i < length; i++) {
            JSONObject dataJson = arrayJson.getJSONObject(i);
            String userId = "";
            String userName = "";
            int gender = 1;
            int callType = 1;
            boolean isOnline = false;
            String avatarId = "";
            int duration = 0;
            int response = 0;
            String startTime = "";
            String lastLogin = "";
            double longitude = 0;
            double latitude = 0;
            double distance = 0;
            boolean isVoiceWaiting = false;
            boolean isVideoWaiting = false;
            if (dataJson.has("partner_id")) {
              userId = dataJson.getString("partner_id");
            }
            if (dataJson.has("partner_name")) {
              userName = dataJson.getString("partner_name");
            }
            if (dataJson.has("gender")) {
              gender = dataJson.getInt("gender");
            }
            if (dataJson.has("call_type")) {
              callType = dataJson.getInt("call_type");
            }
            if (dataJson.has("is_online")) {
              isOnline = dataJson.getBoolean("is_online");
            }
            if (dataJson.has("ava_id")) {
              avatarId = dataJson.getString("ava_id");
            }
            if (dataJson.has("duration")) {
              duration = dataJson.getInt("duration");
            }
            if (dataJson.has("partner_respond")) {
              response = dataJson.getInt("partner_respond");
            }
            if (dataJson.has("start_time")) {
              startTime = dataJson.getString("start_time");
            }
            if (dataJson.has("long")) {
              longitude = dataJson.getDouble("long");
            }
            if (dataJson.has("lat")) {
              latitude = dataJson.getDouble("lat");
            }
            if (dataJson.has("dist")) {
              distance = dataJson.getDouble("dist");
            }
            if (dataJson.has("last_login")) {
              lastLogin = dataJson.getString("last_login");
            }
            if (dataJson.has("voice_call_waiting")) {
              isVoiceWaiting = dataJson
                  .getBoolean("voice_call_waiting");
            }
            if (dataJson.has("video_call_waiting")) {
              isVideoWaiting = dataJson
                  .getBoolean("video_call_waiting");
            }
            CallLog callLog = new CallLog(userId, userName, gender,
                callType, isOnline, avatarId, duration,
                response, startTime, longitude, latitude,
                distance, lastLogin, isVoiceWaiting,
                isVideoWaiting);
            listCallLog.add(callLog);
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
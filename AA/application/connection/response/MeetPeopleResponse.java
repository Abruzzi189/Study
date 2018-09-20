package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.MeetPeople;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MeetPeopleResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private List<MeetPeople> peoples;
  private boolean isMoreAvaiable;

  // for API 27
  public MeetPeopleResponse(ResponseData responseData) {
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
        JSONArray dataArrayJson = jsonObject.getJSONArray("data");
        List<MeetPeople> peoples = new ArrayList<MeetPeople>();
        for (int i = 0; i < dataArrayJson.length(); i++) {
          JSONObject dataJson = dataArrayJson.getJSONObject(i);
          MeetPeople people = new MeetPeople();
          if (dataJson.has("user_id")) {
            people.setUserId(dataJson.getString("user_id"));
          }
          if (dataJson.has("user_name")) {
            people.setUser_name(dataJson.getString("user_name"));
          }
          if (dataJson.has("email")) {
            people.setEmail(dataJson.getString("email"));
          }
          if (dataJson.has("is_online")) {
            people.setIs_online(dataJson.getBoolean("is_online"));
          }
          if (dataJson.has("lat")) {
            people.setLat(dataJson.getDouble("lat"));
          }
          if (dataJson.has("long")) {
            people.setLongitute(dataJson.getDouble("long"));
          }
          if (dataJson.has("dist")) {
            people.setDistance(dataJson.getDouble("dist"));
          }
          if (dataJson.has("age")) {
            people.setAge(dataJson.getInt("age"));
          }
          if (dataJson.has("ava_id")) {
            people.setAva_id(dataJson.getString("ava_id"));
          }
          if (dataJson.has("gender")) {
            people.setGender(dataJson.getInt("gender"));
          }
          if (dataJson.has("status")) {
            people.setStatus(dataJson.getString("status"));
          }
          if (dataJson.has("unread_num")) {
            people.setUnreadNum(dataJson.getInt("unread_num"));
          }
          if (dataJson.has("last_login")) {
            people.setLastLogin(dataJson.getString("last_login"));
          }
          if (dataJson.has("video_call_waiting")) {
            people.setVideoCallWaiting(dataJson
                .getBoolean("video_call_waiting"));
          }
          if (dataJson.has("voice_call_waiting")) {
            people.setVoiceCallWaiting(dataJson
                .getBoolean("voice_call_waiting"));
          }
          if (dataJson.has("abt")) {
            people.setAbout(dataJson.getString("abt"));
          }
          if (dataJson.has("region")) {
            people.setRegion(dataJson.getInt("region"));
          }

          if (dataJson.has("is_fav")) {
            people.isFav = dataJson.getInt("is_fav");
          }

          peoples.add(people);
        }
        if (peoples.size() > 0) {
          setMoreAvaiable(true);
        } else {
          setMoreAvaiable(false);
        }
        setPeoples(peoples);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<MeetPeople> getPeoples() {
    return peoples;
  }

  public void setPeoples(List<MeetPeople> peples) {
    this.peoples = peples;
  }

  @Override
  protected void appendResponse(Response response) {
    super.appendResponse(response);
    MeetPeopleResponse newResponse = (MeetPeopleResponse) response;
    int serverSize = newResponse.getPeoples().size();
    if (serverSize > 0) {
      setMoreAvaiable(true);
      if (mResponse == null) {
        return;
      }

      MeetPeopleResponse curentResponse = (MeetPeopleResponse) mResponse;
      if (peoples == null) {
        peoples = new ArrayList<>(curentResponse.getPeoples());
      } else {
        int oldSize = peoples.size();
        filter(peoples, curentResponse.getPeoples());
        int curentSize = peoples.size();
        if (oldSize >= curentSize) {
          setMoreAvaiable(false);
        }
      }
    } else {
      setMoreAvaiable(false);
    }
  }

  public List<MeetPeople> filter(List<MeetPeople> old, List<MeetPeople> listNew) {
    listNew = filterInSeft(listNew);
    if (old.size() == 0) {
      old.addAll(listNew);
      return old;
    }
    for (MeetPeople meetPeople : listNew) {
      String id = meetPeople.getUserId();
      boolean duplicate = false;
      for (MeetPeople meetPeople2 : old) {
        if (id.equals(meetPeople2.getUserId())) {
          LogUtils.i("TAG", "duplicate");
          duplicate = true;
          break;
        }
      }
      if (!duplicate) {
        old.add(meetPeople);
      }
    }
    return old;
  }

  public List<MeetPeople> filterInSeft(List<MeetPeople> peoples) {
    List<MeetPeople> list = new ArrayList<MeetPeople>();
    for (MeetPeople meetPeople : peoples) {
      if (!list.contains(meetPeople)) {
        list.add(meetPeople);
      }
    }
    return list;
  }

  public boolean isMoreAvaiable() {
    return isMoreAvaiable;
  }

  public void setMoreAvaiable(boolean value) {
    isMoreAvaiable = value;
  }
}

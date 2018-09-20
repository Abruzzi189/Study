package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.MeetPeople;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchByNameResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -2255526295126138375L;

  private List<MeetPeople> people;

  private boolean isMoreAvaiable;

  public SearchByNameResponse(ResponseData responseData) {
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
        List<MeetPeople> people = new ArrayList<MeetPeople>();

        for (int i = 0; i < dataArrayJson.length(); i++) {
          JSONObject dataJson = dataArrayJson.getJSONObject(i);
          MeetPeople person = new MeetPeople();

          if (dataJson.has("user_id")) {
            person.setUserId(dataJson.getString("user_id"));
          }

          if (dataJson.has("user_name")) {
            person.setUser_name(dataJson.getString("user_name"));
          }

          if (dataJson.has("email")) {
            person.setEmail(dataJson.getString("email"));
          }

          if (dataJson.has("is_online")) {
            person.setIs_online(dataJson.getBoolean("is_online"));
          }

          if (dataJson.has("lat")) {
            person.setLat(dataJson.getDouble("lat"));
          }

          if (dataJson.has("long")) {
            person.setLongitute(dataJson.getDouble("long"));
          }

          if (dataJson.has("dist")) {
            person.setDistance(dataJson.getDouble("dist"));
          }

          if (dataJson.has("age")) {
            person.setAge(dataJson.getInt("age"));
          }

          if (dataJson.has("ava_id")) {
            person.setAva_id(dataJson.getString("ava_id"));
          }

          if (dataJson.has("gender")) {
            person.setGender(dataJson.getInt("gender"));
          }

          if (dataJson.has("status")) {
            person.setStatus(dataJson.getString("status"));
          }

          if (dataJson.has("unread_num")) {
            person.setUnreadNum(dataJson.getInt("unread_num"));
          }

          if (dataJson.has("last_login")) {
            person.setLastLogin(dataJson.getString("last_login"));
          }

          if (dataJson.has("video_call_waiting")) {
            person.setVideoCallWaiting(dataJson
                .getBoolean("video_call_waiting"));
          }

          if (dataJson.has("voice_call_waiting")) {
            person.setVoiceCallWaiting(dataJson
                .getBoolean("voice_call_waiting"));
          }

          if (dataJson.has("abt")) {
            person.setAbout(dataJson.getString("abt"));
          }

          if (dataJson.has("region")) {
            person.setRegion(dataJson.getInt("region"));
          }

          people.add(person);
        }

        setMoreAvaiable(people.size() > 0);

        setPeople(people);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<MeetPeople> getPeople() {
    return people;
  }

  public void setPeople(List<MeetPeople> people) {
    this.people = people;
  }

  public boolean isMoreAvaiable() {
    return isMoreAvaiable;
  }

  public void setMoreAvaiable(boolean value) {
    isMoreAvaiable = value;
  }
}

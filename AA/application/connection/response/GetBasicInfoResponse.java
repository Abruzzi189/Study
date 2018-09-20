package com.application.connection.response;

import android.content.Context;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.preferece.UserPreferences;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

public class GetBasicInfoResponse extends Response {

  private static final long serialVersionUID = 1L;

  @SerializedName("user_id")
  private String userId;

  @SerializedName("user_name")
  private String userName;

  @SerializedName("ava_id")
  private String avataId;

  @SerializedName("is_online")
  private boolean isOnline;

  @SerializedName("long")
  private double longitude;

  @SerializedName("lat")
  private double latitude;

  @SerializedName("dist")
  private double distance;

  @SerializedName("voice_call_waiting")
  private boolean isVoiceWaiting;

  @SerializedName("video_call_waiting")
  private boolean isVideoWaiting;

  @SerializedName("gender")
  private int gender;

  private int isFav = 0;

  @SerializedName("point")
  private int point;

  private int chat_point;
  private int view_image_point;
  private int view_image_time;
  private int watch_video_point;
  private int watch_video_time;
  private int listen_audio_point;
  private int listen_audio_time;

  public GetBasicInfoResponse(Context context, ResponseData responseData) {
    super(context, responseData);
    parseData(responseData);
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
        JSONObject dataJson = jsonObject.getJSONObject("data");

        if (dataJson.has("user_id")) {
          setUserId(dataJson.getString("user_id"));
        }
        if (dataJson.has("user_name")) {
          setUserName(dataJson.getString("user_name"));
        }
        if (dataJson.has("ava_id")) {
          setAvataId(dataJson.getString("ava_id"));
        }
        if (dataJson.has("is_online")) {
          setOnline(dataJson.getBoolean("is_online"));
        }
        if (dataJson.has("long")) {
          setLongitude(dataJson.getDouble("long"));
        }
        if (dataJson.has("lat")) {
          setLatitude(dataJson.getDouble("lat"));
        }
        if (dataJson.has("dist")) {
          setDistance(dataJson.getDouble("dist"));
        }
        if (dataJson.has("ava_id")) {
          setAvataId(dataJson.getString("ava_id"));
        }
        if (dataJson.has("voice_call_waiting")) {
          setVoiceWaiting(dataJson.getBoolean("voice_call_waiting"));
        }
        if (dataJson.has("video_call_waiting")) {
          setVideoWaiting(dataJson.getBoolean("video_call_waiting"));
        }
        if (dataJson.has("gender")) {
          setGender(dataJson.getInt("gender"));
        }
        if (dataJson.has("chat_point")) {
          setChatPoint(dataJson.getInt("chat_point"));
        }
        if (dataJson.has("point")) {
          setPoint(dataJson.getInt("point"));
        }

        if (dataJson.has("view_image_point")) {
          setVietImagePoint(dataJson.getInt("view_image_point"));
        }
        if (dataJson.has("view_image_time")) {
          setViewImageTime(dataJson.getInt("view_image_time"));
        }
        if (dataJson.has("watch_video_point")) {
          setWatchVideoPoint(dataJson.getInt("watch_video_point"));
        }
        if (dataJson.has("watch_video_time")) {
          setWatchVideoTime(dataJson.getInt("watch_video_time"));
        }
        if (dataJson.has("listen_audio_point")) {
          setListenAudioPoint(dataJson.getInt("listen_audio_point"));
        }
        if (dataJson.has("listen_audio_time")) {
          setListenAudioTime(dataJson.getInt("listen_audio_time"));
        }
        if (dataJson.has("is_fav")) {
          setIsFav(dataJson.getInt("is_fav"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getAvataId() {
    return avataId;
  }

  public void setAvataId(String avataId) {
    this.avataId = avataId;
    UserPreferences userPreferences = UserPreferences.getInstance();
    String currentUserId = userPreferences.getUserId();
    if (currentUserId.equals(userId)) {
      userPreferences.saveAvaId(avataId);
    }
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getDistance() {
    return this.distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public boolean isVoiceWaiting() {
    return this.isVoiceWaiting;
  }

  public void setVoiceWaiting(boolean isVoiceWaiting) {
    this.isVoiceWaiting = isVoiceWaiting;
  }

  public boolean isVideoWaiting() {
    return this.isVideoWaiting;
  }

  public void setVideoWaiting(boolean isVideoWaiting) {
    this.isVideoWaiting = isVideoWaiting;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public int getChatPoint() {
    return chat_point;
  }

  public void setChatPoint(int point) {
    this.chat_point = point;
  }

  public void setVietImagePoint(int point) {
    this.view_image_point = point;
  }

  public int getViewImagePoint() {
    return view_image_point;
  }

  public int getViewImageTime() {
    return view_image_time;
  }

  public void setViewImageTime(int time) {
    this.view_image_time = time;
  }

  public int getWatchVideoPoint() {
    return watch_video_point;
  }

  public void setWatchVideoPoint(int point) {
    this.watch_video_point = point;
  }

  public int getWatchVideoTime() {
    return watch_video_time;
  }

  public void setWatchVideoTime(int time) {
    this.watch_video_time = time;
  }

  public int getListenAudioPoint() {
    return listen_audio_point;
  }

  public void setListenAudioPoint(int point) {
    this.listen_audio_point = point;
  }

  public int getListenAudioTime() {
    return listen_audio_time;
  }

  public void setListenAudioTime(int time) {
    this.listen_audio_time = time;
  }

  public int isFav() {
    return isFav;
  }

  public void setIsFav(int isFav) {
    this.isFav = isFav;
  }
  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

}
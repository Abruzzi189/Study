package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.UserSetting;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoResponse extends Response implements Serializable {

  public static final int HEIGHT_ASKME = -1;
  private static final long serialVersionUID = 6050398293261734383L;
  @SerializedName("email")
  public String email;
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
  @SerializedName("abt")
  private String about;
  @SerializedName("age")
  private int age;
  @SerializedName("gender")
  private int gender;
  @SerializedName("relsh_stt")
  private int relationshipStatus;
  @SerializedName("bdy_tpe")
  private int[] bodyType;
  @SerializedName("inters_in")
  private int interestedIn;
  @SerializedName("lkg_for")
  private int[] lookingFor;
  // initial cm
  // tungdx modified
  /**
   * Height=-1 ->ask me
   */
  private double height;
  @SerializedName("ethn")
  private int ethnicity;
  @SerializedName("inters")
  private int[] interests;
  @SerializedName("frd_num")
  private int friendNumber;
  @SerializedName("fvt_num")
  private int favouritedNumber;
  @SerializedName("fav_num")
  private int favouristNumber;
  @SerializedName("point")
  private int point;
  @SerializedName("gift_num")
  private int giftNumber;
  @SerializedName("bckstg_num")
  private int backstageNumber;
  @SerializedName("bckstg_pri")
  private float priceBackstage;
  @SerializedName("pbimg_num")
  private int publicImageNumber;
  @SerializedName("is_fav")
  private boolean isFavorite;
  @SerializedName("is_frd")
  private boolean isFriend;
  @SerializedName("last_msg")
  private String lastMessage;
  @SerializedName("lst_gift")
  private String[] giftList;
  private String status;
  @SerializedName("is_alt")
  private int isAlt;

  @SerializedName("bir")
  private String birthday;

  @SerializedName("job")
  private int job;

  @SerializedName("region")
  private int region;

  @SerializedName("auto_region")
  private int autoRegion;

  @SerializedName("measurements")
  private int[] threeSizes;

  @SerializedName("cup")
  private int cupSize;

  @SerializedName("cute_type")
  private int cuteType;

  @SerializedName("type_of_man")
  private String typeMan;

  @SerializedName("fetish")
  private String fetish;

  @SerializedName("join_hours")
  private int joinHours;

  @SerializedName("hobby")
  private String hobby;

  @SerializedName("unread_num")
  private int unread_num;

  private boolean isVideoCallWaiting;
  private boolean isVoiceCallWaiting;
  private String lastLoginTime;
  private boolean isUpdateEmail;
  private String reviewingAvatar;

  private ProfilePictureData profilePicData;

  public UserInfoResponse(ResponseData responseData) {
    super(responseData);
  }

  // CuongNV : Khoi tao doi tuong UserInfoResponse dua vao dau vao la userID
  public UserInfoResponse(String userId) {
    super();
    this.userId = userId;
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
          if (dataJson.has("status")) {
            setStatus(dataJson.getString("status"));
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
          } else {
            setAvataId("");
          }
          if (dataJson.has("last_msg")) {
            setLastMessage(dataJson.getString("last_msg"));
          }
          if (dataJson.has("abt")) {
            setAbout(dataJson.getString("abt"));
          }
          if (dataJson.has("age")) {
            setAge(dataJson.getInt("age"));
          }
          if (dataJson.has("gender")) {
            setGender(dataJson.getInt("gender"));
          }
          if (dataJson.has("relsh_stt")) {
            setRelationshipStatus(dataJson.getInt("relsh_stt"));
          } else {
            setRelationshipStatus(-1);
          }
          if (dataJson.has("bdy_tpe")) {
            JSONArray bodyJson = dataJson.getJSONArray("bdy_tpe");
            int[] bodies = new int[bodyJson.length()];
            for (int i = 0; i < bodyJson.length(); i++) {
              bodies[i] = bodyJson.getInt(i);
            }
            setBodyType(bodies);
          }
          if (dataJson.has("inters_in")) {
            setInterestedIn(dataJson.getInt("inters_in"));
          }
          if (dataJson.has("lkg_for")) {
            JSONArray lookingForJson = dataJson
                .getJSONArray("lkg_for");

            int lookingFors[] = new int[lookingForJson.length()];
            for (int i = 0; i < lookingForJson.length(); i++) {
              lookingFors[i] = lookingForJson.getInt(i);
            }
            setLookingFor(lookingFors);
          }
          if (dataJson.has("height")) {
            setHeight(dataJson.getDouble("height"));
          } else {
            setHeight(HEIGHT_ASKME);
          }
          if (dataJson.has("is_alt")) {
            setIsAlt(dataJson.getInt("is_alt"));
          } else {
            setIsAlt(0);
          }
          if (dataJson.has("ethn")) {
            setEthnicity(dataJson.getInt("ethn"));
          } else {
            setEthnicity(-1);
          }
          if (dataJson.has("inters")) {
            JSONArray interestsJson = dataJson
                .getJSONArray("inters");
            int[] interests = new int[interestsJson.length()];
            for (int i = 0; i < interestsJson.length(); i++) {
              interests[i] = interestsJson.getInt(i);
            }
            setInterests(interests);
          }
          if (dataJson.has("frd_num")) {
            setFriendNumber(dataJson.getInt("frd_num"));
          }
          if (dataJson.has("fvt_num")) {
            setFavouritedNumber(dataJson.getInt("fvt_num"));
          }
          if (dataJson.has("fav_num")) {
            setFavouritedNumber(dataJson.getInt("fav_num"));
          }
          if (dataJson.has("point")) {
            setPoint(dataJson.getInt("point"));
          }
          if (dataJson.has("gift_num")) {
            setGiftNumber(dataJson.getInt("gift_num"));
          }
          if (dataJson.has("bckstg_num")) {
            setBackstageNumber(dataJson.getInt("bckstg_num"));
          } else {
            setBackstageNumber(0);
          }
          if (dataJson.has("bckstg_pri")) {
            setPriceBackstage(dataJson.getInt("bckstg_pri"));
          }
          if (dataJson.has("unread_num")) {
            setUnreadNum(dataJson.getInt("unread_num"));
          }
          if (dataJson.has("pbimg_num")) {
            setPublicImageNumber(dataJson.getInt("pbimg_num"));
          }
          if (dataJson.has("is_fav")) {
            if (dataJson.getInt("is_fav") == 0) {
              setFavorite(false);
            } else {
              setFavorite(true);
            }
          }

          if (dataJson.has("is_frd")) {
            if (dataJson.getInt("is_frd") == 0) {
              setFriend(false);
            } else {
              setFriend(true);
            }
          } else {
            setFriend(false);
          }
          if (dataJson.has("lst_gift")) {
            String[] list = new String[dataJson.getJSONArray(
                "lst_gift").length()];
            JSONArray giftJson = dataJson.getJSONArray("lst_gift");
            for (int i = 0; i < giftJson.length(); i++) {
              list[i] = giftJson.getString(i);
            }
            setGiftList(list);
          }

          if (dataJson.has("bir")) {
            setBirthday(dataJson.getString("bir"));
          }

          if (dataJson.has("job")) {

            // because client (android) has 2 array store job for
            // male and female
            // but server (backup) just only have 1 array store both
            // job for male and female
            // so when register,before update info user, if gender
            // equal male we are add a constant
            // UserSetting.NUMBER_JOBS_FEMALE
            // so here, we are must subtract an amount equal that
            if (getGender() == UserSetting.GENDER_MALE) {
              int job = dataJson.getInt("job");
              if (job >= UserSetting.NUMBER_JOBS_FEMALE) {
                job -= UserSetting.NUMBER_JOBS_FEMALE;
              }
              setJob(job);
            } else {
              setJob(dataJson.getInt("job"));
            }
          } else {

            setJob(-1);
          }

          if (dataJson.has("region")) {
            setRegion(dataJson.getInt("region"));
          }

          if (dataJson.has("auto_region")) {
            setAutoRegion(dataJson.getInt("auto_region"));
          }

          if (dataJson.has("measurements")) {
            int measurements[] = new int[dataJson.getJSONArray(
                "measurements").length()];
            JSONArray array = dataJson.getJSONArray("measurements");
            for (int i = 0; i < measurements.length; i++) {
              measurements[i] = array.getInt(i);
            }
            setThreeSizes(measurements);
          }

          if (dataJson.has("cup")) {
            setCupSize(dataJson.getInt("cup"));
          } else {
            setCupSize(-1);
          }

          if (dataJson.has("cute_type")) {
            setCuteType(dataJson.getInt("cute_type"));
          } else {
            setCuteType(-1);
          }

          if (dataJson.has("type_of_man")) {
            setTypeMan(dataJson.getString("type_of_man"));
          }

          if (dataJson.has("fetish")) {
            setFetish(dataJson.getString("fetish"));
          }

          if (dataJson.has("join_hours")) {
            setJoinHours(dataJson.getInt("join_hours"));
          } else {
            setJoinHours(-1);
          }

          if (dataJson.has("hobby")) {
            setHobby(dataJson.getString("hobby"));
          }

          UserPreferences userPreferences = UserPreferences
              .getInstance();
          String currentUserId = userPreferences.getUserId();
          if (currentUserId.equals(userId)) {
            userPreferences.saveAvaId(avataId);
          }

          if (dataJson.has("video_call_waiting")) {
            setVideoCallWaiting(dataJson
                .getBoolean("video_call_waiting"));
          }

          if (dataJson.has("voice_call_waiting")) {
            setVoiceCallWaiting(dataJson
                .getBoolean("voice_call_waiting"));
          }

          if (dataJson.has("last_login")) {
            setLastLoginTime(dataJson.getString("last_login"));
          }

          if (dataJson.has("update_email_flag")) {
            setUpdateEmail(dataJson.getInt("update_email_flag") == 1);
            UserPreferences.getInstance().saveUpdateEmail(
                isUpdateEmail);
          }

          if (dataJson.has("reviewing_avatar")) {
            setReviewingAvatar(dataJson
                .getString("reviewing_avatar"));
          }

          if (dataJson.has("email")) {
            email = dataJson.getString("email");
          }


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
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public int getRelationshipStatus() {
    return relationshipStatus;
  }

  public void setRelationshipStatus(int relationshipStatus) {
    this.relationshipStatus = relationshipStatus;
  }

  public int[] getBodyType() {
    return bodyType;
  }

  public void setBodyType(int[] bodyType) {
    this.bodyType = bodyType;
  }

  public int getInterestedIn() {
    return interestedIn;
  }

  public void setInterestedIn(int interestedIn) {
    this.interestedIn = interestedIn;
  }

  public int[] getLookingFor() {
    return lookingFor;
  }

  public void setLookingFor(int[] lookingFor) {
    this.lookingFor = lookingFor;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public int getEthnicity() {
    return ethnicity;
  }

  public void setEthnicity(int ethnicity) {
    this.ethnicity = ethnicity;
  }

  public int[] getInterests() {
    return interests;
  }

  public void setInterests(int[] interests) {
    this.interests = interests;
  }

  public int getFriendNumber() {
    return friendNumber;
  }

  public void setFriendNumber(int friendNumber) {
    this.friendNumber = friendNumber;
  }

  public int getFavouritedNumber() {
    return favouritedNumber;
  }

  public void setFavouritedNumber(int favouritedNumber) {
    this.favouritedNumber = favouritedNumber;
  }

  public int getFavouristNumber() {
    return favouristNumber;
  }

  public void setFavouristNumber(int favouristNumber) {
    this.favouristNumber = favouristNumber;
  }

  public int getPoint() {
    return point;
  }

  public void setPoint(int point) {
    this.point = point;
  }

  public int getGiftNumber() {
    return giftNumber;
  }

  public void setGiftNumber(int giftNumber) {
    this.giftNumber = giftNumber;
  }

  public int getBackstageNumber() {
    return backstageNumber;
  }

  public void setBackstageNumber(int backstageNumber) {
    this.backstageNumber = backstageNumber;
  }

  public float getPriceBackstage() {
    return priceBackstage;
  }

  public void setPriceBackstage(float priceBackstage) {
    this.priceBackstage = priceBackstage;
  }

  public int getPublicImageNumber() {
    return publicImageNumber;
  }

  public void setPublicImageNumber(int publicImageNumber) {
    this.publicImageNumber = publicImageNumber;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean isFavorite) {
    this.isFavorite = isFavorite;
  }

  public boolean isFriend() {
    return isFriend;
  }

  public void setFriend(boolean isFriend) {
    this.isFriend = isFriend;
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

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public String[] getGiftList() {
    return giftList;
  }

  public void setGiftList(String[] giftList) {
    this.giftList = giftList;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getIsAlt() {
    return isAlt;
  }

  public void setIsAlt(int isAlt) {
    this.isAlt = isAlt;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public int getJob() {
    return job;
  }

  public void setJob(int job) {
    this.job = job;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }

  public int getAutoRegion() {
    return autoRegion;
  }

  public void setAutoRegion(int autoRegion) {
    this.autoRegion = autoRegion;
  }

  public int[] getThreeSizes() {
    return threeSizes;
  }

  public void setThreeSizes(int[] threeSizes) {
    this.threeSizes = threeSizes;
  }

  public int getCupSize() {
    return cupSize;
  }

  public void setCupSize(int cupSize) {
    this.cupSize = cupSize;
  }

  public int getCuteType() {
    return cuteType;
  }

  public void setCuteType(int cuteType) {
    this.cuteType = cuteType;
  }

  public String getTypeMan() {
    return typeMan;
  }

  public void setTypeMan(String typeMan) {
    this.typeMan = typeMan;
  }

  public String getFetish() {
    return fetish;
  }

  public void setFetish(String fetish) {
    this.fetish = fetish;
  }

  public int getJoinHours() {
    return joinHours;
  }

  public void setJoinHours(int joinHours) {
    this.joinHours = joinHours;
  }

  public String getHobby() {
    return hobby;
  }

  public void setHobby(String hobby) {
    this.hobby = hobby;
  }

  public boolean isVideoCallWaiting() {
    return isVideoCallWaiting;
  }

  public void setVideoCallWaiting(boolean isVideoCallWaiting) {
    this.isVideoCallWaiting = isVideoCallWaiting;
  }

  public boolean isVoiceCallWaiting() {
    return isVoiceCallWaiting;
  }

  public void setVoiceCallWaiting(boolean isVoiceCallWaiting) {
    this.isVoiceCallWaiting = isVoiceCallWaiting;
  }

  public String getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(String lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }

  public boolean isUpdateEmail() {
    return isUpdateEmail;
  }

  public void setUpdateEmail(boolean isUpdateEmail) {
    this.isUpdateEmail = isUpdateEmail;
  }

  public String getReviewingAvatar() {
    return reviewingAvatar;
  }

  public void setReviewingAvatar(String reviewingAvatar) {
    this.reviewingAvatar = reviewingAvatar;
  }

  public int getUnreadNum() {
    return this.unread_num;
  }

  public void setUnreadNum(int unreadNum) {
    this.unread_num = unreadNum;
  }

  public ProfilePictureData getProfilePicData() {
    return profilePicData;
  }

  public void setProfilePicData(ProfilePictureData profilePicData) {
    this.profilePicData = profilePicData;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
package com.application.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.application.util.LogUtils;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationMessage implements Parcelable {

  public static final Creator<NotificationMessage> CREATOR = new Creator<NotificationMessage>() {
    @Override
    public NotificationMessage createFromParcel(Parcel source) {
      return new NotificationMessage(source);
    }

    @Override
    public NotificationMessage[] newArray(int size) {
      return new NotificationMessage[size];
    }
  };
  /**
   *
   */
  private static final long serialVersionUID = 3585497105767008035L;
  private final static String TAG_ALERT = "alert";
  private final static String TAG_BADGE = "badge";
  private final static String TAG_DATA = "data";
  private final static String TAG_LOC_KEY = "loc-key";
  private final static String TAG_LOC_ARGS = "loc-args";
  private final static String TAG_NOTI_TYPE = "noti_type";
  private final static String TAG_USER_ID = "userid";
  private final static String TAG_BUZZ_ID = "buzzid";
  private final static String TAG_OWNER_ID = "ownerid";
  private final static String TAG_IMAGE_ID = "imageid";
  private final static String TAG_CONTENT = "content";
  private final static String TAG_URL = "url";
  @SerializedName(TAG_LOC_KEY)
  private String lockey;
  @SerializedName(TAG_LOC_ARGS)
  private String[] logArgs;
  @SerializedName(TAG_NOTI_TYPE)
  private int notiType;
  @SerializedName(TAG_USER_ID)
  private String userid;
  @SerializedName(TAG_BUZZ_ID)
  private String buzzid;
  @SerializedName(TAG_OWNER_ID)
  private String ownerId;
  private String message = "";
  private String mImageId;
  @SerializedName(TAG_CONTENT)
  private String content;
  @SerializedName(TAG_URL)
  private String url;
  private int badgeNumber;

  public NotificationMessage() {
    super();
  }

  public NotificationMessage(String message) {
    super();

    //NotificationMessage notificationMessage = new NotificationMessage();
    setMessage(message);
    try {
      JSONObject jsonObject = new JSONObject(message);
      if (jsonObject.has(TAG_ALERT)) {
        JSONObject alertObject = new JSONObject(jsonObject.getString(TAG_ALERT));
        JSONObject alertInternalObject = new JSONObject(alertObject.toString());
        if (alertInternalObject.has(TAG_LOC_KEY)) {
          setLockey(alertObject.getString(TAG_LOC_KEY));
        }
        if (alertInternalObject.has(TAG_LOC_ARGS)) {
          JSONArray jsonArray = alertObject.getJSONArray(TAG_LOC_ARGS);
          if (jsonArray.length() > 0) {
            String[] logArr = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
              logArr[i] = jsonArray.getString(i);
            }
            setLogArgs(logArr);
          } else {
            setLogArgs(new String[]{"", ""});
          }
        }
      }
      if (jsonObject.has(TAG_BADGE)) {
        setBadgeNumber(jsonObject.getInt(TAG_BADGE));
      }
      if (jsonObject.has(TAG_DATA)) {
        JSONObject dataObject = new JSONObject(jsonObject.getString(TAG_DATA));
        LogUtils.e("NotificationMessage", "data=" + jsonObject.getString(TAG_DATA));
        JSONObject dataInternalObject = new JSONObject(dataObject.toString());
        if (dataInternalObject.has(TAG_NOTI_TYPE)) {
          LogUtils.e("NotificationMessage", "NotificationType=" + dataObject.getInt(TAG_NOTI_TYPE));
          setNotiType(dataObject.getInt(TAG_NOTI_TYPE));
        }
        if (dataInternalObject.has(TAG_USER_ID)) {
          setUserid(dataObject
              .getString(TAG_USER_ID));
        }
        if (dataInternalObject.has(TAG_OWNER_ID)) {
          setOwnerId(dataObject
              .getString(TAG_OWNER_ID));
        }
        if (dataInternalObject.has(TAG_BUZZ_ID)) {
          setBuzzid(dataObject
              .getString(TAG_BUZZ_ID));
        }
        if (dataInternalObject.has(TAG_IMAGE_ID)) {
          String imgId = dataObject.getString(TAG_IMAGE_ID);
          setImageId(imgId);
        }
        if (dataInternalObject.has(TAG_CONTENT)) {
          setContent(dataObject
              .getString(TAG_CONTENT));
        }
        if (dataInternalObject.has(TAG_URL)) {
          setUrl(dataObject.getString(TAG_URL));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    //return notificationMessage;
  }

  protected NotificationMessage(Parcel in) {
    this.lockey = in.readString();
    this.logArgs = in.createStringArray();
    this.notiType = in.readInt();
    this.userid = in.readString();
    this.buzzid = in.readString();
    this.ownerId = in.readString();
    this.message = in.readString();
    this.mImageId = in.readString();
    this.content = in.readString();
    this.url = in.readString();
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getLockey() {
    return lockey;
  }

  public void setLockey(String lockey) {
    this.lockey = lockey;
  }

  public String[] getLogArgs() {
    return logArgs;
  }

  public void setLogArgs(String[] logArgs) {
    this.logArgs = logArgs;
  }

  public int getNotiType() {
    return notiType;
  }

  public void setNotiType(int notiType) {
    this.notiType = notiType;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getBuzzid() {
    return buzzid;
  }

  public void setBuzzid(String buzzid) {
    this.buzzid = buzzid;
  }

  public String getMessage() {
    return message;
  }

  private void setMessage(String message) {
    this.message = message;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getImageId() {
    return mImageId;
  }

  public void setImageId(String mImageId) {
    this.mImageId = mImageId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.lockey);
    dest.writeStringArray(this.logArgs);
    dest.writeInt(this.notiType);
    dest.writeString(this.userid);
    dest.writeString(this.buzzid);
    dest.writeString(this.ownerId);
    dest.writeString(this.message);
    dest.writeString(this.mImageId);
    dest.writeString(this.content);
    dest.writeString(this.url);
  }

  public void setBadgeNumber(int badgeNumber) {
    this.badgeNumber = badgeNumber;
  }

  public int getBadgeNumber() {
    return badgeNumber;
  }
}

package com.application.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * author: LinhNC
 * for store data send from service to client interface with service
 */
public class DataObject implements Parcelable {

  public static final Parcelable.Creator<DataObject> CREATOR = new Creator<DataObject>() {

    @Override
    public DataObject[] newArray(int size) {
      return new DataObject[size];
    }

    @Override
    public DataObject createFromParcel(Parcel source) {
      return new DataObject(source);
    }
  };
  public int dataInt;
  public String dataString = "";
  // Information of a calling session
  public long sessionId;
  public int mediaType;
  public String caller;
  public String callee;
  public boolean hasEarlyMedia;
  public String callerDisplayName;
  public String calleeDisplayName;
  public int statusCode;
  public String statusText;
  public int callState;    //status of telephone call: ringing/call end.
  public DataObject(int dataInt) {
    this.dataInt = dataInt;
  }
  public DataObject(String dataString) {
    this.dataString = dataString;
  }

  public DataObject(int dataInt, String dataString) {
    this.dataInt = dataInt;
    this.dataString = dataString;
  }

  public DataObject(Parcel parcel) {
    dataInt = parcel.readInt();
    dataString = parcel.readString();
    sessionId = parcel.readLong();
    mediaType = parcel.readInt();
    caller = parcel.readString();
    callee = parcel.readString();
    parcel.readBooleanArray(new boolean[]{hasEarlyMedia});
    callerDisplayName = parcel.readString();
    calleeDisplayName = parcel.readString();
    statusCode = parcel.readInt();
    statusText = parcel.readString();
    callState = parcel.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(dataInt);
    dest.writeString(dataString);
    dest.writeLong(sessionId);
    dest.writeInt(mediaType);
    dest.writeString(caller);
    dest.writeString(callee);
    dest.writeBooleanArray(new boolean[]{hasEarlyMedia});
    dest.writeString(callerDisplayName);
    dest.writeString(calleeDisplayName);
    dest.writeInt(statusCode);
    dest.writeString(statusText);
    dest.writeInt(callState);
  }
}

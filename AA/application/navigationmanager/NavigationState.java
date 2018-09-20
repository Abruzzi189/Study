package com.application.navigationmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationState implements Parcelable {

  public static final Creator<NavigationState> CREATOR = new Creator<NavigationState>() {

    @Override
    public NavigationState[] newArray(int size) {
      return new NavigationState[size];
    }

    @Override
    public NavigationState createFromParcel(Parcel source) {
      return new NavigationState(source);
    }
  };
  public int placeholder;
  public String backStackName;

  public NavigationState(Parcel parcel) {
    placeholder = parcel.readInt();
    backStackName = parcel.readString();
  }

  public NavigationState(int type, String backStackName) {
    this.placeholder = type;
    this.backStackName = backStackName;
  }

  public NavigationState(int type) {
    this(type, Integer.toString((int) (Integer.MAX_VALUE * Math.random())));
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(placeholder);
    dest.writeString(backStackName);
  }
}

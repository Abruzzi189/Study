package com.example.thangpq.demolistimage;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaFile implements Parcelable{
    public String name;
    public String path;
    public String folder;
    public long time;
    public String mineType;

    protected MediaFile(Parcel in) {
        name = in.readString();
        path = in.readString();
        folder = in.readString();
        time = in.readLong();
        mineType = in.readString();
    }

    public MediaFile(String name, String path, String folder, long time, String mineType) {
        this.name = name;
        this.path = path;
        this.folder = folder;
        this.time = time;
        this.mineType = mineType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public static Creator<MediaFile> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(folder);
        dest.writeLong(time);
        dest.writeString(mineType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };
}

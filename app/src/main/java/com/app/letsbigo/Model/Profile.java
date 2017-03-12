package com.app.letsbigo.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nguyennam on 2/19/17.
 */

public class Profile implements Parcelable {
    private String name;
    private String thumbnail;
    private String url;
    private String status;
    private String view;
    private long sid;

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public static String LIVE_URL = "live_url";
    public static String SID = "sid";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }



    public Profile() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.thumbnail);
        dest.writeString(this.url);
        dest.writeString(this.status);
        dest.writeString(this.view);
        dest.writeLong(this.sid);
    }

    protected Profile(Parcel in) {
        this.name = in.readString();
        this.thumbnail = in.readString();
        this.url = in.readString();
        this.status = in.readString();
        this.view = in.readString();
        this.sid = in.readLong();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}

package com.example.dmc;

import android.os.Parcel;
import android.os.Parcelable;

public class Bullet implements Parcelable {

    private String uid;
    private String comment;

    public Bullet(String uid, String comment) {
        String[] temp=uid.split(",");
        this.uid = temp[temp.length-3];
        this.comment = comment;
    }

    protected Bullet(Parcel in) {
        uid = in.readString();
        comment = in.readString();
    }

    public static final Creator<Bullet> CREATOR = new Creator<Bullet>() {
        @Override
        public Bullet createFromParcel(Parcel in) {
            return new Bullet(in);
        }

        @Override
        public Bullet[] newArray(int size) {
            return new Bullet[size];
        }
    };

    public String getComment() {
        return comment;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(comment);
    }
}


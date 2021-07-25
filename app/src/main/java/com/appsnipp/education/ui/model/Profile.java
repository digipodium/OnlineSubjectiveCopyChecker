/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Profile implements Parcelable {
    public String username;
    public String email;
    public String mobile;
    public String usertype;
    public String uid;

    public Profile() {
    }

    public Profile(String uid, String username, String email, String mobile, String usertype) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.usertype = usertype;
        this.uid = uid;
    }

    protected Profile(Parcel in) {
        username = in.readString();
        email = in.readString();
        mobile = in.readString();
        usertype = in.readString();
        uid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(usertype);
        dest.writeString(uid);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}

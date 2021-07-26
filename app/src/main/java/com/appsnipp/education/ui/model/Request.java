/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {
    public String sid;
    public String pdfName;
    public String message;

    public Request() {
    }

    public Request(String sid, String pdfName, String message) {
        this.sid = sid;
        this.pdfName = pdfName;
        this.message = message;
    }

    protected Request(Parcel in) {
        sid = in.readString();
        pdfName = in.readString();
        message = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeString(pdfName);
        dest.writeString(message);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
}
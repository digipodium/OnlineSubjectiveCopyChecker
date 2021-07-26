/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Pdf implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Pdf> CREATOR = new Parcelable.Creator<Pdf>() {
        @Override
        public Pdf createFromParcel(Parcel in) {
            return new Pdf(in);
        }

        @Override
        public Pdf[] newArray(int size) {
            return new Pdf[size];
        }
    };
    public String url;
    public String name;
    public String tid;
    public String teacher;
    public String date;
    public String marks;
    public boolean isChecked;
    public String subject;

    public Pdf(String url, String name, String tid, String teacher, String date, String marks, String subject, boolean isChecked) {
        this.url = url;
        this.name = name;
        this.tid = tid;
        this.teacher = teacher;
        this.date = date;
        this.marks = marks;
        this.isChecked = isChecked;
        this.subject = subject;
    }

    public Pdf() {
    }

    protected Pdf(Parcel in) {
        url = in.readString();
        name = in.readString();
        tid = in.readString();
        teacher = in.readString();
        date = in.readString();
        marks = in.readString();
        isChecked = in.readByte() != 0x00;
        subject = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(tid);
        dest.writeString(teacher);
        dest.writeString(date);
        dest.writeString(marks);
        dest.writeString(subject);
        dest.writeByte((byte) (isChecked ? 0x01 : 0x00));
    }
}
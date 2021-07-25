/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Teacher implements Parcelable {
    public String tid;
    public String subject;
    public String department;
    public String gender;

    public Teacher(String tid, String subject, String department, String gender) {
        this.tid = tid;
        this.subject = subject;
        this.department = department;
        this.gender = gender;
    }

    public Teacher() {
    }

    protected Teacher(Parcel in) {
        tid = in.readString();
        subject = in.readString();
        department = in.readString();
        gender = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tid);
        dest.writeString(subject);
        dest.writeString(department);
        dest.writeString(gender);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Teacher> CREATOR = new Parcelable.Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };
}
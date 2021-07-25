/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    public String sid;
    public String course;
    public String semester;
    public String roll;
    public String gender;

    public Student() {
    }

    public Student(String sid, String course, String semester, String roll, String gender) {
        this.sid = sid;
        this.course = course;
        this.semester = semester;
        this.roll = roll;
        this.gender = gender;
    }

    protected Student(Parcel in) {
        sid = in.readString();
        course = in.readString();
        semester = in.readString();
        roll = in.readString();
        gender = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeString(course);
        dest.writeString(semester);
        dest.writeString(roll);
        dest.writeString(gender);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
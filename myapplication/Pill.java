package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Pill implements Parcelable { // We implement the Parcelable interface so that we can transfer the arrays between activities
    private String pillName; // name of pill
    private String time; // time to be taken
    private int id; // unique ID for each pill

    public Pill(String pillName, String time) {
        this.pillName = pillName;
        this.time = time;
        this.id =  (int) hashCode(); // as identifier we use the hashcode of the pill
    }

    protected Pill(Parcel in) {
        pillName = in.readString();
        time = in.readString();
        id = in.readInt();
    }

    public static final Creator<Pill> CREATOR = new Creator<Pill>() {
        @Override
        public Pill createFromParcel(Parcel in) {
            return new Pill(in);
        }

        @Override
        public Pill[] newArray(int size) {
            return new Pill[size];
        }
    };

    public String getPillName() {
        return pillName;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(pillName);
        parcel.writeString(time);
        parcel.writeInt(id);
    }

    @Override
    public boolean equals(Object o) { // in order to check if the pill is contained in array list
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pill pill = (Pill) o;

        if (!pillName.equals(pill.pillName)) return false;
        return time.equals(pill.time);
    }

    @Override
    public int hashCode() { // in order to check if a pill is contained in an array list
        int result = pillName.hashCode();
        result = 31 * result + time.hashCode();
        return result;
    }
}


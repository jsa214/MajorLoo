package com.sfuapichallenge.droptableteam.majorloo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by seongchanlee on 2017. 1. 7..
 */

public class Washroom {
    String name, address, type, location, summerHours, winterHours, wheelchairAccess;
    String note, maintainer;
    LatLng latLng;
    float numOfStars;

    public Washroom(String name, String address, String type, String location, String summerHours,
                    String winterHours, String wheelchairAccess, String note, String maintainer,
                    LatLng latLng, float numOfStars) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.location = location;
        this.summerHours = summerHours;
        this.winterHours = winterHours;
        this.wheelchairAccess = wheelchairAccess;
        this.note = note;
        this.maintainer = maintainer;
        this.latLng = latLng;
        this.numOfStars = numOfStars;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getSummerHours() {
        return summerHours;
    }

    public String getWinterHours() {
        return winterHours;
    }

    public String getWheelchairAccess() {
        return wheelchairAccess;
    }

    public String getNote() {
        return note;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public float getNumOfStars() { return numOfStars; }

    public void setNumOfStars(float numOfStars) { this.numOfStars = numOfStars; }
}

package com.niu.myapplication;

import android.app.Application;

/**
 * Created by A on 2016/12/18.
 */
public class LocationApplication extends Application {
    private String location,locationRef;

    public String getLocation() {
        return location;
    }

    public String getLocationRef() {
        return locationRef;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLocationRef(String locationRef) {
        this.locationRef = locationRef;
    }

}

package com.flaker.flaker;

/**
 * Created by rslee on 2/20/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Meeting {

    public String address;
    public Double longitude;
    public Double latitude;
    public String ownerId;
    public Long scheduledTime;

    public Meeting(){

    }

    public Meeting(String address, Double longitude, Double latitude, String ownerId, Long scheduledTime) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ownerId = ownerId;
        this.scheduledTime = scheduledTime;
    }
}

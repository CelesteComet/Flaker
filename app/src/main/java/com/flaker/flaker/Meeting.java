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
    public String owner;
    public Long scheduled_time;

    public Meeting(){

    }

    public Meeting(String address, Double longitude, Double latitude, String owner, Long scheduled_time) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.owner = owner;
        this.scheduled_time = scheduled_time;
    }
}

package com.flaker.flaker;

/**
 * Created by rslee on 2/25/18.
 */

public class InvitedUser {
    public Double longitude;
    public Double latitude;
    public String imgUrl;
    public String username;

    public InvitedUser() {

    }

    public InvitedUser(String imgUrl, String username) {
        this.longitude = null;
        this.latitude = null;
        this.imgUrl = imgUrl;
        this.username = username;
    }
}

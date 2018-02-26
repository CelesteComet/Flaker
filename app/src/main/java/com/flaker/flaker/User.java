package com.flaker.flaker;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alexkite on 2/21/18.
 */

@IgnoreExtraProperties
public class User {

    public String email;
    public String name;
    public String imageUrl;
    public Integer score;

    public User(String name, String email, String imageUrl) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}

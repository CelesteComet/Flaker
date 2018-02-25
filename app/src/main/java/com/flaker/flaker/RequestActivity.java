package com.flaker.flaker;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rslee on 2/23/18.
 */

public class RequestActivity {
    String address = "99 e st";
    Double longitude = 12.32;
    Double latitude = 15.80;
    String ownerId = "123ABC";
    Long scheduledTime = 12349241420L;

    Meeting meeting = new Meeting(address, longitude, latitude, ownerId, scheduledTime);

    private void addMeetingToDb(Meeting meeting) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = database.getReference();
        DatabaseReference mDestinationRef = mRootRef.child("meetups");

        Log.d("meeting", meeting.toString());

        mDestinationRef.push().setValue(meeting);
    }

}

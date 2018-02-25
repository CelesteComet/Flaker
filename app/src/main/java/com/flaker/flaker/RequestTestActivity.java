package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestTestActivity extends AppCompatActivity {


    String address = "99 e st";
    Double longitude = 12.32;
    Double latitude = 15.80;
    String ownerId = "123ABC";
    Long scheduledTime = 12349241420L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_test);

        Meeting meeting = new Meeting(address, longitude, latitude, ownerId, scheduledTime);

        addMeetingToDb(meeting);
    }

    private void addMeetingToDb(Meeting meeting) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = database.getReference();
        DatabaseReference mDestinationRef = mRootRef.child("meetups");

        Log.d("meeting", meeting.toString());

        mDestinationRef.push().setValue(meeting);
    }

}

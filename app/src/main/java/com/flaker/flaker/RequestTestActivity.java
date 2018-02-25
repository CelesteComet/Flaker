package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class RequestTestActivity extends BaseActivity {


    String address = "99 e st";
    Double longitude = 12.32;
    Double latitude = 15.80;
    String ownerId = "123ABC";
    Long scheduledTime = 12349241420L;

    String userId1 = "user_key";
    String meetup1 = "meetup_key";

    ArrayList<Meeting> meetings = new ArrayList<Meeting>();
    Meeting meetup = new Meeting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_test);

        Meeting meeting = new Meeting(address, longitude, latitude, ownerId, scheduledTime);

//        addMeetingToDb(meeting);
        fetchInvites(userId1);
        fetchMeetup(meetup1);
    }

    private void addMeetingToDb(Meeting meeting) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = database.getReference();
        DatabaseReference mDestinationRef = mRootRef.child("meetups");

        Log.d("meeting", meeting.toString());

        mDestinationRef.push().setValue(meeting);
    }

    private void fetchMeetup(String meetupId) {
        DatabaseReference meetupRef = MeetupsDatabase.child(meetupId);

        meetupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot meetupSnapshot) {
                meetup = meetupSnapshot.getValue(Meeting.class);
                System.out.println("MEETUP FETCHED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private ArrayList<Meeting> fetchInvites(String userId) {
        DatabaseReference UserInvitesReference = UsersDatabase.child(userId + "/invitedMeetups");
        UserInvitesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot invitedMeetupsSnapshot) {

                for (DataSnapshot indSnapshot: invitedMeetupsSnapshot.getChildren()) {
                    System.out.println(indSnapshot.getValue());
                    String meetupKey = indSnapshot.getValue().toString();
                    fetchMeetup(meetupKey);
                    meetings.add(meetup);

                }
                System.out.println(meetings.toString());
                System.out.println("INVITES FETCHED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return meetings;
    }

}

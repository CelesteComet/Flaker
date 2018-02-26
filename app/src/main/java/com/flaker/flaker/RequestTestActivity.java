package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class RequestTestActivity extends BaseActivity {


    String address = "99 very big st";
    Double longitude = -12.32;
    Double latitude = 150.80;
    String ownerId = "ABCDEFG";
    Long scheduledTime = 12349241420L;

    String userId1 = "user_key";
    String meetup1 = "meetup_key";
    User user1 = new User("winston", "winston@email.com", "gorilla.jpg");

    ArrayList<Meeting> meetings = new ArrayList<Meeting>();
    Meeting meetup = new Meeting();
    String meetingId;
    ArrayList<InvitedUser> invitedUsers = new ArrayList<InvitedUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_test);

        Meeting meeting = new Meeting(address, longitude, latitude, ownerId, scheduledTime);

//        addMeetingToDb(meeting);
//        fetchInvites(userId1);
//        fetchMeetup(meetup1);
//        fetchUserByEmail();
//        fetchCurrentUserOwnedMeetupId();
//        fetchLocationOfInvitedUsers(meetup1);
        addInvitedUserToMeetup(user1, meetup1);
//        updateInvitedUserLocationInMeetup(meetup1, longitude, latitude);

    }

    private void addMeetingToDb(Meeting meeting) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = database.getReference();
        DatabaseReference mDestinationRef = mRootRef.child("meetups");

        Log.d("meeting", meeting.toString());

        DatabaseReference newMeetupRef = mDestinationRef.push();
        newMeetupRef.setValue(meeting);
        String key = newMeetupRef.getKey();
        MeetupsDatabase.child(key).child("meetingId").setValue(key);
        UsersDatabase.child(meeting.ownerId).child("ownedMeetup").setValue(key);
    }

    private void addInvitedUserToMeetup(User user, String meetupKey) {
        //create an InvitedUser obj from user
        String newInvitedUserName = user.name;
        String newInvitedUserImgUrl = user.imageUrl;

        //Replace "G1223232" with newInvitedUserId when it becomes available
//        String newInvitedUserId = user.getKey();
        InvitedUser newInvitedUser = new InvitedUser(newInvitedUserImgUrl, newInvitedUserName);

        //add InvitedUser to acceptedUsers for Meetup
        DatabaseReference invitedUsersRef = MeetupsDatabase.child(meetupKey).child("acceptedUsers");
        invitedUsersRef.child("G1223232").setValue(newInvitedUser);

        //add Meetup to the invited user's InvitedMeetups
        DatabaseReference userInvitedMeetupsRef = UsersDatabase.child("G1223232").child("invitedMeetups");
        userInvitedMeetupsRef.push().setValue(meetupKey);
    }

    private void updateInvitedUserLocationInMeetup(String meetupId, Double longitude, Double latitude) {
        MeetupsDatabase.child(meetupId).child("acceptedUsers").child(currentUser.getUid()).child("latitude").setValue(latitude);
        MeetupsDatabase.child(meetupId).child("acceptedUsers").child(currentUser.getUid()).child("longitude").setValue(longitude);
    }

    private void fetchCurrentUserOwnedMeetupId() {
        //"ABCDEFG" should be replaced by the current user's id
        UsersDatabase.child("ABCDEFG").child("ownedMeetup").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetingId = dataSnapshot.getValue().toString();
                System.out.println("YAYAYAYAYAY");
                System.out.println(meetingId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    private ArrayList<InvitedUser> fetchLocationOfInvitedUsers(String meetingId) {
        DatabaseReference InvitedUsersRef = MeetupsDatabase.child(meetingId + "/acceptedUsers");
        InvitedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot invitedUsersSnapshot) {

                for (DataSnapshot indSnapshot: invitedUsersSnapshot.getChildren()) {
                    InvitedUser iuser = (indSnapshot.getValue(InvitedUser.class));
                    System.out.println(iuser.longitude.toString());
                    invitedUsers.add(iuser);
                }
//
                System.out.println(invitedUsers.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return invitedUsers;
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

//    private void fetchUserByEmail(String email) {
//        Query query = UsersDatabase.orderByChild("email").equalTo(email);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                System.out.println(user.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void fetchUserByEmail() {
//        UsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot indSnapshot : dataSnapshot.getChildren()) {
//                    System.out.println(indSnapshot.getValue());
//                    System.out.println("USER FETCHED");
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}

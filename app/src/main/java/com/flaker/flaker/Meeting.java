package com.flaker.flaker;

/**
 * Created by rslee on 2/20/18.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import static com.flaker.flaker.BaseActivity.MeetupsDatabase;
import static com.flaker.flaker.BaseActivity.UsersDatabase;


@IgnoreExtraProperties
public class Meeting {

    public String address;
    public Double longitude;
    public Double latitude;
    public String ownerId;
    public String ownerName;
    public Long scheduledTime;
    public String meetingId;

    public Meeting(){

    }

    public Meeting(String address, Double longitude, Double latitude, String ownerId, String ownerName, Long scheduledTime) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.scheduledTime = scheduledTime;
        this.meetingId = null;
    }

    public static String addMeetingToDb(FirebaseUser user, Meeting meeting) {
        DatabaseReference newMeetupRef = MeetupsDatabase.push();
        newMeetupRef.setValue(meeting);
        String key = newMeetupRef.getKey();
        Log.d("func", "Adding a new meeting with ID: " + key);
        MeetupsDatabase.child(key).child("meetingId").setValue(key);
        Meeting.addInvitedUserToMeetup(user, key);
        UsersDatabase.child(meeting.ownerId).child("ownedMeetup").setValue(key);
        return key;
    }

    public static void addInvitedUserToMeetup(final FirebaseUser user, final String meetupKey) {
        String newInvitedUserName = user.getDisplayName();
        String newInvitedUserImgUrl = user.getPhotoUrl().toString();

        // Replace "G1223232" with newInvitedUserId when it becomes available
        // String newInvitedUserId = user.getKey();
        InvitedUser newInvitedUser = new InvitedUser(newInvitedUserImgUrl, newInvitedUserName);

        //add InvitedUser to acceptedUsers for Meetup
        Task pushTask = MeetupsDatabase.child(meetupKey)
                .child("acceptedUsers").child(user.getUid()).setValue(newInvitedUser);

        pushTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DatabaseReference userInvitedMeetupsRef = UsersDatabase.child(user.getUid()).child("invitedMeetups");
                    userInvitedMeetupsRef.child(meetupKey).setValue(true);
                }
            }
        });




    }




}


package com.flaker.flaker;

/**
 * Created by rslee on 2/20/18.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

import static com.flaker.flaker.BaseActivity.MeetupsDatabase;
import static com.flaker.flaker.BaseActivity.UsersDatabase;


@IgnoreExtraProperties
public class Meeting implements Parcelable {

    public String address;
    public Double longitude;
    public Double latitude;
    public String ownerId;
    public String ownerName;
    public Long scheduledTime;
    public String meetingId;

    private int mData;

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

    public static void leaveMeetup(final FirebaseUser user, final String meetupKey) {
        //remove user from acceptedUsers for Meetup
        Task pushTask = MeetupsDatabase.child(meetupKey)
                .child("acceptedUsers").child(user.getUid()).removeValue();

        pushTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DatabaseReference userInvitedMeetupsRef = UsersDatabase.child(user.getUid()).child("invitedMeetups");
                    userInvitedMeetupsRef.child(meetupKey).removeValue();
                }
            }
        });
    }

    protected Meeting(Parcel in) {
        address = in.readString();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        ownerId = in.readString();
        ownerName = in.readString();
        scheduledTime = in.readByte() == 0x00 ? null : in.readLong();
        meetingId = in.readString();
        mData = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        dest.writeString(ownerId);
        dest.writeString(ownerName);
        if (scheduledTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(scheduledTime);
        }
        dest.writeString(meetingId);
        dest.writeInt(mData);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };
}


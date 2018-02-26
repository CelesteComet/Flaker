package com.flaker.flaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by alexkite on 2/25/18.
 */

public class InviteeListAdapter extends ArrayAdapter {
    Context context;
    public InviteeListAdapter(@NonNull Context context, ArrayList<String[]> friends) {
        super(context, R.layout.invitee_row, friends);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View inviteesView = theInflater.inflate(R.layout.invitee_row, parent, false);

        String[] singleFriend = (String[]) getItem(position);
        String singleName = singleFriend[0];

        String singlePhotoUrl = singleFriend[1];

        final String singleUserId = singleFriend[3];

        String singleUserEmail = singleFriend[4];

        TextView nameText = (TextView)  inviteesView.findViewById(R.id.invitee_name_text);
        ImageView photoView = (ImageView) inviteesView.findViewById(R.id.invitee_row_photo);
        Button inviteButton = (Button) inviteesView.findViewById(R.id.invite_button);

        final User tempUser = new User(singleName, singleUserEmail, singlePhotoUrl, 0);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInvitedUserToMeetup(tempUser, BaseActivity.meetingId, singleUserId);
            }
        });

        nameText.setText(singleName);
        Picasso.with(this.context).load(singlePhotoUrl).into(photoView);

        return inviteesView;
    }

    private void addInvitedUserToMeetup(User user, String meetupKey, String userId) {

        FirebaseDatabase database;
        DatabaseReference RootDatabaseReference;
        DatabaseReference UsersDatabase;
        DatabaseReference MeetupsDatabase;

        database = FirebaseDatabase.getInstance();
        RootDatabaseReference = database.getReference();
        UsersDatabase = RootDatabaseReference.child("users");
        MeetupsDatabase = RootDatabaseReference.child("meetups");

        //create an InvitedUser obj from user
        String newInvitedUserName = user.name;
        String newInvitedUserImgUrl = user.imageUrl;

        //Replace "G1223232" with newInvitedUserId when it becomes available
//        String newInvitedUserId = user.getKey();
        InvitedUser newInvitedUser = new InvitedUser(newInvitedUserImgUrl, newInvitedUserName);

        Log.d("hi", BaseActivity.meetingId);
        Log.d("meetupkey", meetupKey);
        //add InvitedUser to acceptedUsers for Meetup
        DatabaseReference invitedUsersRef = MeetupsDatabase.child(meetupKey).child("acceptedUsers");
        invitedUsersRef.child(userId).setValue(newInvitedUser);

        //add Meetup to the invited user's InvitedMeetups
        DatabaseReference userInvitedMeetupsRef = UsersDatabase.child("G1223232").child("invitedMeetups");
        userInvitedMeetupsRef.push().setValue(meetupKey);
    }


}

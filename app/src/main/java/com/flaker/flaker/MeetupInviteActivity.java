package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MeetupInviteActivity extends BaseActivity {

    String currentUserId;
    FirebaseDatabase database;
    DatabaseReference friendsListRef;
    public ArrayList<String[]> friendsList = new ArrayList<String[]>();
    ArrayAdapter adapter;
    private ListView inviteesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup_invite);
        currentUserId = currentUser.getUid();

        inviteesListView = (ListView) findViewById(R.id.invitee_list_view);

        adapter = new InviteeListAdapter(this, friendsList);
        inviteesListView.setAdapter(adapter);

        database = database.getInstance();

        final DatabaseReference mrootRef = database.getReference();
        friendsListRef = mrootRef.child("friends_list").child(currentUserId);

        fetchFriendsList();
    }

    private void fetchFriendsList() {

        friendsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot friendSnapShot: dataSnapshot.getChildren()) {
                    String[] friend = new String[5];

                    friend[0] = friendSnapShot.child("name").getValue().toString();
                    friend[1] = friendSnapShot.child("imageUrl").getValue().toString();
                    friend[2] = friendSnapShot.child("score").getValue().toString();
                    friend[3] = friendSnapShot.getKey().toString();
                    friend[4] = friendSnapShot.child("email").getValue().toString();

                    friendsList.add(friend);


                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




}

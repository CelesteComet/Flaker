package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsListActivity extends BaseActivity {
    String currentUserId;
    FirebaseDatabase database;
    DatabaseReference friendsListRef;
    public ArrayList<String[]> friendsList = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        currentUserId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference mrootRef = database.getReference();
        friendsListRef = mrootRef.child("friends_list").child(currentUserId);

        Log.d("friendslist", currentUserId);

        fetchFriendsList();

    }

    private void fetchFriendsList() {

        friendsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot friendSnapShot: dataSnapshot.getChildren()) {
                    String[] friend = new String[3];
                    friend[0] = friendSnapShot.child("name").getValue().toString();

                    friendsList.add(friend);

                    Log.d("friendslist2", friendsList.get(0)[0].toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
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
    ArrayAdapter adapter;
    private ListView friendsListView;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsListView = (ListView) findViewById(R.id.friends_list_view);

        adapter = new FriendsListAdapter(this, friendsList);
        friendsListView.setAdapter(adapter);

        currentUserId = currentUser.getUid();

        database = FirebaseDatabase.getInstance();

        usersRef = database.getReference().child("users");

        final DatabaseReference mrootRef = database.getReference();
        friendsListRef = mrootRef.child("friends_list").child(currentUserId);

        Log.d("friendslist", currentUserId);

        //initial friends list fetch
        fetchFriendsList();

    }

    private void getUser(String userId) {
        //put info into a user class

    }

    private void addUserToFriendsList() {
        //add user to friends list view and add to friends list in db (maybe split up?)

//        friendsListRef.child(userId).setValue(userObject);
    }

    private void fetchFriendsList() {

        friendsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot friendSnapShot: dataSnapshot.getChildren()) {
                    String[] friend = new String[3];
                    friend[0] = friendSnapShot.child("name").getValue().toString();
                    friend[1] = friendSnapShot.child("photo_url").getValue().toString();
                    friend[2] = friendSnapShot.child("score").getValue().toString();

                    friendsList.add(friend);

                    Log.d("friendslist2", friendsList.get(0)[0].toString());
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

package com.flaker.flaker;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by joeyfeng on 2/21/18.
 */

public class FriendActivity extends BaseActivity {

    private static final String TAG = "FriendActivity";

    ListView friendListView;
    private ArrayList<String[]> stuff = new ArrayList<String[]>();
    String[] names;
    String[] eta;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DatabaseReference myRef = UsersDatabase;
        
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        super.onCreate(savedInstanceState);
        Log.d("whatever", "friends");
        setContentView(R.layout.activity_friend);

        Resources res = getResources();
        friendListView = (ListView) findViewById(R.id.friendListView);
        names = res.getStringArray(R.array.names);
        eta = res.getStringArray(R.array.eta);


//        FriendsAdapter friendAdapter = new FriendsAdapter(this, names, eta);
//        friendListView.setAdapter(friendAdapter);
    }

    private void fetchFriends(DatabaseReference reference) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                // Clear data array
                stuff.clear();

                for (DataSnapshot indSnapshot: dataSnapshot.getChildren()) {
                    String[] user = new String[3];
                    user[0] = indSnapshot.child("email").getValue().toString();
                    user[1] = indSnapshot.child("name").getValue().toString();
                    user[2] = indSnapshot.child("photo_url").getValue().toString();
                    stuff.add(user);
                }

//                stuff.add(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

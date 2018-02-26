package com.flaker.flaker;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsListActivity extends BaseActivity {
    String currentUserId;
    FirebaseDatabase database;
    DatabaseReference friendsListRef;
    public ArrayList<String[]> friendsList = new ArrayList<String[]>();
    ArrayAdapter adapter;
    private ListView friendsListView;

    Button submitButton;
    EditText emailInput;
    TextView errorMessage;

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
        emailInput = (EditText) findViewById(R.id.email_input);
        errorMessage = (TextView) findViewById(R.id.error_message);

        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                Log.d("email", email);
                fetchUser(email);
            }
        });

        final DatabaseReference mrootRef = database.getReference();
        friendsListRef = mrootRef.child("friends_list").child(currentUserId);



        //initial friends list fetch
        fetchFriendsList();

    }

    private void fetchUser(String email) {
        if (currentUser.getEmail().equals(email)) {
            errorMessage.setText("You can't add yourself");
            return;
        }


        Query query = usersRef.orderByChild("name").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() == null) {
                    errorMessage.setText("User not found");
                } else {
                    Log.d("dlksjf", dataSnapshot.getValue().toString());
                    errorMessage.setText("");
                    addUserToFriendsList(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("failure", "failureee");
            }
        });
    }

    private void addUserToFriendsList(DataSnapshot users) {
        String[] friend = new String[3];
        DataSnapshot userSnapshot = null;

        for (DataSnapshot theUser: users.getChildren()) {
            userSnapshot = theUser;
        }

        Log.d("user", userSnapshot.toString());

        if (userSnapshot.child("email").getValue().equals(currentUser.getEmail())) {
            errorMessage.setText("Already a friend");
        }

        friend[0] = userSnapshot.child("name").getValue().toString();
        friend[1] = userSnapshot.child("imageUrl").getValue().toString();
        friend[2] = userSnapshot.child("score").getValue().toString();

        friendsList.add(friend);

        adapter.notifyDataSetChanged();

        User newFriend = new User(friend[0], null, friend[1]);

        addFriendToDb(newFriend, userSnapshot.getKey(), 10);
    }

    private void addFriendToDb(User user, String userId, int score) {
        friendsListRef.child(userId).setValue(user);
        friendsListRef.child(userId).child("score").setValue(score);
    }



    private void fetchFriendsList() {

        friendsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot friendSnapShot: dataSnapshot.getChildren()) {
                    String[] friend = new String[3];

                    friend[0] = friendSnapShot.child("name").getValue().toString();
                    friend[1] = friendSnapShot.child("imageUrl").getValue().toString();
                    friend[2] = friendSnapShot.child("score").getValue().toString();

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

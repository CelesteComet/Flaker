package com.flaker.flaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddFriendActivity extends BaseActivity {
    String currentUserId;
    Button submitButton;
    EditText emailInput;
    TextView errorMessage;
    DatabaseReference friendsListRef;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        final TextView textView1 = (TextView) findViewById(R.id.titleText);
        textView1.setText("New Friend");

        currentUserId = currentUser.getUid();

        final DatabaseReference mrootRef = database.getReference();
        friendsListRef = mrootRef.child("friends_list").child(currentUserId);


        usersRef = database.getReference().child("users");
        emailInput = (EditText) findViewById(R.id.email_input);
        errorMessage = (TextView) findViewById(R.id.error_message);

        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                fetchUser(email);
            }
        });

        includeDrawer(true);
    }

    private void fetchUser(String email) {
        if (currentUser.getEmail().equals(email)) {
            errorMessage.setText("You can't add yourself");
            return;
        }


        Query query = usersRef.orderByChild("email").equalTo(email);
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

    private Integer addUserToFriendsList(DataSnapshot users) {
        String[] friend = new String[4];
        DataSnapshot userSnapshot = null;

        for (DataSnapshot theUser: users.getChildren()) {
            userSnapshot = theUser;
        }

        Log.d("user", userSnapshot.getKey().toString());


        String key = userSnapshot.getKey().toString();

//        if (friendsListRef.hasChild(key) != null) {
//            errorMessage.setText("Already a friend");
//            return 0;
//        }


        friend[0] = userSnapshot.child("name").getValue().toString();
        friend[1] = userSnapshot.child("imageUrl").getValue().toString();
        friend[2] = userSnapshot.child("score").getValue().toString();
        String email = userSnapshot.child("email").getValue().toString();

//        friendsList.add(friend);
//
//        adapter.notifyDataSetChanged();

        User newFriend = new User(friend[0], email, friend[1], Integer.parseInt(friend[2]));

        addFriendToDb(newFriend, userSnapshot.getKey(), newFriend.score);

        return 0;
    }

    private void addFriendToDb(User user, String userId, int score) {
        friendsListRef.child(userId).setValue(user);
        friendsListRef.child(userId).child("score").setValue(score);



        Intent goToFriendListIntent = new Intent(this, FriendsListActivity.class);
        goToFriendListIntent.putExtra("newFriend", user.name);
        startActivity(goToFriendListIntent);
    }
}

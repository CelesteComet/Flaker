package com.flaker.flaker;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.flaker.flaker.BaseActivity.RootDatabaseReference;

/**
 * Created by xenovia on 3/13/18.
 */

public class MyApp extends Application {

    // Provide Firebase database reference to entire app
    protected FirebaseDatabase db;

    public static FirebaseDatabase database;
    public static DatabaseReference RootDatabaseReference;
    public static DatabaseReference UsersDatabase;
    public static DatabaseReference MeetupsDatabase;

    // User Authentication References
    public static FirebaseUser currentUser;
    protected FirebaseAuth mAuth;

    // Prevent first fetch
    private Boolean firstLoad = false;

    public MyApp() {
        // this method fires only once per application start.
        // getApplicationContext returns null here

        Log.i("main", "Constructor fired");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance();

        setupFirebaseReferences();
        setupFirebaseAuth();
        listenForNotifications();

        // this method fires once as well as constructor
        // but also application has context here

        Log.i("main", "onCreate fired");
    }

    private void setupFirebaseReferences() {
        Log.d("func", "Setting up Firebase database references in BaseActivity");
        database = FirebaseDatabase.getInstance();
        RootDatabaseReference = database.getReference();
        UsersDatabase = RootDatabaseReference.child("users");
        MeetupsDatabase = RootDatabaseReference.child("meetups");
    }

    protected void listenForNotifications() {

        ValueEventListener invitedMeetupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("BRUCE", dataSnapshot.toString());
                if (firstLoad) {
                    NotificationManagerBruce myNotificationManager = new NotificationManagerBruce(getApplicationContext());
                    myNotificationManager.createNotification("Don't Be a Flake!", "Your friend has requested a meetup!");
                } else {
                    firstLoad = true;
                }

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        UsersDatabase.child(currentUser.getUid().toString()).addValueEventListener(invitedMeetupsListener);
    }

    private void setupFirebaseAuth() {
        // Retrieve firebase authentication instance and get the current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
}

package com.flaker.flaker;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Date libraries
import java.util.Calendar;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    // Provide Firebase database reference to entire app
    protected FirebaseDatabase db = FirebaseDatabase.getInstance();

    FirebaseDatabase database;
    DatabaseReference RootDatabaseReference;
    DatabaseReference UsersDatabase;
    DatabaseReference MeetupsDatabase;

    // User Authentication References
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFirebaseAuth();
        setupFirebaseReferences();
        executeCalendarTest();
    }

    private void setupFirebaseReferences() {
        database = FirebaseDatabase.getInstance();
        RootDatabaseReference = database.getReference();
        UsersDatabase = RootDatabaseReference.child("users");
        MeetupsDatabase = RootDatabaseReference.child("meetups");
    }


    private void executeCalendarTest() {
        //Gets a calendar using the default time zone and locale.
        //The Calendar returned is based on the current time in the default time zone with the default FORMAT locale.

        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "LOOK HERE");
        Log.d(TAG, calendar.toString());
        //Returns a Date object representing this Calendar's time value (millisecond offset from the Epoch").
        Log.d(TAG, calendar.getTime().toString());
        //Returns this Calendar's time value in milliseconds.
        Log.d(TAG, String.valueOf(calendar.getTimeInMillis()));

        //Create a Date obj from a long
        Date date = new Date(calendar.getTimeInMillis());
        Log.d(TAG, date.toString());


        //Compares the time values (millisecond offsets from the Epoch) represented by two Calendar objects. Returns 1 or -1
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR_OF_DAY, 2);
        Log.d(TAG, calendar2.toString());
        Log.d(TAG, String.valueOf(calendar.compareTo(calendar2)));

        calendar.add(Calendar.HOUR_OF_DAY, 10);
        Log.d(TAG, calendar.toString());
        Log.d(TAG, String.valueOf(calendar.compareTo(calendar2)));
    }

    private void setupFirebaseAuth() {
        // Retrieve firebase authentication instance and get the current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    protected void includeDrawer() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Make drawer toggle-able
        final DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Get the navigationView and set an item selected listener
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.bringToFront();

        // Show user information in navigation view
        View headerView = navigationView.getHeaderView(0);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.nav_userName);
        TextView navUserName = (TextView) headerView.findViewById(R.id.nav_userEmail);
        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.

                int id = item.getItemId();

                if (id == R.id.home) {

                } else if (id == R.id.nav_friends) {
//                    Intent displayFriendsActivityIntent = new Intent(getApplicationContext(), FriendActivity.class);
//                    finish();
//                    startActivity(displayFriendsActivityIntent);
                } else if (id == R.id.nav_requests) {
                    // Create request list here
                    Intent displayRequestsActivityIntent = new Intent(getApplicationContext(), EtaActivity.class);
                    startActivity(displayRequestsActivityIntent);
                } else if (id == R.id.nav_manage) {


                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}

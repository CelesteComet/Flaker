package com.flaker.flaker;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

// Date libraries
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.*;
import java.text.*;

import static com.flaker.flaker.MapsActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class BaseActivity extends AppCompatActivity {

    // Provide Firebase database reference to entire app
    protected FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static FirebaseDatabase database;
    public static DatabaseReference RootDatabaseReference;
    public static DatabaseReference UsersDatabase;
    public static DatabaseReference MeetupsDatabase;

    public static String meetingId;
    public static boolean currentlyRouting = false;

    // Permissions
    protected static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static Boolean mLocationPermissionGranted = false;



    // User Authentication References
    public static FirebaseUser currentUser;
    protected FirebaseAuth mAuth;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();
        setupFirebaseAuth();
        setupFirebaseReferences();
//        listenForNotifications();
        executeCalendarTest();
    }

    protected void listenForNotifications() {

        ValueEventListener invitedMeetupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("BRUCE", "WOWOWOW NEW STUFF ");
                Log.d("BRUCE", dataSnapshot.toString());

                NotificationManagerBruce myNotificationManager = new NotificationManagerBruce(getApplicationContext());
                myNotificationManager.createNotification("HELLO", "WORLD");
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        UsersDatabase.child(currentUser.getUid().toString()).addValueEventListener(invitedMeetupsListener);
    }



    protected void print(String str) {
        System.out.println(str);
    }

    private void setupFirebaseReferences() {
        Log.d("func", "Setting up Firebase database references in BaseActivity");
        database = FirebaseDatabase.getInstance();
        RootDatabaseReference = database.getReference();
        UsersDatabase = RootDatabaseReference.child("users");
        MeetupsDatabase = RootDatabaseReference.child("meetups");
    }

    protected static void sendCurrentLatLngToDatabase(InvitedUser user, String meetupId) {
        MeetupsDatabase.child(meetupId).child("acceptedUsers").child(currentUser.getUid()).setValue(user);
    }

    // get seconds of meet up time and convert to normal time
    protected void epochParser(long epoch_sec) {
//        long unix_seconds = 1519623565;
        long unix_seconds = epoch_sec;
        //convert seconds to milliseconds
        Date date = new Date(unix_seconds*1000L);
        // format of the date
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");


        hour.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_hour = hour.format(date);
        min.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_min = min.format(date);
        time.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_time = time.format(date);
        System.out.println("TESTTT");
//        Prints out in HH:MM format
        System.out.println(normal_time);
//        Prints out just hour
//        System.out.println("\n"+normal_hour+"\n");
//        Prints out minutes
//        System.out.println("\n"+normal_min+"\n");
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

    protected void includeDrawer(Boolean back) {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Make drawer toggle-able
        final DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);



        if (back != true) {
            toggle.syncState();
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("cek", "home selected");
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        } else {
            Drawable icArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black);
            icArrow.setColorFilter(getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            myToolbar.setLogo(icArrow);
            myToolbar.setOnClickListener(new Toolbar.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
//            toggle.setDrawerIndicatorEnabled(true);
//            toggle.setHomeAsUpIndicator(R.drawable.ic_happy_black);
        }


        // Get the navigationView and set an item selected listener
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.bringToFront();

        // Show user information in navigation view
        View headerView = navigationView.getHeaderView(0);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.nav_userName);
        TextView navUserName = (TextView) headerView.findViewById(R.id.nav_userEmail);
        ImageView profileImg = (ImageView) headerView.findViewById(R.id.profileImg);
        Picasso.with(this).load(currentUser.getPhotoUrl()).into(profileImg);
        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.

                int id = item.getItemId();

                if (id == R.id.home) {

                } else if (id == R.id.nav_friends) {
                    Intent displayFriendsActivityIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
                    startActivity(displayFriendsActivityIntent);
                } else if (id == R.id.nav_etas) {
                    // Create request list here
                    if (currentlyRouting == false) {
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                                "Please begin a new route to view ETAs", Snackbar.LENGTH_LONG);
                        mySnackbar.show();
                    } else {
                        Intent displayETAsActivityIntent = new Intent(getApplicationContext(), EtaActivity.class);
                        startActivity(displayETAsActivityIntent);
                    }
                } else if (id == R.id.nav_requests) {
                    if (currentlyRouting == true) {
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                                "Please cancel your current route", Snackbar.LENGTH_LONG);
//                        mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
                        mySnackbar.show();
                    } else {

                        Intent displayRequestsActivityIntent = new Intent(getApplicationContext(), RequesteeViewActivity.class);
                        startActivity(displayRequestsActivityIntent);

                    }

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

//    public void addMeetingToDb(Meeting meeting) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference mRootRef = database.getReference();
//        DatabaseReference mDestinationRef = mRootRef.child("meetups");
//
//        Log.d("meeting", meeting.toString());
//
//        DatabaseReference newMeetupRef = mDestinationRef.push();
//        newMeetupRef.setValue(meeting);
//        String key = newMeetupRef.getKey();
//        BaseActivity.meetingId = key;
//        MeetupsDatabase.child(key).child("meetingId").setValue(key);
//        UsersDatabase.child(meeting.ownerId).child("ownedMeetup").setValue(key);
//    }

    public static String timeParse(int secondInput) {
        int seconds = secondInput;
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;


        String s1 = "";
        String s2 = "";
        String s3 = "";

        if (p2 > 0) {
            s2 = Integer.toString(p2) + "h ";
        } else {
            s2 = "";
        }

        if (p3 > 0) {
            s3 = Integer.toString(p3);
        } else {
            s3 = "0";
        }
        // return s2 + ":" + s3 + ":" + s1;
        // System.out.print(s2+s3 + "min");
        return s2 + s3 + "min";


    }

    public static String normalizeTime(Long epoch_sec) {
        //        long unix_seconds = 1519623565;
        long unix_seconds = epoch_sec;
        //convert seconds to milliseconds
        Date date = new Date(unix_seconds*1000L);
        // format of the date
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");


        hour.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_hour = hour.format(date);
        min.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_min = min.format(date);
        time.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        String normal_time = time.format(date);
        System.out.println("TESTTT");
//        Prints out in HH:MM format
        System.out.println(normal_time);
//        Prints out just hour
//        System.out.println("\n"+normal_hour+"\n");
//        Prints out minutes
//        System.out.println("\n"+normal_min+"\n");
        return normal_time;
    }

    protected void getLocationPermission() {
        Log.d("func", "Getting location permission in BaseActivity");
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),

                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("func", "Locations were permitted");
            mLocationPermissionGranted = true;
        } else {
            Log.d("func", "Locations not permitted");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }





}

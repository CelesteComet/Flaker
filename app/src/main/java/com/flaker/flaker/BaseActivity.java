package com.flaker.flaker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

// Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Date libraries
import java.util.Calendar;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRootRef = database.getReference();
    DatabaseReference mDestinationRef = mRootRef.child("meetings/1");

    // User Authentication References
    protected FirebaseUser currentUser;
    protected FirebaseAuth mAuth;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFirebaseAuth();
        executeCalendarTest();
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
}

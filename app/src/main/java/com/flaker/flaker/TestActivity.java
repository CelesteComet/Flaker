package com.flaker.flaker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.flaker.flaker.MyMapFragment.drawRoute;
import static com.flaker.flaker.MyMapFragment.lastKnownLatLng;
import static com.flaker.flaker.MyMapFragment.mFusedLocationProviderClient;
import static com.flaker.flaker.MyMapFragment.mLocationCallback;
import static com.flaker.flaker.MyMapFragment.moveMapToLatLng;
import static com.flaker.flaker.MyMapFragment.placeLatLng;
import static com.flaker.flaker.MyMapFragment.travelMode;
import static com.flaker.flaker.MyMapFragment.place;




public class TestActivity extends BaseActivity {

    public Fragment mMapFragment;
    public static Fragment bottomModalFragment;
    public static Boolean timeSelected;
    public Calendar scheduledTime = Calendar.getInstance();

    public static Boolean shouldRoute = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        mMapFragment = new MyMapFragment();

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.mapFrame, mMapFragment);
        transaction.commit();



        includeDrawer(false);
        setupAutoCompleteWidget();




    }


    private void setupAutoCompleteWidget() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Enter a rendezvous point");



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place _place) {
                place = _place;
                placeLatLng = _place.getLatLng();

                MyMapFragment.createSingleMarker(placeLatLng, null);
                bottomModalFragment = new BottomModalFragment();
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_bottom);
                transaction.add(R.id.bottomModal, bottomModalFragment);
                transaction.commit();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error for autocomplete
                Log.d("AWD", "An error occurred: " + status);
            }
        });
    }



    protected void updateUI(String ui) {
        ConstraintLayout mFloatingActionsMenu;
        Button flakeButton = (Button) findViewById(R.id.flakeButton);
        ViewGroup autocompleteLayout = findViewById(R.id.place_autocomplete_layout);
        timeSelected = false;
        switch (ui) {
            case "normal":
                currentlyRouting = false;
                MyMapFragment.stopFriendUpdates(meetingId);
                flakeButton.setVisibility(View.INVISIBLE);
                if (mLocationCallback != null) {
                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                }
                autocompleteLayout.setVisibility(View.VISIBLE);
                MyMapFragment.clearAll();
                meetingId = null;
                break;
            case "searching":
                // Views
                currentlyRouting = false;
                TextView placeTitle = findViewById(R.id.confirmTitleText);
                TextView placeAddress = findViewById(R.id.confirmAddressText);
                TextView eta = findViewById(R.id.confirmETAText);
                TextView meetingTime = findViewById(R.id.confirmMeetingTime);
                mFloatingActionsMenu = findViewById(R.id.fabby);

                autocompleteLayout.setVisibility(View.GONE);
                mFloatingActionsMenu.setVisibility(View.VISIBLE);
                placeTitle.setText(place.getName());
                placeAddress.setText(place.getAddress());
                break;
            case "navigating":
                flakeButton.setVisibility(View.VISIBLE);
                currentlyRouting = true;
                MyMapFragment.requestFriendUpdates(meetingId);
                MyMapFragment.requestLocationUpdates(meetingId);
                mFloatingActionsMenu = findViewById(R.id.fabby);
                mFloatingActionsMenu.setVisibility(View.INVISIBLE);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_bottom);
                transaction.remove(bottomModalFragment);
                transaction.commit();
                break;
            case "requesteeNavigating":
                currentlyRouting = true;
                flakeButton.setVisibility(View.VISIBLE);
                MyMapFragment.requestFriendUpdates(meetingId);
                MyMapFragment.requestLocationUpdates(meetingId);
                MyMapFragment.drawRoute(lastKnownLatLng, placeLatLng, Routing.TravelMode.DRIVING, this);
                findViewById(R.id.place_autocomplete_layout).setVisibility(View.INVISIBLE);

                break;
            default:
                break;
        }
    }


    public void selectTime(View view) {
        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeSelected = true; // timeSelected false in updateView
                Calendar calendar = Calendar.getInstance();
                scheduledTime.set(Calendar.HOUR_OF_DAY, i);
                scheduledTime.set(Calendar.MINUTE, i1);
                long sub = scheduledTime.getTimeInMillis() - calendar.getTimeInMillis();
                if (sub < 0) {
                    Toast.makeText(TestActivity.this, "Please select a time past the current time", Toast.LENGTH_SHORT).show();
                } else {
                    TextView confirmMeetingTime = findViewById(R.id.confirmMeetingTime);
                    String ampm = "";
                    if(i > 12) {
                        ampm = "PM";
                        i -= 12;
                    } else if (i == 12) {
                        ampm = "AM";
                    } else {
                        ampm = "AM";
                    }
                    confirmMeetingTime.setText("Meeting at " + Integer.toString(i) + ":" + Integer.toString(i1) + ampm);
                }
            }
        };
        TimePickerDialog mTimePicker = new TimePickerDialog(this, R.style.TimePickerTheme, mTimeSetListener, 12, 30, false);
        mTimePicker.show();
    }


    public void changeTravelMode(View view) {
        Integer viewId = view.getId();
        FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) this.findViewById(R.id.multiple_actions);
        menuMultipleActions.toggle();
        switch (viewId) {
            case R.id.action_a:
                Log.d(TAG, "*** CHOSE TO WALK ***");
                travelMode = Routing.TravelMode.WALKING;
                drawRoute(lastKnownLatLng, placeLatLng, Routing.TravelMode.WALKING, this);
                break;
            case R.id.action_b:
                Log.d(TAG, "*** CHOSE TO BIKE ***");
                travelMode = Routing.TravelMode.BIKING;
                drawRoute(lastKnownLatLng, placeLatLng, Routing.TravelMode.BIKING, this);
                break;
            case R.id.action_c:
                Log.d(TAG, "*** CHOSE TO DRIVE ***");
                travelMode = Routing.TravelMode.DRIVING;
                drawRoute(lastKnownLatLng, placeLatLng, Routing.TravelMode.DRIVING, this);
                break;
            default:
                break;
        }
    }

    public void confirmDestination(View view) {
        if (timeSelected != false) {
            beginRequest(view);
            updateUI("navigating");
        }
    }

        public Integer beginRequest(View view) {
        Log.d("func", "beginRequest is called in MainActivity");

        // Check if a time for the meetup was selected
        if (timeSelected == false) {
            Toast.makeText(TestActivity.this, "Please select a meetup time", Toast.LENGTH_SHORT).show();
            return 0;
        }



        // Create a new meeting object
        Meeting meeting = new Meeting(
                place.getAddress().toString(),
                placeLatLng.longitude,
                placeLatLng.latitude,
                currentUser.getUid(),
                currentUser.getDisplayName(),
                scheduledTime.getTimeInMillis());

        // Add the meeting object to the database
        meetingId = Meeting.addMeetingToDb(currentUser, meeting);
        scheduledMillis = meeting.scheduledTime;


        // Update the UI with the new state of the app
//        updateUI("navigating");

        return 0;
    }


    public void onFlake(View view) {

        DateTime now = DateTime.now();

        DateTime dateTime = new DateTime(scheduledMillis);
        Minutes minutes = Minutes.minutesBetween(now, dateTime);
        final Integer differenceInTimeInMinutes = minutes.getMinutes();
        System.out.println(minutes.getMinutes());
        // get the current time

        // calculate the difference in time

        // get position and check if it is near the end position
        // firstLocation and secondLocation are Location class instances
        // distance is stored in result array at index 0
        float[] result = new float[1];
        Location.distanceBetween (lastKnownLatLng.latitude, lastKnownLatLng.longitude, placeLatLng.latitude, placeLatLng.longitude, result);
        // If the person is here, then don't do anything and reset everything
        if (result[0] < 50) {
            // distance between first and second location is less than 50m
            Log.d("BRUCE", "YOU ARE HERE");
        } else {
            // add difference in time to user's flake score
            final DatabaseReference scoreRef = UsersDatabase.child(currentUser.getUid().toString()).child("score");
            scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Integer prevScore = dataSnapshot.getValue(Integer.class);
                    scoreRef.setValue(prevScore + differenceInTimeInMinutes);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            MeetupsDatabase.child(meetingId.toString()).child("acceptedUsers").child(currentUser.getUid().toString()).removeValue();


            updateUI("normal");
        }
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
//        super.onBackPressed();  // optional depending on your needs
    }
}

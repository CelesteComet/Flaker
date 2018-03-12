package com.flaker.flaker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.flaker.flaker.MyMapFragment.drawRoute;
import static com.flaker.flaker.MyMapFragment.lastKnownLatLng;
import static com.flaker.flaker.MyMapFragment.moveMapToLatLng;
import static com.flaker.flaker.MyMapFragment.placeLatLng;
import static com.flaker.flaker.MyMapFragment.travelMode;
import static com.flaker.flaker.MyMapFragment.place;


public class TestActivity extends BaseActivity {

    public Fragment mMapFragment;
    public Fragment bottomModalFragment;
    public Boolean timeSelected;
    public Calendar scheduledTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mMapFragment = new MyMapFragment();

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.mapFrame, mMapFragment);
        transaction.commit();

        includeDrawer();
        setupAutoCompleteWidget();


    }

    private void setupAutoCompleteWidget() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place _place) {
                place = _place;
                placeLatLng = _place.getLatLng();

                MyMapFragment.createSingleMarker(placeLatLng);
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
        timeSelected = false;
        switch (ui) {
            case "searching":
                // Views
                TextView placeTitle = findViewById(R.id.confirmTitleText);
                TextView placeAddress = findViewById(R.id.confirmAddressText);
                TextView eta = findViewById(R.id.confirmETAText);
                TextView meetingTime = findViewById(R.id.confirmMeetingTime);
                mFloatingActionsMenu = findViewById(R.id.fabby);
                ViewGroup autocompleteLayout = findViewById(R.id.place_autocomplete_layout);
                autocompleteLayout.setVisibility(View.GONE);
                mFloatingActionsMenu.setVisibility(View.VISIBLE);
                placeTitle.setText(place.getName());
                placeAddress.setText(place.getAddress());
                break;
            case "navigating":
                mFloatingActionsMenu = findViewById(R.id.fabby);
                mFloatingActionsMenu.setVisibility(View.INVISIBLE);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_bottom);
                transaction.remove(bottomModalFragment);
                transaction.commit();
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
        TimePickerDialog mTimePicker = new TimePickerDialog(this, mTimeSetListener, 12, 30, false);
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


        // Update the UI with the new state of the app
//        updateUI("navigating");

        return 0;
    }

}

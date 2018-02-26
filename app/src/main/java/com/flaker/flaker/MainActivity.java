package com.flaker.flaker;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends MapsActivity {



    // Google Map
    private LatLng placeLatLng = mDefaultLatLng;
    private Place destinationPlace;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Calendar c2 = Calendar.getInstance();
    private Boolean timeSelected;


    // UI
    private FloatingActionsMenu menuMultipleActions;

    // Activity View State
    /*

    */
    private String viewState = "searchDestination";


    private final String TAG = this.toString();
    private final String PLACE_LATLNG_KEY = "PLACE_LATLNG_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BRUCE", "MAIN ACTIVITY CREATING");

//        request.add(data.child("address").getValue().toString());
//        request.add(data.child("ownerId").getValue().toString());
//        request.add(data.child("scheduledTime").getValue().toString());
//        request.add(data.child("latitude").getValue().toString());
//        request.add(data.child("longitude").getValue().toString());


        setContentView(R.layout.activity_main);

        if (getIntent().getStringArrayListExtra("fromRequestData") != null) {
            ArrayList<String> list = getIntent().getStringArrayListExtra("fromRequestData");
            Log.d("BRUCE", list.toString());
            double latitude = Double.parseDouble(list.get(3));
            double longitude = Double.parseDouble(list.get(4));
            meetingId = list.get(5);

            Log.d("BRUCE", meetingId);
            placeLatLng = new LatLng(latitude, longitude);
            viewState = "requesteeView";

        }

        setupOnMapReadyCallback();
        includeDrawer();
        includeFAB();
//        updateUI(viewState);




    }

    private void includeFAB() {
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
    }


    private void setupAutoCompleteWidget() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeLatLng = place.getLatLng();
                destinationPlace = place;
                moveMapToLatLngWithBounds(placeLatLng, true);
                drawRoute(mLastKnownLatLng, placeLatLng, travelMode);
                createSingleMarker(placeLatLng);
                viewState = "confirmDestination";
                updateUI(viewState);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error for autocomplete
                Log.d(TAG, "An error occurred: " + status);
            }
        });
    }

    private void setupOnMapReadyCallback() {
        final Context context = this;
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment =
                (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                try {
//                    // Customise the styling of the base map using a JSON object defined
//                    // in a raw resource file.
//                    boolean success = googleMap.setMapStyle(
//                            MapStyleOptions.loadRawResourceStyle(
//                                    context, R.raw.map_json));
//                    if (!success) {
//                        Log.e(TAG, "Style parsing failed.");
//                    }
//                } catch (Resources.NotFoundException e) {
//                    Log.e(TAG, "Can't find style. Error: ", e);
//                }
                mGoogleMap = googleMap;
                getLocationPermission();
                getDeviceLocation();
                updateLocationUI();

                mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        updateUI(viewState);
                        Log.d("BRUCE", viewState);

                    }
                });

                setupAutoCompleteWidget();
            }
        });
    }

    private void updateUI(String viewState) {
        switch (viewState) {
            case "searchDestination":
                ConstraintLayout autoCompleteLayout = this.findViewById(R.id.place_autocomplete_layout);
                autoCompleteLayout.setVisibility(ConstraintLayout.VISIBLE);

                View cancelButton = findViewById(R.id.endMeetupButton);
                cancelButton.setVisibility(View.GONE);

                View searchDestinationFAB = findViewById(R.id.multiple_actions);
                searchDestinationFAB.setVisibility(View.GONE);
                break;
            case "confirmDestination":
                timeSelected = false;
                ValueAnimator animation = ValueAnimator.ofFloat(1.0f, 0.77f);
                ValueAnimator animation2 = ValueAnimator.ofFloat(1.0f, 0.63f);

                final Guideline guideLine = (Guideline) this.findViewById(R.id.guideline);
                final Guideline guideLine2 = (Guideline) this.findViewById(R.id.guideline2);
                final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine.getLayoutParams();
                final ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) guideLine2.getLayoutParams();

                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        float animatedValue = (float)updatedAnimation.getAnimatedValue();
                        params.guidePercent = animatedValue; // 45% // range: 0 <-> 1
                        guideLine.setLayoutParams(params);
                    }
                });

                animation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        float animatedValue = (float)updatedAnimation.getAnimatedValue();
                        params2.guidePercent = animatedValue; // 45% // range: 0 <-> 1
                        guideLine2.setLayoutParams(params2);
                    }
                });



                animation2.setDuration(800);
                animation2.start();

                animation.setDuration(800);
                animation.start();

                ConstraintLayout autoCompleteLayout2 = this.findViewById(R.id.place_autocomplete_layout);
                autoCompleteLayout2.setVisibility(ConstraintLayout.GONE);

                View confirmDestinationFAB = findViewById(R.id.multiple_actions);
                confirmDestinationFAB.setVisibility(View.VISIBLE);

                TextView confirmTitleText = findViewById(R.id.confirmTitleText);
                confirmTitleText.setText(destinationPlace.getName());

                TextView confirmAddressText = findViewById(R.id.confirmAddressText);
                confirmAddressText.setText(destinationPlace.getAddress());



                // Display the ETA on the confirm box

                Log.d("ETA", "TRYING TO CHANGE");

                // Change icon to Arrow back
//                this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);//your icon here
                break;
            case "requesterView":
                LinearLayout confirmLinearLayout = this.findViewById(R.id.confirmLinearLayout);
                confirmLinearLayout.setVisibility(LinearLayout.GONE);
                ValueAnimator backAnimation = ValueAnimator.ofFloat(0.77f, 1.00f);
                ValueAnimator backAnimation2 = ValueAnimator.ofFloat(0.63f, 1.0f);

                final Guideline backGuideLine = (Guideline) this.findViewById(R.id.guideline);
                final Guideline backGuideLine2 = (Guideline) this.findViewById(R.id.guideline2);
                final ConstraintLayout.LayoutParams backParams = (ConstraintLayout.LayoutParams) backGuideLine.getLayoutParams();
                final ConstraintLayout.LayoutParams backParams2 = (ConstraintLayout.LayoutParams) backGuideLine2.getLayoutParams();

                backAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        float animatedValue = (float)updatedAnimation.getAnimatedValue();
                        backParams.guidePercent = animatedValue; // 45% // range: 0 <-> 1
                        backGuideLine.setLayoutParams(backParams);
                    }
                });

                backAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        float animatedValue = (float)updatedAnimation.getAnimatedValue();
                        backParams2.guidePercent = animatedValue; // 45% // range: 0 <-> 1
                        backGuideLine2.setLayoutParams(backParams2);
                    }
                });

                backAnimation2.setDuration(800);
                backAnimation2.start();

                backAnimation.setDuration(800);
                backAnimation.start();

                // Show View Button
                View cancelButton2 = findViewById(R.id.endMeetupButton);
                cancelButton2.setVisibility(View.VISIBLE);



                requestLocationUpdates(meetingId);
                currentlyRouting = true;
                // Change icon to Arrow back
//                this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);//your icon here

                break;
            case "requesteeView":
                ConstraintLayout autoCompleteLayout3 = this.findViewById(R.id.place_autocomplete_layout);
                autoCompleteLayout3.setVisibility(ConstraintLayout.GONE);
                moveMapToLatLngWithBounds(placeLatLng, true);
                createSingleMarker(placeLatLng);
                Log.d("BRUCE", "REQUESTING LOCATION UPDATES!!!!");
                Log.d("RIGHT BRUCE", meetingId);

                // Show View Button
                View cancelButton3 = findViewById(R.id.endMeetupButton);
                cancelButton3.setVisibility(View.VISIBLE);


                currentlyRouting = true;
                requestLocationUpdates(meetingId);
                break;
            default:
                break;
        }
    }

    @SuppressLint("MissingPermission")

    private void requestLocationUpdates(final String meetingId) {
        Log.d("LOCATION CALLBACK", "REQUESTING A LOCATION");

        if (mLocationCallback != null) {
            Log.d("LOCATION CALLBACK", "Attempting to remove");
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
        mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        // Construct a location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("BRUCE", "GOT A NEW LOCATION");
                    mLastKnownLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    sendCurrentLatLngToDatabase(location.getLatitude(), location.getLongitude(), meetingId);
                    //drawRoute(mLastKnownLatLng, placeLatLng, travelMode);

                }
            };
        };



        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


    public void changeTravelMode(View view) {
        Integer viewId = view.getId();
        menuMultipleActions.toggle();
        switch (viewId) {
            case R.id.action_a:
                Log.d(TAG, "*** CHOSE TO WALK ***");
                travelMode = Routing.TravelMode.WALKING;
                drawRoute(mLastKnownLatLng, placeLatLng, Routing.TravelMode.WALKING);
                break;
            case R.id.action_b:
                Log.d(TAG, "*** CHOSE TO BIKE ***");
                travelMode = Routing.TravelMode.BIKING;
                drawRoute(mLastKnownLatLng, placeLatLng, Routing.TravelMode.BIKING);
                break;
            case R.id.action_c:
                Log.d(TAG, "*** CHOSE TO DRIVE ***");
                travelMode = Routing.TravelMode.DRIVING;
                drawRoute(mLastKnownLatLng, placeLatLng, Routing.TravelMode.DRIVING);
                break;
            default:
                break;
        }

    }

    public void selectMeetupTime(View view) {
        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeSelected = true; // timeSelected false in updateView
                Calendar calendar = Calendar.getInstance();
                c2 = Calendar.getInstance();
                c2.set(Calendar.HOUR_OF_DAY, i);
                c2.set(Calendar.MINUTE, i1);
                long sub = c2.getTimeInMillis() - calendar.getTimeInMillis();
                if (sub < 0) {
                    Toast.makeText(MainActivity.this, "Please select a time past the current time", Toast.LENGTH_SHORT).show();
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


    public Integer beginRequest(View view) {
        if (timeSelected == false) {
            Toast.makeText(MainActivity.this, "Please select a meetup time", Toast.LENGTH_SHORT).show();
            return 0;
        }
//        Intent showRequesterViewIntent = new Intent(this, RequesterViewActivity.class);
//        startActivity(showRequesterViewIntent);
        viewState = "requesterView";


        DatabaseReference newRef = MeetupsDatabase.push();
        String key = newRef.getKey();
        Meeting meeting = new Meeting(
                destinationPlace.getAddress().toString(),
                placeLatLng.longitude,
                placeLatLng.latitude,
                currentUser.getUid(),
                currentUser.getDisplayName(),
                c2.getTimeInMillis());
        addMeetingToDb(meeting);
//        newRef.setValue(meeting);
//        newRef.child("meetingId").setValue(key);


        updateUI(viewState);
        return 0;
    }

    @Override
    public void onBackPressed() {

    }

    public void endMeetup(View view) {
        if (viewState == "requesterView") {

            viewState = "searchDestination";
            updateUI(viewState);

        } else if (viewState == "requesteeView") {
            viewState = "searchDestination";
            updateUI(viewState);
        }

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}

//    If you invoke the Firebase push() method without arguments it is a pure client-side operation.
//
//        var newRef = ref.push(); // this does *not* call the server
//        You can then add the key() of the new ref to your item:
//
//        var newItem = {
//        name: 'anauleau'
//        id: newRef.key()
//        };
//        And write the item to the new location:
//
//        newRef.set(newItem);

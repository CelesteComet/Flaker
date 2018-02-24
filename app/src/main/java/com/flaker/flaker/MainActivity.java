package com.flaker.flaker;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import static com.flaker.flaker.myGoogleMap.drawRoute;
import static com.flaker.flaker.myGoogleMap.moveMapToLatLngWithBounds;

public class MainActivity extends MapsActivity {

    // Google Map
    private LatLng placeLatLng;

    private final String TAG = this.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupOnMapReadyCallback();
    }


    private void setupAutoCompleteWidget() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeLatLng = place.getLatLng();
                moveMapToLatLngWithBounds(placeLatLng);
                drawRoute(mLastKnownLatLng, placeLatLng);
                createSingleMarker(placeLatLng);
                viewState = "confirmDestination";
                updateUI(context);
                requestLocationUpdates();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error for autocomplete
                Log.d("Bruce", "An error occurred: " + status);
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
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    context, R.raw.map_json));
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                mGoogleMap = googleMap;
                getLocationPermission();
                getDeviceLocation();
                updateLocationUI();
                setupAutoCompleteWidget();
            }
        });
    }


//        Guideline guideLine = (Guideline) findViewById(R.id.guideline);
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine.getLayoutParams();
//        params.guidePercent = 1f; // 45% // range: 0 <-> 1
//        guideLine.setLayoutParams(params);

//        myGoogleMap.init(this);





}



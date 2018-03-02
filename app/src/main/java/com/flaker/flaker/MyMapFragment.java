package com.flaker.flaker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SupportActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static com.flaker.flaker.BaseActivity.mLocationPermissionGranted;


public class MyMapFragment extends android.support.v4.app.Fragment {

    // API Clients
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;

    protected SupportMapFragment mMapView;
    private GoogleMap mGoogleMap;
    private LatLng lastKnownLatLng;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    // Default Map Values
    protected static final Integer DEFAULT_ZOOM = 15;
    protected static LatLng mDefaultLatLng = new LatLng(-33.8523341, 151.2106085); // Australia


    protected SupportActivity mActivity;

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setupAPIClients();
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the map fragment which is situated in another fragment so we need
        // to use the child fragment manager
        FragmentManager mFragmentManager = getChildFragmentManager();
        mMapView = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);

        getDeviceLocation();

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapLoaded() {
                            moveMapToLatLng(lastKnownLatLng);
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    });
                }
            });
        }
    }

    private void setupAPIClients() {
        Log.d("func", "Setting up API Clients in MyMapFragment");
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                } else {
                                    requestSingleLocationUpdate();
                                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void moveMapToLatLng(LatLng lastKnownLatLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, DEFAULT_ZOOM);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    protected void requestSingleLocationUpdate() {
        // Construct a location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    lastKnownLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                }
            };
        };
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(1000);

                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null);
                return;
            }
        } catch (Exception e) {
            Log.e("_error", e.toString());
        }
    }


}



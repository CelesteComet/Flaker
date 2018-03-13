package com.flaker.flaker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.TextView;


import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.flaker.flaker.BaseActivity.MeetupsDatabase;
import static com.flaker.flaker.BaseActivity.mLocationPermissionGranted;
import static com.flaker.flaker.BaseActivity.timeParse;


public class MyMapFragment extends android.support.v4.app.Fragment {

    // API Clients
    protected static GeoDataClient mGeoDataClient;
    protected static PlaceDetectionClient mPlaceDetectionClient;
    protected static FusedLocationProviderClient mFusedLocationProviderClient;

    protected SupportMapFragment mMapView;
    private static GoogleMap mGoogleMap;
    public static LatLng lastKnownLatLng;
    protected static LocationCallback mLocationCallback;
    protected static LocationRequest mLocationRequest;


    // Public map variables
    public static LatLng placeLatLng;
    public static Place place;
    public static Routing.TravelMode travelMode = Routing.TravelMode.DRIVING;
    public static ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    public static Marker destinationMarker;

    public static HashMap<String, Object> friendMarkers;
    public static ArrayList<Marker> friendMarkersArray = new ArrayList<Marker>();



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

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the map fragment which is situated in another fragment so we need
        // to use the child fragment manager
        FragmentManager mFragmentManager = getChildFragmentManager();
        mMapView = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
        getDeviceLocation();



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
        final Activity a = getActivity();

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

                                    if (mMapView != null) {
                                        a.findViewById(R.id.mapFrame).setVisibility(View.INVISIBLE);
                                        mMapView.onCreate(null);
                                        mMapView.onResume();

                                        final TestActivity t = (TestActivity) getActivity();
                                        if (t.getIntent().getExtras() != null) {
                                            Meeting meeting = t.getIntent().getParcelableExtra("bundle");
                                            double latitude = meeting.latitude;
                                            double longitude = meeting.longitude;
                                            t.meetingId = meeting.meetingId;
                                            placeLatLng = new LatLng(latitude, longitude);
                                            a.findViewById(R.id.mapFrame).setVisibility(View.VISIBLE);
                                            t.updateUI("requesteeNavigating");
                                        }

                                        mMapView.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {
                                                mGoogleMap = googleMap;
                                                moveMapToLatLng(lastKnownLatLng);
                                                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                    @SuppressLint("MissingPermission")
                                                    @Override
                                                    public void onMapLoaded() {
                                                    mGoogleMap.setMyLocationEnabled(true);
                                                    a.findViewById(R.id.mapFrame).setVisibility(View.VISIBLE);
                                                    UiSettings mUISettings = mGoogleMap.getUiSettings();
                                                    mUISettings.setCompassEnabled(false);
                                                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                                    }
                                                });
                                            }
                                        });

                                    }
                                } else {
                                    requestSingleLocationUpdate();
                                    getDeviceLocation();
                                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static void moveMapToLatLng(LatLng lastKnownLatLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, DEFAULT_ZOOM);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    public static void moveMapToLatLngWithBounds(LatLng latLng, boolean animate, Activity ctx) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        builder.include(lastKnownLatLng);

        LatLngBounds bounds = builder.build();

        int width = ctx.getResources().getDisplayMetrics().widthPixels;
        int height = ctx.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.1); // offset from edges of the map 10% of screen

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height/2, padding);

        if (animate == true) {
            mGoogleMap.animateCamera(cameraUpdate);
        } else {
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public static void createSingleMarker(LatLng latLng, String title) {
        // If a marker exists already, remove the marker
        if (destinationMarker != null) {
            destinationMarker.remove();
        }

        if (title != null) {
            destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        } else {
            destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        }

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
            if (true) {

                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(1);

                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null);
                return;
            }
        } catch (Exception e) {
            Log.e("_error", e.toString());
        }
    }



    public static void drawRoute(LatLng start, final LatLng end, Routing.TravelMode mode, final Activity a) {
        Log.d("APIUSAGE", "DRAWING ROUTE USING API");
        Routing routing = new Routing.Builder()
                .travelMode(mode)
                .key("AIzaSyA75L5OdKIdMirIibjLha3M_gZUZYtK2j8")
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {
                        Log.d("BRUCE", e.toString());
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

                        if (polylines.size() > 0) {
                            for (Polyline poly : polylines) {
                                poly.remove();
                            }
                        }

                        polylines = new ArrayList<>();
                        //add route(s) to the map.
                        for (int i = 0; i < route.size(); i++) {

                            //In case of more than 5 alternative routes


                            PolylineOptions polyOptions = new PolylineOptions().width(5).color(Color.parseColor("#03A9F4")).geodesic(true);


                            polyOptions.width(10 + i * 3);
                            polyOptions.addAll(route.get(i).getPoints());
                            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
                            polylines.add(polyline);

                            Integer estimatedTimeOfArrival = route.get(i).getDistanceValue();
                            String parsed = timeParse(estimatedTimeOfArrival);

                            TextView confirmETAText = a.findViewById(R.id.confirmETAText);


                            if (confirmETAText != null) {
                                confirmETAText.setText("Travel Time: " + parsed);
                            }


                            createSingleMarker(end, "Travel Time: " + parsed);


                            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    protected static void requestFriendUpdates(String meetingId) {

        // Check if we currently have friendMarkers, if so remove them
        if (friendMarkers == null) {
            friendMarkers = new HashMap<String, Object>();
        }

        MeetupsDatabase.child(meetingId).child("acceptedUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Object values = snap.getValue();
                    Map<String, Object> map = (Map<String, Object>) snap.getValue();
                    map.put(snap.getKey(), snap.getValue());
                    friendMarkers.put(snap.getKey(), snap.getValue());
                    drawFriends(friendMarkers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected static void drawFriends(HashMap<String, Object> friendMarkers) {
        // Clear buffer of friend markers
        if (friendMarkersArray.size() > 0) {
            for(int i = 0; i < friendMarkersArray.size(); i++) {
                friendMarkersArray.get(i).remove();
            }
        }

        for (Map.Entry<String, Object> entry : friendMarkers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            HashMap<String, Object> map = new HashMap<String, Object>();
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(value);// obj is your object
            try {
                JSONObject jsonObj = new JSONObject(json);
                double lat = (double) jsonObj.get("latitude");
                double lng = (double) jsonObj.get("longitude");

                friendMarkersArray.add(mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void requestLocationUpdates(final String meetingId) {
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
                    lastKnownLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    InvitedUser user = new InvitedUser();
                    user.longitude = location.getLongitude();
                    user.latitude = location.getLatitude();
                    BaseActivity.sendCurrentLatLngToDatabase(user, meetingId);
                    //drawRoute(mLastKnownLatLng, placeLatLng, travelMode);


                }
            };
        };



        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


}



package com.flaker.flaker;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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


public class MapsActivity extends BaseActivity {

    // API Clients
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;

    // Permissions



    // Locations
    protected static LocationRequest mLocationRequest;
    protected static LocationCallback mLocationCallback;

    // Google Map
    protected GoogleMap mGoogleMap;
    protected Location mLastKnownLocation;
    protected ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    protected Marker destinationMarker;
    protected Integer estimatedTimeOfArrival;
    protected Routing.TravelMode travelMode = Routing.TravelMode.WALKING;
    protected ArrayList<LatLng> otherUsers = new ArrayList<LatLng>();
    protected HashMap<String, Object> friendMarkers;
    protected ArrayList<Marker> friendMarkersArray = new ArrayList<Marker>();

    // Default Map Values
    protected static final Integer DEFAULT_ZOOM = 15;
    protected static LatLng mDefaultLatLng = new LatLng(-33.8523341, 151.2106085); // Australia

    protected LatLng mLastKnownLatLng = mDefaultLatLng;
    protected Location mCameraPosition;

    // Class constants
    private final String TAG = this.toString();
    private final String KEY_LAT_LNG = "KEY_LAT_LNG";
    private final String KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLatLng = savedInstanceState.getParcelable(KEY_LAT_LNG);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setupAPIClients();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    protected void updateLocationUI() {
        Log.d("func", "Updating the map's UI settings");
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                Log.d("func", "Permission was granted and location UI is enabled");
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Log.d("func", "Permission wasn't granted and location UI is disabled");
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    protected void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Got last known location. In some rare situations this can be null.
                                    Log.d("BAD", "DOING GOOD");
                                    mLastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    moveMapToLatLng(mLastKnownLatLng);
                                } else {
                                    // Logic to handle location object
                                    requestSingleLocationUpdate();
                                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                }
                            }
                        });
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            mLastKnownLocation = task.getResult();
//                            if(mLastKnownLocation != null) {
//                                Log.d("BAD", "DOING GOOD");
//                                mLastKnownLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                                moveMapToLatLng(mLastKnownLatLng);
//                            } else {
//                                Log.d("BAD", "DOING BAD");
//                                mLastKnownLatLng = mDefaultLatLng;
//                                moveMapToLatLng(mLastKnownLatLng);
//                                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
//                            }
//                        }
//                    }
//                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    protected void setupAPIClients() {
        Log.d("func", "Setting up API Clients in MapsActivity");
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    protected void moveMapToLatLngWithBounds(LatLng latLng, boolean animate) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mLastKnownLatLng);
        builder.include(latLng);
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 400);
        if (animate == true) {
            mGoogleMap.animateCamera(cameraUpdate);
        } else {
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    protected void createSingleMarker(LatLng latLng) {
        // If a marker exists already, remove the marker
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
    }

    protected void requestFriendUpdates(String meetingId) {

        // Check if we currently have friendMarkers, if so remove them
        if (friendMarkers == null) {
            friendMarkers = new HashMap<String, Object>();
        }

        MeetupsDatabase.child(meetingId).child("acceptedUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Log.d("BRUCE", snap.toString());
                    Object values = snap.getValue();
                    Map<String, Object> map = (Map<String, Object>) snap.getValue();

                    map.put(snap.getKey(), snap.getValue());

                    print(map.toString());




                    friendMarkers.put(snap.getKey(), snap.getValue());



                    drawFriends(friendMarkers);




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void drawFriends(HashMap<String, Object> friendMarkers) {
        // Clear buffer of friend markers
        if (friendMarkersArray.size() > 0) {
            for(int i = 0; i < friendMarkersArray.size(); i++) {
                friendMarkersArray.get(i).remove();
            }
        }

        for (Map.Entry<String, Object> entry : friendMarkers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            print(value.toString());
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

    protected void drawOtherUsersOnMap() {
        MeetupsDatabase.child(meetingId).child("acceptedUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot indSnapshot : dataSnapshot.getChildren()) {

                    double latitude = Double.parseDouble(indSnapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(indSnapshot.child("longitude").getValue().toString());

                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void drawRoute(LatLng start, LatLng end, Routing.TravelMode mode) {
        Log.d("APIUSAGE", "DRAWING ROUTE USING API");
        Routing routing = new Routing.Builder()
                .travelMode(mode)
                .key(getString(R.string.google_directions_key))
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


                            PolylineOptions polyOptions = new PolylineOptions();

                            polyOptions.width(10 + i * 3);
                            polyOptions.addAll(route.get(i).getPoints());
                            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
                            polylines.add(polyline);

                            estimatedTimeOfArrival = route.get(i).getDistanceValue();
                            String parsed = timeParse(estimatedTimeOfArrival);
                            TextView confirmETAText = findViewById(R.id.confirmETAText);

                            confirmETAText.setText("Travel Time: " + parsed);


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

    protected void moveMapToLatLng(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mGoogleMap.moveCamera(cameraUpdate);
        Log.d("BRUCE", "SHOULD BE MOVING");
    }

    protected void requestSingleLocationUpdate() {
        Log.d("func", "Requesting a single location update");
        // Construct a location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    moveMapToLatLng(mLastKnownLatLng);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            };
        };

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null);
                return;
            }
        } catch (Exception e) {
            Log.e("_error", e.toString());
        }
    }






}

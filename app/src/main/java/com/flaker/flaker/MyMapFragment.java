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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.flaker.flaker.BaseActivity.mLocationPermissionGranted;
import static com.flaker.flaker.BaseActivity.timeParse;


public class MyMapFragment extends android.support.v4.app.Fragment {

    // API Clients
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;

    protected SupportMapFragment mMapView;
    private static GoogleMap mGoogleMap;
    public static LatLng lastKnownLatLng;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;


    // Public map variables
    public static LatLng placeLatLng;
    public static Place place;
    public static Routing.TravelMode travelMode = Routing.TravelMode.DRIVING;
    public static ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    public static Marker destinationMarker;



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

    public static void createSingleMarker(LatLng latLng) {
        // If a marker exists already, remove the marker
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
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



    public static void drawRoute(LatLng start, LatLng end, Routing.TravelMode mode, final Activity a) {
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


                            PolylineOptions polyOptions = new PolylineOptions();

                            polyOptions.width(10 + i * 3);
                            polyOptions.addAll(route.get(i).getPoints());
                            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
                            polylines.add(polyline);

                            Integer estimatedTimeOfArrival = route.get(i).getDistanceValue();
                            String parsed = timeParse(estimatedTimeOfArrival);

                            TextView confirmETAText = a.findViewById(R.id.confirmETAText);

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


}



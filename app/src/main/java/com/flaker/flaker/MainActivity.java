package com.flaker.flaker;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;


import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.design.widget.NavigationView;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.AutoTransition;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;


import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;


import android.util.Log;

import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;


import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    /* The entry points to the Places API. */
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // The Google map fragment object
    private GoogleMap mGoogleMap;
    private Marker destinationMarker;
    private LatLng placeLatLng;
    private List<Polyline> polylines = new ArrayList<>();
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Boolean currentlyRouting = false;

    // Confirm View Object
    private ViewGroup mainMapContent;
    private View mTextView;

    // Permissions
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Default location for the map
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LAT_LNG = "lat_lng";

    // The last known latitude and longitude
    private LatLng mLastKnownLatLng;
    private CameraPosition mCameraPosition;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLatLng = savedInstanceState.getParcelable(KEY_LAT_LNG);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        Guideline guideLine = (Guideline) findViewById(R.id.guideline);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine.getLayoutParams();
        params.guidePercent = 1f; // 45% // range: 0 <-> 1
        guideLine.setLayoutParams(params);



        // get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // get current user
        currentUser = mAuth.getCurrentUser();
        myGoogleMap.init(this);
        setupUI();


//
//        setupMapAPIClients();
//
//        setupGoogleMapCallback();
//

    }



    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // check to see if we have a location request object
        if(mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
        }
        Log.d("Bruce", "starting location updates");
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null);
        return;
    }

    private void setupUI() {
        // method to setup initial views

        // Get the root view and create a transition
        mainMapContent  = (ViewGroup) findViewById(R.id.mainMapContent);


        mTextView = (View) findViewById(R.id.textView);
        mTextView.setTranslationY(-20f);
//        mainMapContent.removeView(mTextView);




    }


//    private void setupNavigation() {
//        // Get the toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        // Make the toolbar into an action bar for interactivity
//        setSupportActionBar(toolbar);
//
//        // Make drawer toggle-able
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // Get the navigationView and set an item selected listener
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//
//        // Show user information in navigation view
//        View headerView = navigationView.getHeaderView(0);
//        TextView navUserEmail = (TextView) headerView.findViewById(R.id.nav_userName);
//        TextView navUserName = (TextView) headerView.findViewById(R.id.nav_userEmail);
//        navUserEmail.setText(currentUser.getEmail());
//        navUserName.setText(currentUser.getDisplayName());
//
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                // Handle navigation view item clicks here.
//                int id = item.getItemId();
//
//                if (id == R.id.nav_camera) {
//                    // Handle the camera action
//                } else if (id == R.id.nav_gallery) {
//
//                } else if (id == R.id.nav_slideshow) {
//
//                } else if (id == R.id.nav_manage) {
//
//                } else if (id == R.id.nav_share) {
//
//                } else if (id == R.id.nav_send) {
//
//                }
//
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                drawer.closeDrawer(GravityCompat.START);
//                return true;
//            }
//        });
//    }

    private void setupNavigation() {
        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Make the toolbar into an action bar for interactivity
        setSupportActionBar(toolbar);

        // Make drawer toggle-able
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Get the navigationView and set an item selected listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    // Handle the camera action
                } else if (id == R.id.nav_gallery) {

                } else if (id == R.id.nav_slideshow) {

                } else if (id == R.id.nav_manage) {


                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });










    }




















}



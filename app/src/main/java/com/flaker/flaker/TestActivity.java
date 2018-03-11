package com.flaker.flaker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;

import static com.flaker.flaker.MyMapFragment.drawRoute;
import static com.flaker.flaker.MyMapFragment.lastKnownLatLng;
import static com.flaker.flaker.MyMapFragment.moveMapToLatLng;
import static com.flaker.flaker.MyMapFragment.placeLatLng;
import static com.flaker.flaker.MyMapFragment.travelMode;
import static com.flaker.flaker.MyMapFragment.place;


public class TestActivity extends BaseActivity {

    Fragment mMapFragment;

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

//                createSingleMarker(placeLatLng);
                Fragment random = new BottomModalFragment();
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_bottom);
                transaction.add(R.id.bottomModal, random);
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
        switch (ui) {
            case "searching":

                // Views
                TextView placeTitle = findViewById(R.id.confirmTitleText);
                TextView placeAddress = findViewById(R.id.confirmAddressText);
                TextView eta = findViewById(R.id.confirmETAText);
                TextView meetingTime = findViewById(R.id.confirmMeetingTime);

                ViewGroup a = findViewById(R.id.place_autocomplete_layout);
                a.setVisibility(View.GONE);

                placeTitle.setText(place.getName());
                placeAddress.setText(place.getAddress());


                break;
            default:
                break;
        }
    }





}

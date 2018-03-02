package com.flaker.flaker;

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
            public void onPlaceSelected(Place place) {
                placeLatLng = place.getLatLng();
                moveMapToLatLng(placeLatLng);
                drawRoute(lastKnownLatLng, placeLatLng, travelMode);
//                createSingleMarker(placeLatLng);



            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error for autocomplete
                Log.d("AWD", "An error occurred: " + status);
            }
        });
    }


}

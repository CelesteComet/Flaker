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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;

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

    }

    protected void includeDrawer() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Make drawer toggle-able
        final DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Get the navigationView and set an item selected listener
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.bringToFront();

        // Show user information in navigation view
        View headerView = navigationView.getHeaderView(0);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.nav_userName);
        TextView navUserName = (TextView) headerView.findViewById(R.id.nav_userEmail);
        ImageView profileImg = (ImageView) headerView.findViewById(R.id.profileImg);
        Picasso.with(this).load(currentUser.getPhotoUrl()).into(profileImg);
        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.

                int id = item.getItemId();

                if (id == R.id.home) {

                } else if (id == R.id.nav_friends) {
                    Intent displayFriendsActivityIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
                    startActivity(displayFriendsActivityIntent);
                } else if (id == R.id.nav_etas) {
                    // Create request list here
                    if (currentlyRouting == false) {
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                                "Please begin a new route to view ETAs", Snackbar.LENGTH_LONG);
                        mySnackbar.show();
                    } else {
                        Intent displayETAsActivityIntent = new Intent(getApplicationContext(), EtaActivity.class);
                        startActivity(displayETAsActivityIntent);
                    }
                } else if (id == R.id.nav_requests) {
                    if (currentlyRouting == true) {
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                                "Please cancel your current route", Snackbar.LENGTH_LONG);
//                        mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
                        mySnackbar.show();
                    } else {
                        Intent displayRequestsActivityIntent = new Intent(getApplicationContext(), RequesteeViewActivity.class);
                        startActivity(displayRequestsActivityIntent);
                    }

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}

package com.flaker.flaker;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;



import android.view.Window;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BaseActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRootRef = database.getReference();
    DatabaseReference mDestinationRef = mRootRef.child("meetings/1");


    // User Authentication References
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private static final String TAG = "BaseActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // get current user
        currentUser = mAuth.getCurrentUser();

        setupNavigation();


        // Retro stuff
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        APIUtil client = retrofit.create(APIUtil.class);

//        Call<List<GitHubRepo>> call = client.reposForUser("fs-opensource");
//
//        call.enqueue(new Callback<List<GitHubRepo>>() {
//            @Override
//            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
//                Log.d("RESPONSE", "GOT RESPONSE **********");
//                Log.d("RESPONSE", response.body().toString());
//            }
//            @Override
//            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
//                Log.d("RESPONSE", "ERROR");
//            }
//        });

        



    }

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

        // Show user information in navigation view
        View headerView = navigationView.getHeaderView(0);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.nav_userName);
        TextView navUserName = (TextView) headerView.findViewById(R.id.nav_userEmail);
        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

//        navigationView.bringToFront();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Log.d("BRUCE", String.valueOf(id));
//                Intent displayMainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(displayMainActivityIntent);
                if (id == R.id.nav_friends) {
                    // Handle the camera action

                } else if (id == R.id.nav_requests) {

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

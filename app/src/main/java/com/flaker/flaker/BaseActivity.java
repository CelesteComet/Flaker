package com.flaker.flaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("whatever", "HELLO WORLD");



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



}

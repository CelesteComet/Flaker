package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class EtaActivity extends AppCompatActivity {
    public ArrayList<String> stuff = new ArrayList<String>();
    private ListView numbers;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);

        numbers = (ListView) findViewById(R.id.eta_list_view2);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stuff);
        numbers.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRootRef = database.getReference();
        final DatabaseReference mDestinationRef = mRootRef.child("test");

        //initial ETA fetch
        etaFetch(mRootRef);

        //Start fetch ETAs loop
        timedFetch(mRootRef);

    }

    private void timedFetch(final DatabaseReference reference) {
        Timer timer = new Timer();
        TimerTask intervalTask = new TimerTask() {
            @Override
            public void run() {
                etaFetch(reference);
            }
        };

        timer.schedule(intervalTask, 5, 1000);

    }

    private void etaFetch(DatabaseReference reference) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                stuff.clear();
                Log.d("alkfdj", dataSnapshot.getClass().toString());
                for (DataSnapshot indSnapshot: dataSnapshot.getChildren()) {

                    stuff.add(indSnapshot.getValue().toString());
                }

//                stuff.add(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

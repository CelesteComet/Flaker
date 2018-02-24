package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class EtaActivity extends AppCompatActivity {
    public ArrayList<String[]> stuff = new ArrayList<String[]>();
    private ListView numbers;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);

        numbers = (ListView) findViewById(R.id.eta_list_view2);
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stuff);
        adapter = new EtaAdapter(this, (ArrayList<String[]>) stuff);
        numbers.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRootRef = database.getReference();
        final DatabaseReference mDestinationRef = mRootRef.child("meetups").child("0329230").child("users");

        //initial ETA fetch
        etaFetch(mDestinationRef);

        //Start fetch ETAs loop
        timedFetch(mDestinationRef);

    }

    private void timedFetch(final DatabaseReference reference) {
        Timer timer = new Timer();
        TimerTask intervalTask = new TimerTask() {
            @Override
            public void run() {
                etaFetch(reference);
            }
        };

        timer.schedule(intervalTask, 5, 3 * 1000);

    }

    private void etaFetch(DatabaseReference reference) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                stuff.clear();
                Log.d("alkfdj", dataSnapshot.getClass().toString());
                for (DataSnapshot indSnapshot: dataSnapshot.getChildren()) {
                    String[] user = new String[3];
                    user[0] = indSnapshot.child("eta").getValue().toString();
                    user[1] = indSnapshot.child("name").getValue().toString();
                    user[2] = indSnapshot.child("photo_url").getValue().toString();
//                    stuff.add(indSnapshot.child("eta").getValue().toString());
                    stuff.add(user);
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

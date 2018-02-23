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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EtaTestActivity extends AppCompatActivity {
    public ArrayList<String> stuff = new ArrayList<String>();
    private ListView numbers;
    ArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta_test);
        String[] things = new String[1];
        things[0] = "things";
        stuff.add("afdlj");
        numbers = (ListView) findViewById(R.id.eta_list_view2);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stuff);
        numbers.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRootRef = database.getReference();
        DatabaseReference mDestinationRef = mRootRef.child("requestees");
        mDestinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("ajsf", dataSnapshot.getValue().get(1));

                adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stuff);
                numbers.setAdapter(adapter);
                stuff = (ArrayList<String>) dataSnapshot.getValue();
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

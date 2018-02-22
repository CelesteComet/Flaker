package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ETAsActivity extends AppCompatActivity {

    private String name;
    private TextView thing;

//    private String[] idList;

    private ArrayList<String> idList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etas);

        idList2 = new ArrayList<String>();
        idList2.add("6487iAKo2NQ6qWvLZ0pKWNNnXW43");
        idList2.add("WjyeHpL8VyPOXmESHqJUhvbZTk52");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = database.getReference();

//        idList = new String[] {"6487iAKo2NQ6qWvLZ0pKWNNnXW43", "WjyeHpL8VyPOXmESHqJUhvbZTk52"};


        for (int i = 0; i < idList2.size(); i++) {

            DatabaseReference mDestinationRef = mRootRef.child("users").child(idList[i]);



            mDestinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    thing = (TextView) findViewById(R.id.textView2);
                    name = dataSnapshot.toString();
                    Log.d("asdfkljaf - - - - - - ", name);
                    thing.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}

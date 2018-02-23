package com.flaker.flaker;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ETAsActivity extends AppCompatActivity {

    private String name;
    private TextView thing;
    private ListView etaListView;

    private String[] idList;

    private ArrayList<String> idList2;
    ArrayAdapter adapter;
    public ArrayList<String> users = new ArrayList<String>();
//    private RelativeLayout etaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etas);

//        idList2 = new ArrayList<String>();
//        idList2.add("6487iAKo2NQ6qWvLZ0pKWNNnXW43");
//        idList2.add("WjyeHpL8VyPOXmESHqJUhvbZTk52");
//
////        etaView = (RelativeLayout) findViewById(R.id.whole_view);
//
//
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference mRootRef = database.getReference();
//
//        idList = new String[] {"6487iAKo2NQ6qWvLZ0pKWNNnXW43", "WjyeHpL8VyPOXmESHqJUhvbZTk52"};
//
//        etaListView = findViewById(R.id.eta_list_view);
//
//
//        users = fetchUsers(idList2, mRootRef, etaListView);

//        final Context suh = this;
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);
//        adapter.notifyDataSetChanged();
//
//
//
//        etaListView.setAdapter(adapter);
//
//        Timer timer = new Timer();
//        TimerTask intervalTask = new TimerTask() {
//            @Override
//            public void run() {
////                FirebaseDatabase database = FirebaseDatabase.getInstance();
////                final DatabaseReference mRootRef = database.getReference();
//                Log.d("adslfkj", "asdlfkj");
//                users = fetchUsers(idList2, mRootRef, etaListView);
//
////                Log.d("asdfklj", "alkdsfjasdf");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("asdfklj", "alkdsfjasdf");
//
////                        updateList(users);
//                        Log.d("asdflkja", String.valueOf(users));
////                        etaListView.invalidate();
//
//                    }
//                });
//            }
//        };
//
//        timer.schedule(intervalTask, 5, 1000 );





    }




//    private ArrayList<String> fetchUsers(final ArrayList<String> idList, DatabaseReference mRootRef, ListView list) {
////        final ArrayList<String> users = new ArrayList<String>();
//        users.clear();
//        final ListView etaList = list;
//
//        for (int i = 0; i < idList.size(); i++) {
//
//            DatabaseReference mDestinationRef = mRootRef.child("users").child(idList.get(i));
//
//            final int h = i;
//            mDestinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    thing = (TextView) findViewById(R.id.textView2);
//                    name = dataSnapshot.toString();
//
//                    if (h==idList.size()-1) {
//                        thing.setText("");
//                    }
////                    etaList.re
//
//                    getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
//
//                    users.add(dataSnapshot.child("thing").getValue().toString());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//        Log.d("user1", String.valueOf(users));
//        return users;
//    }
}

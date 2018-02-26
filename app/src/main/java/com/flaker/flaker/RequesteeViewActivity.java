package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RequesteeViewActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<ArrayList<String>> requestsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestee_view);


        mRecyclerView = (RecyclerView) findViewById(R.id.requesteeViewRequestList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RequestsAdapter(requestsList, this);
        mRecyclerView.setAdapter(mAdapter);

        //initial Requests fetch
        requestsFetch(MeetupsDatabase);
    }

    private void requestsFetch(DatabaseReference reference) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                requestsList.clear();

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    ArrayList<String> request = new ArrayList<String>();
                    request.add(data.child("address").getValue().toString());
                    request.add(data.child("ownerId").getValue().toString());
                    request.add(data.child("scheduledTime").getValue().toString());
                    request.add(data.child("latitude").getValue().toString());
                    request.add(data.child("longitude").getValue().toString());
                    request.add(data.child("meetingId").getValue().toString());
                    requestsList.add(request);
                }

                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.flaker.flaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

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


    private Meeting meetup;
    private ArrayList<Meeting> meetings = new ArrayList<Meeting>();

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
        mAdapter = new RequestsAdapter(meetings, this);

        // decorate each item
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());

        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(mAdapter);

        final TextView textView1 = (TextView) findViewById(R.id.titleText);
        textView1.setText("Meetups");

        includeDrawer(true);

        //initial Requests fetch
        requestsFetch(MeetupsDatabase);
    }

    private void requestsFetch(DatabaseReference reference) {
        DatabaseReference UserInvitesReference = UsersDatabase.child(currentUser.getUid() + "/invitedMeetups");
        UserInvitesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot invitedMeetupsSnapshot) {
                for (DataSnapshot snap: invitedMeetupsSnapshot.getChildren()) {
                    final String meetupKey = snap.getKey().toString();
                    print(meetupKey);
                    MeetupsDatabase.child(meetupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Meeting meeting = dataSnapshot.getValue(Meeting.class);
                            meetings.add(meeting);

                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//                requestsList.clear();
//
//                for (DataSnapshot data: dataSnapshot.getChildren()) {
//                    ArrayList<String> request = new ArrayList<String>();
//                    request.add(data.child("address").getValue().toString());
//                    request.add(data.child("ownerId").getValue().toString());
//                    request.add(data.child("scheduledTime").getValue().toString());
//                    request.add(data.child("latitude").getValue().toString());
//                    request.add(data.child("longitude").getValue().toString());
//                    request.add(data.child("meetingId").getValue().toString());
//                    requestsList.add(request);
//                }
//
//                mAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void fetchMeetup(String meetupId) {
        DatabaseReference meetupRef = MeetupsDatabase.child(meetupId);

        meetupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot meetupSnapshot) {
                meetup = meetupSnapshot.getValue(Meeting.class);
                print(meetup.toString());
                System.out.println("MEETUP FETCHED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}


//    private ArrayList<Meeting> fetchInvites(String userId) {
//        DatabaseReference UserInvitesReference = UsersDatabase.child(userId + "/invitedMeetups");
//        UserInvitesReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot invitedMeetupsSnapshot) {
//
//                for (DataSnapshot indSnapshot: invitedMeetupsSnapshot.getChildren()) {
//                    System.out.println(indSnapshot.getValue());
//                    String meetupKey = indSnapshot.getValue().toString();
//                    fetchMeetup(meetupKey);
//                    meetings.add(meetup);
//
//                }
//                System.out.println(meetings.toString());
//                System.out.println("INVITES FETCHED");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
//
//        return meetings;
//    }
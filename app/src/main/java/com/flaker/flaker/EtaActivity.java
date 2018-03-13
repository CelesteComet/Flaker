package com.flaker.flaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.flaker.flaker.BaseActivity.MeetupsDatabase;
import static com.flaker.flaker.BaseActivity.meetingId;

public class EtaActivity extends AppCompatActivity {
    public ArrayList<String[]> etaList = new ArrayList<String[]>();
    private ListView etaListView;
    ArrayAdapter adapter;
    Button meetupInvitees;
    ArrayList<InvitedUser> invitedUsers = new ArrayList<InvitedUser>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);

        etaListView = (ListView) findViewById(R.id.eta_list_view2);

        adapter = new EtaAdapter(this, (ArrayList<String[]>) etaList);
        etaListView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mRootRef = database.getReference();
        final DatabaseReference mDestinationRef = mRootRef.child("meetups").child(meetingId.toString()).child("acceptedUsers");

        //initial ETA fetch
        etaFetch(mDestinationRef);

        //Start fetch ETAs loop
        timedFetch(mDestinationRef);

        Button addInvitees = (Button) findViewById(R.id.add_invitees);
        addInvitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToInvitees();
            }
        });
    }



    private void redirectToInvitees() {
        Intent displayMeetupInviteActivityIntent = new Intent(getApplicationContext(), MeetupInviteActivity.class);
        startActivity(displayMeetupInviteActivityIntent);
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

        DatabaseReference InvitedUsersRef = MeetupsDatabase.child(meetingId + "/acceptedUsers");
        InvitedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot invitedUsersSnapshot) {

                for (DataSnapshot indSnapshot: invitedUsersSnapshot.getChildren()) {
                    InvitedUser iuser = (indSnapshot.getValue(InvitedUser.class));
                    invitedUsers.add(iuser);
                    Log.d("BRUCE", invitedUsers.toString());
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



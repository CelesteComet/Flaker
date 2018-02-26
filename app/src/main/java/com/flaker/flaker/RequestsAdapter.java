package com.flaker.flaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Bruce on 2/25/18.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<ArrayList<String>> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RequestsAdapter(ArrayList<ArrayList<String>> requests, Context context) {
        mDataset = requests;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.request_row, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
        CardView mCardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_row, parent, false);
        ViewHolder vh = new ViewHolder(mCardView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView requestRowOwnerId = holder.mCardView.findViewById(R.id.requestRowOwner);
        TextView requestRowAddress = holder.mCardView.findViewById(R.id.requestRowAddress);
        TextView requestRowScheduledTime = holder.mCardView.findViewById(R.id.requestRowScheduledTime);


        requestRowAddress.setText(mDataset.get(position).get(0)); // address


        // get the owner Id and change it to owner's name
        String ownerId = mDataset.get(position).get(1);

        String ownerName = fetchUserById(ownerId);



        requestRowOwnerId.setText(ownerName); // ownerName
        requestRowScheduledTime.setText(mDataset.get(position).get(2)); // scheduledTime

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(mDataset.get(position));
                Intent displayMainActivityIntent = new Intent(mContext, MainActivity.class);
                displayMainActivityIntent.putExtra("fromRequestData", mDataset.get(position));
                mContext.startActivity(displayMainActivityIntent);

            }
        });
//        requestRowScheduledTime.setText(mDataset.get(position).get(2));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String fetchUserById(String userId) {

        final String[] username = new String[1];

        Query query = BaseActivity.UsersDatabase.child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("BRUCE", dataSnapshot.child("name").getValue().toString());
                username[0] = dataSnapshot.child("name").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("failure", "failureee");
            }
        });

        return username[0];

    }




}

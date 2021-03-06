package com.flaker.flaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import junit.framework.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Bruce on 2/25/18.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<Meeting> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RequestsAdapter(ArrayList<Meeting> meetings, Context context) {
        mDataset = meetings;
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
        LinearLayout mLinearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_row, parent, false);
        ViewHolder vh = new ViewHolder(mLinearLayout);



        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView requestRowOwnerId = holder.mLinearLayout.findViewById(R.id.requestRowOwner);
        TextView requestRowAddress = holder.mLinearLayout.findViewById(R.id.requestRowAddress);
//        TextView requestRowScheduledTime = holder.mLinearLayout.findViewById(R.id.requestRowScheduledTime);


        final Meeting meeting = mDataset.get(position);

        String militaryTime = BaseActivity.normalizeTime(meeting.scheduledTime).toString();

        requestRowAddress.setText(meeting.address); // address


        requestRowOwnerId.setText(meeting.ownerName + " @ " + militaryTime); // ownerName




//        requestRowScheduledTime.setText(BaseActivity.normalizeTime(meeting.scheduledTime).toString()); // scheduledTime

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent displayMainActivityIntent = new Intent(mContext, TestActivity.class);
            displayMainActivityIntent.putExtra("bundle", meeting);

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






}

package com.flaker.flaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by alexkite on 2/25/18.
 */

public class InviteeListAdapter extends ArrayAdapter {
    Context context;
    public InviteeListAdapter(@NonNull Context context, ArrayList<String[]> friends) {
        super(context, R.layout.invitee_row, friends);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View inviteesView = theInflater.inflate(R.layout.invitee_row, parent, false);

        String[] singleFriend = (String[]) getItem(position);
        String singleName = singleFriend[0];

        String singlePhotoUrl = singleFriend[1];

        TextView nameText = (TextView)  inviteesView.findViewById(R.id.invitee_name_text);
        ImageView photoView = (ImageView) inviteesView.findViewById(R.id.invitee_row_photo);
        Button inviteButton = (Button) inviteesView.findViewById(R.id.invite_button);

        nameText.setText(singleName);
        Picasso.with(this.context).load(singlePhotoUrl).into(photoView);

        return inviteesView;
    }


}

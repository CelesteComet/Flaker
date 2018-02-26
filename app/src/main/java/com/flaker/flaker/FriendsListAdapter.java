package com.flaker.flaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by alexkite on 2/24/18.
 */

public class FriendsListAdapter extends ArrayAdapter {

    private Context context;

    public FriendsListAdapter(@NonNull Context context, ArrayList<String[]> friends) {
        super(context, R.layout.friends_list_row, friends);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View friendsView = theInflater.inflate(R.layout.friends_list_row, parent, false);

        String[] singleFriend = (String[]) getItem(position);
        String singleName = singleFriend[0];

        String singlePhotoUrl = singleFriend[1];

        String singleScore = singleFriend[2];

        TextView nameText = (TextView) friendsView.findViewById(R.id.friend_name_text);
        ImageView photoView = (ImageView) friendsView.findViewById(R.id.friend_row_photo);
        TextView scoreText = (TextView) friendsView.findViewById(R.id.friend_score_text);

        Picasso.with(this.context).load(singlePhotoUrl).into(photoView);

        nameText.setText(singleName);
        scoreText.setText(singleScore);
        return friendsView;


    }
}

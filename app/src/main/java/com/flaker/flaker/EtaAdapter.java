package com.flaker.flaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alexkite on 2/23/18.
 */

class EtaAdapter extends ArrayAdapter {
    Context context;

    public EtaAdapter(@NonNull Context context, ArrayList<String[]> etas) {
        super(context,R.layout.eta_row, etas);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View etaView = theInflater.inflate(R.layout.eta_row, parent, false);

        String[] singleUser = (String[]) getItem(position);
        String singleEta = singleUser[0];

        String singleName = singleUser[1];
        String singlePhotoUrl = singleUser[2];


        TextView etaText = (TextView) etaView.findViewById(R.id.eta_text);
        TextView nameText = (TextView) etaView.findViewById(R.id.eta_name_text);
        ImageView photoView = (ImageView) etaView.findViewById(R.id.eta_row_photo);

        Picasso.with(this.context).load(singlePhotoUrl).into(photoView);

        etaText.setText("ETA: " + singleEta);
        nameText.setText(singleName);
        return etaView;
    }
}

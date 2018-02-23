package com.flaker.flaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alexkite on 2/23/18.
 */

class EtaAdapter extends ArrayAdapter {


    public EtaAdapter(@NonNull Context context, ArrayList<String> etas) {
        super(context,R.layout.eta_row, etas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View etaView = theInflater.inflate(R.layout.eta_row, parent, false);

        String singleEta = (String) getItem(position);

        TextView etaText = (TextView) etaView.findViewById(R.id.eta_text);

        etaText.setText(singleEta);
        return etaView;
    }
}

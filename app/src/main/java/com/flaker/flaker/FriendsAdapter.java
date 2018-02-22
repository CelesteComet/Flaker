package com.flaker.flaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by joeyfeng on 2/20/18.
 */

public class FriendsAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    String[] names;
    String[] eta;

    public FriendsAdapter(Context c, String[] n, String[] e) {
        names = n;
        eta = e;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {

        return names[i];

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.friend_listview_detail, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        TextView etaTextView = (TextView) v.findViewById(R.id.etaTextView);

        String name = names[i];
        String et = eta[i];

        nameTextView.setText(name);
        etaTextView.setText(et);
        return v;
    }
}

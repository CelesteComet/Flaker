package com.flaker.flaker;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by joeyfeng on 2/21/18.
 */

public class FriendActivity extends AppCompatActivity {

    ListView friendListView;
    String[] names;
    String[] eta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("whatever", "friends");
        setContentView(R.layout.activity_friend);

        Resources res = getResources();
        friendListView = (ListView) findViewById(R.id.friendListView);
        names = res.getStringArray(R.array.names);
        eta = res.getStringArray(R.array.eta);


        FriendsAdapter friendAdapter = new FriendsAdapter(this, names, eta);
        friendListView.setAdapter(friendAdapter);
    }

}

package com.flaker.flaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView user_email;
    private ImageView user_image;
    String userImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
//
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("aslkdfja - -- - ", user.getPhotoUrl().toString());
        user_email = (TextView) findViewById(R.id.user_email);
        user_email.setText(user.getEmail());

        userImageUrl = user.getPhotoUrl().toString();


        user_image = (ImageView) findViewById(R.id.user_image);
        getImageUrl(userImageUrl);


    }

    private void getImageUrl(String url) {
        Picasso.with(getApplicationContext()).load(url).into(user_image);
//        Log.d("asdlkfjaslkdfaj"," aksldfj - -");
    }
}

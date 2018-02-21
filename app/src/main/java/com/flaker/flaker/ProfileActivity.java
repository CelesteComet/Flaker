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
    private TextView userEmail;
    private ImageView userImage;
    private TextView phoneNumber;
    private String phoneNumberVal;
    private TextView userName;
    String userImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
//
        FirebaseUser user = mAuth.getCurrentUser();
        userEmail = (TextView) findViewById(R.id.user_email);
        userEmail.setText("Email: " + user.getEmail());

        phoneNumber = (TextView) findViewById(R.id.phone_number);
        phoneNumberVal = user.getPhoneNumber();

        if (phoneNumberVal != null) {
            Integer thing = phoneNumberVal.length();
            Log.d("phonenumber - - - -", user.getPhoneNumber().toString());
            Log.d("asdklfj", thing.toString());


            phoneNumber.setText(phoneNumberVal.toString());
        } else {
            phoneNumber.setText("none");
        }

        userName = (TextView) findViewById(R.id.userName);
        userName.setText(user.getDisplayName());


        userImageUrl = user.getPhotoUrl().toString();


        userImage = (ImageView) findViewById(R.id.user_image);
        getImageUrl(userImageUrl);


    }

    private void getImageUrl(String url) {
        Picasso.with(getApplicationContext()).load(url).into(userImage);
//        Log.d("asdlkfjaslkdfaj"," aksldfj - -");
    }
}

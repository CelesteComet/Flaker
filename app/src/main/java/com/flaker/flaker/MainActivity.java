package com.flaker.flaker;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends MapsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






//        Guideline guideLine = (Guideline) findViewById(R.id.guideline);
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideLine.getLayoutParams();
//        params.guidePercent = 1f; // 45% // range: 0 <-> 1
//        guideLine.setLayoutParams(params);

        myGoogleMap.init(this);



    }





}



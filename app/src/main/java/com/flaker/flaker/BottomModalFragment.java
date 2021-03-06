package com.flaker.flaker;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.directions.route.Routing;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import static android.content.ContentValues.TAG;
import static com.flaker.flaker.MyMapFragment.drawRoute;
import static com.flaker.flaker.MyMapFragment.lastKnownLatLng;
import static com.flaker.flaker.MyMapFragment.moveMapToLatLngWithBounds;
import static com.flaker.flaker.MyMapFragment.placeLatLng;
import static com.flaker.flaker.MyMapFragment.travelMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomModalFragment extends android.support.v4.app.Fragment {
    Context ctx;
    public BottomModalFragment() {
        // Required empty public constructor
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (enter == false) {
                    FrameLayout mapFrame = getActivity().findViewById(R.id.mapBrame);
                    FrameLayout blocker = getActivity().findViewById(R.id.mapLayoutBlock);

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  mapFrame.getLayoutParams();
                    params.weight = 1f;

                    LinearLayout.LayoutParams marams = (LinearLayout.LayoutParams)  blocker.getLayoutParams();
                    marams.weight = 0.0f;


                    blocker.setLayoutParams(marams);
                    mapFrame.setLayoutParams(params);
                }

            }


            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (enter != false) {
                    FrameLayout mapFrame = getActivity().findViewById(R.id.mapBrame);
                    FrameLayout blocker = getActivity().findViewById(R.id.mapLayoutBlock);

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  mapFrame.getLayoutParams();
                    params.weight = 0.5f;

                    LinearLayout.LayoutParams marams = (LinearLayout.LayoutParams)  blocker.getLayoutParams();
                    marams.weight = 0.5f;


                    blocker.setLayoutParams(marams);
                    mapFrame.setLayoutParams(params);

                    moveMapToLatLngWithBounds(placeLatLng, true, getActivity());
                    drawRoute(lastKnownLatLng, placeLatLng, travelMode, getActivity());

                    TestActivity n = (TestActivity) getActivity();
                    n.updateUI("searching");
                } else {

                }

            }
        });

        return anim;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_modal, container, false);
    }


}

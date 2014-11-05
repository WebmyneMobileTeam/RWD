package com.webmyne.riteway_driver.trip;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.model.MapController;


public class CurrentTripFragment extends Fragment  {
    private MapView mv;
    private MapController mc;
    public static CurrentTripFragment newInstance(String param1, String param2) {
        CurrentTripFragment fragment = new CurrentTripFragment();
        return fragment;
    }
    public CurrentTripFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_current_trip, container, false);
        mv = (MapView)rootView.findViewById(R.id.map);
        setView(savedInstanceState);
        return rootView;
    }

    private void setView(Bundle savedInstanceState) {
        mv.onCreate(savedInstanceState);
        mc = new MapController(mv.getMap());
//        mc.whenMapClick(this);
    }
    @Override
    public void onResume() {
        super.onResume();
         mv.onResume();

        mc.startTrackMyLocation(mc.getMap(),2000,0, MapController.TrackType.TRACK_TYPE_NONE,new MapController.ChangeMyLocation() {
            @Override
            public void changed(GoogleMap map, Location location, boolean lastLocation) {
            }
        });

        new CountDownTimer(1500, 1000) {
            @Override
            public void onFinish() {
                int zoom = (int)(mc.getMap().getMaxZoomLevel() - (mc.getMap().getMinZoomLevel()*2.5));
                mc.animateTo(mc.getMyLocation().getLatitude(),mc.getMyLocation().getLongitude(),zoom);
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();

    }

    @Override
    public void onPause() {
        mv.onPause();
        mc.stopTrackMyLocation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mv.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }
}

package com.webmyne.riteway_driver.trip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.MyApplication;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.MapController;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.my_orders.Trip;
import com.webmyne.riteway_driver.receipt_and_feedback.ReceiptAndFeedbackActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class CurrentTripFragment extends Fragment  {
    private MapView mv;
    private MapController mc;
    TextView txtArrivedOnSite,txtStartTrip,txtStopTrip;
    private ProgressDialog progressDialog;
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
        txtArrivedOnSite=(TextView)rootView.findViewById(R.id.txtArrivedOnSite);
        txtArrivedOnSite.setVisibility(View.VISIBLE);
        txtArrivedOnSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtArrivedOnSite.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.VISIBLE);
                txtStopTrip.setVisibility(View.GONE);
                updateDriverStatus();

            }
        });
        txtStartTrip=(TextView)rootView.findViewById(R.id.txtStartTrip);
        txtStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtArrivedOnSite.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);
                txtStopTrip.setVisibility(View.VISIBLE);
            }
        });
        txtStopTrip=(TextView)rootView.findViewById(R.id.txtStopTrip);
        txtStopTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtArrivedOnSite.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);
                txtStopTrip.setVisibility(View.GONE);
                Intent i=new Intent(getActivity(), ReceiptAndFeedbackActivity.class);
                startActivity(i);

            }
        });
        mv = (MapView)rootView.findViewById(R.id.map);
        setView(savedInstanceState);
        return rootView;
    }

    private void setView(Bundle savedInstanceState) {
        mv.onCreate(savedInstanceState);
        mc = new MapController(mv.getMap());
//        mc.whenMapClick(this);
    }


    public void updateDriverStatus(){

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(getActivity());
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                JSONObject driverStatusObject = new JSONObject();
                try {
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
                    Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
                    driverStatusObject.put("CustomerID", currentTrip.CustomerID);
                    driverStatusObject.put("CustomerNotificationID", currentTrip.CustomerNotificationID);
                    driverStatusObject.put("TripID", currentTrip.TripID);
                    Log.e("driverStatusObject: ", driverStatusObject + "");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.DriverArrivedNotification, driverStatusObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jobj) {
                        String response = jobj.toString();
                        Log.e("response continue: ", response + "");
                        ResponseMessage  responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
                        Log.e("Response: ",responseMessage.Response+"");

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error response: ",error+"");
                    }
                });
                MyApplication.getInstance().addToRequestQueue(req);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();

            }
        }.execute();


    }

    @Override
    public void onResume() {
        super.onResume();
         mv.onResume();
                SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
               final String updatedTimeInterval=preferencesTimeInterval.getString("driver_time_interval", "5");
        mc.startTrackMyLocation(mc.getMap(),2000,0, MapController.TrackType.TRACK_TYPE_NONE,new MapController.ChangeMyLocation() {
            @Override
            public void changed(GoogleMap map, Location location, boolean lastLocation) {

//                Log.e("latitude: ",location.getLatitude()+"");
//                Log.e("Longitude",location.getLongitude()+"");
            }
        });



        new CountDownTimer(1500, 1000) {
            @Override
            public void onFinish() {
                int zoom = (int)(mc.getMap().getMaxZoomLevel() - (mc.getMap().getMinZoomLevel()*2.5));
                try {
                    mc.animateTo(mc.getMyLocation().getLatitude(), mc.getMyLocation().getLongitude(), zoom);
//                    try {
//                        Timer timer=new Timer();
//                        // stopLoginTimer();
//                        timer.scheduleAtFixedRate(new TimerTask() {
//                            @Override
//                            public void run() {
//                                try {
//                                    updateDriverLocation();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        },0,1000*10*Integer.parseInt(updatedTimeInterval));
//                    }catch (NullPointerException e){
//                        e.printStackTrace();
//                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();

    }

    public void updateDriverLocation() {


//                Log.e("latitude: ",mc.getMap().getMyLocation().getLatitude()+"");
//                Log.e("Longitude",mc.getMap().getMyLocation().getLongitude()+"");
//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                JSONObject driverCurrentLocation = new JSONObject();
//                try {
//
//                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
//                    DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
//                    driverCurrentLocation.put("DriverID", driverProfile.DriverID);
//                    driverCurrentLocation.put("Webmyne_Latitude", mc.getMyLocation().getLatitude()+"");
//                    driverCurrentLocation.put("Webmyne_Longitude", mc.getMyLocation().getLongitude()+"");
//
//                    Log.e("driverCurrentLocation: ", driverCurrentLocation + "");
//
//
//                }catch(JSONException e) {
//                    e.printStackTrace();
//                }
//                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.DriverCurrentLocation, driverCurrentLocation, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject jobj) {
//                        String response = jobj.toString();
//                        Log.e("response continue: ", response + "");
//                        ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
//                        Log.e("Response: ",responseMessage.Response+"");
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("error response: ",error+"");
//                    }
//                });
//                MyApplication.getInstance().addToRequestQueue(req);
//                return null;
//            }
//
//        }.execute();

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

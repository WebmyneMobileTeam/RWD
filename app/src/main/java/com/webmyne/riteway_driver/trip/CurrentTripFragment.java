package com.webmyne.riteway_driver.trip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.MapNavigator.Navigator;
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
    TextView txtArrivedOnSite;
    private ProgressDialog progressDialog;

    public boolean needUpdatedLocation=true;
    double updatedDriverLatitude;
    double updatedDriverLongitude;
    LatLng pickup_latlng;
    LatLng dropoff_latlng;
    Location currentLocation;
    Location pickupLocation;
    Location dropoffLocation;

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
        txtArrivedOnSite.setVisibility(View.GONE);
        txtArrivedOnSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtArrivedOnSite.getText().equals("ARRIVED ON SITE")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Title");
                    alert.setMessage("Message");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            updateArrivedOnSiteStatus();
                            dialog.dismiss();

                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();
                } else if(txtArrivedOnSite.getText().equals("START TRIP")) {

                    updateStartTripStatus(AppConstants.tripOnTripStatus);

                    // change map
//                    txtArrivedOnSite.setText("STOP TRIP");
                    txtArrivedOnSite.setVisibility(View.INVISIBLE);
                } else if(txtArrivedOnSite.getText().equals("STOP TRIP")) {
                    SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("updated_fare_and_destination",getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencesTimeInterval.edit();
                    //TODO
                                        editor.putString("fare","");
                    editor.putString("dropoff_address","");
                    editor.putString("dropoff_latitude","");
                    editor.putString("dropoff_longitude","");
                    editor.commit();
//                    Intent i=new Intent(getActivity(), ReceiptAndFeedbackActivity.class);
//                    startActivity(i);
                    Log.e("fare: ",pickupLocation.distanceTo(currentLocation)+"");
                    Log.e("dropoff_latitude: ",currentLocation.getLatitude()+"");
                    Log.e("dropoff_longitude: ",currentLocation.getLongitude()+"");
                    Log.e("dropoff_address: ",currentLocation.getLongitude()+"");
                }




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

    public void updateArrivedOnSiteStatus(){

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
                txtArrivedOnSite.setText("START TRIP");

            }
        }.execute();


    }


    public void updateStartTripStatus(final String status) {
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
                JSONObject tripObject = new JSONObject();
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
                Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
                try {
                    tripObject.put("CustomerID",currentTrip.CustomerID+"");
                    tripObject.put("CustomerNotificationID",currentTrip.CustomerNotificationID+"");
                    tripObject.put("TripID", currentTrip.TripID+"");
                    tripObject.put("TripStatus", status);
                    Log.e("tripObject: ", tripObject + "");


                }catch(JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.RequestedTripStatus, tripObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jobj) {
                        String response = jobj.toString();

                        ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
                        Log.e("after start trip response: ", responseMessage.Response +"");

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
                Toast.makeText(getActivity(), "Trip Started Successfully", Toast.LENGTH_SHORT).show();


            }
        }.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        mv.onResume();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
        pickup_latlng=new LatLng(Double.parseDouble(currentTrip.PickupLatitude),Double.parseDouble(currentTrip.PickupLongitude));
        dropoff_latlng=new LatLng(Double.parseDouble(currentTrip.DropoffLatitude),Double.parseDouble(currentTrip.DropoffLongitude));

        if(pickup_latlng != null) {

            MarkerOptions opts = new MarkerOptions();
            opts.position(pickup_latlng);
            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_pin));
            opts.title("PICK ME UP HERE");
            opts.snippet("");
            addMarker(opts);


        }

        if(dropoff_latlng != null) {

            MarkerOptions opts = new MarkerOptions();
            opts.position(dropoff_latlng);
            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dropoff_pin));
            opts.title("DROP ME HERE");
            opts.snippet("");
            addMarker(opts);


        }

        Navigator nav = new Navigator(mv.getMap(),pickup_latlng,dropoff_latlng);
        nav.findDirections(false);
        nav.setPathColor(Color.parseColor("#4285F4"),Color.BLUE,Color.BLUE);

        pickupLocation=new Location("");
        pickupLocation.setLatitude(pickup_latlng.latitude);
        pickupLocation.setLongitude(pickup_latlng.longitude);

        dropoffLocation=new Location("");
        dropoffLocation.setLatitude(dropoff_latlng.latitude);
        dropoffLocation.setLongitude(dropoff_latlng.longitude);


        SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
        final String updatedTimeInterval=preferencesTimeInterval.getString("driver_time_interval", "5");

        mc.startTrackMyLocation(mc.getMap(),2000,0, MapController.TrackType.TRACK_TYPE_NONE,new MapController.ChangeMyLocation() {
            @Override
            public void changed(GoogleMap map, Location location, boolean lastLocation) {
                currentLocation=location;
                int zoom = (int)(mc.getMap().getMaxZoomLevel() - (mc.getMap().getMinZoomLevel()*2.5));
                try {
                    mc.animateTo(mc.getMyLocation().getLatitude(), mc.getMyLocation().getLongitude(), zoom);

                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                float pickupDistance=location.distanceTo(pickupLocation);
                if(pickupDistance<200){
                    if(!txtArrivedOnSite.isShown() ) {

                        if(!txtArrivedOnSite.getText().toString().equals("START TRIP")) {
                            Toast.makeText(getActivity(), "arrived on site", Toast.LENGTH_SHORT).show();
                            txtArrivedOnSite.setVisibility(View.VISIBLE);
                        }
                    }

                }

                float dropOffDistance=location.distanceTo(dropoffLocation);
                if(dropOffDistance<200) {
                    if(txtArrivedOnSite.getText().toString().equals("START TRIP")) {
                        txtArrivedOnSite.setText("STOP TRIP");
                        Toast.makeText(getActivity(), "arrived on destination", Toast.LENGTH_SHORT).show();
                        txtArrivedOnSite.setVisibility(View.VISIBLE);
                    }
                }

                if(needUpdatedLocation==true){
                    updatedDriverLatitude=location.getLatitude();
                    updatedDriverLongitude=location.getLongitude();
                    needUpdatedLocation=false;
                }
            }
        });



//        new CountDownTimer(1500, 1000) {
//            @Override
//            public void onFinish() {
////                int zoom=(int)(mc.getMap().getMaxZoomLevel()*3);
//                int zoom = (int)(mc.getMap().getMaxZoomLevel() - (mc.getMap().getMinZoomLevel()*2.5));
//                try {
//                    mc.animateTo(mc.getMyLocation().getLatitude(), mc.getMyLocation().getLongitude(), zoom);
//
//                }catch (NullPointerException e){
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//        }.start();


        new CountDownTimer(3000, 1000) {
            @Override
            public void onFinish() {

                try {

                    Timer timer=new Timer();
                    // stopLoginTimer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            updateDriverLocation();
                        }
                    },0,1000*15);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();




    }


    private void addMarker(MarkerOptions opts) {

        mc.addMarker(opts, new MapController.MarkerCallback() {
            @Override
            public void invokedMarker(GoogleMap map, Marker marker) {

            }
        });
    }

    public void updateDriverLocation() {

        needUpdatedLocation=true;
        Log.e("latitude: ",updatedDriverLatitude+"");
        Log.e("Longitude",updatedDriverLongitude+"");

        JSONObject driverCurrentLocation = new JSONObject();
        try {

            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
            DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
            driverCurrentLocation.put("DriverID", driverProfile.DriverID);
            driverCurrentLocation.put("Webmyne_Latitude", updatedDriverLatitude+"");
            driverCurrentLocation.put("Webmyne_Longitude",updatedDriverLongitude+"");




        }catch(JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.DriverCurrentLocation, driverCurrentLocation, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {
                String response = jobj.toString();
                Log.e("response after update driver location: ", response + "");
                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
                Log.e("Response: ",responseMessage.Response+"");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error response: ",error+"");
            }
        });
        MyApplication.getInstance().addToRequestQueue(req);

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

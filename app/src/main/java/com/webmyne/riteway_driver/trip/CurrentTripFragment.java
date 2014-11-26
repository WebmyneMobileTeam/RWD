package com.webmyne.riteway_driver.trip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.webmyne.riteway_driver.customViews.CircleDialog;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.MapController;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.my_orders.Trip;
import com.webmyne.riteway_driver.receipt_and_feedback.ReceiptAndFeedbackFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class CurrentTripFragment extends Fragment  {

    private MapView mv;
    private MapController mc;
    private TextView txtCommonButton;
    private CircleDialog circleDialog;
    public boolean needUpdatedLocation=true;
    private Location currentLocation;
    private double updatedDriverLatitude;
    private double updatedDriverLongitude;
    private LatLng pickup_latlng;
    private LatLng dropoff_latlng;
    private Location pickupLocation;
    private Location dropoffLocation;
    private Trip currentTrip;

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
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_current_trip, container, false);

        txtCommonButton =(TextView)rootView.findViewById(R.id.txtArrivedOnSite);
        txtCommonButton.setVisibility(View.GONE);
        txtCommonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtCommonButton.getText().equals("ARRIVED ON SITE")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Arived On Site Alert");
                    alert.setMessage("Send notification to customer about you reached at pickup location");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isConnected() == true) {
                                updateArrivedOnSiteStatus();
                            } else {
                                Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                    alert.show();

                } else if (txtCommonButton.getText().equals("START TRIP")) {

                    updateStartTripStatus(AppConstants.tripOnTripStatus);
                    txtCommonButton.setVisibility(View.INVISIBLE);

                } else if (txtCommonButton.getText().equals("STOP TRIP")) {

                    try {
                        String address = getAddressValue(currentLocation);
                        SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("updated_fare_and_destination", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencesTimeInterval.edit();
                        editor.putString("fare", String.format("%.2f", (pickupLocation.distanceTo(currentLocation) / 1000) * 0.6214 * Double.parseDouble(currentTrip.TripFare)) + "");
                        editor.putString("dropoff_address", address + "");
                        editor.putString("dropoff_latitude", currentLocation.getLatitude() + "");
                        editor.putString("dropoff_longitude", currentLocation.getLongitude() + "");
                        editor.putString("distance", (String.format("%.2f", pickupLocation.distanceTo(currentLocation) / 1000)) + "");
                        editor.commit();

                        updateStopTripStatus(AppConstants.tripStopStatus);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mv = (MapView)rootView.findViewById(R.id.map);

        setView(savedInstanceState);

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);

        mv.onResume();

        // Update common button's text value according Trip Status
        updateCommonButtonText();

        // Display show pickup and dropoff location on map
        showTripPath();

        // track current location
        trackCurrentLocation();

        // update driver's location after every 15 seconds
        new CountDownTimer(3000, 1000) {
            @Override
            public void onFinish() {

                try {

                    Timer timer = new Timer();
                    // stopLoginTimer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {

                            updateDriverLocation();

                        }
                    }, 0, 1000 * 15);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();

    }


    private void updateCommonButtonText() {

        if(currentTrip.TripStatus.equalsIgnoreCase(AppConstants.tripStopStatus)) {
            txtCommonButton.setText("STOP TRIP");
            txtCommonButton.setVisibility(View.VISIBLE);
        }

        if (currentTrip.TripStatus.equalsIgnoreCase(AppConstants.tripArrivedOnSiteStatus)) {
            txtCommonButton.setText("START TRIP");
            txtCommonButton.setVisibility(View.VISIBLE);
        }

        if (currentTrip.TripStatus.equalsIgnoreCase(AppConstants.tripOnTripStatus)) {
            txtCommonButton.setText("START TRIP");
            txtCommonButton.setVisibility(View.INVISIBLE);
        }

    }

    private void showTripPath() {

        pickup_latlng = new LatLng(Double.parseDouble(currentTrip.PickupLatitude), Double.parseDouble(currentTrip.PickupLongitude));
        dropoff_latlng = new LatLng(Double.parseDouble(currentTrip.DropoffLatitude), Double.parseDouble(currentTrip.DropoffLongitude));

        if (pickup_latlng != null) {
            MarkerOptions opts = new MarkerOptions();
            opts.position(pickup_latlng);
            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_pin));
            opts.title("PICK ME UP HERE");
            opts.snippet("");
            addMarker(opts);
        }

        if (dropoff_latlng != null) {
            MarkerOptions opts = new MarkerOptions();
            opts.position(dropoff_latlng);
            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dropoff_pin));
            opts.title("DROP ME HERE");
            opts.snippet("");
            addMarker(opts);
        }

        Navigator nav = new Navigator(mv.getMap(), pickup_latlng, dropoff_latlng);
        nav.findDirections(false);
        nav.setPathColor(Color.parseColor("#4285F4"), Color.BLUE, Color.BLUE);

    }

    private void trackCurrentLocation() {

        pickupLocation = new Location("");
        pickupLocation.setLatitude(pickup_latlng.latitude);
        pickupLocation.setLongitude(pickup_latlng.longitude);

        dropoffLocation = new Location("");
        dropoffLocation.setLatitude(dropoff_latlng.latitude);
        dropoffLocation.setLongitude(dropoff_latlng.longitude);

        mc.startTrackMyLocation(mc.getMap(), 2000, 0, MapController.TrackType.TRACK_TYPE_NONE, new MapController.ChangeMyLocation() {
            @Override
            public void changed(GoogleMap map, Location location, boolean lastLocation) {

                currentLocation = location;

                try {
                    int zoom = (int) (mc.getMap().getMaxZoomLevel() - (mc.getMap().getMinZoomLevel() * 2.5));
                    mc.animateTo(mc.getMyLocation().getLatitude(), mc.getMyLocation().getLongitude(), zoom);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                // when driver reached at pickup location
                float pickupDistance = location.distanceTo(pickupLocation);
                if (pickupDistance < 200) {
                    if (!txtCommonButton.isShown()) {

                        if (!txtCommonButton.getText().toString().equals("START TRIP") ) {
                            Toast.makeText(getActivity(), "arrived on site", Toast.LENGTH_SHORT).show();
                            txtCommonButton.setVisibility(View.VISIBLE);
                        }
                    }

                }

                // when driver reached at dropoff location
                float dropOffDistance = location.distanceTo(dropoffLocation);
                if (dropOffDistance < 200) {
                    if (txtCommonButton.getText().toString().equals("START TRIP")) {
                        txtCommonButton.setText("STOP TRIP");
                        Toast.makeText(getActivity(), "arrived on destination", Toast.LENGTH_SHORT).show();
                        txtCommonButton.setVisibility(View.VISIBLE);
                    }
                }

                // get updated latitude and longitude value
                if (needUpdatedLocation == true) {
                    updatedDriverLatitude = location.getLatitude();
                    updatedDriverLongitude = location.getLongitude();
                    needUpdatedLocation = false;
                }
            }
        });
    }

    public  boolean isConnected() {

        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }

    public String getAddressValue(Location currentLocation) {
        StringBuilder result = new StringBuilder();
        try {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Address address = addresses.get(0);
            String locality = address.getLocality();
            String city = address.getCountryName();
            String region_code = address.getCountryCode();
            String zipcode = address.getPostalCode();
            String street = address.getAddressLine(0);
            String street2 = address.getAddressLine(1);


            if (street != null) {
                result.append(street + " ");
            }

            if (street2 != null) {
                result.append(street2 + " ");
            }

            if (locality != null) {
                result.append(locality + " ");
            }

            if (city != null) {
                result.append(city + " " + region_code + " ");
            }

            if (zipcode != null) {
                result.append(zipcode);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private void setView(Bundle savedInstanceState) {
        mv.onCreate(savedInstanceState);
        mc = new MapController(mv.getMap());
    }

    public void updateArrivedOnSiteStatus(){

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                circleDialog=new CircleDialog(getActivity(),0);
                circleDialog.setCancelable(true);
                circleDialog.show();
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
                    driverStatusObject.put("TripStatus", AppConstants.tripArrivedOnSiteStatus);
//                    Log.e("driverStatusObject: ", driverStatusObject + "");

                }catch(JSONException e) {
                    e.printStackTrace();
                }

                Reader reader = API.callWebservicePost(AppConstants.DriverArrivedNotification, driverStatusObject.toString());
                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(reader, ResponseMessage.class);
//                Log.e("responseMessage:",responseMessage.Response+"");
                handlePostData();

                return null;
            }


        }.execute();
    }

    public void handlePostData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleDialog.dismiss();
                txtCommonButton.setText("START TRIP");
            }
        });
    }

    public void updateStartTripStatus(final String status) {

        circleDialog=new CircleDialog(getActivity(),0);
        circleDialog.setCancelable(true);
        circleDialog.show();

        JSONObject tripObject = new JSONObject();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
        try {
            tripObject.put("CustomerID",currentTrip.CustomerID+"");
            tripObject.put("CustomerNotificationID",currentTrip.CustomerNotificationID+"");
            tripObject.put("TripID", currentTrip.TripID+"");
            tripObject.put("TripStatus", status);
//            Log.e("tripObject: ", tripObject + "");

        }catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.RequestedTripStatus, tripObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {
                String response = jobj.toString();

                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
//                Log.e("after start trip response: ", responseMessage.Response +"");

                circleDialog.dismiss();

                if(responseMessage.Response.equalsIgnoreCase("Success")) {
                    Toast.makeText(getActivity(), "Trip Started Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error response: ",error+"");
            }
        });
        MyApplication.getInstance().addToRequestQueue(req);

    }


    public void updateStopTripStatus(final String status) {

        circleDialog=new CircleDialog(getActivity(),0);
        circleDialog.setCancelable(true);
        circleDialog.show();

        JSONObject tripObject = new JSONObject();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
        try {
            tripObject.put("CustomerID",currentTrip.CustomerID+"");
            tripObject.put("CustomerNotificationID",currentTrip.CustomerNotificationID+"");
            tripObject.put("TripID", currentTrip.TripID+"");
            tripObject.put("TripStatus", status);
//            Log.e("tripObject: ", tripObject + "");


        }catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.RequestedTripStatus, tripObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {
                String response = jobj.toString();

                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
//                Log.e("after stop trip response: ", responseMessage.Response +"");

                circleDialog.dismiss();

                if(responseMessage.Response.equalsIgnoreCase("Success")) {
//                    Toast.makeText(getActivity(), "Trip Stop Successfully", Toast.LENGTH_SHORT).show();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();

                    ReceiptAndFeedbackFragment receiptAndFeedbackFragment = ReceiptAndFeedbackFragment.newInstance("", "");
                    if (manager.findFragmentByTag("RECEIPT_AND_FEEDBACK") == null) {
                        ft.replace(R.id.main_content, receiptAndFeedbackFragment,"RECEIPT_AND_FEEDBACK").commit();
                    }

                } else {
                    Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error response: ",error+"");
            }
        });
        MyApplication.getInstance().addToRequestQueue(req);

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
//        Log.e("latitude: ",updatedDriverLatitude+"");
//        Log.e("Longitude",updatedDriverLongitude+"");

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
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
                Reader reader = API.callWebservicePost(AppConstants.DriverCurrentLocation, driverCurrentLocation.toString());

                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(reader, ResponseMessage.class);
//                Log.e("responseMessage:",responseMessage.Response+"");

                return null;

            }

        }.execute();

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

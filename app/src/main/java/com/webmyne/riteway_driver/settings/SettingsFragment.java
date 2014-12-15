package com.webmyne.riteway_driver.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.CustomViews.CircleDialog;
import com.webmyne.riteway_driver.CustomViews.ComplexPreferences;
import com.webmyne.riteway_driver.CustomViews.ListDialog;
import com.webmyne.riteway_driver.application.MyApplication;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.home.DrawerActivity;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.GPSTracker;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.my_orders.MyOrdersFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsFragment extends Fragment  {
//    private LinearLayout linearIntervalTime;
//    private TextView txtUpdateTime;
//    private ArrayList<String> timeList;
    private Switch driverStatusSwitch;
    private CircleDialog circleDialog;

    private GPSTracker gpsTracker;
    private double updatedDriverLatitude;
    private double updatedDriverLongitude;
    public static Timer timer;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        timeList=new ArrayList<String>();
//        timeList.add("1");
//        timeList.add("2");
//        timeList.add("3");
//        timeList.add("5");
//        timeList.add("7");
//        timeList.add("10");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView=inflater.inflate(R.layout.fragment_settings, container, false);

        initView(convertView);

            SharedPreferences preferences = getActivity().getSharedPreferences("driver_status", getActivity().MODE_PRIVATE);
            driverStatusSwitch.setChecked(preferences.getBoolean("driver_status", true));

//            SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval", getActivity().MODE_PRIVATE);
//            txtUpdateTime.setText(preferencesTimeInterval.getString("driver_time_interval", "5") + " minutes");

        return convertView;
    }

    private void initView(View convertView){
//        linearIntervalTime=(LinearLayout)convertView.findViewById(R.id.linearIntervalTime);
//        txtUpdateTime=(TextView)convertView.findViewById(R.id.txtUpdateTime);
        driverStatusSwitch=(Switch)convertView.findViewById(R.id.driverStatusSwitch);

//        linearIntervalTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });

        driverStatusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverStatusSwitch.isChecked()) {
                    AppConstants.driverStatusBoolValue=true;
                    if(isConnected()==true) {
                        updateDriverStatus();
                    } else {
                        Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AppConstants.driverStatusBoolValue=false;
                    if(isConnected()==true) {
                        updateDriverStatus();
                    } else {
                        Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public  boolean isConnected() {

        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }


    @Override
    public void onResume() {
        super.onResume();
        gpsTracker = new GPSTracker(getActivity());
    }

    public void updateDriverStatus(){

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
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
                    DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);

                    driverStatusObject.put("Webmyne_Active", AppConstants.driverStatusBoolValue);
                    driverStatusObject.put("DriverID", driverProfile.DriverID);
                    Log.e("driverStatusObject: ", driverStatusObject + "");

                }catch(JSONException e) {
                    e.printStackTrace();
                }

                Reader reader = API.callWebservicePost(AppConstants.DriverStatus, driverStatusObject.toString());
                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(reader, ResponseMessage.class);
                Log.e("responseMessage:",responseMessage.Response+"");
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
                SharedPreferences preferences = getActivity().getSharedPreferences("driver_status", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("driver_status", AppConstants.driverStatusBoolValue);
                editor.commit();
                Toast.makeText(getActivity(), "Driver Status is updated", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    public void showDialog() {
//
//        ListDialog listDialog = new ListDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
//        listDialog.setCancelable(true);
//        listDialog.setCanceledOnTouchOutside(true);
//        listDialog.title("SELECT TIME");
//        listDialog.setItems(timeList);
//        listDialog.setSelectedListner(this);
//        listDialog.show();
//    }


//    @Override
//    public void selected(final String value) {
//
//        txtUpdateTime.setText(value+" minutes");
//        SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
//        SharedPreferences.Editor editor=preferencesTimeInterval.edit();
//        editor.putString("driver_time_interval",value);
//        editor.commit();
//
//        try {
//            if (MyOrdersFragment.timer != null) {
//                MyOrdersFragment.timer.cancel();
////                Log.e("MyOrdersFragment.timer", "canceled");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//                try {
//                    SharedPreferences preferencesTimeIntervalUpdate = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
//                    final String updatedTimeInterval=preferencesTimeIntervalUpdate.getString("driver_time_interval", "5");

//                    timer=new Timer();
//                    timer.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//
//                            updateDriverLocation();
//                        }
//                    },0,1000*60*Integer.parseInt(updatedTimeInterval));
//                }catch (NullPointerException e){
//                    e.printStackTrace();
//                }
//    }

//    public void updateDriverLocation() {
//
//        if (gpsTracker.canGetLocation()) {
//            updatedDriverLatitude=gpsTracker.latitude;
//            updatedDriverLongitude=gpsTracker.longitude;
//        }
//        try {
////            Log.e("timer: ", "timer cancelled");
//            MyOrdersFragment.timer.cancel();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
////        Log.e("latitude setting: ",updatedDriverLatitude+"");
////        Log.e("Longitude setting: ",updatedDriverLongitude+"");
//
//
//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                JSONObject driverCurrentLocation = new JSONObject();
//                try {
//
//                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
//                    DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
//
//                    driverCurrentLocation.put("DriverID", driverProfile.DriverID);
//                    driverCurrentLocation.put("Webmyne_Latitude", updatedDriverLatitude+"");
//                    driverCurrentLocation.put("Webmyne_Longitude",updatedDriverLongitude+"");
//
//                }catch(JSONException e) {
//                    e.printStackTrace();
//                }
//                Reader reader = API.callWebservicePost(AppConstants.DriverCurrentLocation, driverCurrentLocation.toString());
//                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(reader, ResponseMessage.class);
////                Log.e("responseMessage:",responseMessage.Response+"");
//
//                return null;
//
//            }
//
//
//        }.execute();
//
//    }

}

package com.webmyne.riteway_driver.home;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.application.MyApplication;
import com.webmyne.riteway_driver.customViews.CallWebService;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A placeholder fragment containing a splash screen.
     */
    public static class PlaceholderFragment extends Fragment {

        private GoogleCloudMessaging gcm;
        private String regid;
        private String PROJECT_NUMBER = "766031645889";
        private String driverIMEI_Number;
        private DriverProfile driverProfile;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_launcher, container, false);
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            if(isConnected()) { // Check Internet Coneection Availability
                if (isFirstTime()) { // get GCM Id and post IMEI Number
                    getRegId();
                } else {    // show home screen
                    new CountDownTimer(2500, 1000) {
                        @Override
                        public void onFinish() {
                            try {
                                Intent i = new Intent(getActivity(), DrawerActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                    }.start();

                }
            } else { // show dialog when internet connection unavailable
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Error");
                alert.setMessage("No Internet Connection");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
                alert.show();
            }
        }

        public void getRegId(){

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    driverIMEI_Number= telephonyManager.getDeviceId();

                    Log.e("imei number...........",driverIMEI_Number+"");

                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(getActivity());
                        }
                        regid = gcm.register(PROJECT_NUMBER);
                        Log.e("GCM ID :", regid);
                        if(regid==null || regid==""){

                            SharedPreferences preferences = getActivity().getSharedPreferences("run_before",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("RanBefore", false);
                            editor.commit();

                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Error");
                            alert.setMessage("Internal Server Error");
                            alert.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getRegId();
                                    dialog.dismiss();
                                }
                            });
                            alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                            alert.show();

                        } else {

                            if(isConnected()==true) {
                                checkValidDriver();
                            } else {
                                Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }
            }.execute();

        } // end of getRegId

        public void checkValidDriver() {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    JSONObject driverProfileObject = new JSONObject();
                    try {
                        driverProfileObject.put("Active", AppConstants.driverStatusBoolValue);
                        driverProfileObject.put("Webmyne_DeviceType", AppConstants.deviceType);
                        driverProfileObject.put("Webmyne_DriverIMEI_Number",driverIMEI_Number+ "");
                        driverProfileObject.put("Webmyne_NotificationID", regid+"");
                        Log.e("driverProfileObject: ",driverProfileObject+"");
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    Reader reader = API.callWebservicePost(AppConstants.DriverProfile, driverProfileObject.toString());
                    driverProfile = new GsonBuilder().create().fromJson(reader, DriverProfile.class);
                    handlePostData();
                    return null;
                }
            }.execute();
        }

        public void handlePostData() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.e("Active",driverProfile.Active+"");
                    Log.e("CompanyID",driverProfile.CompanyID+"");
                    Log.e("DriverID",driverProfile.DriverID+"");
                    Log.e("FirstName",driverProfile.FirstName+"");
                    Log.e("LastName",driverProfile.LastName+"");
                    Log.e("Response",driverProfile.Response+"");
                    Log.e("Webmyne_DeviceType",driverProfile.Webmyne_DeviceType+"");
                    Log.e("Webmyne_DriverIMEI_Number",driverProfile.Webmyne_DriverIMEI_Number+"");
                    Log.e("Webmyne_Latitude",driverProfile.Webmyne_Latitude+"");
                    Log.e("Webmyne_Longitude",driverProfile.Webmyne_Longitude+"");
                    Log.e("Webmyne_NotificationID",driverProfile.Webmyne_NotificationID+"");

                    Log.e("Check Valid Driver",driverProfile.Response+"");
                    Toast.makeText(getActivity(), driverProfile.Webmyne_DriverIMEI_Number+"", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), driverProfile.Response+"", Toast.LENGTH_LONG).show();
                    if(driverProfile.Response.equalsIgnoreCase("Success")) {

                        SharedPreferences preferences = getActivity().getSharedPreferences("run_before",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("RanBefore", true);
                        editor.commit();

                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
                        complexPreferences.putObject("driver_data", driverProfile);
                        complexPreferences.commit();

                        Intent i = new Intent(getActivity(), DrawerActivity.class);
                        startActivity(i);
                        getActivity().finish();

                    } else {

                        SharedPreferences preferences = getActivity().getSharedPreferences("run_before",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("RanBefore", false);
                        editor.commit();

                        // show alert when driver's imei number is not match with ddatabase
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Invalid Driver");
                        alert.setMessage("Driver not Found");
                        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                        alert.show();

                    }
                }
            });
        }

        // Check Internet Connection
        public  boolean isConnected() {

            ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            return  isConnected;
        }

        public boolean isFirstTime() {

            SharedPreferences preferences = getActivity().getSharedPreferences("run_before", MODE_PRIVATE);
            boolean ranBefore = preferences.getBoolean("RanBefore", false);
            if (!ranBefore) {
                // first time
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("RanBefore", true);
                editor.commit();
            }
            return !ranBefore;
        }

    } // end of fragment


}

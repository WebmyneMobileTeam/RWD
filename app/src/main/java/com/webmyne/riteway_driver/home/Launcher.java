package com.webmyne.riteway_driver.home;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;

import java.io.IOException;


public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a splash screen.
     */
    public static class PlaceholderFragment extends Fragment {

        GoogleCloudMessaging gcm;
        String regid;
        String PROJECT_NUMBER = "766031645889";

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

        // Check Internet Connection
        public  boolean isConnected() {

            ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
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
            } else {
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
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                String SENDER_ID="APA91bHGfPgU7dKGF6cbBX8xhPePRTWdXooX2ZkFRDgVjNpcSWogjoUxYsbtrJH0MimExsdtpNMO_Clapjm1blkWxGuWwqB3WrerMBA-uh48CtXlIauvZj6hfEwefWDqApz37xELI4hrjRFW0yLBNTHCOMOP7IPqKg";
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(getActivity());
                        }
                        regid = gcm.register(PROJECT_NUMBER);
                        try {
                            Bundle data = new Bundle();
                            data.putString("my_message", "Hello World");
                            data.putString("my_action",
                                    "com.google.android.gcm.demo.app.ECHO_NOW");
                            String id = Integer.toString(2);
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
//                            msg = "Sent message";
                        } catch (IOException ex) {
//                            msg = "Error :" + ex.getMessage();
                        }

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
                            SharedPreferences preferences = getActivity().getSharedPreferences("run_before",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("RanBefore", true);
                            editor.commit();

                            Intent i = new Intent(getActivity(), DrawerActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } // end of getRegId
    } // end of fragment


}

package com.webmyne.riteway_driver.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmyne.riteway_driver.R;


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
            SharedPreferences preferences = getActivity().getSharedPreferences("run_before", MODE_PRIVATE);
            boolean ranBefore = preferences.getBoolean("RanBefore", false);
            if(isConnected()) { // Check Internet Coneection Availability
                if (ranBefore==false) { // get GCM Id and post IMEI Number
//                    getRegId();
                    new CountDownTimer(2500, 1000) {
                        @Override
                        public void onFinish() {
                            try {
                                Intent i = new Intent(getActivity(), CheckValidDriver.class);
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


        // Check Internet Connection
        public  boolean isConnected() {

            ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            return  isConnected;
        }

    } // end of fragment


}

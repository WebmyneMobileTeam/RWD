package com.webmyne.riteway_driver.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.webmyne.riteway_driver.application.MyApplication;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.customViews.ListDialog;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.home.DrawerActivity;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsFragment extends Fragment implements ListDialog.setSelectedListner {
    LinearLayout linearIntervalTime;
    TextView txtUpdateTime;
    ArrayList<String> timeList;
    Switch driverStatusSwitch;
    ProgressDialog progressDialog;

//    private static int CLICKED_POSITION = 0;
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
        timeList=new ArrayList<String>();
        timeList.add("3");
        timeList.add("5");
        timeList.add("7");
        timeList.add("10");
        timeList.add("15");
        timeList.add("20");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convView=inflater.inflate(R.layout.fragment_settings, container, false);
        linearIntervalTime=(LinearLayout)convView.findViewById(R.id.linearIntervalTime);
        txtUpdateTime=(TextView)convView.findViewById(R.id.txtUpdateTime);
        driverStatusSwitch=(Switch)convView.findViewById(R.id.driverStatusSwitch);
        SharedPreferences preferences = getActivity().getSharedPreferences("driver_status",getActivity().MODE_PRIVATE);
        driverStatusSwitch.setChecked(preferences.getBoolean("driver_status", true));
        SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
        txtUpdateTime.setText(preferencesTimeInterval.getString("driver_time_interval", "5")+" minutes");
        linearIntervalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        driverStatusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverStatusSwitch.isChecked()) {
                    AppConstants.driverStatusBoolValue=true;
                    updateDriverStatus();
                } else {
                    AppConstants.driverStatusBoolValue=false;
                    updateDriverStatus();
                }
            }
        });
        return convView;
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
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
                    DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
                    driverStatusObject.put("Active", AppConstants.driverStatusBoolValue);
                    driverStatusObject.put("DriverID", driverProfile.DriverID);

                    Log.e("driverStatusObject: ", driverStatusObject + "");


                }catch(JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.DriverStatus, driverStatusObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jobj) {
                        String response = jobj.toString();
                        Log.e("response continue: ", response + "");
                      ResponseMessage  responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
                        Log.e("Response: ",responseMessage.Response+"");
                        SharedPreferences preferences = getActivity().getSharedPreferences("driver_status",getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("driver_status", AppConstants.driverStatusBoolValue);
                        editor.commit();
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
                Toast.makeText(getActivity(), "Driver Status is updated", Toast.LENGTH_SHORT).show();
            }
        }.execute();


    }
    public void showDialog() {

        ListDialog listDialog = new ListDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        listDialog.setCancelable(true);
        listDialog.setCanceledOnTouchOutside(true);
        listDialog.title("SELECT TIME");
        listDialog.setItems(timeList);
        listDialog.setSelectedListner(this);
        listDialog.show();
    }

    @Override
    public void selected(final String value) {

        txtUpdateTime.setText(value+" minutes");

        SharedPreferences preferencesTimeInterval = getActivity().getSharedPreferences("driver_time_interval",getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencesTimeInterval.edit();
        editor.putString("driver_time_interval",value);
        editor.commit();
    }



}

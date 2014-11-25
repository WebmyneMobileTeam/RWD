package com.webmyne.riteway_driver.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.CustomTypeface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;

public class CheckValidDriver extends BaseActivity {

    private EditText etCode;
    private Button btnCheck;
    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "766031645889";
    private DriverProfile driverProfile;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_imeinumber);
        etCode=(EditText)findViewById(R.id.etCode);
        txtHeader.setText("CHECK VALID DRIVER");
        btnCheck=(Button)findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCode.getText().toString().length()>0){
                    if(isConnected()==true){
                        getRegId();
                    } else {
                        Toast.makeText(CheckValidDriver.this, "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckValidDriver.this, "Fill up code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getRegId(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(CheckValidDriver.this);
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(CheckValidDriver.this);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    Log.e("GCM ID :", regid);
                    if(regid==null || regid==""){

                        SharedPreferences preferences = getSharedPreferences("run_before", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("RanBefore", false);
                        editor.commit();

                        AlertDialog.Builder alert = new AlertDialog.Builder(CheckValidDriver.this);
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
                                finish();
                            }
                        });
                        alert.show();

                    } else {

                        if(isConnected()==true) {
                            checkValidDriver();
                        } else {
                            Toast.makeText(CheckValidDriver.this, "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
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
                    driverProfileObject.put("Webmyne_DriverIMEI_Number",etCode.getText().toString().trim());
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

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return CustomTypeface.getInstance().createView(name, context, attrs);
    }

    public void handlePostData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Log.e("Active", driverProfile.Active + "");
                Log.e("CompanyID", driverProfile.CompanyID + "");
                Log.e("DriverID", driverProfile.DriverID + "");
                Log.e("FirstName", driverProfile.FirstName + "");
                Log.e("LastName", driverProfile.LastName + "");
                Log.e("Response", driverProfile.Response + "");
                Log.e("Webmyne_DeviceType", driverProfile.Webmyne_DeviceType + "");
                Log.e("Webmyne_DriverIMEI_Number", driverProfile.Webmyne_DriverIMEI_Number + "");
                Log.e("Webmyne_Latitude", driverProfile.Webmyne_Latitude + "");
                Log.e("Webmyne_Longitude", driverProfile.Webmyne_Longitude + "");
                Log.e("Webmyne_NotificationID", driverProfile.Webmyne_NotificationID + "");

                Log.e("Check Valid Driver", driverProfile.Response + "");
//                Toast.makeText(CheckValidDriver.this, driverProfile.Webmyne_DriverIMEI_Number + "", Toast.LENGTH_LONG).show();
//                Toast.makeText(CheckValidDriver.this, driverProfile.Response + "", Toast.LENGTH_LONG).show();
                if (driverProfile.Response.equalsIgnoreCase("Success")) {

                    SharedPreferences preferences = getSharedPreferences("run_before", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("RanBefore", true);
                    editor.commit();

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(CheckValidDriver.this, "driver_data", 0);
                    complexPreferences.putObject("driver_data", driverProfile);
                    complexPreferences.commit();

                    Intent i = new Intent(CheckValidDriver.this, DrawerActivity.class);
                    startActivity(i);
                    finish();

                } else {

                    SharedPreferences preferences = getSharedPreferences("run_before", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("RanBefore", false);
                    editor.commit();

                    // show alert when driver's imei number is not match with ddatabase
                    AlertDialog.Builder alert = new AlertDialog.Builder(CheckValidDriver.this);
                    alert.setTitle("Invalid Driver");
                    alert.setMessage("Driver not Found");
                    alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    alert.show();

                }
            }
        });
    }




    // Check Internet Connection
    public  boolean isConnected() {

        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }

}

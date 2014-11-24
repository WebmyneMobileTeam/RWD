package com.webmyne.riteway_driver.my_orders;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.application.MyApplication;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.CustomTypeface;
import com.webmyne.riteway_driver.model.ResponseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrderDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        txtHeader.setText("TRIP DETAILS");
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return CustomTypeface.getInstance().createView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a order detail view.
     */
    public static class PlaceholderFragment extends android.app.Fragment {


        ProgressDialog progressDialog;
        Trip currentTrip;
        TextView currentTripDriverName, currentTripPickup, currentTripDropoff, currentTripPickupNote, currentTripDate, currentTripTime,
                currentTripDistance, txtTripStatus, currentTripPaymentType, currentTripFare, currentTripTip, currentTripFee,txtTotalAmount,
                txtCancelTrip,txtAcceptTrip,currentTripMobile,currentTripEmail,txtpaymentType;
        LinearLayout bottomButtonSelection;
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
            txtCancelTrip=(TextView)rootView.findViewById(R.id.txtCancelTrip);
            txtAcceptTrip=(TextView)rootView.findViewById(R.id.txtAcceptTrip);
            txtCancelTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected()==true) {
                        tripResponse("Cancelled By Driver");
                    }else {
                        Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            txtAcceptTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected()==true) {
                        tripResponse("Accept");
                    } else {
                        Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            bottomButtonSelection=(LinearLayout)rootView.findViewById(R.id.bottomButtonSelection);
            currentTripMobile=(TextView)rootView.findViewById(R.id.currentTripMobile);
            currentTripEmail=(TextView)rootView.findViewById(R.id.currentTripEmail);
            currentTripDriverName=(TextView)rootView.findViewById(R.id.currentTripCustomerName);
            currentTripPickup=(TextView)rootView.findViewById(R.id.currentTripPickup);
            currentTripDropoff=(TextView)rootView.findViewById(R.id.currentTripDropoff);
            currentTripPickupNote=(TextView)rootView.findViewById(R.id.currentTripPickupNote);
            currentTripDate=(TextView)rootView.findViewById(R.id.currentTripDate);
            currentTripTime=(TextView)rootView.findViewById(R.id.currentTripTime);
            currentTripDistance=(TextView)rootView.findViewById(R.id.currentTripDistance);
            txtTripStatus=(TextView)rootView.findViewById(R.id.txtTripStatus);
            currentTripFare=(TextView)rootView.findViewById(R.id.currentTripFare);
            currentTripTip=(TextView)rootView.findViewById(R.id.currentTripTip);
            currentTripFee=(TextView)rootView.findViewById(R.id.currentTripFee);
            txtTotalAmount=(TextView)rootView.findViewById(R.id.txtTotalAmount);

            currentTripPaymentType=(TextView)rootView.findViewById(R.id.currentTripPaymentType);
            txtpaymentType=(TextView)rootView.findViewById(R.id.txtpaymentType);

            return rootView;
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
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
            currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
            currentTripMobile.setText(currentTrip.CustomerMobile);
            currentTripEmail.setText(currentTrip.CustomerEmail);
            currentTripDriverName.setText(currentTrip.CustomerName);
            currentTripPickup.setText(currentTrip.PickupAddress);
            currentTripDropoff.setText(currentTrip.DropOffAddress);
            currentTripPickupNote.setText(currentTrip.PickupNote);
            currentTripDate.setText(getFormatedDate());
            currentTripTime.setText(currentTrip.PickupTime);
            currentTripDistance.setText(currentTrip.TripDistance+" kms");

            if(currentTrip.PaymentType !=null){
                currentTripPaymentType.setVisibility(View.VISIBLE);
                txtpaymentType.setVisibility(View.VISIBLE);
                currentTripPaymentType.setText(currentTrip.PaymentType);
            } else {
                currentTripPaymentType.setVisibility(View.GONE);
                txtpaymentType.setVisibility(View.GONE);
            }

            currentTripFare.setText("$ "+String.format("%.2f", Double.parseDouble(currentTrip.TripDistance)*0.6214*Double.parseDouble(currentTrip.TripFare)));
            currentTripTip.setText(currentTrip.TipPercentage+" %");
            currentTripFee.setText("$ "+currentTrip.TripFee);
            txtTotalAmount.setText(String.format("$ %.2f", getTotal(currentTrip))+"");
            txtTripStatus.setText(currentTrip.TripStatus);
            if(currentTrip.TripStatus.contains(AppConstants.tripInProgressStatus) ){
                bottomButtonSelection.setVisibility(View.VISIBLE);
            } else {
                bottomButtonSelection.setVisibility(View.GONE);
            }
        }

        public String getFormatedDate() {

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            float dateinFloat = Float.parseFloat(currentTrip.TripDate);
            Date date = float2Date(dateinFloat);
            return  format.format(date);
        }
        public static java.util.Date float2Date(float nbSeconds) {
            java.util.Date date_origine;
            java.util.Calendar date = java.util.Calendar.getInstance();
            java.util.Calendar origine = java.util.Calendar.getInstance();
            origine.set(1970, Calendar.JANUARY, 1);
            date_origine = origine.getTime();
            date.setTime(date_origine);
            date.add(java.util.Calendar.SECOND, (int) nbSeconds);
            return date.getTime();
        }

        public double getTotal(Trip currentTrip) {
            Double total;
            String tripFareValue=String.format("%.2f", Double.parseDouble(currentTrip.TripDistance)*0.6214*Double.parseDouble(currentTrip.TripFare));
            if(Integer.parseInt(currentTrip.TipPercentage)>0){

                Double tip=((Double.parseDouble(tripFareValue)*Double.parseDouble(currentTrip.TipPercentage))/100);
                total= Double.parseDouble(tripFareValue)+tip;
            } else {
                total=Double.parseDouble(tripFareValue);
            }
            total=total+Double.parseDouble(currentTrip.TripFee);
            return total;
        }

        public void tripResponse(final String status) {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog=new ProgressDialog(getActivity());
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    JSONObject tripObject = new JSONObject();
                    try {
                        tripObject.put("CustomerID",currentTrip.CustomerID+"");
                        tripObject.put("CustomerNotificationID",currentTrip.CustomerNotificationID+"");
                        tripObject.put("TripID", currentTrip.TripID+"");
                        tripObject.put("TripStatus", status);
                        Log.e("tripObject: ", tripObject + "");


                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                    Reader reader = API.callWebservicePost(AppConstants.RequestedTripStatus, tripObject.toString());

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
                    progressDialog.dismiss();
                    getActivity().finish();
                }
            });
        }

    }
}

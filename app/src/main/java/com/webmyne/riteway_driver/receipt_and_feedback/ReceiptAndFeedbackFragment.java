package com.webmyne.riteway_driver.receipt_and_feedback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.MyApplication;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.customViews.ListDialog;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.my_orders.MyOrdersFragment;
import com.webmyne.riteway_driver.my_orders.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReceiptAndFeedbackFragment extends Fragment implements ListDialog.setSelectedListner{

    TextView txtPaymentType,txtTripComplete,customerRatting,
            txtTripCustomerName,txtTripPickupAddress,txtTripDropoffAddress,
            txtTripDistance,txtTripDate,txtTripFare,txtTripTip,txtTripFee,txtTotalAmount;
    EditText customerComments;
    RatingBar rattings;

    String newFare,newDropoffAddress,newDropoffLatitude,newDropoffLongitude,newDistance;
    Trip currentTrip;
    ArrayList<String> dateSelectionArray=new ArrayList<String>();
    private ProgressDialog progressDialog;
    public static ReceiptAndFeedbackFragment newInstance(String param1, String param2) {
        ReceiptAndFeedbackFragment fragment = new ReceiptAndFeedbackFragment();

        return fragment;
    }

    public ReceiptAndFeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateSelectionArray.add("Cash");
        dateSelectionArray.add("Credit Card");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.fragment_receipt_and_feedback, container, false);

        txtTripCustomerName=(TextView)convertView.findViewById(R.id.txtTripCustomerName);
        txtTripPickupAddress=(TextView)convertView.findViewById(R.id.txtTripPickupAddress);
        txtTripDropoffAddress=(TextView)convertView.findViewById(R.id.txtTripDropoffAddress);
        txtTripDistance=(TextView)convertView.findViewById(R.id.txtTripDistance);
        txtTripDate=(TextView)convertView.findViewById(R.id.txtTripDate);
        txtTripFare=(TextView)convertView.findViewById(R.id.txtTripFare);
        txtTripTip=(TextView)convertView.findViewById(R.id.txtTripTip);
        txtTripFee=(TextView)convertView.findViewById(R.id.txtTripFee);
        txtTotalAmount=(TextView)convertView.findViewById(R.id.txtTotalAmount);
        customerRatting=(TextView)convertView.findViewById(R.id.txtCustomerRatting);
        rattings=(RatingBar)convertView.findViewById(R.id.rattings);
        rattings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                customerRatting.setText(String.valueOf(rating)+"");
            }
        });

        txtPaymentType=(TextView)convertView.findViewById(R.id.txtPaymentType);
        txtTripComplete=(TextView)convertView.findViewById(R.id.txtTripComplete);

        customerComments=(EditText)convertView.findViewById(R.id.txtCustomerComments);
        txtTripComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completTrip();
            }
        });
        txtPaymentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getActivity().getSharedPreferences("updated_fare_and_destination",getActivity().MODE_PRIVATE);
        newFare=preferences.getString("fare", "");
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
        newDropoffAddress=preferences.getString("dropoff_address", "");
        newDropoffLatitude=preferences.getString("dropoff_latitude", "");
        newDropoffLongitude=preferences.getString("dropoff_longitude", "");
        newDistance=preferences.getString("distance", "");

        txtTripCustomerName.setText(currentTrip.CustomerName+"");
        txtTripPickupAddress.setText(currentTrip.PickupAddress+"");
        txtTripDropoffAddress.setText(newDropoffAddress+"");
        txtTripDistance.setText(newDistance+" kms");
        txtTripDate.setText(getFormatedDate(currentTrip)+"");
        txtTripFare.setText("$ "+newFare+"");
        txtTripTip.setText(currentTrip.TipPercentage+" %");
        txtTripFee.setText("$ "+currentTrip.TripFee+"");
        txtTotalAmount.setText(String.format("$ %.2f", getTotal(currentTrip))+"");
    }

    public void completTrip(){

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JSONObject driverStatusObject = new JSONObject();
        try {
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
            Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
            driverStatusObject.put("CustomerID", currentTrip.CustomerID+"");
            driverStatusObject.put("CustomerComments", customerComments.getText().toString()+"");
            driverStatusObject.put("CustomerRattings", customerRatting.getText().toString()+"");
            driverStatusObject.put("PaymentType",txtPaymentType.getText().toString()+"" );
            driverStatusObject.put("TripID", currentTrip.TripID+"");
            driverStatusObject.put("TripStatus", AppConstants.tripSuccessStatus);
            driverStatusObject.put("DropOffAddress", newDropoffAddress+"");
            driverStatusObject.put("DropoffLatitude", newDropoffLatitude+"");
            driverStatusObject.put("DropoffLongitude", newDropoffLongitude+"");
            driverStatusObject.put("TripFare", currentTrip.TripFare+"");
            driverStatusObject.put("isCustomerFeedbackGiven", true);
            driverStatusObject.put("TripDistance", newDistance+"");

            Log.e("driverStatusObject: ", driverStatusObject + "");
        }catch(JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.TripCompletion, driverStatusObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {
                String response = jobj.toString();
                Log.e("response continue: ", response + "");
                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
                Log.e("Response: ",responseMessage.Response+"");
                progressDialog.dismiss();

                if(responseMessage.Response.equalsIgnoreCase("Success")){
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    Toast.makeText(getActivity(), "Trip completed Successfully", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = manager.beginTransaction();
                    MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
                    if (manager.findFragmentByTag("MY_ORDERS") == null) {
                        ft.replace(R.id.main_content, myOrdersFragment,"MY_ORDERS").commit();
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

    public void showDialog() {

        ListDialog listDialog = new ListDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        listDialog.setCancelable(true);
        listDialog.setCanceledOnTouchOutside(true);
        listDialog.title("SELECT PAYMENT TYPE");
        listDialog.setItems(dateSelectionArray);
        listDialog.setSelectedListner(this);
        listDialog.show();
    }

    @Override
    public void selected(String value) {

        txtPaymentType.setText(value);

    }

    public String getFormatedDate(Trip currentTrip) {

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        float dateinFloat = Float.parseFloat(currentTrip.TripDate);
        Date date = float2Date(dateinFloat);
        return  format.format(date);
    }
    public  java.util.Date float2Date(float nbSeconds) {
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


}

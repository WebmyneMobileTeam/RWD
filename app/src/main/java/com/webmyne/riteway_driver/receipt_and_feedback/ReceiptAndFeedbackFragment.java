package com.webmyne.riteway_driver.receipt_and_feedback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.webmyne.riteway_driver.CustomViews.CircleDialog;
import com.webmyne.riteway_driver.CustomViews.ComplexPreferences;
import com.webmyne.riteway_driver.CustomViews.ListDialog;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.MyApplication;

import com.webmyne.riteway_driver.model.API;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.my_orders.MyOrdersFragment;
import com.webmyne.riteway_driver.my_orders.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReceiptAndFeedbackFragment extends Fragment implements ListDialog.setSelectedListner{

    private TextView txtPaymentType,txtTripComplete,customerRatting,
            txtTripCustomerName,txtTripPickupAddress,txtTripDropoffAddress,
            txtTripDistance,txtTripDate,txtTripFare,txtTripTip,txtTripFee,txtTotalAmount;
    private EditText customerComments;
    private RatingBar rattings;
    private String newFare,newDropoffAddress,newDropoffLatitude,newDropoffLongitude,newDistance;
    private Trip currentTrip;
    private ArrayList<String> dateSelectionArray=new ArrayList<String>();
    private CircleDialog circleDialog;

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

        initView(convertView);

        return convertView;
    }

    private void initView(View convertView){

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
        txtPaymentType=(TextView)convertView.findViewById(R.id.txtPaymentType);
        txtTripComplete=(TextView)convertView.findViewById(R.id.txtTripComplete);
        customerComments=(EditText)convertView.findViewById(R.id.txtCustomerComments);

        txtPaymentType.setText("Cash");

        rattings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                customerRatting.setText(String.valueOf(rating)+"");
            }
        });

        txtTripComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    completTrip();
                } else {
                    Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });


        txtPaymentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
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
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
        currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);

        SharedPreferences preferences = getActivity().getSharedPreferences("updated_fare_and_destination",getActivity().MODE_PRIVATE);
        newFare=preferences.getString("fare", "");
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
//                    Log.e("driverStatusObject: ", driverStatusObject + "");
                }catch(JSONException e) {
                    e.printStackTrace();
                }

                Reader reader = API.callWebservicePost(AppConstants.TripCompletion, driverStatusObject.toString());
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
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Toast.makeText(getActivity(), "Trip completed Successfully", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = manager.beginTransaction();
                MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
                if (manager.findFragmentByTag("MY_ORDERS") == null) {
                    ft.replace(R.id.main_content, myOrdersFragment,"MY_ORDERS").commit();
                }
            }
        });
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
//        String tripFareValue=String.format("%.2f", Double.parseDouble(newDistance)*0.6214*Double.parseDouble(currentTrip.TripFare));
        if(Integer.parseInt(currentTrip.TipPercentage)>0){

            Double tip=((Double.parseDouble(newFare)*Double.parseDouble(currentTrip.TipPercentage))/100);
            total= Double.parseDouble(newFare)+tip;
        } else {
            total=Double.parseDouble(newFare);
        }
        total=total+Double.parseDouble(currentTrip.TripFee);
        return total;
    }

}

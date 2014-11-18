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

import java.util.ArrayList;

public class ReceiptAndFeedbackFragment extends Fragment implements ListDialog.setSelectedListner{

    TextView txtPaymentType,txtTripComplete,customerRatting;
    EditText customerComments;
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
        txtPaymentType=(TextView)convertView.findViewById(R.id.txtPaymentType);
        txtTripComplete=(TextView)convertView.findViewById(R.id.txtTripComplete);
        customerRatting=(TextView)convertView.findViewById(R.id.txtCustomerRatting);

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
        preferences.getString("fare", "");
        preferences.getString("dropoff_address", "");
        preferences.getString("dropoff_latitude", "");
        preferences.getString("dropoff_longitude", "");
    }

    public void completTrip(){

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
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
                    Trip currentTrip=complexPreferences.getObject("current_trip_details", Trip.class);
                    driverStatusObject.put("CustomerID", currentTrip.CustomerID+"");
                    driverStatusObject.put("CustomerComments", customerComments.getText().toString()+"");
                    driverStatusObject.put("CustomerRattings", customerRatting.getText().toString()+"");
                    driverStatusObject.put("PaymentType",txtPaymentType.getText().toString()+"" );
                    driverStatusObject.put("TripID", currentTrip.TripID+"");
                    driverStatusObject.put("TripStatus", "Success");
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
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Toast.makeText(getActivity(), "Trip completed Successfully", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = manager.beginTransaction();
                MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
                if (manager.findFragmentByTag("MY_ORDERS") == null) {
                    ft.replace(R.id.main_content, myOrdersFragment,"MY_ORDERS").commit();
                }

            }
        }.execute();


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

}

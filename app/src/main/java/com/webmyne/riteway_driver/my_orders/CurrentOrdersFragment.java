package com.webmyne.riteway_driver.my_orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.model.SharedPreferenceTrips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.xml.datatype.Duration;


public class CurrentOrdersFragment extends Fragment {

    ListView currentOrdersListView;
    CurrentOrdersAdapter currentOrdersAdapter;
    ArrayList<Trip> currentOrdersList;
    SharedPreferenceTrips sharedPreferenceTrips;
    public static CurrentOrdersFragment newInstance(String param1, String param2) {
        CurrentOrdersFragment fragment = new CurrentOrdersFragment();
        return fragment;
    }
    public CurrentOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView=inflater.inflate(R.layout.fragment_current_orders, container, false);
        currentOrdersListView=(ListView)convertView.findViewById(R.id.currentOrdersList);
//        currentOrdersAdapter=new CurrentOrdersAdapter(getActivity(), currentOrdersList);
//        currentOrdersListView.setAdapter(currentOrdersAdapter);
        return convertView;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            sharedPreferenceTrips = new SharedPreferenceTrips();
            currentOrdersList = sharedPreferenceTrips.loadTrip(getActivity());
            ArrayList<Trip> filteredCurruntOrderList=new ArrayList<Trip>();
            for(Trip trip: currentOrdersList){
                if(!trip.TripStatus.contains("Cancel")){
                    filteredCurruntOrderList.add(trip);
                }
            }
            if(currentOrdersList !=null) {
                Collections.reverse(filteredCurruntOrderList);
                currentOrdersAdapter = new CurrentOrdersAdapter(getActivity(), filteredCurruntOrderList);
                currentOrdersListView.setAdapter(currentOrdersAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CurrentOrdersAdapter extends BaseAdapter {

        Context context;
        ArrayList<Trip> currentOrdersList;

        public CurrentOrdersAdapter(Context context, ArrayList<Trip> currentOrdersList) {
            this.context = context;
            this.currentOrdersList = currentOrdersList;
        }

        public int getCount() {
            return currentOrdersList.size();
        }

        public Object getItem(int position) {
            return currentOrdersList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView currentOrderCname,currentOrderDate,currentOrderPickupLocation,currentOrderDropoffLocation,currentOrderFareAmount,
                    orderHistoryStatus;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_current_orders, parent, false);
                holder = new ViewHolder();
                holder.currentOrderCname=(TextView)convertView.findViewById(R.id.currentOrderCname);
                holder.currentOrderDate=(TextView)convertView.findViewById(R.id.currentOrderDate);
                holder.currentOrderPickupLocation=(TextView)convertView.findViewById(R.id.currentOrderPickupLocation);
                holder.currentOrderDropoffLocation=(TextView)convertView.findViewById(R.id.currentOrderDropoffLocation);
                holder.currentOrderFareAmount=(TextView)convertView.findViewById(R.id.currentOrderFareAmount);
                holder. orderHistoryStatus=(TextView)convertView.findViewById(R.id.orderHistoryStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.currentOrderCname.setText(currentOrdersList.get(position).DriverName);
            holder.currentOrderDate.setText(getFormatedDate(currentOrdersList.get(position)));
            holder.currentOrderPickupLocation.setText("pickup: "+currentOrdersList.get(position).PickupAddress);
            holder.currentOrderDropoffLocation.setText("dropoff: "+currentOrdersList.get(position).DropOffAddress);
            holder.currentOrderFareAmount.setText(String.format("$ %.2f", getTotal(currentOrdersList.get(position)))+"");
            holder.orderHistoryStatus.setText("status: "+currentOrdersList.get(position).TripStatus);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "current_trip_details", 0);
                    complexPreferences.putObject("current_trip_details", currentOrdersList.get(position));
                    complexPreferences.commit();
                    Intent i=new Intent(getActivity(), OrderDetailActivity.class);
                    startActivity(i);


                }
            });
            return convertView;


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
            if(Integer.parseInt(currentTrip.TipPercentage)>0){
                Double tip=((Double.parseDouble(currentTrip.TripFare)*Double.parseDouble(currentTrip.TipPercentage))/100);
                total= Double.parseDouble(currentTrip.TripFare)+tip;
            } else {
                total=Double.parseDouble(currentTrip.TripFare);
            }
            total=total+Double.parseDouble(currentTrip.TripFee);
            return total;
        }

    }

}

package com.webmyne.riteway_driver.my_orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.webmyne.riteway_driver.customViews.ListDialog;
import com.webmyne.riteway_driver.model.SharedPreferenceTrips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class OrdersHistoryFragment extends Fragment implements ListDialog.setSelectedListner {

    SharedPreferenceTrips sharedPreferenceTrips;
    ListView ordersHistoryListView;
    OrdersHistoryAdapter ordersHistoryAdapter;
    TextView txtDateSelection;
    ArrayList<Trip> ordersHistoryList;
    ArrayList<String> dateSelectionArray=new ArrayList<String>();
    public static OrdersHistoryFragment newInstance(String param1, String param2) {
        OrdersHistoryFragment fragment = new OrdersHistoryFragment();
        return fragment;
    }
    public OrdersHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dateSelectionArray.add("Current Week");
        dateSelectionArray.add("Last Week");
        dateSelectionArray.add("Current Month");
        dateSelectionArray.add("Last Month");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView=inflater.inflate(R.layout.fragment_orders_history, container, false);
        txtDateSelection=(TextView)convertView.findViewById(R.id.txtDateSelection);
        txtDateSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        ordersHistoryListView =(ListView)convertView.findViewById(R.id.ordersHistoryList);
//        ordersHistoryAdapter =new OrdersHistoryAdapter(getActivity(), ordersHistoryList);
//        ordersHistoryListView.setAdapter(ordersHistoryAdapter);
        return convertView;
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            sharedPreferenceTrips=new SharedPreferenceTrips();
            ordersHistoryList=sharedPreferenceTrips.loadTrip(getActivity());
            ArrayList<Trip> filteredCurruntOrderList=new ArrayList<Trip>();
            for(Trip trip: ordersHistoryList){
                if(!trip.TripStatus.contains("Cancel")){
                    filteredCurruntOrderList.add(trip);
                }
            }
            if(ordersHistoryList != null) {
                ordersHistoryAdapter = new OrdersHistoryAdapter(getActivity(), filteredCurruntOrderList);
                ordersHistoryListView.setAdapter(ordersHistoryAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class OrdersHistoryAdapter extends BaseAdapter {

        Context context;
        ArrayList<Trip> currentOrdersList;

        public OrdersHistoryAdapter(Context context, ArrayList<Trip> currentOrdersList) {
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
            TextView orderHistoryCname,orderHistoryDate,orderHistoryPickupLocation,orderHistoryDropoffLocation,orderHistoryStatus
                    ,orderHistoryFareAmount;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_ordered_history, parent, false);
                holder = new ViewHolder();
                holder.orderHistoryCname=(TextView)convertView.findViewById(R.id.orderHistoryCname);
                holder.orderHistoryDate=(TextView)convertView.findViewById(R.id.orderHistoryDate);
                holder.orderHistoryPickupLocation=(TextView)convertView.findViewById(R.id.orderHistoryPickupLocation);
                holder.orderHistoryDropoffLocation=(TextView)convertView.findViewById(R.id.orderHistoryDropoffLocation);
                holder.orderHistoryStatus=(TextView)convertView.findViewById(R.id.orderHistoryStatus);
                holder.orderHistoryFareAmount=(TextView)convertView.findViewById(R.id.currentOrderFareAmount);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.orderHistoryCname.setText(currentOrdersList.get(position).DriverName);
            holder.orderHistoryDate.setText(getFormatedDate(currentOrdersList.get(position)));
            holder.orderHistoryPickupLocation.setText("pickup: "+currentOrdersList.get(position).PickupAddress);
            holder.orderHistoryDropoffLocation.setText("dropoff: "+currentOrdersList.get(position).DropOffAddress);
            holder.orderHistoryStatus.setText("status: "+currentOrdersList.get(position).TripStatus);
            holder.orderHistoryFareAmount.setText(String.format("$ %.2f", getTotal(currentOrdersList.get(position)))+"");

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

    public void showDialog() {

        ListDialog listDialog = new ListDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        listDialog.setCancelable(true);
        listDialog.setCanceledOnTouchOutside(true);
        listDialog.title("SELECT DATE FILTER");
        listDialog.setItems(dateSelectionArray);
        listDialog.setSelectedListner(this);
        listDialog.show();
    }

    @Override
    public void selected(String value) {

        txtDateSelection.setText("Filtered By "+value);

    }
}

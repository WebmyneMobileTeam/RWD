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
import com.webmyne.riteway_driver.customViews.ListDialog;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.SharedPreferenceTrips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CanceledOrdersFragment extends Fragment implements ListDialog.setSelectedListner{
    ListView ordersCanceledListView;
    OrdersCanceledAdapter ordersCanceledAdapter;
    ArrayList<Trip> filteredOrderList;
    ArrayList<Trip> ordersCanceledList;
    TextView txtDateSelectionForOrderCancel;
    ArrayList<String> dateSelectionArray=new ArrayList<String>();
    SharedPreferenceTrips sharedPreferenceTrips;
    public static CanceledOrdersFragment newInstance(String param1, String param2) {
        CanceledOrdersFragment fragment = new CanceledOrdersFragment();

        return fragment;
    }

    public CanceledOrdersFragment() {
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
        View convertView= inflater.inflate(R.layout.fragment_canceled_orders, container, false);
        txtDateSelectionForOrderCancel=(TextView)convertView.findViewById(R.id.txtDateSelectionForOrderCancel);
        txtDateSelectionForOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        ordersCanceledListView =(ListView)convertView.findViewById(R.id.canceledOrdersList);
//        ordersCanceledAdapter =new OrdersCanceledAdapter(getActivity(), ordersCanceledList);
//        ordersCanceledListView.setAdapter(ordersCanceledAdapter);
        return  convertView;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {

            sharedPreferenceTrips=new SharedPreferenceTrips();
            ordersCanceledList=sharedPreferenceTrips.loadTrip(getActivity());

            filteredOrderList=new ArrayList<Trip>();
            filterData("Current Week");
            if(ordersCanceledList !=null) {
                Collections.reverse(filteredOrderList);
                ordersCanceledAdapter = new OrdersCanceledAdapter(getActivity(), filteredOrderList);
                ordersCanceledListView.setAdapter(ordersCanceledAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class OrdersCanceledAdapter extends BaseAdapter {

        Context context;
        ArrayList<Trip> currentOrdersList;

        public OrdersCanceledAdapter(Context context, ArrayList<Trip> currentOrdersList) {
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
                    ,canceledOrdersFareAmount;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_canceled_orders, parent, false);
                holder = new ViewHolder();
                holder.orderHistoryCname=(TextView)convertView.findViewById(R.id.orderCanceledCname);
                holder.orderHistoryDate=(TextView)convertView.findViewById(R.id.orderCanceledDate);
                holder.orderHistoryPickupLocation=(TextView)convertView.findViewById(R.id.orderCanceledPickupLocation);
                holder.orderHistoryDropoffLocation=(TextView)convertView.findViewById(R.id.orderCanceledDropoffLocation);
                holder.orderHistoryStatus=(TextView)convertView.findViewById(R.id.orderCanceledStatus);
                holder. canceledOrdersFareAmount=(TextView)convertView.findViewById(R.id.currentOrderFareAmount);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.orderHistoryCname.setText(currentOrdersList.get(position).DriverName);
            holder.orderHistoryDate.setText(getFormatedDate(currentOrdersList.get(position)));
            holder.orderHistoryPickupLocation.setText("pickup: "+currentOrdersList.get(position).PickupAddress);
            holder.orderHistoryDropoffLocation.setText("dropoff: "+currentOrdersList.get(position).DropOffAddress);
            holder.orderHistoryStatus.setText("status: "+currentOrdersList.get(position).TripStatus);
            holder.canceledOrdersFareAmount.setText(String.format("$ %.2f", getTotal(currentOrdersList.get(position)))+"");
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

        txtDateSelectionForOrderCancel.setText("Filtered By "+value);
        filterData(value);
    }

    private void filterData(String filterType){
        try {

            filteredOrderList.clear();

            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

            int day = Integer.parseInt(dayFormat.format(new Date()));
            int month = Integer.parseInt(monthFormat.format(new Date()))-1;
            int year = Integer.parseInt(yearFormat.format(new Date()));

            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.YEAR,year);
            calendar.set(calendar.MONTH,month);
            calendar.set(calendar.DAY_OF_MONTH,day);

            int currentWeekOfyear=calendar.get(calendar.WEEK_OF_YEAR);
            int lastWeekOfYear=currentWeekOfyear-1;
            if(lastWeekOfYear<1){
                Calendar c = Calendar.getInstance();
                c.set(c.YEAR,calendar.YEAR-1);
                c.set(c.MONTH,11);
                c.set(c.DAY_OF_MONTH,31);
                lastWeekOfYear=c.get(c.WEEK_OF_YEAR);
            }
            int currentMonth=calendar.get(calendar.MONTH);
            int lastMonth=currentMonth-1;
            if(lastMonth<0){
                Calendar c = Calendar.getInstance();
                c.set(c.YEAR,calendar.YEAR-1);
                c.set(c.MONTH,11);
                c.set(c.DAY_OF_MONTH,31);
                lastMonth=c.get(c.MONTH);
            }

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            for (int i = 0; i < ordersCanceledList.size(); i++) {
                Date loopDate = format.parse(getFormatedDate(ordersCanceledList.get(i)));
                int loopDay = Integer.parseInt(dayFormat.format(loopDate));
                int loopMonth = Integer.parseInt(monthFormat.format(loopDate))-1;
                int loopYear = Integer.parseInt(yearFormat.format(loopDate));

                Calendar loopCalendar = Calendar.getInstance();
                loopCalendar.set(loopCalendar.YEAR,loopYear);
                loopCalendar.set(loopCalendar.MONTH,loopMonth);
                loopCalendar.set(loopCalendar.DAY_OF_MONTH,loopDay);

                int loopCurrentWeekOfyear=loopCalendar.get(loopCalendar.WEEK_OF_YEAR);
                int loopCurrentMonth=loopCalendar.get(loopCalendar.MONTH);

                if( ordersCanceledList.get(i).TripStatus.contains(AppConstants.tripCancelledByDriverStatus) || ordersCanceledList.get(i).TripStatus.contains(AppConstants.tripCancelledByCustomerStatus)) {
                    if (filterType.equalsIgnoreCase("Current Week")) {
                        if (currentWeekOfyear == loopCurrentWeekOfyear) {
                            filteredOrderList.add(ordersCanceledList.get(i));
                        }
                    } else if (filterType.equalsIgnoreCase("Last Week")) {
                        if (lastWeekOfYear == loopCurrentWeekOfyear) {
                            filteredOrderList.add(ordersCanceledList.get(i));
                        }
                    } else if (filterType.equalsIgnoreCase("Current Month")) {
                        if (currentMonth == loopCurrentMonth) {
                            filteredOrderList.add(ordersCanceledList.get(i));
                        }
                    } else if (filterType.equalsIgnoreCase("Last Month")) {
                        if (lastMonth == loopCurrentMonth) {
                            filteredOrderList.add(ordersCanceledList.get(i));
                        }
                    }
                }
            }
            if(ordersCanceledAdapter != null) {
                ordersCanceledAdapter.notifyDataSetChanged();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

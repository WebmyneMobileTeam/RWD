package com.webmyne.riteway_driver.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.customViews.CallWebService;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.customViews.ListDialog;
import com.webmyne.riteway_driver.home.DrawerActivity;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.ResponseMessage;
import com.webmyne.riteway_driver.model.SharedPreferenceNotification;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class NotificationFragment extends Fragment implements ListDialog.setSelectedListner{

    private ArrayList<DriverNotification> notificationList;
    private SharedPreferenceNotification sharedPreferenceNotification;
    private ArrayList<DriverNotification> filteredOrderList;
    private ListView lvCustomerNotifications;
    private NotificationAdapter notificationAdapter;
    private TextView txtDateSelectionForNotification;
    private ArrayList<String> dateSelectionArray=new ArrayList<String>();

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }
    public NotificationFragment() {
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
        View rootView= inflater.inflate(R.layout.fragment_notification, container, false);
        txtDateSelectionForNotification=(TextView)rootView.findViewById(R.id.txtDateSelectionForNotification);
        lvCustomerNotifications=(ListView)rootView.findViewById(R.id.lvDriverNotifications);
        lvCustomerNotifications.setEmptyView(rootView.findViewById(R.id.empty));
        txtDateSelectionForNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isConnected()==true) {
            unreadAllNotification();
        } else {
            Toast.makeText(getActivity(), "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();
        }

        try {
            sharedPreferenceNotification = new SharedPreferenceNotification();
            notificationList = sharedPreferenceNotification.loadNotification(getActivity());
            filteredOrderList=new ArrayList<DriverNotification>();
            filterData("Current Week");

            if (filteredOrderList != null) {
                Collections.reverse(filteredOrderList);
                notificationAdapter = new NotificationAdapter(getActivity(), filteredOrderList);
                lvCustomerNotifications.setAdapter(notificationAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  boolean isConnected() {

        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }


    public void unreadAllNotification() {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
        DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
        new CallWebService(AppConstants.DriverNotificationsStatusChanged+driverProfile.DriverID , CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(String response) {
                ResponseMessage responseMessage = new GsonBuilder().create().fromJson(response, ResponseMessage.class);
//                Log.e("response message for unread all: ",responseMessage.Response+"");
            }

            @Override
            public void error(VolleyError error) {
                Log.e("error: ",error+"");
            }
        }.start();
    }

    public class NotificationAdapter extends BaseAdapter {

        Context context;
        ArrayList<DriverNotification> notificationList;

        public NotificationAdapter(Context context, ArrayList<DriverNotification> notificationList) {
            this.context = context;
            this.notificationList = notificationList;
        }

        public int getCount() {
            return notificationList.size();
        }

        public Object getItem(int position) {
            return notificationList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
        TextView txtMessageTitle,txtNotificationDate,txtNotificationMessage,txtNotificationTime;

        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_notification, parent, false);
                holder = new ViewHolder();

                holder.txtMessageTitle=(TextView)convertView.findViewById(R.id.txtMessageTitle);
                holder.txtNotificationDate=(TextView)convertView.findViewById(R.id.txtNotificationDate);
                holder.txtNotificationMessage=(TextView)convertView.findViewById(R.id.txtNotificationMessage);
                holder.txtNotificationTime=(TextView)convertView.findViewById(R.id.txtNotificationTime);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(notificationList.get(position).notificationStatus.equalsIgnoreCase("false")){
                holder.txtNotificationDate.setTextColor(Color.GREEN);

            }

            holder.txtMessageTitle.setText(notificationList.get(position).Title);
            holder.txtNotificationDate.setText(notificationList.get(position).Date);
            holder.txtNotificationMessage.setText(notificationList.get(position).Message);
            holder.txtNotificationTime.setText(notificationList.get(position).notificationTime);

            return convertView;

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

        txtDateSelectionForNotification.setText("Filtered By "+value);
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
            for (int i = 0; i < notificationList.size(); i++) {
                Date loopDate = format.parse(notificationList.get(i).Date);
                int loopDay = Integer.parseInt(dayFormat.format(loopDate));
                int loopMonth = Integer.parseInt(monthFormat.format(loopDate))-1;
                int loopYear = Integer.parseInt(yearFormat.format(loopDate));

                Calendar loopCalendar = Calendar.getInstance();
                loopCalendar.set(loopCalendar.YEAR,loopYear);
                loopCalendar.set(loopCalendar.MONTH,loopMonth);
                loopCalendar.set(loopCalendar.DAY_OF_MONTH,loopDay);

                int loopCurrentWeekOfyear=loopCalendar.get(loopCalendar.WEEK_OF_YEAR);
                int loopCurrentMonth=loopCalendar.get(loopCalendar.MONTH);


                if (filterType.equalsIgnoreCase("Current Week")) {
                    if (currentWeekOfyear == loopCurrentWeekOfyear) {
                        filteredOrderList.add(notificationList.get(i));
                    }
                } else if (filterType.equalsIgnoreCase("Last Week")) {
                    if (lastWeekOfYear == loopCurrentWeekOfyear) {
                        filteredOrderList.add(notificationList.get(i));
                    }
                } else if (filterType.equalsIgnoreCase("Current Month")) {
                    if (currentMonth == loopCurrentMonth) {
                        filteredOrderList.add(notificationList.get(i));
                    }
                } else if (filterType.equalsIgnoreCase("Last Month")) {
                    if (lastMonth == loopCurrentMonth) {
                        filteredOrderList.add(notificationList.get(i));
                    }
                }

            }
            if(notificationAdapter != null) {
                notificationAdapter.notifyDataSetChanged();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

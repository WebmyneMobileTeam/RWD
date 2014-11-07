package com.webmyne.riteway_driver.notifications;

import android.app.Activity;
import android.content.Context;
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
import com.webmyne.riteway_driver.customViews.ListDialog;

import java.util.ArrayList;


public class NotificationFragment extends Fragment implements ListDialog.setSelectedListner{
    ArrayList<String> notificationList=new ArrayList<String>();
    ListView lvCustomerNotifications;
    NotificationAdapter notificationAdapter;
    TextView txtDateSelectionForNotification;
    ArrayList<String> dateSelectionArray=new ArrayList<String>();
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

        notificationList.add("One");
        notificationList.add("Two");
        notificationList.add("Three");

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
        txtDateSelectionForNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        notificationAdapter=new NotificationAdapter(getActivity(),notificationList);
        lvCustomerNotifications=(ListView)rootView.findViewById(R.id.lvDriverNotifications);
        lvCustomerNotifications.setAdapter(notificationAdapter);
        return rootView;
    }
    public class NotificationAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> notificationList;


        public NotificationAdapter(Context context, ArrayList<String> notificationList) {

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


        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_notification, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

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

    }
}

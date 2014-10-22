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

import java.util.ArrayList;

import javax.xml.datatype.Duration;


public class CurrentOrdersFragment extends Fragment {

    ListView currentOrdersListView;
    CurrentOrdersAdapter currentOrdersAdapter;
    ArrayList<String> currentOrdersList=new ArrayList<String>();
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
        currentOrdersList.add("one");
        currentOrdersList.add("two");
        currentOrdersList.add("three");
        currentOrdersList.add("four");
        currentOrdersList.add("five");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView=inflater.inflate(R.layout.fragment_current_orders, container, false);
        currentOrdersListView=(ListView)convertView.findViewById(R.id.currentOrdersList);
        currentOrdersAdapter=new CurrentOrdersAdapter(getActivity(), currentOrdersList);
        currentOrdersListView.setAdapter(currentOrdersAdapter);
        return convertView;
    }


    public class CurrentOrdersAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> currentOrdersList;

        public CurrentOrdersAdapter(Context context, ArrayList<String> currentOrdersList) {
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
            TextView currentOrderCname,currentOrderDate,currentOrderPickupLocation,currentOrderDropoffLocation,currentOrderFareAmount;
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "search_result_play",0);
//                    complexPreferences.putObject("searched_play",beanSearchList.get(position));
//                    complexPreferences.commit();
                    Intent i=new Intent(getActivity(), OrderDetailActivity.class);
                    startActivity(i);


                }
            });
            return convertView;


        }

    }

}

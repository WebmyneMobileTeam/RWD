package com.webmyne.riteway_driver.my_orders;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.customViews.CallWebService;
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.home.DrawerActivity;
import com.webmyne.riteway_driver.home.DriverProfile;
import com.webmyne.riteway_driver.model.AppConstants;
import com.webmyne.riteway_driver.model.PagerSlidingTabStrip;
import com.webmyne.riteway_driver.model.SharedPreferenceTrips;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MyOrdersFragment extends Fragment {
    private SharedPreferenceTrips sharedPreferenceTrips;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    ProgressDialog progressDialog;
    public ArrayList<Trip> tripArrayList=new ArrayList<Trip>();

    public static MyOrdersFragment newInstance(String param1, String param2) {
        MyOrdersFragment fragment = new MyOrdersFragment();
        return fragment;
    }

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View convertView=inflater.inflate(R.layout.fragment_my_orders, container, false);
        tabs = (PagerSlidingTabStrip)convertView.findViewById(R.id.my_order_tabs);
        pager = (ViewPager) convertView.findViewById(R.id.pager);
        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTripList();
        sharedPreferenceTrips=new SharedPreferenceTrips();
    }

    public void getTripList() {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "driver_data", 0);
        final DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);

        Log.e("list: ", AppConstants.DriverTrips+"9");

        new AsyncTask<Void,Void,Void>() {
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

                new CallWebService(AppConstants.DriverTrips+driverProfile.DriverID , CallWebService.TYPE_JSONARRAY) {

                    @Override
                    public void response(String response) {

                        Type listType=new TypeToken<List<Trip>>(){
                        }.getType();
                        tripArrayList = new GsonBuilder().create().fromJson(response, listType);


                        handleTripListData();
                    }

                    @Override
                    public void error(VolleyError error) {

                        Log.e("error: ",error+"");

                    }
                }.start();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
            }


        }.execute();


    }

    public void handleTripListData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                sharedPreferenceTrips.clearTrip(getActivity());
                for(int i=0;i<tripArrayList.size();i++){
                    sharedPreferenceTrips.saveTrip(getActivity(),tripArrayList.get(i));

                }
                adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
                pager.setAdapter(adapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                tabs.setViewPager(pager);
            }
        });

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = { "Current", "History", "Cancelled" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment=null;
            if(i==0) {
                fragment=CurrentOrdersFragment.newInstance("","");
            } else if(i==1) {
                fragment=OrdersHistoryFragment.newInstance("","");
            } else if(i==2) {
                fragment=CanceledOrdersFragment.newInstance("","");
            }
            return fragment;
        }
    }
}

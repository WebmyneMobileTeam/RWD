package com.webmyne.riteway_driver.my_orders;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.model.PagerSlidingTabStrip;


public class MyOrdersFragment extends Fragment {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView=inflater.inflate(R.layout.fragment_my_orders, container, false);
        tabs = (PagerSlidingTabStrip)convertView.findViewById(R.id.my_order_tabs);
        pager = (ViewPager) convertView.findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = { "Current Orders", "History" };

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
            }
            return fragment;
        }
    }
}

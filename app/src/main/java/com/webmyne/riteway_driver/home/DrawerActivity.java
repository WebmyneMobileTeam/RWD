package com.webmyne.riteway_driver.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.model.CustomTypeface;
import com.webmyne.riteway_driver.my_orders.MyOrdersFragment;
import com.webmyne.riteway_driver.my_profile.MyProfileFragment;
import com.webmyne.riteway_driver.notifications.NotificationFragment;
import com.webmyne.riteway_driver.settings.SettingsFragment;
import com.webmyne.riteway_driver.trip.CurrentTripFragment;

public class DrawerActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private DrawerLayout drawer;
    private ListView leftDrawerList;

    private String[] leftSliderData = {"CURRENT TRIP", "MY ORDERS", "MY PROFILE","NOTIFICATIONS", "SETTINGS"};

    public static String CURRENT_TRIP = "current_trip";
    public static String MY_ORDERS = "my_orders";
    public static String MY_PROFILE = "my_profile";
    public static String NOTIFICATIONS = "notifications";
    public static String SETTINGS = "settings";


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return CustomTypeface.getInstance().createView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        //Load My Orders First
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();


        MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
        if (manager.findFragmentByTag(MY_ORDERS) == null) {
            ft.replace(R.id.main_content, myOrdersFragment,MY_ORDERS).commit();
        }
        txtHeader.setText("MY ORDERS");
        initFields();
        initDrawer();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume ", "in drawer activty");
    }

    private void initFields() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setAdapter(new NavigationDrawerAdapter(DrawerActivity.this, leftSliderData));
        leftDrawerList.setOnItemClickListener(this);

    }

    private void initDrawer() {

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,drawerArrow, R.string.drawer_open,R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Add your onclick logic here
        drawer.closeDrawers();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //drawer items for pupil

        switch (position) {

            case 0:
                CurrentTripFragment currentTripFragment = CurrentTripFragment.newInstance("", "");
                if (manager.findFragmentByTag(CURRENT_TRIP) == null) {
                    ft.replace(R.id.main_content, currentTripFragment,CURRENT_TRIP).commit();
                }
                txtHeader.setText("CURRENT TRIP");
                break;

            case 1:
                MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
                if (manager.findFragmentByTag(MY_ORDERS) == null) {
                    ft.replace(R.id.main_content, myOrdersFragment,MY_ORDERS).commit();
                }
                txtHeader.setText("MY ORDERS");
                break;

            case 2:
                MyProfileFragment myProfileFragment = MyProfileFragment.newInstance("", "");
                if (manager.findFragmentByTag(MY_PROFILE) == null) {
                    ft.replace(R.id.main_content, myProfileFragment,MY_PROFILE).commit();
                }
                txtHeader.setText("MY PROFILE");
                break;

            case 3:
                NotificationFragment notificationFragment = NotificationFragment.newInstance("", "");
                if (manager.findFragmentByTag(NOTIFICATIONS) == null) {
                    ft.replace(R.id.main_content, notificationFragment,NOTIFICATIONS).commit();
                }
                txtHeader.setText("NOTIFICATIONS");
                break;

            case 4:
                SettingsFragment settingsFragment = SettingsFragment.newInstance("", "");
                if (manager.findFragmentByTag(SETTINGS) == null) {
                    ft.replace(R.id.main_content, settingsFragment,SETTINGS).commit();
                }
                txtHeader.setText("SETTINGS");
                break;



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerToggle.onOptionsItemSelected(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    //region Drawer code
    // Navigation Drawer Adapter
    public class NavigationDrawerAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        String[] leftSliderData;

        public NavigationDrawerAdapter(Context context, String[] leftSliderData) {
            this.context = context;
            this.leftSliderData = leftSliderData;
        }

        public int getCount() {

            return leftSliderData.length;

        }

        public Object getItem(int position) {
            return leftSliderData[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView txtDrawerItem;
        }


        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer, parent, false);
                holder = new ViewHolder();
                holder.txtDrawerItem = (TextView) convertView.findViewById(R.id.txtDrawerItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtDrawerItem.setText(leftSliderData[position]);
            return convertView;

        }

    }
    //</editor-fold>


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
}
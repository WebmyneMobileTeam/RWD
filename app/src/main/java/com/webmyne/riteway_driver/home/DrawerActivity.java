package com.webmyne.riteway_driver.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.webmyne.riteway_driver.customViews.ComplexPreferences;
import com.webmyne.riteway_driver.model.CustomTypeface;
import com.webmyne.riteway_driver.my_orders.MyOrdersFragment;
import com.webmyne.riteway_driver.my_orders.OrderDetailActivity;
import com.webmyne.riteway_driver.notifications.NotificationFragment;
import com.webmyne.riteway_driver.settings.SettingsFragment;
import com.webmyne.riteway_driver.trip.CurrentTripFragment;

import java.util.Timer;
import java.util.TimerTask;

public class DrawerActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private String badgevalue;
    private String[] leftSliderData = {"MY ORDERS", "NOTIFICATIONS", "SETTINGS"};

    public static String CURRENT_TRIP = "current_trip";
    public static String MY_ORDERS = "my_orders";

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


//        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(DrawerActivity.this, "driver_data", 0);
//        DriverProfile driverProfile=complexPreferences.getObject("driver_data", DriverProfile.class);
//        Log.e("driver notification id: ",driverProfile.Webmyne_NotificationID+"");


//        if(OrderDetailActivity.isAcceptRequest==true){
//            OrderDetailActivity.isAcceptRequest=false;
//            FragmentManager manager = getSupportFragmentManager();
//            FragmentTransaction ft = manager.beginTransaction();
//
//            CurrentTripFragment currentTripFragment = CurrentTripFragment.newInstance("", "");
//            if (manager.findFragmentByTag(CURRENT_TRIP) == null) {
//                ft.replace(R.id.main_content, currentTripFragment,CURRENT_TRIP).commit();
//            }
//            txtHeader.setText("CURRENT TRIP");
//
//        }
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
                SharedPreferences sharedPreferences = getSharedPreferences("badge_value",MODE_PRIVATE);
                badgevalue=(sharedPreferences.getString("badge_value",null));
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                SharedPreferences sharedPreferences = getSharedPreferences("badge_value",MODE_PRIVATE);
                badgevalue=(sharedPreferences.getString("badge_value",null));
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
                MyOrdersFragment myOrdersFragment = MyOrdersFragment.newInstance("", "");
                if (manager.findFragmentByTag(MY_ORDERS) == null) {
                    ft.replace(R.id.main_content, myOrdersFragment,MY_ORDERS).commit();
                }
                txtHeader.setText("MY ORDERS");
                break;


            case 1:
                NotificationFragment notificationFragment = NotificationFragment.newInstance("", "");
                if (manager.findFragmentByTag(NOTIFICATIONS) == null) {
                    ft.replace(R.id.main_content, notificationFragment,NOTIFICATIONS).commit();
                }
                txtHeader.setText("NOTIFICATIONS");
                break;

            case 2:
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
        TextView txtBadgeValue;
    }


    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_drawer, parent, false);
            holder = new ViewHolder();
            holder.txtDrawerItem = (TextView) convertView.findViewById(R.id.txtDrawerItem);
            holder.txtBadgeValue = (TextView) convertView.findViewById(R.id.txtBadgeValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==1) {

            Log.e("badge value:.....",badgevalue+"");
            if(badgevalue !=null && (!badgevalue.equalsIgnoreCase("0"))){
                holder.txtBadgeValue.setVisibility(View.VISIBLE);
                holder.txtBadgeValue.setText(badgevalue+"");
                notifyDataSetChanged();
            }

        } else {
            holder.txtBadgeValue.setVisibility(View.GONE);
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

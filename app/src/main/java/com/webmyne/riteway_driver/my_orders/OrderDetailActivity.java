package com.webmyne.riteway_driver.my_orders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.model.CustomTypeface;
import com.webmyne.riteway_driver.trip.CurrentTripFragment;

import org.w3c.dom.Text;

public class OrderDetailActivity extends BaseActivity {
    public static boolean isAcceptRequest=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        txtHeader.setText("ORDER DETAILS");
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return CustomTypeface.getInstance().createView(name, context, attrs);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a order detail view.
     */
    public static class PlaceholderFragment extends Fragment {


        public static String CURRENT_TRIP = "current_trip";
        TextView txtAcceptTrip;
        TextView txtCancelTrip;
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
            txtCancelTrip=(TextView)rootView.findViewById(R.id.txtCancelTrip);
            txtAcceptTrip=(TextView)rootView.findViewById(R.id.txtAcceptTrip);
            txtAcceptTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAcceptRequest=true;
                    getActivity().finish();
                }
            });

            txtCancelTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();

        }



    }
}

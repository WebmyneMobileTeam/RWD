package com.webmyne.riteway_driver.receipt_and_feedback;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.model.CustomTypeface;
import com.webmyne.riteway_driver.trip.CurrentTripFragment;

public class ReceiptAndFeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_and_feedback);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ReceiptAndFeedbackFragment receiptAndFeedbackFragment = ReceiptAndFeedbackFragment.newInstance("", "");
        if (manager.findFragmentByTag("RECEIPT_AND_FEEDBACK") == null) {
            ft.replace(R.id.main_content, receiptAndFeedbackFragment,"RECEIPT_AND_FEEDBACK").commit();
        }
        txtHeader.setText("TRIP COMPLETE");
    }
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return CustomTypeface.getInstance().createView(name, context, attrs);
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


}

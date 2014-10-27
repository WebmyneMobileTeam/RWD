package com.webmyne.riteway_driver.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmyne.riteway_driver.R;

/**
 * Created by dhruvil on 22-10-2014.
 */
public class CustomHeaderView extends LinearLayout{


    public CustomHeaderView(Context context) {
        super(context);
    }

    public CustomHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomHeaderView, 0, 0);
        String titleText = a.getString(R.styleable.CustomHeaderView_titleText);

        a.recycle();

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_header, this, true);

        TextView tv = (TextView)this.findViewById(R.id.txtHeaderCustomView);
        tv.setText(titleText);

        TextView tvSubTitle = (TextView)this.findViewById(R.id.txtHeaderSubTitle);




    }

    public void setTitle(String title){


    }

    public void setSubTitle(String subTitle){

    }

    public CustomHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}

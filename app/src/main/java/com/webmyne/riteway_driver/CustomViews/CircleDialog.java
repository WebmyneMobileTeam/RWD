package com.webmyne.riteway_driver.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.webmyne.riteway_driver.R;


/**
 * Created by nirav on 26-11-2014.
 */
public class CircleDialog extends Dialog {

    private Context context;
    private View convertView;

    public CircleDialog(Context context, int theme) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context=context;
        init();
    }

    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.circle_dialog,null);
        setContentView(convertView);
    }
}

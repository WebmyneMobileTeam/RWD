package com.webmyne.riteway_driver.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.webmyne.riteway_driver.R;

import java.util.ArrayList;



/**
 * Created by dhruvil on 26-08-2014.
 */
public  class ListDialog extends Dialog {


    private setSelectedListner listner;
    private View convertView;
    private Context ctx;
    private TextView txtTitle;
    private ListView listDialog;
    private FrameLayout parentDialog;


    public ListDialog(Context context, int theme) {
        super(context, theme);
        this.ctx = context;
        init();

    }

    public void setSelectedListner(setSelectedListner listner){
        this.listner = listner;
    }

    public void init() {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.view_dialog,null);
        setContentView(convertView);

        txtTitle = (TextView)convertView.findViewById(R.id.titleDialog);
        listDialog = (ListView)convertView.findViewById(R.id.listDialog);
        parentDialog=(FrameLayout)convertView.findViewById(R.id.parentDialog);
        parentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }





    public void title(String title){

        txtTitle.setText(title);
        txtTitle.setTextColor(Color.parseColor("#000000"));
        txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setItems(ArrayList items){

        TypeAdapter adapter = new TypeAdapter(ctx,android.R.layout.simple_list_item_1,items);
        listDialog.setAdapter(adapter);

    }


    public class TypeAdapter extends ArrayAdapter {

        ArrayList list;

        public TypeAdapter(Context context, int resource, ArrayList objects) {
            super(context, resource, objects);
            this.list = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final TextView textView = new TextView(ctx);
            textView.setText(list.get(position).toString());
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 8, 10, 8);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            textView.setTextColor(Color.BLACK);


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dismiss();
                    listner.selected(textView.getText().toString());


                }
            });

            return textView;
        }
    }

   public static interface setSelectedListner{

       public void selected(String value);
   }



}

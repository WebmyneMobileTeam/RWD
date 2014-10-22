package com.webmyne.riteway_driver.my_orders;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.webmyne.riteway_driver.R;
import com.webmyne.riteway_driver.application.BaseActivity;
import com.webmyne.riteway_driver.model.PinnedHeaderListView;
import com.webmyne.riteway_driver.model.SectionedBaseAdapter;
import com.webmyne.riteway_driver.model.ViewHolder;

import java.util.ArrayList;

public class OrderDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        txtHeader.setText("ORDER DETAILS");
        getActionBar().setDisplayHomeAsUpEnabled(true);
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

        OrderDetailSectionAdapter orderDetailSectionAdapter;
        PinnedHeaderListView orderDetailListView;
        private ArrayList<ArrayList<String>> orderDetailContents=new ArrayList<ArrayList<String>>();
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ArrayList<String> customerInfoList=new ArrayList<String>();
            customerInfoList.add("one");
            customerInfoList.add("two");
            customerInfoList.add("three");
            customerInfoList.add("four");

            ArrayList<String> tripInfoList=new ArrayList<String>();
            tripInfoList.add("one");
            tripInfoList.add("two");
            tripInfoList.add("three");
            tripInfoList.add("four");
            tripInfoList.add("five");
            ArrayList<String> paymentInfoList=new ArrayList<String>();
            paymentInfoList.add("one");
            paymentInfoList.add("two");
            paymentInfoList.add("three");
            paymentInfoList.add("four");
            orderDetailContents.add(customerInfoList);
            orderDetailContents.add(tripInfoList);
            orderDetailContents.add(paymentInfoList);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
            orderDetailListView=(PinnedHeaderListView)rootView.findViewById(R.id.orderDetailListView);
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            orderDetailSectionAdapter=new OrderDetailSectionAdapter(getActivity());
            orderDetailListView.setAdapter(orderDetailSectionAdapter);
        }

        public class OrderDetailSectionAdapter extends SectionedBaseAdapter {

            private LayoutInflater mInflater;

            public OrderDetailSectionAdapter(Context context) {

                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public Object getItem(int section, int position) {

                return  null;
            }

            @Override
            public long getItemId(int section, int position) {

                return 0;
            }

            // return number of sections
            @Override
            public int getSectionCount() {
//            Log.e("section count: ",marrSectionsWithContent.size()+"");
                return orderDetailContents.size();
            }

            @Override
            public int getCountForSection(int section) {

                ArrayList<String> orderDetailChildList=orderDetailContents.get(section);
//            Log.e("child for section count: ",songFileses.size()+"");
                return orderDetailChildList.size();

            }


            public class SectionHeaderHolder {
                TextView headerTitle;
            }
            @Override
            public View getSectionHeaderView(final int section, View convertView, ViewGroup parent) {
                View view = convertView;
                SectionHeaderHolder sectionHeaderHolder;
                if (view == null) {
                    sectionHeaderHolder=new SectionHeaderHolder();
                    LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflator.inflate(R.layout.item_order_detail_header, parent,false);
                    view.setTag(sectionHeaderHolder);
                } else {
                    sectionHeaderHolder=(SectionHeaderHolder)view.getTag();
                }

                return view;
            }

            @Override
            public int getItemViewTypeCount() {
                return 1;
            }

//            @Override
//            public int getItemViewType(int section, int position) {
//
//                ArrayList<SongFiles> songFileses=marrSectionsWithContent.get(section);
//                SongFiles songFiles=songFileses.get(position);
//
//                if(songFiles.FileType.equalsIgnoreCase("mp3")) {
//                    return MP3_FILE;
//                } else {
//                    return PDF_FILE;
//                }
//
//            }

            @Override
            public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

                ViewHolder.HolderOrderDetailCell viewHolderForOrderDetail=null;
//                int type=getItemViewType(section,position);
                    if(convertView == null){
                        viewHolderForOrderDetail = new ViewHolder().new HolderOrderDetailCell();
                        convertView = mInflater.inflate(R.layout.item_order_detail_view, parent,false);
                        viewHolderForOrderDetail.cellOrderDetail=new CellOrderDetail(convertView,getActivity());
                        convertView.setTag(viewHolderForOrderDetail);
                    }else{
                        viewHolderForOrderDetail = (ViewHolder.HolderOrderDetailCell)convertView.getTag();
                    }

                return convertView;
            }
        }
    }
}

package com.yiwucheguanjia.carmgr.home;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yiwucheguanjia.carmgr.R;
import com.yiwucheguanjia.carmgr.WaitActivity;
import com.yiwucheguanjia.carmgr.account.RegisterActivity;

import java.util.ArrayList;

/**
 * 热门业务ListView的适配器
 */
public class BusinessAdapter extends BaseAdapter{

    private Activity activity;
    private MyViewholder viewHolder;
    private ArrayList<BusinessBean> list;
    private LayoutInflater layoutInflater;
    /**
     *
     * @param activity
     * @param list
     */
    public BusinessAdapter(Activity activity, ArrayList<BusinessBean> list) {
        this.list = list;
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) { // 获取组件布局
            convertView = layoutInflater.inflate(R.layout.home_business_item, null);

            viewHolder = new MyViewholder();
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.business_item_rl);
            viewHolder.businessImg = (ImageView)convertView.findViewById(R.id.business_img);
            viewHolder.businessNameTxt = (TextView)convertView.findViewById(R.id.business_name_txt);
            viewHolder.businessNameTxt.setText(list.get(position).getBusinessName());
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MyViewholder)convertView.getTag();
        }
        viewHolder.businessImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wait","wait");
                Intent intent = new Intent(activity, WaitActivity.class);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    class MyViewholder {
        public ImageView businessImg;
        public TextView businessNameTxt;
        public RelativeLayout relativeLayout;
    }
}
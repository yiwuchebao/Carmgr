package com.yiwucheguanjia.merchantcarmgr.appointment.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.yiwucheguanjia.merchantcarmgr.R;
import com.yiwucheguanjia.merchantcarmgr.appointment.controller.AppointAdapter;
import com.yiwucheguanjia.merchantcarmgr.appointment.model.AppointmentBean;
import com.yiwucheguanjia.merchantcarmgr.callback.MyStringCallback;
import com.yiwucheguanjia.merchantcarmgr.utils.UrlString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/27.
 */
public class AllAppointmentFragment extends Fragment {


    View homeView;
    @BindView(R.id.allappoint_item_rv)
    RecyclerView itemRv;
    AppointAdapter appointAdapter;
    private SharedPreferences sharedPreferences;
    private ArrayList<AppointmentBean> appointmentBeanArrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("CARMGR_MERCHANT",getActivity().MODE_PRIVATE);
        homeView = (View)inflater.inflate(R.layout.activity_allapoint_fragment,container,false);
        ButterKnife.bind(this,homeView);
        getData();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        itemRv.setAdapter(appointAdapter);
        return homeView;
    }

    private void getData(){
        OkGo.post(UrlString.APPOINTMENT_MANAGER)
                .tag(this)
                .params("username",sharedPreferences.getString("ACCOUNT",null))
                .params("token",sharedPreferences.getString("TOKEN",null))
                .params("version",UrlString.APP_VERSION)
                .execute(new MyStringCallback(getActivity(),getResources().getString(R.string.loading)) {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("list_size") > 0){
                                AppointmentBean appointmentBean;
                                JSONArray serviceListJN = jsonObject.getJSONArray("services_list");
                                appointmentBeanArrayList = new ArrayList<AppointmentBean>();
                                for (int i = 0;i < serviceListJN.length();i++){
                                    appointmentBean = new AppointmentBean();
                                    JSONObject serviceItem = serviceListJN.getJSONObject(i);
                                    appointmentBean.setSubscribe_state(serviceItem.getString("subscribe_state"));
                                    appointmentBean.setSubscribe_service_total(serviceItem.getString("subscribe_service_total"));
                                    appointmentBean.setSubscribe_price(serviceItem.getString("subscribe_price"));
                                    appointmentBean.setSubscribe_user_mobile(serviceItem.getString("subscribe_user_mobile"));
                                    appointmentBean.setSubscribe_user_id(serviceItem.getString("subscribe_user_id"));
                                    appointmentBean.setSubscribe_order(serviceItem.getString("subscribe_order"));
                                    appointmentBean.setSubscribe_date(serviceItem.getString("subscribe_date"));
                                    appointmentBean.setSubscribe_service_name(serviceItem.getString("subscribe_service_name"));
                                    appointmentBean.setSubscribe_service_img(serviceItem.getString("subscribe_service_img"));
                                    appointmentBeanArrayList.add(appointmentBean);
                                }
                                Log.e("appoint",s);

                                appointAdapter = new AppointAdapter(getActivity(),appointmentBeanArrayList);
                                itemRv.setAdapter(appointAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}

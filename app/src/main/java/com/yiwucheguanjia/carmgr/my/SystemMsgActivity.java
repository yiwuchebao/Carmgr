package com.yiwucheguanjia.carmgr.my;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.yiwucheguanjia.carmgr.R;
import com.yiwucheguanjia.carmgr.my.controller.SystemMsgAdapter;

/**
 * Created by Administrator on 2016/10/21.
 */
public class SystemMsgActivity extends Activity {
    private SystemMsgAdapter systemMsgAdapter;
    @BindView(R.id.system_msg_rv)
    protected RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_msg_activity);
        ButterKnife.bind(this);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        systemMsgAdapter = new SystemMsgAdapter(SystemMsgActivity.this);
        recyclerView.setAdapter(systemMsgAdapter);

    }
}

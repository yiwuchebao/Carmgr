package com.yiwucheguanjia.merchantcarmgr;

/**
 * Created by Administrator on 2016/10/17.
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.yiwucheguanjia.merchantcarmgr.appointment.view.AppointmentFragment;
import com.yiwucheguanjia.merchantcarmgr.my.view.MyFragment;
import com.yiwucheguanjia.merchantcarmgr.post.PostFragment;
import com.yiwucheguanjia.merchantcarmgr.workbench.view.WorkbenchFragment;

import rx.Subscription;

/**
 * 主Activity
 *
 * @author
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    // 以下四个是底部控件
    private RelativeLayout workbenchRl;
    private RelativeLayout commercialLayout;
    private RelativeLayout progressLayout;
    private RelativeLayout callYiwuLayout;

    // 以下四个是底部标签的文本
    private TextView homeTxt;
    private TextView commercialTxt;
    private TextView progressTxt;
    private TextView callYiwuTxt;

    //以下四个是底部标签的图标
    private ImageView homeImg;
    private ImageView commercialImg;
    private ImageView progressImg;
    private ImageView callYiwuImg;

    // 以下四个是底部标签切换的Fragment
    private Fragment workbenchFragment;
    private PostFragment postFragment;
    private Fragment progressFragment;
    private Fragment callYiwuFragment;
    //当前显示的Fragment
    private Fragment currentFragment;
    private SharedPreferences sharedPreferences;
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    private int ft_pos;
    Subscription mSubscription;
    private Toolbar mToolbar;
    private int POST_MANAGE_REQUEST = 201;//发布服务后的请求码
    private int POST_MANAGE_RESULT = 202;//发布服务后的结果码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.orange), 0);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("CARMGR", MainActivity.MODE_PRIVATE);
        //联网判断是否登录或登录过期，过期则跳到登录界面，登录界面不可返回
        initUI();
        initTab();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.post_manage");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        ft_pos = savedInstanceState.getInt("position");
        clickTab1Layout(ft_pos);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 记录当前的position
        outState.putInt("position", ft_pos);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        workbenchRl = (RelativeLayout) findViewById(R.id.main_workbench_rl);
        commercialLayout = (RelativeLayout) findViewById(R.id.main_commercial_rl);
        progressLayout = (RelativeLayout) findViewById(R.id.rl_progress);
        callYiwuLayout = (RelativeLayout) findViewById(R.id.rl_callyiwu);
        homeTxt = (TextView) findViewById(R.id.main_workbench_tv);
        commercialTxt = (TextView) findViewById(R.id.main_post_tv);
        progressTxt = (TextView) findViewById(R.id.main_appoint_tv);
        callYiwuTxt = (TextView) findViewById(R.id.main_my_tv);
        homeImg = (ImageView) findViewById(R.id.main_workbench_img);
        commercialImg = (ImageView) findViewById(R.id.main_post_img);
        progressImg = (ImageView) findViewById(R.id.main_appoint_img);
        callYiwuImg = (ImageView) findViewById(R.id.main_my_img);
        workbenchRl.setOnClickListener(this);
        commercialLayout.setOnClickListener(this);
        progressLayout.setOnClickListener(this);
        callYiwuLayout.setOnClickListener(this);
    }

    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (workbenchFragment == null) {
            workbenchFragment = new WorkbenchFragment();
        }

        if (!workbenchFragment.isAdded()) {
            // 提交事务
            fragmentManager.beginTransaction().add(R.id.content_layout, workbenchFragment).commit();

            // 记录当前Fragment
            currentFragment = workbenchFragment;
            // 设置图片文本的变化
//            homeImg.setImageResource(R.mipmap.tab_home_img_pre);
//            homeTxt.setTextColor(getResources().getColor(R.color.orange));
//            commercialImg.setImageResource(R.mipmap.tab_commercial_img_nor);
//            commercialTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            progressImg.setImageResource(R.mipmap.tab_progress_img_nor);
//            progressTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            callYiwuImg.setImageResource(R.mipmap.tab_callyiwu_img_nor);
//            callYiwuTxt.setTextColor(getResources().getColor(R.color.gray_default));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_workbench_rl: // 首页标签
                Log.e("haha", "kkk");
                clickTab1Layout(0);
                break;
            case R.id.main_commercial_rl: // 商户标签
                Log.e("haha", "kkk");
                clickTab1Layout(1);
                break;
            case R.id.rl_progress: // 进度标签
                Log.e("haha", "kkk");
                clickTab1Layout(2);
                break;
            case R.id.rl_callyiwu:
                clickTab1Layout(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //这是登录处返回的请求码与结果码，规定workbenchFragment发出的请求码全部是1
        if (requestCode == 1 && resultCode == 1) {
            fragmentManager.beginTransaction().remove(workbenchFragment).commit();
            workbenchFragment = new WorkbenchFragment();
            postFragment = new PostFragment();
            progressFragment = new AppointmentFragment();
            callYiwuFragment = new MyFragment();
            addOrShowFragment(fragmentManager.beginTransaction(), workbenchFragment);
        } else if (requestCode == 1 && resultCode == 10) {//workbenchFragment选择地区
            workbenchFragment.onActivityResult(1, 2, null);
        } else if (requestCode == 2 && resultCode == 10) {//postFragment选择地区
            postFragment.onActivityResult(2, 10, null);
        } else if (requestCode == 3 && resultCode == 10) {//ProgressFragment选择地区
            progressFragment.onActivityResult(3, 10, null);
        } else if (requestCode == 4 && resultCode == 10) {//CallYiwu选择地区
            callYiwuFragment.onActivityResult(4, 10, null);
        }
//        else if (resultCode == 2 && requestCode == 2) {
//            fragmentManager.beginTransaction().remove(workbenchFragment).commit();
//            workbenchFragment = new WorkbenchFragment();
//            postFragment = new PostFragment();
//            progressFragment = new AppointmentFragment();
//            callYiwuFragment = new MyFragment();
//            addOrShowFragment(fragmentManager.beginTransaction(), workbenchFragment);
//        }
        else if (resultCode == 1 && requestCode == 3) {
            fragmentManager.beginTransaction().remove(workbenchFragment).commit();
            workbenchFragment = new WorkbenchFragment();
            postFragment = new PostFragment();
            progressFragment = new AppointmentFragment();
            callYiwuFragment = new MyFragment();
            addOrShowFragment(fragmentManager.beginTransaction(), workbenchFragment);
        } else if (requestCode == 1 && resultCode == 20) {//扫描界面返回了workbenchFragment

        } else if (requestCode == 2 && resultCode == 20) {//扫描界面返回了merchantFragment

        } else if (requestCode == 3 && resultCode == 20) {//扫描界面返回了progressFragment

        } else if (requestCode == 4 && resultCode == 20) {//扫描界面返回了yiwuFragment

        }

    }

    /**
     * 点击第一个tab
     */
    private void clickTab1Layout(int id) {
        ft_pos = id;
        if (id == 0) {
            if (workbenchFragment == null) {

                workbenchFragment = new WorkbenchFragment();
            }
            addOrShowFragment(fragmentManager.beginTransaction(), workbenchFragment);
            // 设置底部tab变化
//            homeImg.setImageResource(R.mipmap.tab_home_img_pre);
//            homeTxt.setTextColor(getResources().getColor(R.color.orange));
//            commercialImg.setImageResource(R.mipmap.tab_commercial_img_nor);
//            commercialTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            progressImg.setImageResource(R.mipmap.tab_progress_img_nor);
//            progressTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            callYiwuImg.setImageResource(R.mipmap.tab_callyiwu_img_nor);
//            callYiwuTxt.setTextColor(getResources().getColor(R.color.gray_default));
        } else if (id == 1) {
            if (postFragment == null) {
                postFragment = new PostFragment();
            }
            addOrShowFragment(fragmentManager.beginTransaction(), postFragment);
//            homeImg.setImageResource(R.mipmap.tab_home_img_nor);
//            homeTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            commercialImg.setImageResource(R.mipmap.tab_commercial_img_pre);
//            commercialTxt.setTextColor(getResources().getColor(R.color.orange));
//            progressImg.setImageResource(R.mipmap.tab_progress_img_nor);
//            progressTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            callYiwuImg.setImageResource(R.mipmap.tab_callyiwu_img_nor);
//            callYiwuTxt.setTextColor(getResources().getColor(R.color.gray_default));
        } else if (id == 2) {
            if (progressFragment == null) {
                progressFragment = new AppointmentFragment();
            }
            addOrShowFragment(fragmentManager.beginTransaction(), progressFragment);
//            homeImg.setImageResource(R.mipmap.tab_home_img_nor);
//            homeTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            commercialImg.setImageResource(R.mipmap.tab_commercial_img_nor);
//            commercialTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            progressImg.setImageResource(R.mipmap.tab_progress_img_pre);
//            progressTxt.setTextColor(getResources().getColor(R.color.orange));
//            callYiwuImg.setImageResource(R.mipmap.tab_callyiwu_img_nor);
//            callYiwuTxt.setTextColor(getResources().getColor(R.color.gray_default));
        } else if (id == 3) {
            if (callYiwuFragment == null) {
                callYiwuFragment = new MyFragment();
            }
            addOrShowFragment(fragmentManager.beginTransaction(), callYiwuFragment);
//            homeImg.setImageResource(R.mipmap.tab_home_img_nor);
//            homeTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            commercialImg.setImageResource(R.mipmap.tab_commercial_img_nor);
//            commercialTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            progressImg.setImageResource(R.mipmap.tab_progress_img_nor);
//            progressTxt.setTextColor(getResources().getColor(R.color.gray_default));
//            callYiwuImg.setImageResource(R.mipmap.tab_callyiwu_img_pre);
//            callYiwuTxt.setTextColor(getResources().getColor(R.color.orange));
        }

    }

    /**
     * 返回键的点击事件
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Exit();
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出事件
     */
    public void Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("友情提示!").setMessage("您确定退出吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.cancel();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment) {
            return;
        }

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            Log.e("kdkw", "iiiiii");
            transaction.hide(currentFragment).add(R.id.content_layout, fragment).commitAllowingStateLoss();
        } else {
            transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
            fragment.onActivityResult(5, 5, null);//跳转到fragment界面后立即更新相关组件
        }

        currentFragment = fragment;
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.post_manage")) {//跳到发布管理界面
                Log.e("kdkw", "jjppw");
                if (postFragment == null) {
                    postFragment = new PostFragment();
                    addOrShowFragment(fragmentManager.beginTransaction(), postFragment);
                } else {
                    addOrShowFragment(fragmentManager.beginTransaction(), postFragment);
                    postFragment.onActivityResult(POST_MANAGE_REQUEST, POST_MANAGE_RESULT, null);//此处实现跳转，用名称变量比较好
                }
//                fragmentManager.beginTransaction().remove(workbenchFragment).commitAllowingStateLoss();
//                workbenchFragment = new WorkbenchFragment();
//                postFragment = new PostFragment();
//                progressFragment = new AppointmentFragment();
//                callYiwuFragment = new MyFragment();
//                addOrShowFragment(fragmentManager.beginTransaction(), workbenchFragment);
            } else if (TextUtils.equals(action,"action.loginout")) {//退出此界面以及所管理的fragment
                MainActivity.this.finish();
            } else if (action.equals("action.appointment")) {//跳到预约界面
                if (callYiwuFragment != null) {
                    addOrShowFragment(fragmentManager.beginTransaction(), callYiwuFragment);
                } else {
                    Log.e("kdkw", "jjppp");
                    addOrShowFragment(fragmentManager.beginTransaction(), new MyFragment());
                }
            }
        }
    };


    @Override
    protected void onDestroy() { // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

}

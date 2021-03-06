package com.yiwucheguanjia.carmgr.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yiwucheguanjia.carmgr.R;
import com.yiwucheguanjia.carmgr.ViewPagerAdapter;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/31.
 */
public class WelcomActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    //定义ViewPager对象
    private ViewPager viewPager;
    private Button goBtn;
    private View welcomeView;
    //定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    private LayoutInflater myInflater;
    //定义一个ArrayList来存放View
    private ArrayList<View> views;

    //引导图片资源
    private static final int[] pics = {R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3};

    //底部小点的图片
    private ImageView[] points;

    //记录当前选中位置
    private int currentIndex;
    private Boolean flag = false;
    private LinearLayout ll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myInflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_welcome);
//        welcomeView = myInflater.inflate(R.layout.activity_welcome,null);
        initView();
        initData();
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainInten = new Intent(WelcomActivity.this, com.yiwucheguanjia.carmgr.MainActivity.class);
                WelcomActivity.this.startActivity(mainInten);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    /**
     * 初始化组件
     */
    private void initView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        goBtn = (Button) findViewById(R.id.welcome_go);
        goBtn.setOnClickListener(this);
        //实例化ArrayList对象
        views = new ArrayList<View>();

        //实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        //初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setAdjustViewBounds(true);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(this);

        //初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pointLL);

        points = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
        if (position == pics.length - 1){
            goBtn.setVisibility(View.VISIBLE);
        }else {
            goBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                flag = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                flag = true;
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                if (viewPager.getCurrentItem() == viewPager.getAdapter()
                        .getCount() - 1 && !flag) {
                    Intent mainInten = new Intent(WelcomActivity.this, com.yiwucheguanjia.carmgr.MainActivity.class);
                    WelcomActivity.this.startActivity(mainInten);
                    finish();
                }
                flag = true;
                break;
        }
    }
}

package com.yiwucheguanjia.merchantcarmgr.workbench.view;

/**
 * Created by Administrator on 2016/10/24.
 */
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.yiwucheguanjia.merchantcarmgr.R;

/**
 *
 * @author Lyloou
 */

public class HistogramView extends View{

    private Paint mPaint;
    private Rect mRect;

    private int mWidth;
    private int mHeight;

    private int mPaddingStart;
    private int mPaddingEnd;
    private int mPaddingTop;
    private int mPaddingBottom;

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private Context mContext;

    private ArrayList<Bar> mBarLists;

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramView(Context context) {
        this(context, null);
    }

    private void initData() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRect = new Rect();

        // default data
        mBarLists = new ArrayList<Bar>();
//        Bar bar1 = new Bar(1, 0.3f, ContextCompat.getColor(mContext,R.color.orange), "one", "30");
//        Bar bar2 = new Bar(2, 0.55f, ContextCompat.getColor(mContext,R.color.orange), "two", "55");
//        Bar bar3 = new Bar(3, 0.8f, Color.BLACK, "three", "80");
//        mBarLists.add(bar1);
//        mBarLists.add(bar2);
//        mBarLists.add(bar3);
    }

    public void setBarLists(ArrayList<Bar> barLists){
        mBarLists = barLists;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getSizeFromMeasureSpec(widthMeasureSpec, 280);
        mHeight = getSizeFromMeasureSpec(heightMeasureSpec, 280);

        mPaddingStart = getPaddingStart();
        mPaddingEnd = getPaddingEnd();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mLeft = mPaddingStart;
        mTop = mPaddingTop;
        mRight = mWidth - mPaddingEnd;
        mBottom = mHeight - mPaddingBottom;

        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        // set background
        canvas.drawColor(Color.alpha(0));
        mRect.set(mLeft, mTop, mWidth, mHeight);
        mPaint.setColor(Color.alpha(0));
        canvas.drawRect(mRect, mPaint);
        //*/

        // 设置底部文字属性
        mPaint.setTextSize(sp2Px(mContext, 10));
        mPaint.setTextAlign(Paint.Align.CENTER);
        FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int fontHeight = (int) Math.ceil(fontMetricsInt.bottom - fontMetricsInt.top);

        int N = mBarLists.size();
        int UNIT_WIDTH = (mRight - mLeft) / (2 * N + 1);

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        // 逐个画bar
        for (int i = 0; i < N; i++) {
            Bar bar = mBarLists.get(i);

            // 画 bar 底部文字
            left = (int) (mLeft + (i * 2 + 0.5f) * UNIT_WIDTH);
            right = left + UNIT_WIDTH * 2;
            top = mBottom - fontHeight;
            bottom = mBottom;
            mRect.set(left, top, right, bottom);
            int baseLine = (mRect.top + mRect.bottom - fontMetricsInt.top - fontMetricsInt.bottom) / 2;
            mPaint.setColor(Color.BLACK);
            canvas.drawText(bar.bootomText, mRect.centerX(), baseLine, mPaint);

            // 画 bar 图形
            left = mLeft + (i * 2 + 1) * UNIT_WIDTH;
            right = left + UNIT_WIDTH;
            bottom = mBottom - fontHeight;
            top = bottom - (int) ((mBottom - mTop - fontHeight * 2) * bar.ratio);
            mRect.set(left, top, right, bottom);
            mPaint.setColor(bar.color);
            canvas.drawRect(mRect, mPaint);

            // 画 bar 顶部文字
            left = (int) (mLeft + (i * 2 + 0.5f) * UNIT_WIDTH);
            right = left + UNIT_WIDTH * 2;
            bottom = top;
            top = top - fontHeight;
            mRect.set(left, top, right, bottom);
            baseLine = (mRect.top + mRect.bottom - fontMetricsInt.top - fontMetricsInt.bottom) / 2;
            mPaint.setColor(Color.BLACK);
            canvas.drawText(bar.topText, mRect.centerX(), baseLine, mPaint);
        }
//        Bar bar = mBarLists.get(0);
        // 画线
        mPaint.setColor(Color.alpha(0));
        canvas.drawLine(mLeft, mBottom - fontHeight, mRight, mBottom - fontHeight, mPaint);
        // canvas.drawLine(mLeft, mTop + fontHeight, mRight, mTop + fontHeight, mPaint);

        super.onDraw(canvas);
    }

    public class Bar {
        public int id;
        public float ratio;
        public int color;
        public String bootomText;
        public String topText;


        public Bar(int id, float ratio, int color, String bootomText, String topText) {
            this.id = id;
            this.ratio = ratio;
            this.color = color;
            this.bootomText = bootomText;
            this.topText = topText;
        }
    }


    // 工具类
    public static int getSizeFromMeasureSpec(int measureSpec, int defaultSize) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            result = defaultSize;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(defaultSize, size);
            }
        }
        return result;
    }
    public static float sp2Px(Context context, float sp){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        float px = metrics.scaledDensity;
        return sp * px;
    }
}

package com.dybs.usbcamera.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dybs.usbcamera.R;


/**
 * describe:  主界动态面识别人脸区域
 * version:0.1
 * author: created by wengliuhu
 * time: 2019/7/26 13
 */
public class AutoScanView extends View {
    private final String TAG = getClass().toString();
    private Context mContext;
    private Paint mPaint;

    private volatile float mLeft   =   0;
    private volatile float mTop    =   0;
    private volatile float mRifht  =   0;
    private volatile float mBottom =   0;

    private int mLineColor;
    private float mLineWidth  =   0;
    private float mLineLengthScale = 0.2f;
//
//    private float mAdjustScale = 0.2f;

    public AutoScanView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public AutoScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public AutoScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        if (attrs == null) return;
        Resources resources = mContext.getResources();
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AutoScanView);
        mLineColor = typedArray.getColor(R.styleable.AutoScanView_auto_lineColor, resources.getColor(R.color.colorAccent));
        mLineWidth = typedArray.getDimension(R.styleable.AutoScanView_auto_linewidth, 0);
        mLineLengthScale = typedArray.getFloat(R.styleable.AutoScanView_auto_scaleLineLength, 0.4f);
        typedArray.recycle();

    }

    /**
     * 刷新位置信息
     * @param left  人脸左
     * @param top   人脸上
     * @param right 右
     * @param bottom 下
     */
    public void freash(float left, float top, float right, float bottom){
        if (left == mLeft && top == mTop) return;
        mLeft = left;
        mTop = top;
        mRifht = right;
        mBottom = bottom;
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            invalidate();
            return;
        }

        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) mPaint = new Paint();
//        if (mLeft == 0 && mRifht == 0){
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            return;
//        }
//
//        if (mScreenHeight == 0 || mScreenWidth == 0)
//        {
//            mScreenHeight = getRight() - getLeft();
//            mScreenWidth = getBottom() - getTop();
//        }

        float width = mRifht - mLeft;
        float height = mBottom - mTop;

        if (width <= 0 || height <= 0) return;
        mLineWidth = 4;
        // 偏右下
//        float left = mLeft + width * mAdjustScale - 560 - width * mAdjustScale;
//        float top = mTop  + height * mAdjustScale + 100 - height * mAdjustScale;
        float left = mLeft;
        float top = mTop;

        int lineLength = (int) (width / 3);

        mPaint.setColor(mLineColor);
        mPaint.setTextSize(12);

        // 画左上角
        canvas.drawRect(left , top, left + lineLength, top + mLineWidth, mPaint);
        canvas.drawRect(left, top + mLineWidth, left + mLineWidth, top + lineLength, mPaint);

        // 画右上角
        canvas.drawRect(left + width - lineLength, top, left + width, top + mLineWidth, mPaint);
        canvas.drawRect(left + width - mLineWidth, top + mLineWidth, left + width, top + lineLength, mPaint);

        // 画左下角
        canvas.drawRect(left, top + height - lineLength, left+ mLineWidth, top + height, mPaint);
        canvas.drawRect(left + mLineWidth, top + height - mLineWidth, left + lineLength, top + height, mPaint);

        // 画右下角
        canvas.drawRect(left + width - lineLength, top + height - mLineWidth, left + width, top + height, mPaint);
        canvas.drawRect(left + width - mLineWidth, top + height - lineLength, left + width, top + height, mPaint);

        // 绘制人脸宽度和高度
//        String text = "宽 * 高：" + (int) width + "*" + (int)height;
////        float textWidth = mPaint.measureText(text);
//        mPaint.setTextSize(24);
//        mPaint.setColor(Color.parseColor("#f00000"));
////        Log.d(TAG, "textsize:" + mPaint.getTextSize() + "width:" + textWidth + "起始位置：" + (getWidth() - textWidth) / 2);
//        canvas.drawText(text, left + lineLength, top - mPaint.getTextSize(), mPaint);
    }
}

package com.tfedu.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * desc :百分比进度播放View
 * author：xiedong
 * data：2018/9/12
 */
@SuppressLint("AppCompatCustomView")
public class PercentPalyView extends ImageView {
    private Paint ringPaint;
    private int startSweepValue;
    private float currentAngle;
    private int currentPercent;
    private long targetPercent;

    public PercentPalyView(Context context) {
        this(context, null);
    }

    public PercentPalyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentPalyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        startSweepValue = -90;
        currentAngle = 0;
        currentPercent = 0;

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setStrokeWidth(8);
        ringPaint.setColor(Color.parseColor("#1ab4ed"));
        ringPaint.setStyle(Paint.Style.STROKE); //设置画笔填充样式
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(8, 8, getMeasuredWidth() - 8, getMeasuredHeight() - 8);
        canvas.drawArc(rectF, startSweepValue, (currentAngle / targetPercent) * 360, false, ringPaint);
    }

    public void setTimeTotalLength(long targetPercent) {
        this.targetPercent = targetPercent;
    }

    public void setCurrentPercent(long currentPercent) {
        currentAngle = currentPercent;
        invalidate(); //如果当前布局中已经渲染成功，需要通知重绘布局
    }

    public void setBackgroundRes(int res) {
        this.setBackgroundResource(res);
    }

}

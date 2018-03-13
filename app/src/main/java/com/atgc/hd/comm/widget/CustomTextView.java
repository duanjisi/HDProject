package com.atgc.hd.comm.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.atgc.hd.R;

/**
 * 点评详细的时间textview
 *
 * @author lianggk
 * @date 17/9/24
 */
public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private RectF oval1;
    private RectF oval2;
    private RectF oval3;

    private RectF ovalLine;

    private float lineHeight;
    private int colorWhite;
    private int colorLighterGray;
    private int colorLighterGreen;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lineHeight = getResources().getDimensionPixelSize(R.dimen.dp_1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        colorWhite = getResources().getColor(R.color.white);
        colorLighterGray = getResources().getColor(R.color.color_theme_background);
        colorLighterGreen = getResources().getColor(R.color.color_green);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float radius = h - 2;
        oval1 = new RectF(w - radius * 2 - 8, -radius * 2, w + 4, radius);
        oval2 = new RectF(0, 0, w, h);
        oval3 = new RectF(w - h, 0, w, h);
        ovalLine = new RectF(0, h - lineHeight, w - h, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画实心灰色矩形
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(colorLighterGray);
        canvas.drawRect(oval2, mPaint);

        // 画实心白色矩形
        mPaint.setColor(colorWhite);
        canvas.drawRect(oval3, mPaint);

        // 画右边实心扇形
        mPaint.setColor(colorLighterGray);
        canvas.drawArc(oval1, 0, 90, true, mPaint);

        // 画底部浅绿色线
        mPaint.setColor(colorLighterGreen);
        canvas.drawRect(ovalLine, mPaint);

        // 画右边浅绿色扇形线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineHeight);
        canvas.drawArc(oval1, 0, 90, false, mPaint);

        super.onDraw(canvas);
    }

}

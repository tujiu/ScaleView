package com.example.administrator.scaleview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Administrator on 2015/2/14.
 */
public class ScaleView extends View implements ValueAnimator.AnimatorUpdateListener {

    private final static String TAG = "ScaleView";

    private final static int DEFAULT_HEIGHT = 150; //dp
    private final static int DEFAULT_WIDTH = 300; //dp
    private final static int MAX_VALUE = 100;
    private final static int CIRCLE_DEGREE = 360;
    private final static int MARK_WIDTH = 2; //dp
    private final static int MARK_HEIGHT = 30; //dp
    private final static int MARD_RADIUS = 120; //dp
    private final static int TEXT_SIZE = 20; //dp
    private final static int DURATION = 1000;

    private Context context;

    private int defalutWidth;
    private int defalutHeight;
    private int markWidth;
    private int markHeight;
    private int markRadius;
    private int textsize;
    private float value = 0;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private ValueAnimator valueAnimator;

    public ScaleView(Context context) {
        super(context);
        init(context);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        float f = getResources().getDisplayMetrics().density;
        defalutWidth = (int) (DEFAULT_WIDTH * f);
        defalutHeight = (int) (DEFAULT_HEIGHT * f);
        markWidth = (int) (MARK_WIDTH * f);
        markHeight = (int) (MARK_HEIGHT * f);
        markRadius = (int) (MARD_RADIUS * f);
        textsize = (int) (TEXT_SIZE * f);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textsize);
    }

    private int measureSize(int measureSpec, int defaultSize) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = defaultSize;
        if (specMode == MeasureSpec.AT_MOST) {
            // the default size is bigger than the parent can offer
            // use the max size
            if (result > specSize) {
                result = specSize;
            }
        } else if (specMode == MeasureSpec.EXACTLY) {
            // given a exactly size, use it.
            result = specSize;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureSize(widthMeasureSpec, defalutWidth);
        int measuredHeight = measureSize(heightMeasureSpec, defalutHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            createBitmp(right - left, bottom - top);
        }
    }

    private void createBitmp(int width, int height) {
        if (bitmap != null) {
            bitmap.recycle();
        }

        bitmap = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ALPHA_8);
        canvas = new Canvas(bitmap);

        drawBitmap(width, height);
    }

    private void drawBitmap(int width, int height) {
        canvas.save();
        canvas.translate(width / 2, height);
        float degree = (float)CIRCLE_DEGREE / MAX_VALUE;
        for (int i = 0; i < MAX_VALUE; i++) {
            // 画数字
            if (i % 10 == 0) {
                canvas.drawText("" + i,-markWidth, -height + textsize, paint);
                // 画一个长刻度
                canvas.drawRect(-markWidth / 2, -markRadius, markWidth / 2, -markRadius + markHeight, paint);
            } else {
                // 画一个短刻度
                canvas.drawRect(-markWidth / 2, -markRadius, markWidth / 2, -markRadius + markHeight / 2, paint);
            }
            canvas.rotate(degree);
        }
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight());
        canvas.drawRect(-markWidth / 2, -markRadius / 2, markWidth / 2, 0, paint);
        canvas.rotate(-value * CIRCLE_DEGREE / MAX_VALUE);
        canvas.drawBitmap(bitmap, -getMeasuredWidth() / 2, -getMeasuredHeight(), null);
        canvas.restore();
    }

    public void setValue(float value) {
        if (value < 0) {
            value = 0;
        }
        if (value > MAX_VALUE) {
            value = MAX_VALUE;
        }
        if (this.value != value) {
            animateToValue(value);
        }
    }

    private void animateToValue(float value) {
        if (valueAnimator == null) {
            valueAnimator = createAnimator(value);
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.setFloatValues(this.value, value);
        valueAnimator.start();
    }

    private ValueAnimator createAnimator(float value) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(this.value, value);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(this);
        return valueAnimator;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        value = Float.valueOf(valueAnimator.getAnimatedValue().toString());
        Log.e(TAG, "value = " + value);
        invalidate();
    }
}

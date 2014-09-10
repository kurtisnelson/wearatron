package com.bignerdranch.android.support.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

import com.bignerdranch.android.support.R;
import com.bignerdranch.android.support.util.SizeUtils;

public class WizardProgressView extends View {

    private static final int DEFAULT_BAR_HEIGHT_DP = 30;
    private static final int DEFAULT_BAR_SPACING_DP = 30;
    private static final int DEFAULT_STEP_COLOR = Color.GRAY;
    private static final int DEFAULT_ACTIVE_COLOR = Color.BLACK;

    private int mTop;
    private int mCount;
    private int mStep;
    private int mBarWidthPx;
    private int mBarHeightPx;
    private int mBarSpacingPx;
    private int mStepWidthPx;
    private Paint mDefaultStepPaint;
    private Paint mActiveStepPaint;

    public WizardProgressView(Context context) {
        this(context, null);
    }

    public WizardProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WizardProgressView, 0, 0);
        try {
            int defaultBarSpacingPx = SizeUtils.getPixels(context, DEFAULT_BAR_SPACING_DP);
            int defaultBarHeightPx = SizeUtils.getPixels(context, DEFAULT_BAR_HEIGHT_DP);

            int stepColor = typedArray.getColor(R.styleable.WizardProgressView_stepColor, DEFAULT_STEP_COLOR);
            int activeColor = typedArray.getColor(R.styleable.WizardProgressView_activeColor, DEFAULT_ACTIVE_COLOR);
            mBarSpacingPx = typedArray.getDimensionPixelSize(R.styleable.WizardProgressView_barSpacing, defaultBarSpacingPx);
            mBarHeightPx = typedArray.getDimensionPixelSize(R.styleable.WizardProgressView_barHeight, defaultBarHeightPx);

            mDefaultStepPaint = new Paint();
            mActiveStepPaint = new Paint();

            mDefaultStepPaint.setColor(stepColor);
            mActiveStepPaint.setColor(activeColor);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = 0;

        Rect bar = new Rect(left, mTop, mBarWidthPx, mTop + mBarHeightPx);

        for (int i = 0; i < mCount; i++) {
            Paint currentPaint = i <= mStep ? mActiveStepPaint : mDefaultStepPaint;
            canvas.drawRect(bar, currentPaint);

            bar.left += mStepWidthPx;
            bar.right = bar.left + mBarWidthPx;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int hPadded = h - getPaddingBottom();
        mTop = (hPadded / 2) - (mBarHeightPx / 2);
        float totalBarSpace = w - (mBarSpacingPx * (mCount - 1));

        // Ceiling operation required to prevent cast from shaving off decimals.
        // These values will accumulate as extra space to the right of the bars.
        mBarWidthPx = (int) FloatMath.ceil(totalBarSpace / mCount);
        mStepWidthPx = mBarWidthPx + mBarSpacingPx;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightPadding = getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mBarHeightPx + heightPadding);
    }

    public void setCount(int count) {
        mCount = count;
    }

    public void setStep(int step) {
        mStep = step;
        invalidate();
    }
}

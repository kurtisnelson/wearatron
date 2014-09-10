package com.bignerdranch.android.support.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class WizardViewPager extends ViewPager {

    private boolean mScrollingEnabled = false;

    public WizardViewPager(Context context) {
        this(context, null);
    }

    public WizardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrollingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mScrollingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setScrollingEnabled(boolean scrollingEnabled) {
        mScrollingEnabled = scrollingEnabled;
    }

}
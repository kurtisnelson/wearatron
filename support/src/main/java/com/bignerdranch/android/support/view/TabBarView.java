package com.bignerdranch.android.support.view;

/**
 Copyright 2014 Mirko Dimartino

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 NOTES AL:
 - removed R.layout.custom_ab
 - commented out CheatSheet call
 - set orientation in code

 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

public class TabBarView extends LinearLayout {
    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }


    private static final int STRIP_HEIGHT = 6;

    public final Paint mPaint;

    private int mStripHeight;
    private float mOffset = 0f;
    public static int mSelectedTab = 0;
    public ViewPager pager;

    public static int tabCount;
    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private View child;

    private View nextChild;

    public static int a;

    public TabBarView(Context context) {
        this(context, null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.actionBarTabBarStyle);
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // AL: set these in code and not xml
//        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        setLayoutParams(layoutParams);

        setOrientation(HORIZONTAL);

        setWillNotDraw(false);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        mStripHeight = (int) (STRIP_HEIGHT * getResources().getDisplayMetrics().density + .5f);
    }

    public void setStripColor(int color) {
        if (mPaint.getColor() != color) {
            mPaint.setColor(color);
            invalidate();
        }
    }

    public void setStripHeight(int height) {
        if (mStripHeight != height) {
            mStripHeight = height;
            invalidate();
        }
    }

    public void setSelectedTab(int tabIndex) {
        if (tabIndex < 0) {
            tabIndex = 0;
        }
        final int childCount = getChildCount();
        if (tabIndex >= childCount) {
            tabIndex = childCount - 1;
        }
        if (mSelectedTab != tabIndex) {
            mSelectedTab = tabIndex;
            invalidate();
        }
    }

    public void setOffset(int position, float offset) {
        if (mOffset != offset) {
            mOffset = offset;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the strip manually
        child = getChildAt(mSelectedTab);
        int height = getHeight();
        if (child != null) {
            float left = child.getLeft();
            float right = child.getRight();
            if (mOffset > 0f && mSelectedTab < tabCount - 1) {
                nextChild = getChildAt(mSelectedTab + 1);
                if (nextChild != null) {
                    final float nextTabLeft = nextChild.getLeft();
                    final float nextTabRight = nextChild.getRight();
                    left = (mOffset * nextTabLeft + (1f - mOffset) * left);
                    right = (mOffset * nextTabRight + (1f - mOffset) * right);
                }
            }
            canvas.drawRect(left, height - mStripHeight, right, height, mPaint);
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            mSelectedTab = position;
            mOffset = positionOffset;

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {

            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    public void notifyDataSetChanged() {

        this.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (getResources().getConfiguration().orientation == 1) {

                // AL: this will throw NPE if the adapter does not return a page title,
                // which it is not required to do. surround with try/catch?

                addTabViewP(i, pager.getAdapter().getPageTitle(i).toString(),
                        ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));

            } else {

                addTabViewL(i, pager.getAdapter().getPageTitle(i).toString(),
                        ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));

            }
        }

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mSelectedTab = pager.getCurrentItem();

            }
        });

    }

    private void addTabViewL(final int i, String string, int pageIconResId) {
        TabView tab = new TabView(getContext());
//		tab.setIcon(pageIconResId);
        tab.setText(string, pageIconResId);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(i);
            }
        });

        this.addView(tab);
    }


    private void addTabViewP(final int i, final String string, int pageIconResId) {
        final TabView tab = new TabView(getContext());
        tab.setIcon(pageIconResId);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                pager.setCurrentItem(i);
            }
        });

        // AL: temporarilty removed
//        CheatSheet.setup(tab, string);

        this.addView(tab);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


}
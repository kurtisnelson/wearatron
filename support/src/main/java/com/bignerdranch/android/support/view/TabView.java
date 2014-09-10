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
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.actionBarTabStyle);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarTabTextStyle, outValue, true);

        int txtstyle = outValue.data;

        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        mImageView = new ImageView(context);
        mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mImageView.setScaleType(ScaleType.CENTER_INSIDE);

        mTextView = new TextView(context);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setCompoundDrawablePadding(pad);
        mTextView.setTextAppearance(context, txtstyle);

        this.addView(mImageView);
        this.addView(mTextView);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setIcon(int resId) {
        setIcon(getContext().getResources().getDrawable(resId));
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageDrawable(icon);
        } else {
            mImageView.setVisibility(View.GONE);
            mImageView.setImageDrawable(null);
        }
    }

    public void setText(int resId, int ico) {
        setText(getContext().getString(resId), ico);
    }

    public void setText(CharSequence text, int ico) {
        mTextView.setText(text);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(ico, 0, 0, 0);
    }

}
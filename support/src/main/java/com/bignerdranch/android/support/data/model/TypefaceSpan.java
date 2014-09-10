package com.bignerdranch.android.support.data.model;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan extends MetricAffectingSpan {

    private Typeface mTypeface;
    private int mTextSize;

    public TypefaceSpan(Typeface typeface) {
        this(typeface, 0);
    }

    public TypefaceSpan(Typeface typeface, int textSize) {
        mTypeface = typeface;
        mTextSize = textSize;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        if (mTextSize != 0) {
            p.setTextSize(mTextSize);
        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        if (mTextSize != 0) {
            tp.setTextSize(mTextSize);
        }
    }
}

package com.bignerdranch.android.support.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class SizeUtils {

    public static int getPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) ((float) dp / metrics.density);
    }
}
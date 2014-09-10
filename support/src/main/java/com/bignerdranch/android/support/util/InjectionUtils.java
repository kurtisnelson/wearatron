package com.bignerdranch.android.support.util;

import android.content.Context;

import com.bignerdranch.android.support.BaseApplication;

public class InjectionUtils {

    public static void injectClass(Context context) {
        injectClass(context, context);
    }

    public static void injectClass(Context context, Object obj) {
        ((BaseApplication) context.getApplicationContext()).inject(obj);
    }

    public static <T> T get(Context context, Class<T> objectClass) {
        return ((BaseApplication) context.getApplicationContext()).get(objectClass);
    }
}
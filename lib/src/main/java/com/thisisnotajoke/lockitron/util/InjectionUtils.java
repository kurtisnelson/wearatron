package com.thisisnotajoke.lockitron.util;

import android.content.Context;

import com.thisisnotajoke.lockitron.WearatronApplication;

public class InjectionUtils {

    public static void injectClass(Context context) {
        injectClass(context, context);
    }

    public static void injectClass(Context context, Object obj) {
        ((WearatronApplication) context.getApplicationContext()).inject(obj);
    }

    public static <T> T get(Context context, Class<T> objectClass) {
        return ((WearatronApplication) context.getApplicationContext()).get(objectClass);
    }
}
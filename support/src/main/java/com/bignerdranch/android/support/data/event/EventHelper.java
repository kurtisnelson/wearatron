package com.bignerdranch.android.support.data.event;

import android.util.Log;

import com.bignerdranch.android.support.AppConstants;

import de.greenrobot.event.EventBus;

public class EventHelper {
    private static final String TAG = AppConstants.APP_TAG + EventHelper.class.getSimpleName();

    public static void postEvent(Object event) {
        if (!(event instanceof BaseEvent)) {
            Log.e(TAG, "Event not of correct type:" + event.getClass().toString());
        }
        getEventBus().post(event);
    }

    public static void registerSubscriber(Object subscriber) {
        if (!getEventBus().isRegistered(subscriber)) {
            getEventBus().register(subscriber);
        }
    }

    public static void unregisterSubscriber(Object subscriber) {
        if (getEventBus().isRegistered(subscriber)) {
            getEventBus().unregister(subscriber);
        }
    }

    private static EventBus getEventBus() {
        return EventBus.getDefault();
    }
}


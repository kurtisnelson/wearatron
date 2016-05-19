package com.thisisnotajoke.mobile.controller;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.thisisnotajoke.lockitron.model.WearDataApi;
import com.thisisnotajoke.mobile.NotificationDecorator;
import com.thisisnotajoke.mobile.R;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    private static final String TAG = "WearDispatchService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if(messageEvent.getPath().equals(WearDataApi.LOCK_ITEM_PATH)){
            NotificationDecorator.notify(this, NotificationDecorator.Type.PUSH, getString(R.string.app_name));
        }
    }
}
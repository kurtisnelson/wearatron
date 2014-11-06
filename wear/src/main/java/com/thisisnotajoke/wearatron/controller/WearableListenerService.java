package com.thisisnotajoke.wearatron.controller;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.thisisnotajoke.lockitron.model.WearDataApi;
import com.thisisnotajoke.wearatron.NotificationDecorator;
import com.thisisnotajoke.wearatron.R;

public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    private static final String TAG = "WearDispatchService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if(messageEvent.getPath().equals(WearDataApi.HINT_PATH)){
            if(messageEvent.getData()[0] == WearDataApi.HINT_ON_PAYLOAD[0]) {
                String name = getString(R.string.lockitron);
                NotificationDecorator.notify(this, NotificationDecorator.Type.HINT, name);
            }else {
                NotificationDecorator.cancel(this);
            }
        }
    }
}

package com.thisisnotajoke.wearatron;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearDispatchService extends WearableListenerService {
    private static final String HINT_PATH = "/hint";
    private static final String TAG = "WearDispatchService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if(messageEvent.getPath().equals(HINT_PATH)){
            if(messageEvent.getData()[0] == 0x1) {
                NotificationDecorator.notify(this, NotificationDecorator.Type.HINT);
            }else {
                NotificationDecorator.cancel(this);
            }
        }
    }
}

package com.thisisnotajoke.wearatron.controller;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.thisisnotajoke.lockitron.model.WearDataApi;
import com.thisisnotajoke.lockitron.util.InjectionUtils;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.lockitron.model.DataManager;

import javax.inject.Inject;

public class MobileListenerService extends WearableListenerService {
    private static final String TAG = "WearDispatchService";

    @Inject
    PreferenceManager mPreferenceManager;

    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        InjectionUtils.injectClass(this);

        if(mPreferenceManager.getToken() == null || mPreferenceManager.getLock() == null){
            Toast.makeText(this, "Please open the phone app, login, and select a lock", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if (messageEvent.getPath().equals(WearDataApi.ACTION_PATH)) {
            if (messageEvent.getData()[0] == 0x1) {
                mDataManager.lockMyLock();
            } else {
                mDataManager.unlockMyLock();
            }
        }
    }
}
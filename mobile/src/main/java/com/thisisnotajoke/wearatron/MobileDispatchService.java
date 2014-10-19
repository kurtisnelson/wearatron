package com.thisisnotajoke.wearatron;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.thisisnotajoke.lockitron.util.InjectionUtils;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.thisisnotajoke.lockitron.GeofenceManager;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.lockitron.model.DataManager;

import javax.inject.Inject;

public class MobileDispatchService extends WearableListenerService {

    private static final String ACTION_PATH = "/action";
    private static final String TAG = "WearDispatchService";

    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    GeofenceManager mGeofenceManager;
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
        if (messageEvent.getPath().equals(ACTION_PATH)) {
            if (messageEvent.getData()[0] == 0x1) {
                mDataManager.lockMyLock();
            } else {
                mDataManager.unlockMyLock();
            }
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, ReceiveTransitionsIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);

            new AsyncTask<PendingIntent, Void, Void>() {
                @Override
                protected Void doInBackground(PendingIntent... params) {
                    mGeofenceManager.setFenceLocation();
                    mGeofenceManager.registerGeofences(params[0]);
                    return null;
                }
            }.execute(pendingIntent);
        }
    }
}
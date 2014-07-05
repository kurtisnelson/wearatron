package com.thisisnotajoke.wearatron;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.thisisnotajoke.lockitron.CommandTask;
import com.thisisnotajoke.lockitron.PreferenceManager;

public class WearDispatchService extends WearableListenerService implements CommandTask.Callback {

    private static final String ACTION_PATH = "/action";
    private static final String SUCCESS_PATH = "/status-success";
    private static final String ERROR_PATH = "/status-error";
    private static final String TAG = "WearDispatchService";

    private String mUUID;
    private String mToken;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager pm = new PreferenceManager(this);
        mToken = pm.getToken().getToken();
        mUUID = pm.getLock();
        Log.d(TAG, "onCreate");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if(messageEvent.getPath().equals(ACTION_PATH)){
            if(messageEvent.getData()[0] == 0x1) {
                execute(CommandTask.LOCK);
            }else{
                execute(CommandTask.UNLOCK);
            }
        }
    }

    @Override
    public void success(String lock) {

    }

    @Override
    public void error(String lock, VolleyError error) {
        Log.e(TAG, "Volley Error", error);
    }

    private void execute(String command) {
        long token = Binder.clearCallingIdentity();
        try {
            new CommandTask(this.getApplicationContext(), mToken, mUUID, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, command);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
package com.thisisnotajoke.lockitron.mobile;

import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.thisisnotajoke.lockitron.CommandTask;
import com.thisisnotajoke.lockitron.Lockitron;
import com.thisisnotajoke.lockitron.PreferenceManager;
import com.thisisnotajoke.lockitron.User;

public class WearDispatchService extends WearableListenerService implements CommandTask.Callback, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOCK_PATH = "/action/lock";
    private static final String UNLOCK_PATH = "/action/unlock";
    private static final String SUCCESS_PATH = "/status/success";
    private static final String ERROR_PATH = "/status/error";
    private static final String TAG = "WearDispatchService";

    private String mUUID;
    private String mToken;
    private User mApi;
    private String mLastNode;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager pm = new PreferenceManager(this);
        mToken = pm.getToken().getToken();
        mUUID = pm.getLock();
        mApi = new Lockitron(this).user(mToken);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        mLastNode = messageEvent.getSourceNodeId();
        if(messageEvent.getPath().equals(LOCK_PATH)){
            execute(CommandTask.LOCK);
        }else if(messageEvent.getPath().equals(UNLOCK_PATH)){
            execute(CommandTask.UNLOCK);
        }
    }

    @Override
    public void success(String lock) {
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, mLastNode, SUCCESS_PATH, null).await();
        if (!result.getStatus().isSuccess()) {
            Log.e(TAG, "ERROR: failed to send success message: " + result.getStatus());
        }
    }

    @Override
    public void error(String lock, VolleyError error) {
        Log.e(TAG, "Volley Error", error);
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, mLastNode, ERROR_PATH, null).await();
        if (!result.getStatus().isSuccess()) {
            Log.e(TAG, "ERROR: failed to send error message: " + result.getStatus());
        }
    }

    private void execute(String command) {
        long token = Binder.clearCallingIdentity();
        try {
            new CommandTask(this.getApplicationContext(), mToken, mUUID, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, command);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Cannot connect to Play Services, we are screweed: " + connectionResult);
    }
}
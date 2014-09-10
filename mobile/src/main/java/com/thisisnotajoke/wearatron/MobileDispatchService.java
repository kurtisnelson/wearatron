package com.thisisnotajoke.wearatron;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bignerdranch.android.support.util.InjectionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.thisisnotajoke.lockitron.CommandTask;
import com.thisisnotajoke.lockitron.GeofenceManager;
import com.thisisnotajoke.lockitron.PreferenceManager;

import javax.inject.Inject;

public class MobileDispatchService extends WearableListenerService implements CommandTask.Callback, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationClient.OnAddGeofencesResultListener {

    private static final String ACTION_PATH = "/action";
    private static final String TAG = "WearDispatchService";

    private String mUUID;
    private String mToken;
    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    GeofenceManager mGeofenceManager;
    private LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        InjectionUtils.injectClass(this);

        if(mPreferenceManager.getToken() == null || mPreferenceManager.getLock() == null){
            Toast.makeText(this, "Please open the phone app, login, and select a lock", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        mToken = mPreferenceManager.getToken().getToken();
        mUUID = mPreferenceManager.getLock();
        Log.d(TAG, "onCreate");

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
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
            mPreferenceManager.setLocation(mLocationClient.getLastLocation());
            Intent intent = new Intent(this, ReceiveTransitionsIntentService.class);

            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mLocationClient.addGeofences(mGeofenceManager.getGeofences(), pendingIntent, this);
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
        Log.d(TAG, "added geofences" + strings);
    }
}
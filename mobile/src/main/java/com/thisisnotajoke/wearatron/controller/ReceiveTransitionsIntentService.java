package com.thisisnotajoke.wearatron.controller;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.thisisnotajoke.wearatron.GeofenceManager;
import com.thisisnotajoke.lockitron.model.WearDataApi;

import java.util.List;

public class ReceiveTransitionsIntentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ReceiveTransitionsServ";

    public ReceiveTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Couldn't setup geofence " + geofencingEvent.getErrorCode());
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER || transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggerList = geofencingEvent.getTriggeringGeofences();
            for (Geofence g : triggerList) {
                if (g.getRequestId().equals(GeofenceManager.HINT_REQUEST_ID)) {
                    Log.d(TAG, "entered geofence, pushing hint");
                    pushHint(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER);
                }
            }
        } else {
            Log.e(TAG, "Invalid transition type: " + transitionType);
        }
    }

    private boolean pushHint(boolean add) {
        GoogleApiClient mClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mClient.blockingConnect();
        Log.d(TAG, "Starting task");
        List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mClient).await().getNodes();
        for (Node node : nodes) {
            Log.d(TAG, "Firing message to " + node);
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mClient, node.getId(), WearDataApi.HINT_PATH, add ? WearDataApi.HINT_ON_PAYLOAD : WearDataApi.HINT_OFF_PAYLOAD).await();
            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
            }
            Log.d(TAG, "Sent message " + result.getStatus());
        }
        mClient.disconnect();
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

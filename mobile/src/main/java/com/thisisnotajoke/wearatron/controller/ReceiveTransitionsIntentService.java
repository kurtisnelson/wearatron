package com.thisisnotajoke.wearatron.controller;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.thisisnotajoke.lockitron.GeofenceManager;
import com.thisisnotajoke.lockitron.model.WearDataApi;

import java.util.List;

public class ReceiveTransitionsIntentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ReceiveTransitionsIntentService";

    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // First check for errors
        if (LocationClient.hasError(intent)) {
            // Get the error code with a static method
            int errorCode = LocationClient.getErrorCode(intent);
            // Log the error
            Log.e("ReceiveTransitionsIntentService",
                    "Location Services error: " +
                            Integer.toString(errorCode)
            );
        } else {
            int transitionType = LocationClient.getGeofenceTransition(intent);
            if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER || transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
                for (Geofence g : triggerList) {
                    if (g.getRequestId().equals(GeofenceManager.HINT_REQUEST_ID)) {
                        pushHint(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ? true : false);
                    }
                }
            } else {
                Log.e("ReceiveTransitionsIntentService",
                        "Geofence transition error: " +
                                Integer.toString(transitionType)
                );
            }
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
        Log.d(TAG, "got nodes");

        for(Node node : nodes) {
            Log.d(TAG, "Firing message to " + node);
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mClient, node.getId(), WearDataApi.HINT_PATH, add ? WearDataApi.HINT_ON_PAYLOAD : WearDataApi.HINT_OFF_PAYLOAD).await();
            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                mClient.disconnect();
                return false;
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

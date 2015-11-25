package com.thisisnotajoke.wearatron.controller;

import android.app.IntentService;
import android.content.Context;
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
import com.thisisnotajoke.wearatron.NotificationDecorator;
import com.thisisnotajoke.wearatron.R;

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
                    String name = getString(R.string.lockitron);
                    if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        NotificationDecorator.notify(this, NotificationDecorator.Type.HINT, name);
                    } else {
                        NotificationDecorator.cancel(this);
                    }
                }
            }
        } else {
            Log.e(TAG, "Invalid transition type: " + transitionType);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static Intent newIntent(Context context) {
        return new Intent(context, ReceiveTransitionsIntentService.class);
    }
}

package com.thisisnotajoke.wearatron;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.wearatron.controller.ReceiveTransitionsIntentService;

import java.util.ArrayList;

public class GeofenceManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    public static final String HINT_REQUEST_ID = "HintRequest";
    private static final String TAG = "GeofenceManager";
    private final ArrayList<Geofence> mFenceList;

    private PreferenceManager mPreferenceManager;
    private GoogleApiClient mGoogleApiClient;


    public GeofenceManager(Context context, PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        mFenceList= new ArrayList<>();
        buildGoogleApiClient(context);
        mGoogleApiClient.connect();
    }

    private void buildGeofences() {
        mFenceList.clear();
        Double lat = mPreferenceManager.getLocationLatitude();
        Double lng = mPreferenceManager.getLocationLongitude();
        if(lat == null || lng == null)
            return;
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GeofenceManager.HINT_REQUEST_ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(
                        lat, lng, 500f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        mFenceList.add(geofence);
    }

    public void registerGeofences(Context context) {
        if(!mGoogleApiClient.isConnected())
            return;
        buildGeofences();
        if(mFenceList.isEmpty())
            return;
        PendingIntent intent = PendingIntent.getService(context, 0, ReceiveTransitionsIntentService.newIntent(context), PendingIntent.
                FLAG_UPDATE_CURRENT);
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                intent
        ).setResultCallback(this);
    }

    public void setFenceLocation() {
        if(!mGoogleApiClient.isConnected())
            return;
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            mPreferenceManager.setLocation(lastLocation);
        }
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mFenceList);
        return builder.build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {
        Log.d(TAG, "Geofence set " + status.toString());
        mPreferenceManager.setLocationEnabled(true);
    }
}

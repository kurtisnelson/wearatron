package com.thisisnotajoke.lockitron;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.bignerdranch.android.support.util.InjectionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GeofenceManager implements LocationClient.OnAddGeofencesResultListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    public static final String HINT_REQUEST_ID = "HintRequest";

    @Inject
    PreferenceManager mPreferenceManager;

    private LocationClient mLocationClient;

    public GeofenceManager(Context context) {
        InjectionUtils.injectClass(context, this);
        mLocationClient = new LocationClient(context, this, this);
        mLocationClient.connect();
    }

    private List<Geofence> buildGeofences() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GeofenceManager.HINT_REQUEST_ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(
                        mPreferenceManager.getLocationLatitude(), mPreferenceManager.getLocationLongitude(), 500f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        ArrayList<Geofence> list = new ArrayList<Geofence>();
        list.add(geofence);
        return list;
    }

    public void registerGeofences(PendingIntent intent) {
        mLocationClient.addGeofences(buildGeofences(), intent, this);
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
        mPreferenceManager.setLocationEnabled(true);
    }

    /**
     * Blocks until a location fix can be grabbed
     */
    public void setFenceLocation() {
        mPreferenceManager.setLocation(mLocationClient.getLastLocation());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GeofenceManager", "location client failure " + connectionResult);
    }
}

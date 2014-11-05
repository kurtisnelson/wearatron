package com.thisisnotajoke.lockitron;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.thisisnotajoke.lockitron.model.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class GeofenceManager implements LocationClient.OnAddGeofencesResultListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    public static final String HINT_REQUEST_ID = "HintRequest";

    private PreferenceManager mPreferenceManager;

    private LocationClient mLocationClient;

    public GeofenceManager(Context context, PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        mLocationClient = new LocationClient(context, this, this);
        mLocationClient.connect();
    }

    private List<Geofence> buildGeofences() {
        Double lat = mPreferenceManager.getLocationLatitude();
        Double lng = mPreferenceManager.getLocationLongitude();
        ArrayList<Geofence> list = new ArrayList<Geofence>();
        if(lat == null || lng == null)
            return list;
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GeofenceManager.HINT_REQUEST_ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(
                        lat, lng, 500f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        list.add(geofence);
        return list;
    }

    public void registerGeofences(PendingIntent intent) {
        List<Geofence> fences = buildGeofences();
        if(fences.isEmpty()) return;
        mLocationClient.addGeofences(fences, intent, this);
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
        mPreferenceManager.setLocationEnabled(true);
    }

    public void setFenceLocation() {
        if(mLocationClient.isConnected()) {
            mPreferenceManager.setLocation(mLocationClient.getLastLocation());
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
        Log.d("GeofenceManager", "location client failure " + connectionResult);
    }
}

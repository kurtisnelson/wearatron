package com.thisisnotajoke.lockitron;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GeofenceManager {
    public static final String HINT_REQUEST_ID = "HintRequest";

    @Inject
    PreferenceManager mPreferenceManager;

    public List<Geofence> getGeofences() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GeofenceManager.HINT_REQUEST_ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(
                        mPreferenceManager.getLocationLatitude(), mPreferenceManager.getLocationLongitude(), 500f)
                .setExpirationDuration(86400000)
                .build();
        ArrayList<Geofence> list = new ArrayList<Geofence>();
        list.add(geofence);
        return list;
    }
}

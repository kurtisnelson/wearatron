package com.thisisnotajoke.lockitron;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.location.Geofence;

import org.scribe.model.Token;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PreferenceManager {
    private static final String TOKEN_SECRET = "Secret";
    private static final String TOKEN_TOKEN = "Token";
    private static final String LOCK_UUID = "Lock.UUID";
    protected static final String BACKUP_KEY = "LockitronBackups";
    protected static final String PREF_NAME = "Lockitron";
    private static final String LOCATION_LONG_KEY = "LocationLong";
    private static final String LOCATION_LAT_KEY = "LocationLat";
    private final SharedPreferences prefs;
    private final Context mContext;

    public PreferenceManager(Context context){
        mContext = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(mContext);
        bm.dataChanged();
    }

    public void setToken(Token token){
        prefs
                .edit()
                .putString(TOKEN_SECRET, token.getSecret())
                .putString(TOKEN_TOKEN, token.getToken())
                .apply();
    }

    public Token getToken() {
        String secret = prefs.getString(TOKEN_SECRET, null);
        String token = prefs.getString(TOKEN_TOKEN, null);

        if (secret == null && token == null) {
            return null;
        }
        return new Token(token, secret);
    }

    public void setLock(String uuid){
        prefs
                .edit()
                .putString(LOCK_UUID, uuid)
                .apply();
    }

    public String getLock() {
        return prefs.getString(LOCK_UUID, null);
    }

    public double getLocationLatitude() {
        String latStr = prefs.getString(LOCATION_LAT_KEY, null);
        return Double.valueOf(latStr);
    }

    public double getLocationLongitude() {
        String longStr = prefs.getString(LOCATION_LONG_KEY, null);
        return Double.valueOf(longStr);
    }

    public void setLocation(Location location) {
        prefs
                .edit()
                .putString(LOCATION_LONG_KEY, String.valueOf(location.getLongitude()))
                .putString(LOCATION_LAT_KEY, String.valueOf(location.getLatitude()))
                .apply();
    }
}

